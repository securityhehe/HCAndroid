package com.hc.accountinfo.vm

import android.Manifest
import android.content.Intent
import android.provider.ContactsContract
import android.view.View
import androidx.databinding.ObservableField
import androidx.fragment.app.Fragment
import androidx.lifecycle.viewModelScope
import androidx.navigation.Navigation
import com.blankj.utilcode.util.ToastUtils
import com.hc.accountinfo.R
import com.hc.accountinfo.api.UserInfoService
import com.hc.data.MenuData
import com.hc.data.common.CommonDataModel
import com.hc.data.user.UserInfoRange
import com.hc.data.user.UserType
import com.hc.permission.AndroidPermissions
import com.hc.uicomponent.LoanObservableField
import com.hc.uicomponent.base.BaseViewModel
import com.hc.uicomponent.call.reqApi
import com.hc.uicomponent.menu.BaseMenuViewModel
import com.hc.uicomponent.menu.BasePopupWindow
import com.hc.uicomponent.provider.ContextProvider
import com.hc.uicomponent.stack.ActivityStack
import com.hc.uicomponent.utils.ScreenAdapterUtils
import com.hc.uicomponent.utils.TextUtil
import com.tools.network.entity.HttpResult
import frame.utils.AndroidUtil
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*


class SupplyInfoViewModel : BaseViewModel() {
    companion object {
        const val PERMISSION_REQ_OTHER_CONTACT_CODE = 1
        const val PERMISSION_REQ_CONTACT_CODE = 2
        const val CONTACT_REQ_OTHER_CONTACT_CODE = 4
        const val CONTACT_REQ_CONTACT_CODE = 5

    }

    private val x217 = ScreenAdapterUtils.dp2px(ContextProvider.app, 217)
    private val x23 = ScreenAdapterUtils.dp2px(ContextProvider.app, 23)
    private val x187 = ScreenAdapterUtils.dp2px(ContextProvider.app, -70)

    private var mMapUserTypeData = mutableMapOf<String, UserType>()
    var mCurrentIndex: Int = 0
    private lateinit var mMenuEntryList: ArrayList<MenuEntry>

    var relationName = ObservableField<String?>()
    var relationId = ObservableField<Int>()
    var otherRelationName = ObservableField<String?>()
    var otherRelationId = ObservableField<Int>()
    var isEnable = LoanObservableField<Boolean?>()

    private var name = LoanObservableField<String?>().setCallT {
        inputCheck()
    }
    private var phone = LoanObservableField<String?>().setCallT {
        inputCheck()
    }
    private var otherName = LoanObservableField<String?>().setCallT {
        inputCheck()
    }
    private var otherPhone = LoanObservableField<String?>().setCallT {
        inputCheck()
    }

    var mViewData = arrayListOf(relationName, otherRelationName, name, phone, otherName, otherPhone)

    var relation = LoanObservableField<UserType>().setCallT {
        relationId.set(it.state.toInt())
        relationName.set(it.info)
        inputCheck()
    }

    var otherRelation = LoanObservableField<UserType>().setCallT {
        otherRelationId.set(it.state.toInt())
        otherRelationName.set(it.info)
        inputCheck()
    }

    val selectNetData = arrayListOf(relation, otherRelation)

    private suspend fun checkMenuData(): Unit {
        return withContext(viewModelScope.coroutineContext) {
            if (!this@SupplyInfoViewModel::mMenuEntryList.isInitialized) {
                val result = reqApi(UserInfoService::class.java, { queryListInfo() }, isShowLoading = true)
                result.data?.let {
                    initData(it)
                }
            }
        }
    }

    private fun initData(menu: UserInfoRange) {
        val relatives = MenuEntry(R.string.relatives_title, menu.relatives)
        val otherRelatives = MenuEntry(R.string.other_title, menu.otherRelatives)
        mMenuEntryList = arrayListOf(
            relatives,
            otherRelatives
        )

        mMenuEntryList.forEach { key ->
            val map = key.data?.associateBy({ "${it.state}_${it.info}" }, { it })
            map?.let {
                mMapUserTypeData.putAll(it)
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

    private fun showMenu(fragment: Fragment, menuVm: BaseMenuViewModel?, data: List<UserType>?, titleRes: Int, view: View) {
        viewModelScope.launch {
            val act = ActivityStack.currentActivity()
            if (data != null && act != null && !act.isFinishing && !act.isDestroyed) {
                var popupWindow: BasePopupWindow?
                menuVm?.apply {
                    val menuData = data.mapIndexed { i, d -> MenuData(d, i, false) }
                    title = view.context.getString(titleRes)
                    listData.clear()
                    listData.addAll(menuData)
                    popupWindow = BasePopupWindow(act, menuVm, view, x217)
                    popupWindow?.show(x187, x23)
                    callbackData = { it ->
                        popupWindow?.dismiss()
                        if (mCurrentIndex in 0 until selectNetData.size) {
                            val loanObservableField = selectNetData[mCurrentIndex]
                            loanObservableField.set(it.menuInfo)
                        }
                        Unit
                    }
                }
            }
        }
    }

    //点击，选择联系人。
    fun clickContact(reqCodeType: Int, fm: Fragment) {
        AndroidPermissions.requestPermissions(fm, "", false, reqCodeType, *arrayOf(Manifest.permission.READ_CONTACTS))
    }

    fun onAllPermissionGranted(fragment: Fragment, requestCode: Int) {
        val intent = Intent()
        var reqType = 0
        if (PERMISSION_REQ_OTHER_CONTACT_CODE == requestCode) {
            reqType = CONTACT_REQ_OTHER_CONTACT_CODE
        } else if (PERMISSION_REQ_CONTACT_CODE == requestCode) {
            reqType = CONTACT_REQ_CONTACT_CODE
        }
        intent.action = Intent.ACTION_PICK
        intent.data = ContactsContract.Contacts.CONTENT_URI
        fragment.startActivityForResult(intent, reqType)
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == CONTACT_REQ_OTHER_CONTACT_CODE || requestCode == CONTACT_REQ_CONTACT_CODE) {
            val uri = data?.data
            val contact: Array<String>? = AndroidUtil.getPhoneContacts(uri, ContextProvider.app)
            if (contact != null) {
                val nameOne = contact[0] //姓名
                val numberOne = contact[1] //手机号
                if (requestCode == CONTACT_REQ_OTHER_CONTACT_CODE) {
                    otherName.set(nameOne)
                    otherPhone.set(numberOne)
                }
                if (requestCode == CONTACT_REQ_CONTACT_CODE) {
                    name.set(nameOne)
                    phone.set(numberOne)
                }
            }
        }
    }

    fun commitSupplyInfo(view: View, isShowDialog: Boolean) {
        viewModelScope.launch {
            var submitResult = reqApi(
                UserInfoService::class.java, {
                    commitSupplementInfo(
                        relationId.get() ?: 0
                        , name.get()?.replace(" ", "")
                        , phone.get()?.replace(" ", "")
                        , otherRelationId.get() ?: 0
                        , otherName.get()?.replace(" ", "")
                        , otherPhone.get()?.replace(" ", "")
                    )
                }
                , isShowLoading = isShowDialog
                , apiFailure = { apiEx: HttpResult<*> ->
                    ToastUtils.showShort(apiEx.msg)
                    false
                }
            )
            if (CommonDataModel.RUNTIME_USER_BANK_INFO_STATE) {
                back(view)
            } else {
                ///跳转到bank认证。
                Navigation.findNavController(view).navigate(R.id.loan_info_sppply_to_bank)
            }
        }
    }


    fun reqReShowSupplementInfo(isShowDialog: Boolean): Unit {
        viewModelScope.launch {
            var userInfo = reqApi(UserInfoService::class.java, { queryUserExtraInfo() }, isShowLoading = isShowDialog)
            userInfo.data?.run {
                relation.set(mMapUserTypeData["${this.relatives}_+${this.relativesStr}"]);
                otherRelation.set(mMapUserTypeData["${this.otherRelatives}_+${this.otherRelativesStr}"]);
                name.set(this.relativesName)
                phone.set(this.colleagueName)
                otherName.set(this.colleagueMobile)
            }
        }
    }

    private fun inputCheck() {
        val nameIsEmpty = name.get()?.isEmpty() ?: false
        val phoneIsEmpty = phone.get()?.isEmpty() ?: false
        val otherNameIsEmpty = otherName.get()?.isEmpty() ?: false
        val otherPhoneIsEmpty = otherPhone.get()?.isEmpty() ?: false
        val otherId = otherRelationId.get() == null
        val relationId = relationId.get() == null
        isEnable.set(!nameIsEmpty && !phoneIsEmpty && !otherNameIsEmpty && !otherPhoneIsEmpty && !otherId && !relationId)
    }


}