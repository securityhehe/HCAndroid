package com.hc.load.vm

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.view.View
import androidx.core.os.bundleOf
import androidx.databinding.ObservableInt
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import com.blankj.utilcode.util.ToastUtils
import com.hc.data.CREDIT
import com.hc.data.KYC_CERTIFY_FINISH
import com.hc.data.NavContents
import com.hc.data.OrderStateEnum
import com.hc.data.common.CommonDataModel
import com.hc.data.mall.*
import com.hc.data.user.AuthInfo
import com.hc.load.R
import com.hc.load.net.LoanInfoService
import com.hc.uicomponent.base.BaseViewModel
import com.hc.uicomponent.base.jumpDeepLikPage
import com.hc.uicomponent.call.reqApi
import com.hc.uicomponent.config.Constants
import com.hc.uicomponent.provider.ContextProvider
import com.hc.uicomponent.provider.navigationStackPrintln
import com.hc.uicomponent.utils.*
import com.tools.network.entity.HttpResult
import frame.utils.DateUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.*


class LogicData {

    companion object {
        var TEST = true
        var TEST_INVALID: Boolean = true
        var TEST_SUBMIT_NO_COMMIT_ORDER: Boolean = false
        var TEST_CLOSE = false
        var TEST_FIRST_REVIEW_ING = false
        var TEST_AUTO_REVIEW_ING = false
        var TEST_AUTO_REFUSED = false
        var TEST_AUTO_REVIEW_PASS = false
        var TEST_CASH_FAIL = false
        var TEST_OVERDUE = false
        var TEST_RENEWAL = false
        var TEST_REPAY = false
    }

    //当前下单的数据。
    var currentOrderGoodsSx: GoodsSx? = null

    //数据。
    var mainDataRec: MainDataRec? = null
    var isKycCertifyFinish = false
    var isLastOrderIsEnd: Boolean = false
    var orderStatus: String? = null
    var isBindBankFlag: Boolean? = null
    var visibleHistoryOrder = ObservableInt()
    var visibleFlowMap = ObservableInt()
    var visibleMainMenuOrderTable = ObservableInt()

    var moreData = MutableLiveData<MutableList<IListData>>()

    //状态控制View.
    var mOrderFlowMapStateData = MutableLiveData<String>()
    var mOrderViewControl = MutableLiveData<String>()

    var mProductList = MutableLiveData<MutableList<IListData>>()
    var mControlMoreBtnVisibleOrGone = MutableLiveData<Int>(View.GONE)
    var mControlOrderVisibleOrGone = MutableLiveData<Int>(View.GONE)
    var mControlProductVisibleOrGone = MutableLiveData<Int>(View.GONE)


    fun initData(certifyStateRes: HttpResult<AuthInfo>, homeDataHttpRes: HttpResult<MainDataRec>) {
        certifyStateRes.data?.run {
            isKycCertifyFinish = (this.kycState == Constants.STATUS_30)
        }
        homeDataHttpRes.data?.run {
            mainDataRec = this
            isLastOrderIsEnd = (Constants.NUMBER_1 == this.lastOrderIsEnd)
        }
    }

    fun dataProcess(view: View) {
        if (isJumpToNotLoginPage()) {
            toLoginPage(view)
        } else {
            //订单

            var orderInfo = this.mainDataRec?.orderInfo
            if (TEST) {
                orderInfo = createTestData(orderInfo)
            }

            //判断订单是否存在。
            val isExistOrder = orderInfo != null

            //设置所有状态
            setOrderStateLogic(isExistOrder)

            //数据显示,商品信息&订单信息显示。
            setDataList(orderInfo, mainDataRec?.goodsSxs, mainDataRec?.diversionList)

            //更多按钮显示。
            setMoreProductViewData(mainDataRec?.diversionList)

            //是否显示订单流程图。
            val visibleMap = isHideLoanFlowMap()
            visibleFlowMap.set(if (visibleMap) View.GONE else View.VISIBLE)

            //是否在首页显示订单table
            visibleMainMenuOrderTable.set(showMainOrderMenu())

            //显示历史订单。
            val orderHistory = mainDataRec?.historOrder == Constants.NUMBER_1
            visibleHistoryOrder.set(if (orderHistory) View.VISIBLE else View.INVISIBLE)
        }
    }

    private fun setOrderStateLogic(isExistOrder: Boolean) {
        if (!isExistOrder) {
            this.mainDataRec?.let {
                when {
                    //1-正常下单状态
                    Constants.NUMBER_1 == it.orderType -> {
                        orderStatus  =  OrderStateEnum.SUBMIT_NO_COMMIT_ORDER.state
                        mOrderFlowMapStateData.value = OrderStateEnum.SUBMIT_NO_COMMIT_ORDER.state
                    }
                    //2-失效订单状态
                    Constants.NUMBER_2 == it.orderType -> {
                        orderStatus  =  OrderStateEnum.INVALID.state
                        mOrderFlowMapStateData.value = OrderStateEnum.INVALID.state
                    }
                    //3-关闭订单状态
                    Constants.NUMBER_3 == it.orderType -> {
                        orderStatus  =  OrderStateEnum.CLOSE.state
                        mOrderFlowMapStateData.value = OrderStateEnum.CLOSE.state
                    }
                }
            }
        } else {
            orderStatus = mainDataRec?.orderInfo?.state
            mOrderFlowMapStateData.value = orderStatus
        }
    }

    private fun setMoreProductViewData(diversionList: List<DiversionProduct>?) {
        diversionList?.let {
            val more = mutableListOf<IListData>()
            it.forEach { data ->
                if (data.isTop != 1) {
                    more.add(data)
                }
            }
            moreData.value = more
        }
        if (diversionList.isNullOrEmpty()) {
            mControlMoreBtnVisibleOrGone.value = View.GONE
        } else {
            mControlMoreBtnVisibleOrGone.value = View.VISIBLE
        }
    }

    private fun setDataList(orderInfo: OrderInfo?, goodsSxs: MutableList<GoodsSx>?, diversionList: List<DiversionProduct>?) {
        val refused = (OrderStateEnum.AUTO_REFUSED.state == this.orderStatus || OrderStateEnum.MANUAL_REFUSED.state == this.orderStatus)
        if (orderInfo != null && !refused) {
            mControlOrderVisibleOrGone.value = View.VISIBLE
            mControlProductVisibleOrGone.value = View.GONE
            mOrderViewControl.value = orderStatus
        } else {
            mControlProductVisibleOrGone.value = View.VISIBLE
            mControlOrderVisibleOrGone.value = View.GONE
            val data = mutableListOf<IListData>()
            goodsSxs?.let {
                data.addAll(it);
            }
            diversionList?.forEach { it ->
                if (it.isTop == 1) {
                    data.add(it)
                }
            }
            mProductList.value = data
        }
    }

    //需要添加参数。
    private fun toLoginPage(view: View) {
        mainDataRec?.let {
            val data = bundleOf(Pair(CREDIT, it.userCredit), Pair(KYC_CERTIFY_FINISH, isKycCertifyFinish))
            Navigation.findNavController(view).navigate(ContextProvider.mNavIdProvider?.getToLogInActionId() ?: 0, data)
        }
    }

    private fun showMainOrderMenu(): Int {
        return if (this.mainDataRec?.otherOrderDisPlay == Constants.NUMBER_1) View.VISIBLE else View.GONE
    }

    //是否显示流程控制图。
    private fun isHideLoanFlowMap(): Boolean {
        mainDataRec?.let {
            val orderStatus = this.orderStatus
            isBindBankFlag = it.userAuth?.bankCardState == Constants.STATUS_30
            return OrderStateEnum.CREDIT_VERIFIY_LOADING.state == orderStatus
                    || OrderStateEnum.FIRST_REVIEW_ING.state == orderStatus
                    || OrderStateEnum.RENEWAL_ING.state == orderStatus
                    || OrderStateEnum.INVALID.state == orderStatus
                    || OrderStateEnum.OVERDUE.state == orderStatus
                    || OrderStateEnum.CLOSE.state == orderStatus
                    || OrderStateEnum.AUTO_REFUSED.state == orderStatus
                    || OrderStateEnum.MANUAL_REFUSED.state == orderStatus
                    || OrderStateEnum.RENEWAL.state == orderStatus
                    || OrderStateEnum.REPAY_ING.state == orderStatus
                    || (OrderStateEnum.CASH_FAIL.state == orderStatus)
                    && (isBindBankFlag == false)
        }
        return true
    }

    private fun isJumpToNotLoginPage(): Boolean {
        mainDataRec?.let {
            val idCreditIsOK = (it.userAuth?.id == Constants.CREDIT_OK)
            val hasOrderInfo = (it.orderInfo != null)
            if (idCreditIsOK) {
                return !hasOrderInfo
            }
        }
        return false
    }
}


class LoanViewModel() : BaseViewModel() {
    var isCommitOrder = false
    var logicData = LogicData()

    fun reqHomeData(view: View, isRefresh: Boolean = false) {
        val taskJob = viewModelScope.launch {
            if (CommonDataModel.mLoggedIn) {
                val certifyStateDeferred = async(Dispatchers.IO) {
                    reqApi(LoanInfoService::class.java, { queryCreditState() })
                }
                val homeDataDeferred = async(Dispatchers.IO) {
                    reqApi(LoanInfoService::class.java, { reqHeadPageData() })
                }

                try {
                    val certifyStateRes = certifyStateDeferred.await()
                    val homeDataHttpRes = homeDataDeferred.await()

                    logicData.initData(certifyStateRes, homeDataHttpRes)
                    logicData.dataProcess(view)

                } catch (e: Exception) {

                } finally {

                }
            }
        }
    }


    fun handleDiversionClick(item: DiversionProduct, sourceType: String, activity: Activity) {
        // 获取保存在SharedInfo中的点击统计数据
        val diverCountJson = mmkv().decodeString(Constants.DIVERSION_COUNT_LIST, "")
        val diversionCountList: MutableList<DiversionCount>? = if (diverCountJson.isEmpty()) {
            null
        } else {
            GsonUtils.parserJsonToArrayBeans(diverCountJson, DiversionCount::class.java)
        }
        val currentTime = DateUtil.getFormat2DateStr(Date(), DateUtil.Format.MINUTE)
        var uploadTime = ""
        if (diversionCountList != null && diversionCountList.size > 0) {
            diversionCountList.forEach {
                if (item.id == it.id) {     // 找到同一个ID的APP
                    uploadTime = it.uploadTime
                }
            }
        } else {
            postDiversionCount("${logicData.mainDataRec?.dpNodes}", item, currentTime, sourceType)
            activity.jumpBrowser(item.url)
            return
        }

        if (uploadTime.isEmpty()) {
            postDiversionCount("${logicData.mainDataRec?.dpNodes}", item, currentTime, sourceType)
            activity.jumpBrowser(item.url)
            return
        }

        // 如果时间超过了一个小时（60分钟）,进行统计否则只进行跳转
        val format2Date = DateUtil.getFormat2Date(currentTime, DateUtil.Format.MINUTE)
        val format2Date1 = DateUtil.getFormat2Date(uploadTime, DateUtil.Format.MINUTE)
        if (DateUtil.diffDateToMin(format2Date, format2Date1) > 60) {
            postDiversionCount("${logicData.mainDataRec?.dpNodes}", item, currentTime, sourceType)
            activity.jumpBrowser(item.url)
        } else {
            activity.jumpBrowser(item.url)
        }
        return
    }


    private fun postDiversionCount(dpNodes: String, diversionProduct: DiversionProduct, uploadTime: String, sourceType: String) {
        viewModelScope.launch {
            try {
                val reqResult = reqApi(LoanInfoService::class.java, { diversionCount(diversionProduct.id, dpNodes, sourceType) })
                reqResult.run {
                    if (code == 200) {
                        // 失败的话不应该记为请求过，异常也是请求没有成功，应该可以再请求接口，去除时间和ID限制
                        val diverCountJson = mmkv().decodeString(Constants.DIVERSION_COUNT_LIST, "")
                        var diversionCountList: MutableList<DiversionCount>? =
                            if (diverCountJson.isEmpty())
                                null
                            else
                                GsonUtils.parserJsonToArrayBeans(diverCountJson, DiversionCount::class.java)

                        val diversionCount = DiversionCount(diversionProduct.id, uploadTime)
                        if (diversionCountList != null) {
                            diversionCountList.forEach {
                                if (diversionProduct.id == it.id) {
                                    it.uploadTime = uploadTime
                                }
                            }
                            if (diversionCountList.contains(diversionCount)) {
                                mmkv().encode(Constants.DIVERSION_COUNT_LIST, GsonUtils.toJsonString(diversionCountList))
                            } else {
                                diversionCountList.add(diversionCount)
                                mmkv().encode(Constants.DIVERSION_COUNT_LIST, GsonUtils.toJsonString(diversionCountList))
                            }
                            return@launch
                        }
                        diversionCountList = mutableListOf<DiversionCount>()
                        diversionCountList.add(diversionCount)
                        mmkv().encode(Constants.DIVERSION_COUNT_LIST, GsonUtils.toJsonString(diversionCountList))
                        return@launch
                    }
                }
            } catch (e: Exception) {
            }
        }
    }

    fun saveGetNowMoney(price: GoodsSx) {
        logicData.currentOrderGoodsSx = price
    }

    fun commitOrder(view: View) {
        if (isCommitOrder) {
            return
        }
        isCommitOrder = true
        viewModelScope.launch end@{
            try {
                var reqResult = reqApi(LoanInfoService::class.java, block = { queryCreditState() }, isShowLoading = false)
                reqResult.data?.run {
                    if (!TextUtil.isExistEmpty(this.bankCardState, this.kycState, this.idState, this.userState, this.supplementState)) {
                        val isCreditSupplement = this.supplementState.toInt() != Constants.NUMBER_30
                        val isCreditBank = this.bankCardState.toInt() != Constants.NUMBER_30
                        CommonDataModel.RUNTIME_USER_SUPPLEMENT_INFO_STATE = isCreditSupplement
                        CommonDataModel.RUNTIME_USER_BANK_INFO_STATE = isCreditBank

                        var mobileH5State: Int = if (TextUtil.isEmpty(this.operatorState)) {
                            10
                        } else {
                            this.operatorState.toInt()
                        }

                        val isCanBorrow = userState.toInt() == Constants.NUMBER_20
                        val isFinishKyc = kycState.toInt() == Constants.NUMBER_30
                        val isFinishPersonInfo = idState.toInt() == Constants.NUMBER_30
                        val isFinishSupplement = supplementState.toInt() == Constants.NUMBER_30
                        val isFinishBank = bankCardState.toInt() == Constants.NUMBER_30

                        //跳转到认证结果页面。
                        if (!isCanBorrow) {
                            val resultUrl = String.format(NavContents.loanResult, Constants.NUMBER_10)
                            jump(view, resultUrl)
                            return@end
                        }

                        //主页跳转到kyc
                        if (!isFinishKyc) {
                            if (kycState.toInt() == Constants.NUMBER_10 || kycState.toInt() == Constants.NUMBER_40) {//to write kyc
                                jump(view, NavContents.loanKyc)
                            } else if (kycState.toInt() == Constants.NUMBER_20) {                //tip
                                val viewBind = DialogUtils.createBaseDialogBind(view.context, ContextProvider.getString(R.string.mall_kyc_loading), false) {}
                                viewBind?.let { viewBind ->
                                    DialogUtils.showCenterTips(view.context, viewBind)
                                }
                            }
                            return@end
                        }

                        //用户信息认证
                        if (!isFinishPersonInfo) {
                            val suppleUrl = String.format(NavContents.loanUserInfo, false)
                            jump(view, suppleUrl)
                            return@end
                        }

                        //补全个人信息。
                        if (!isFinishSupplement) {
                            val suppleUrl = String.format(NavContents.loanSupplement, false)
                            jump(view, suppleUrl)
                            return@end
                        }

                        //绑定银行卡
                        if (!logicData.isLastOrderIsEnd && !isFinishBank) {
                            val data = String.format(NavContents.loanBank,false)
                            jump(view, data)
                            return@end
                        }

                        //提交订单
                        if (logicData.isLastOrderIsEnd || isFinishBank) {
                            logicData.currentOrderGoodsSx?.let {
                                checkOrder(view, it, logicData.isLastOrderIsEnd, isFinishBank, logicData.mainDataRec?.indexImgPath)
                            }
                        }
                    }
                }
            } catch (e: Exception) {

            } finally {
                isCommitOrder = false
            }
        }

    }

    //检验订单
    private fun checkOrder(view: View, goodsSx: GoodsSx, isLastOrderIsEnd: Boolean = false, isCertifyBank: Boolean = false, indexImgPath: String?) {
        viewModelScope.launch {
            val checkOrderResult = reqApi(LoanInfoService::class.java, block = { checkOrder(goodsSx.choseAmount, goodsSx.id) }, isShowLoading = true)
            checkOrderResult.data?.run {
                submitOrder(view, goodsSx, isLastOrderIsEnd, isCertifyBank, indexImgPath)
            }
        }
    }

    // 提交订单
    private fun submitOrder(view: View, goodsSx: GoodsSx, isLastOrderIsEnd: Boolean = false, isCertifyBank: Boolean = false, indexImgPath: String?) {
        FirseBaseEventUtils.trackEvent(StatEventTypeName.LOAN_ORDER_CONFIRMATION_PAGE)
        //调整到
        //跳转到提交订单页面
        val bundle = bundleOf(
            Pair(Constants.ORDER_COMMIT_GOODS_SX, goodsSx)
            , Pair(Constants.ORDER_COMMIT_AUTO_ENTRY_PAGE, isLastOrderIsEnd)
            , Pair(Constants.ORDER_NBFC_IMG_URL, indexImgPath)
        )
        if (isLastOrderIsEnd) {
            bundle.putBoolean(Constants.ORDER_COMMIT_GET_CERTIFY_BANK_STATE, isCertifyBank);
        }

        ContextProvider.mNavIdProvider?.let {
            val opt = NavOptions.Builder()
                .setEnterAnim(R.anim.anim_right_to_middle)
                .setLaunchSingleTop(true)
                .setPopExitAnim(R.anim.anim_middle_to_right).build()
            Navigation.findNavController(view).navigate(it.getCommitOrderNavId(), bundle, opt)
        }
    }

    private fun jump(nextBtn: View, url: String) {
        jumpDeepLikPage(nextBtn,ContextProvider.mNavIdProvider?.getInfoContainerId(),url)
    }
}


fun Activity.jumpBrowser(url: String) {
    ToastUtils.showShort("Going to the browser and downloading the app!")
    val intent = Intent(Intent.ACTION_VIEW)
    intent.addCategory(Intent.CATEGORY_BROWSABLE)
    intent.data = Uri.parse(url)
    this.startActivity(intent)
}





fun LogicData.createTestData(orderInfo: OrderInfo?): OrderInfo? {

    var orderInfo1 = orderInfo
    if (LogicData.TEST_SUBMIT_NO_COMMIT_ORDER) {
        mainDataRec?.orderType = 1
        this.orderStatus = OrderStateEnum.SUBMIT_NO_COMMIT_ORDER.state
       orderInfo1 =  null
    }

    if (LogicData.TEST_INVALID) {
        orderInfo1 =  null
        mainDataRec?.orderType = 2
        this.orderStatus = OrderStateEnum.INVALID.state
    }

    if (LogicData.TEST_CLOSE) {
        mainDataRec?.orderType = 3
        this.orderStatus = OrderStateEnum.CLOSE.state
    }

    if (LogicData.TEST_FIRST_REVIEW_ING) {
        orderStatus = OrderStateEnum.FIRST_REVIEW_ING.state
        orderInfo1 = OrderInfo(
            "", "", "",
            "", "", "", "",
            "", "", "", "", "",
            "", "", "", "",
            "", "", "", "",
            "", 1, OrderStateEnum.FIRST_REVIEW_ING.state, "",
            "", "", "", "", "", ""
        )

    }

    if (LogicData.TEST_AUTO_REVIEW_ING) {
        orderStatus = OrderStateEnum.AUTO_REVIEW_ING.state
        orderInfo1 = OrderInfo(
            "", "", "",
            "", "", "", "",
            "", "", "", "", "",
            "", "", "", "",
            "", "", "", "",
            "", 1, OrderStateEnum.AUTO_REVIEW_ING.state, "",
            "", "", "", "", "", ""
        )
    }

    if (LogicData.TEST_AUTO_REFUSED) {
        orderStatus = OrderStateEnum.AUTO_REFUSED.state
        orderInfo1 = OrderInfo(
            "", "", "",
            "", "", "", "",
            "", "", "", "", "",
            "", "", "", "",
            "", "", "", "",
            "", 1, OrderStateEnum.AUTO_REFUSED.state, "",
            "", "", "", "", "", ""
        )
    }

    if (LogicData.TEST_AUTO_REVIEW_PASS) {
        orderStatus = OrderStateEnum.AUTO_REVIEW_PASS.state
        orderInfo1 = OrderInfo(
            "", "", "",
            "", "", "", "",
            "", "", "", "", "",
            "", "", "", "",
            "", "", "", "",
            "", 1, OrderStateEnum.AUTO_REVIEW_PASS.state, "",
            "", "", "", "", "", ""
        )
    }

    if (LogicData.TEST_CASH_FAIL) {
        orderStatus = OrderStateEnum.CASH_FAIL.state
        orderInfo1 = OrderInfo(
            "", "", "",
            "", "", "", "",
            "", "", "", "", "",
            "", "", "", "",
            "", "", "", "",
            "", 1, OrderStateEnum.CASH_FAIL.state, "",
            "", "", "", "", "", ""
        )
    }

    if (LogicData.TEST_OVERDUE) {
        orderStatus = OrderStateEnum.OVERDUE.state
        orderInfo1 = OrderInfo(
            "", "", "",
            "", "", "", "",
            "", "", "", "", "",
            "", "", "", "",
            "", "", "", "",
            "", 1, OrderStateEnum.OVERDUE.state, "",
            "", "", "", "", "", ""
        )
    }

    if (LogicData.TEST_RENEWAL) {
        orderStatus = OrderStateEnum.RENEWAL.state
        orderInfo1 = OrderInfo(
            "", "", "",
            "", "", "", "",
            "", "", "", "", "",
            "", "", "", "",
            "", "", "", "",
            "", 1, OrderStateEnum.RENEWAL.state, "",
            "", "", "", "", "", ""
        )
    }

    if (LogicData.TEST_REPAY) {
        orderStatus = OrderStateEnum.REPAY.state
        orderInfo1 = OrderInfo(
            "", "", "",
            "", "", "", "",
            "", "", "", "", "",
            "", "", "", "",
            "", "", "", "",
            "", 1, OrderStateEnum.REPAY.state, "",
            "", "", "", "", "", ""
        )
    }
    return orderInfo1
}
