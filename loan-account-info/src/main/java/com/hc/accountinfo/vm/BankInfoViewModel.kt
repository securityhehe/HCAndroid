package com.hc.accountinfo.vm

import android.content.Context
import android.view.View
import androidx.databinding.ObservableField
import androidx.fragment.app.Fragment
import androidx.lifecycle.viewModelScope
import com.blankj.utilcode.util.KeyboardUtils
import com.blankj.utilcode.util.ToastUtils
import com.hc.accountinfo.R
import com.hc.accountinfo.api.UserInfoService
import com.hc.data.MenuData
import com.hc.data.user.BankDictList
import com.hc.data.user.BankInfo
import com.hc.data.user.UserBankRec
import com.hc.data.user.UserType
import com.hc.uicomponent.LoanObservableField
import com.hc.uicomponent.base.BaseViewModel
import com.hc.uicomponent.call.reqApi
import com.hc.uicomponent.config.Constants
import com.hc.uicomponent.menu.BaseMenuViewModel
import com.hc.uicomponent.menu.BasePopupWindow
import com.hc.uicomponent.provider.ContextProvider
import com.hc.uicomponent.stack.ActivityStack
import com.hc.uicomponent.utils.ScreenAdapterUtils
import com.hc.uicomponent.utils.TextUtil
import frame.utils.DeviceUtil
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


data class BankMenuData(var title: Int, var data: List<BankInfo>)

class BankInfoViewModel : BaseViewModel() {
    private val x217 = ScreenAdapterUtils.dp2px(ContextProvider.app, 217)
    private val x23 = ScreenAdapterUtils.dp2px(ContextProvider.app, 23)
    private val x187 = ScreenAdapterUtils.dp2px(ContextProvider.app, -70)
    private lateinit var mMenuEntryList: ArrayList<BankMenuData>


    var mCurrentIndex = 0
    var isEnable = LoanObservableField<Boolean>()

   var mSelectCardData = LoanObservableField<UserType>().setCallT {
        mCardName.set(it.info)
        inputCheck()
    }

    var mCardName = ObservableField<String>()

    var cardNum = LoanObservableField<String>().setCallT {
        inputCheck()
    }

    var cardNumConfirm = LoanObservableField<String>().setCallT {
        inputCheck()
    }

    var code = LoanObservableField<String>().setCallT {
        inputCheck()
    }

    var viewData = mutableListOf(cardNum,cardNumConfirm,code);

    private suspend fun checkMenuData() {
        return withContext(viewModelScope.coroutineContext) {
            if (!this@BankInfoViewModel::mMenuEntryList.isInitialized) {
                val result = reqApi(UserInfoService::class.java, { getBankListDictionary(Constants.BANKTYPE) }, isShowLoading = true)
                result.data?.run {
                    this.bankTypeList?.run {
                        mMenuEntryList.clear()
                        mMenuEntryList.add(BankMenuData(R.string.bank_info, this))
                    }
                }
            }
        }
    }

    fun showMenu(index: Int, fragment: Fragment, menuVm: BaseMenuViewModel?, view: View) {
        viewModelScope.launch {
            try {
                mCurrentIndex = index
                checkMenuData()
                if (index in 0 until mMenuEntryList.size) {
                    val menuEntry = mMenuEntryList[index]
                    showMenu(fragment, menuVm, menuEntry.data, menuEntry.title, view)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun showMenu(fragment: Fragment, menuVm: BaseMenuViewModel?, data: List<BankInfo>?, titleRes: Int, view: View) {
        viewModelScope.launch {
            val act = ActivityStack.currentActivity()
            if (data != null && act != null && !act.isFinishing && !act.isDestroyed) {
                var popupWindow: BasePopupWindow?
                menuVm?.apply {
                    val menuData = data.mapIndexed { i, d ->
                        val a = UserType(null, d.code ?: "", d.value ?: "", null, "", "")
                        MenuData(a, i, false)
                    }
                    title = view.context.getString(titleRes)
                    listData.clear()
                    listData.addAll(menuData)
                    popupWindow = BasePopupWindow(act, menuVm, view, x217)
                    popupWindow?.show(x187, x23)
                    callbackData = { it ->
                        popupWindow?.dismiss()
                        mSelectCardData.set(it.menuInfo)
                        Unit
                    }
                }
            }
        }
    }

    private fun inputCheck() {
        isEnable.set(!TextUtil.isExistEmpty(mSelectCardData.get()?.info, cardNum.get(), code.get()))
    }

    fun showBankDetailInfo() {
        viewModelScope.launch {
            var bankRes = reqApi(UserInfoService::class.java, { queryUserBankInfo() }, isCancelDialog = false)
            bankRes.data?.run {
                this.bankCard?.run {
                    mCardName.set(this.bankName)
                    cardNum.set(this.bankNo)
                    cardNumConfirm.set(this.bankNo)
                    code.set(ifscCode)
                }
            }
        }
    }

    fun submitBankInfo(isConfirmIfsc: Boolean) {
        if (cardNum.get() != cardNumConfirm.get()) {
            ToastUtils.showShort(R.string.write_bank_inconsistent_tip)
            return
        }

        val codeText = code.get()
        if (!TextUtil.isEmpty(codeText)) {
            if (codeText?.length != 11 || codeText[4] != '0') {
                ToastUtils.showShort(R.string.write_bank_ifsc_tip)
                return
            }
        }

        viewModelScope.launch {
            var commitRes = reqApi(UserInfoService::class.java,
                block = {
                    saveBankInfo(
                        mCardName.get()
                        , cardNum.get()
                        , codeText
                        , Constants.NUMBER_1
                        , DeviceUtil.getDeviceId(ContextProvider.app),
                        isConfirmIfsc
                    )
                }
                ,isShowLoading = true
            )

        }
    }


}