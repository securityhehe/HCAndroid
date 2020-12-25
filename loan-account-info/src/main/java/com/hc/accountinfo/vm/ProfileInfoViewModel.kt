package com.hc.accountinfo.vm


import android.app.Activity
import android.view.View
import android.widget.TextView
import androidx.databinding.Bindable
import androidx.databinding.ObservableBoolean
import androidx.fragment.app.Fragment
import androidx.lifecycle.viewModelScope
import com.blankj.utilcode.util.ToastUtils
import com.hc.accountinfo.R
import com.hc.accountinfo.api.UserInfoService
import com.hc.data.MenuData
import com.hc.data.user.UserInfoExt
import com.hc.data.user.UserInfoRange
import com.hc.data.user.UserInfoSub
import com.hc.data.user.UserType
import com.hc.uicomponent.LoanObservableField
import com.hc.uicomponent.base.BaseViewModel
import com.hc.uicomponent.call.reqApi
import com.hc.uicomponent.menu.BaseMenuViewModel
import com.hc.uicomponent.menu.BasePopupWindow
import com.hc.uicomponent.provider.ContextProvider
import com.hc.uicomponent.stack.ActivityStack
import com.hc.uicomponent.utils.LocationUtils
import com.hc.uicomponent.utils.ScreenAdapterUtils
import com.hc.uicomponent.utils.TextUtil
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


class ProfileInfoViewModel : BaseViewModel() {

    private var mCurrentIndex = -1

    //viewData.
    inner class ViewUserInfoData {

        //用户信息中的5个选择数据。
        var gender = LoanObservableField<Int>().setCallT {
            mUserInfo.sex = it
            inputCheck()
        }
        var language = LoanObservableField<Int>().setCallT {
            mUserInfo.language = it
            inputCheck()
        }
        var eduQualifier = LoanObservableField<Int>().setCallT {
            mUserInfo.education = it
            inputCheck()
        }

        var maritalStatus = LoanObservableField<Int>().setCallT {
            mUserInfo.marryState = it
            inputCheck()
        }

        var purpose = LoanObservableField<Int>().setCallT {
            mUserInfo.purpose = it
            inputCheck()
        }


        var firstNameA = LoanObservableField<String?>().setCallT {
            mUserInfo.firstNamePan = it
            inputCheck()
        }

        var middleName = LoanObservableField<String?>().setCallT {
            mUserInfo.middleNamePan = it
            inputCheck()
        }

        var lastName = LoanObservableField<String?>().setCallT {
            mUserInfo.lastNamePan = it
            inputCheck()
        }

        //6个view相关数据。
        var genderText =  LoanObservableField<String>().setCallT {
            inputCheck()
        }

        var languageText = LoanObservableField<String>().setCallT {
            inputCheck()
        }

        var eduQualifierText = LoanObservableField<String>().setCallT {
            inputCheck()
        }


        var maritalStatusText = LoanObservableField<String>().setCallT {
            inputCheck()
        }

        var purposeText = LoanObservableField<String>().setCallT {
            inputCheck()
        }

        //号码数据
        var anotherPhone = LoanObservableField<String>().setCallT {
            mUserInfo.anotherPhone = it
            inputCheck()
        }

        var facebook = LoanObservableField<String>().setCallT {
            mUserInfo.facebook = it
            inputCheck()
        }


        var whatsapp = LoanObservableField<String>().setCallT {
            mUserInfo.whatsapp = it
            inputCheck()
        }

        //工作数据。
        var occText = LoanObservableField<String>().setCallT {
            inputCheck()
        }


        fun inputCheck() {
            btnIsEnable.set(
                !(gender.get() == null
                        || language.get() == null
                        || maritalStatus.get() == null
                        || purpose.get() == null
                        || eduQualifier.get() == null
                        || TextUtil.isAllEmpty(firstNameA.get(), lastName.get()))
            )
        }

        var btnIsEnable = ObservableBoolean(false)
        var isEditable = ObservableBoolean(false)
        var isNoWork = ObservableBoolean(false)
        var isShowJobAndLevelOfPosition = ObservableBoolean(false)
        var isShowSmsCodeInput = ObservableBoolean(false)
        var isCheckEmail = ObservableBoolean(false)
        var isCreditFinish = ObservableBoolean(false)

        val viewDataList = arrayListOf(genderText, languageText, eduQualifierText, maritalStatusText, purposeText, occText)

        val dataTextList = arrayListOf(firstNameA,middleName,lastName,anotherPhone,facebook,whatsapp,occText)
    }

    private var mJobNature: List<UserType> = mutableListOf()

    private var selectSalaryRangeClick: (Fragment, BaseMenuViewModel?, TextView) -> Unit =
        { _, menuVm, view ->
            val desc = R.string.loan_info_select_salary_range_title
            val data = mUserMenuList.salaryRange
            showOccMenu(menuVm, data, view, desc)
        }

    private var selectCompanyIndustryClick: (Fragment, BaseMenuViewModel?, TextView) -> Unit =
        { _, menuVm, view ->
            val desc = R.string.loan_info_select_company_industry_title
            val data = mUserMenuList.companyIndustry
            showOccMenu(menuVm, data, view, desc)
        }

    private var selectStaffSizeClick: (Fragment, BaseMenuViewModel?, TextView) -> Unit =
        { _, menuVm, view ->
            val desc = R.string.loan_info_select_staff_size_title
            val data = mUserMenuList.staffSize
            showOccMenu(menuVm, data, view, desc)

        }
    private var selectJobNatureClick: (Fragment, BaseMenuViewModel?, TextView) -> Unit =
        { _, menuVm, view ->
            val tip = view.tag
            if (tip == null && mJobNature.isNullOrEmpty()) {
                ToastUtils.showShort(R.string.loan_info_hint_industry_toast)
            } else {
                val desc = R.string.loan_info_select_job_nature_title
                val data = mJobNature
                showOccMenu(menuVm, data, view, desc)
            }
        }

    private var selectLevelOfPositionClick: (Fragment, BaseMenuViewModel?, TextView) -> Unit =
        { _, menuVm, view ->
            val desc = R.string.loan_info_select_level_of_position_title
            val data = mUserMenuList.levelOfPosition
            showOccMenu(menuVm, data, view, desc)
        }


    private var gender: (Fragment, BaseMenuViewModel?, TextView) -> Unit =
        { fragment, menuVm, view ->
            showUserInfoMenu(
                fragment,
                menuVm,
                mUserMenuList.gender,
                view,
                R.string.loan_info_select_gender_title
            )
        }

    private var language: (Fragment, BaseMenuViewModel?, TextView) -> Unit =
        { fragment, menuVm, view ->
            val data = mUserMenuList.language
            val desc = R.string.loan_info_select_language_title
            showUserInfoMenu(fragment, menuVm, data, view, desc)
        }


    private var edu: (Fragment, BaseMenuViewModel?, TextView) -> Unit = { fragment, menuVm, view ->
        val desc = R.string.loan_info_select_education_title
        val data = mUserMenuList.maritalStatus
        showUserInfoMenu(fragment, menuVm, data, view, desc)
    }

    private var ms: (Fragment, BaseMenuViewModel?, TextView) -> Unit = { fragment, menuVm, view ->
        val data = mUserMenuList.maritalStatus
        val desc = R.string.loan_info_select_marital_status_title
        showUserInfoMenu(fragment, menuVm, data, view, desc)
    }

    private var purpose: (Fragment, BaseMenuViewModel?, TextView) -> Unit =
        { fragment, menuVm, view ->
            val data = mUserMenuList.purpose
            val desc = R.string.loan_info_select_purpose_title
            showUserInfoMenu(fragment, menuVm, data, view, desc)
        }

    private var occupation: (Fragment, BaseMenuViewModel?, TextView) -> Unit = { _, menuVm, view ->
        val desc = R.string.loan_info_select_occupation_title
        val data = mUserMenuList.occupation
        showOccMenu(menuVm, data, view, desc)
    }

    private val functionList = arrayListOf(gender, language, edu, ms, purpose, occupation)

    fun showMenu(index: Int, fragment: Fragment, menuVm: BaseMenuViewModel?, view: TextView) {
        viewModelScope.launch {
            try {
                checkMenuData().await()
                mCurrentIndex = index
                if (index >= 0 && index < functionList.size) {
                    functionList[index].invoke(fragment, menuVm, view)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * 下面是-->数据和方法区域。
     */
    private val mUserInfo: UserInfoSub by lazy {
        UserInfoSub()
    }
    private lateinit var mUserMenuList: UserInfoRange
    private var mPermissionModel: PermissionCheckModel = PermissionCheckModel()
    private val x217 = ScreenAdapterUtils.dp2px(ContextProvider.app, 217)
    private val x23 = ScreenAdapterUtils.dp2px(ContextProvider.app, 23)
    private val x_187 = ScreenAdapterUtils.dp2px(ContextProvider.app,-70 )

    var blackBox: String? = null

    @get:Bindable
    var mViewData = ViewUserInfoData()

    private fun checkMustPermission(fragment: Fragment): Deferred<Boolean> {
        return viewModelScope.async {
            var isPermissionOk = false
            if (mPermissionModel.checkMustPermissions(fragment)) {
                if (mPermissionModel.isGoogleServiceAvail) {
                    LocationUtils.getInstance().resetLocationUpdates(ContextProvider.app) { isOk ->
                        if (isOk) {
                            isPermissionOk = true
                        }
                    }
                } else {
                    isPermissionOk = true
                }
            }
            isPermissionOk
        }
    }


    private fun checkMenuData(): Deferred<Unit> {
        return viewModelScope?.async {
            var activity: Activity? = null
            if (!this@ProfileInfoViewModel::mUserMenuList.isInitialized) {
                val result =
                    reqApi(UserInfoService::class.java, { queryListInfo() }, isShowLoading = true)
                result.data?.let {
                    mUserMenuList = it
                }
            }
        }
    }

    private fun showUserInfoMenu(
        fragment: Fragment,
        menuVm: BaseMenuViewModel?,
        data: List<UserType>?,
        view: TextView,
        loanInfoSelectEducationTitle: Int
    ) {
        viewModelScope.launch {
            val act = ActivityStack.currentActivity()
            if (data!=null&& act != null && !act.isFinishing && !act.isDestroyed) {
                val isOkResult = checkMustPermission(fragment)
                val isOk = isOkResult.await()
                menuVm?.callbackData = { it->
                    view.text = data[it].info

                    Unit
                }
                if (isOk && menuVm != null) {
                    data?.let { data ->
                        val a = data.mapIndexed { i, d -> MenuData(d.info, i, false) }
                        menuVm.title = view.context.getString(loanInfoSelectEducationTitle)
                        menuVm.listData.clear()
                        menuVm.listData.addAll(a)
                        BasePopupWindow(act, menuVm, view, x217).show(x_187, x23)
                    }
                }
            }
        }
    }

    private fun showOccMenu(
        menuVm: BaseMenuViewModel?,
        maritalStatus: List<UserType>?,
        view: View,
        loanInfoSelectEducationTitle: Int
    ) {
        viewModelScope.launch {
            val act = ActivityStack.currentActivity()
            if ((menuVm != null) && (act != null && !act.isFinishing && !act.isDestroyed)) {
                maritalStatus?.let { data ->
                    val a = data.mapIndexed { i, d -> MenuData(d.info, i, false) }
                    menuVm.title = view.context.getString(loanInfoSelectEducationTitle)
                    menuVm.listData.clear()
                    menuVm.listData.addAll(a)
                    BasePopupWindow(act, menuVm, view, x217).show(x_187, x23)
                }
            }
        }
    }

    fun reqUserInfo(isCertifyFinish: Boolean) {
        viewModelScope.launch {
            var userInfo = reqApi(
                UserInfoService::class.java,
                { queryUserExtraInfo() },
                isCancelDialog = false,
                isShowLoading = true
            )
            userInfo.data?.let {
                userInfo.data!!.run {
                    var isCanEdit = false //默认包含30状态的情况
                    if (!isCertifyFinish) {//handler editable
                        isCanEdit = TextUtil.isExistEmpty(purposeStr)
                        //isExistUserInfo = !TextUtil.isEmpty(purposeStr)
                    }
                    //existData(this, isCanEdit)
                }
            }
        }
    }


    private fun showUserInfoAndWorkInfo(info: UserInfoExt?) {
        //回显个人信息
        info?.run {
            mViewData.firstNameA.set(firstNamePan)
            mViewData.middleName.set(middleNamePan)
            mViewData.lastName.set(lastNamePan)
            //
            //mBinding.writeUserInfoGender.text = sexStr
            mViewData.gender.set(sex)
            //
            //mBinding.writeUserInfoLanguage.text = languageStr
            //mViewData.language.set(language)
            //
            //mBinding.writeUserInfoMarital.text = marryStateStr
            mViewData.maritalStatus.set(marryState)
            //
            //mBinding.writeUserInfoPurpose.text = purposeStr
            mViewData.purpose.set(purpose)
            mViewData.facebook.set(facebook)
            mViewData.whatsapp.set(whatsapp)
            mViewData.anotherPhone.set(anotherPhone)
            if (!TextUtil.isEmpty(educationStr)) {
                mViewData.eduQualifier.set(education)
            }
            //mBinding.writeUserInfoEdu.text = educationStr
        }
    }

}



