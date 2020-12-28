package com.hc.accountinfo.vm


import android.Manifest
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.databinding.Bindable
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableInt
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
import com.hc.permission.AndroidPermissions
import com.hc.uicomponent.LoanObservableField
import com.hc.uicomponent.base.BaseViewModel
import com.hc.uicomponent.call.reqApi
import com.hc.uicomponent.menu.BaseMenuViewModel
import com.hc.uicomponent.menu.BasePopupWindow
import com.hc.uicomponent.provider.ContextProvider
import com.hc.uicomponent.stack.ActivityStack
import com.hc.uicomponent.utils.*
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog.newInstance
import frame.utils.Base64
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.*
import kotlin.concurrent.thread


class ProfileInfoViewModel : BaseViewModel() {

    private var mCurrentIndex = -1

    companion object {
        const val NO_WORK = 4

        //
        const val SELF_EMPLOYED = 3
        const val BUSINESS = 5

        const val SALARIED_FULL_TIME = 1
        const val SALARIED_PART_TIME = 2
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
    private val x_187 = ScreenAdapterUtils.dp2px(ContextProvider.app, -70)
    var blackBox: String? = null

    @get:Bindable
    var mViewData = ViewUserInfoData()



    //viewData.
    inner class ViewUserInfoData {
        //名字相关数据。
         var firstNameA = LoanObservableField<String?>().setCallT {
            mUserInfo.firstNamePan = it
            checkUserInfo()
        }

         var middleName = LoanObservableField<String?>().setCallT {
            mUserInfo.middleNamePan = it
            checkUserInfo()
        }

         var lastName = LoanObservableField<String?>().setCallT {
            mUserInfo.lastNamePan = it
            checkUserInfo()
        }

        //选择相关数据。
         var gender = LoanObservableField<Int>().setCallT {
            mUserInfo.sex = it
            checkUserInfo()
        }
         var language = LoanObservableField<Int>().setCallT {
            mUserInfo.language = it
            checkUserInfo()
        }
         var eduQualifier = LoanObservableField<Int>().setCallT {
            mUserInfo.education = it
            checkUserInfo()
        }
         var maritalStatus = LoanObservableField<Int>().setCallT {
            mUserInfo.marryState = it
            checkUserInfo()
        }
         var purpose = LoanObservableField<Int>().setCallT {
            mUserInfo.purpose = it
            checkUserInfo()
        }

        //号码数据
         var anotherPhone = LoanObservableField<String>().setCallT {
            mUserInfo.anotherPhone = it
            checkUserInfo()
        }

         var facebook = LoanObservableField<String>().setCallT {
            mUserInfo.facebook = it
            checkUserInfo()
        }

         var whatsApp = LoanObservableField<String>().setCallT {
            mUserInfo.whatsapp = it
            checkUserInfo()
        }

        private fun checkUserInfo() {
            btnIsEnable.set(
                !(gender.get() == null
                        || language.get() == null
                        || maritalStatus.get() == null
                        || purpose.get() == null
                        || eduQualifier.get() == null
                        || TextUtil.isAllEmpty(firstNameA.get(), lastName.get()))
            )
        }

        //工作相关数据。
        var occ = LoanObservableField<Int>().setCallT {

            checkWork()
        }

        var workFullName = LoanObservableField<String>().setCallT {
            checkWork()
        }

        var workEmail = LoanObservableField<String>().setCallT {
            checkWork()
        }

        var workSince = LoanObservableField<String>().setCallT {
            checkWork()
        }

        var salaryRange = LoanObservableField<String>().setCallT {
            checkWork()
        }

        var emailCode = LoanObservableField<String>().setCallT {
            checkWork()
        }

        var companyIndustry = LoanObservableField<Int>().setCallT {
            checkWork()
        }

        var staffSize = LoanObservableField<Int>().setCallT {
            checkWork()
        }

        var jobNature = LoanObservableField<Int>().setCallT {
            checkWork()
        }


        var levelOfPosition = LoanObservableField<Int>().setCallT {
            checkWork()
        }

        var companyTel = LoanObservableField<String>().setCallT {
            checkWork()
        }

        var isCheckEmail = LoanObservableField<Boolean>().setCallT {
            checkWork()
        }

        private fun checkWork() {
            val b = condition1() || condition2() || condition3()
            btnIsEnable.set(b)
        }

        private fun condition1(): Boolean {
            return (occ.get() == NO_WORK)
        }

        private fun condition2(): Boolean {
            val get = occ.get()
            return ((get == SELF_EMPLOYED || get == BUSINESS) && !(TextUtil.isExistEmpty(workFullName.get(),workEmail.get(),commitWorkSince, companyTel.get())
                    || salaryRange.get()==null
                    || companyIndustry.get() == null
                    || staffSize.get() == null) && if(isCheckEmail.get()==true) !TextUtil.isEmpty(emailCode.get()) else true)
        }

        private fun condition3(): Boolean {
            val get = occ.get()
            return ((get == SALARIED_FULL_TIME || get == SALARIED_PART_TIME) && !(TextUtil.isExistEmpty(workFullName.get(),workEmail.get(),commitWorkSince, companyTel.get())
                    || salaryRange.get()==null
                    || companyIndustry.get() == null
                    || staffSize.get() == null
                    || jobNature.get() == null
                    || levelOfPosition.get() == null
                    )&& if(isCheckEmail.get()==true) !TextUtil.isEmpty(emailCode.get()) else true)
        }

        var commitWorkSince: String? = ""
        var btnIsEnable = ObservableBoolean(false)
        var isEditable = ObservableBoolean(false)
        var isNoWork = ObservableBoolean(false)
        var isShowJobAndLevelOfPosition = ObservableBoolean(false)
        var isShowSmsCodeInput = ObservableBoolean(false)
        var isCreditFinish = ObservableBoolean(false)

        val selectNetData = arrayListOf(gender, language, eduQualifier, maritalStatus, purpose)
        val dataTextList =
            arrayListOf(firstNameA, middleName, lastName, anotherPhone, facebook, whatsApp, occ)
    }

    var writeWork = ObservableInt(View.GONE)

    //用户弹出窗口
    private var gender: (Fragment, BaseMenuViewModel?, TextView) -> Unit =
        { fragment, menuVm, view ->
            val genderTitle = R.string.loan_info_select_gender_title
            showMenu(fragment, menuVm, mUserMenuList.gender, view, genderTitle)
        }

    private var language: (Fragment, BaseMenuViewModel?, TextView) -> Unit =
        { fragment, menuVm, view ->
            val data = mUserMenuList.language
            val desc = R.string.loan_info_select_language_title
            showMenu(fragment, menuVm, data, view, desc)
        }


    private var edu: (Fragment, BaseMenuViewModel?, TextView) -> Unit = { fragment, menuVm, view ->
        val desc = R.string.loan_info_select_education_title
        val data = mUserMenuList.education
        showMenu(fragment, menuVm, data, view, desc)
    }

    private var ms: (Fragment, BaseMenuViewModel?, TextView) -> Unit = { fragment, menuVm, view ->
        val data = mUserMenuList.maritalStatus
        val desc = R.string.loan_info_select_marital_status_title
        showMenu(fragment, menuVm, data, view, desc)
    }

    private var purpose: (Fragment, BaseMenuViewModel?, TextView) -> Unit =
        { fragment, menuVm, view ->
            val data = mUserMenuList.purpose
            val desc = R.string.loan_info_select_purpose_title
            showMenu(fragment, menuVm, data, view, desc)
        }

    //工作信息弹出窗口。
    private var mJobNature: List<UserType>? = mutableListOf()
    private var occupation: (Fragment, BaseMenuViewModel?, TextView) -> Unit = { f, menuVm, view ->
        val desc = R.string.loan_info_select_occupation_title
        val data = mUserMenuList.occupation
        showMenu(f, menuVm, data, view, desc)
    }

    private var companyIndustry: (Fragment, BaseMenuViewModel?, TextView) -> Unit =
        { f, menuVm, view ->
            val desc = R.string.loan_info_select_company_industry_title
            val data = mUserMenuList.companyIndustry
            mUserMenuList.companyIndustry
            showMenu(f, menuVm, data, view, desc)
        }

    private var jobNature: (Fragment, BaseMenuViewModel?, TextView) -> Unit = { f, menuVm, view ->
        val tip = view.tag
        if (tip == null && mJobNature.isNullOrEmpty()) {
            ToastUtils.showShort(R.string.loan_info_hint_industry_toast)
        } else {
            val desc = R.string.loan_info_select_job_nature_title
            val data = mJobNature
            showMenu(f, menuVm, data, view, desc)
        }
    }

    private var staffSize: (Fragment, BaseMenuViewModel?, TextView) -> Unit = { f, menuVm, view ->
        val desc = R.string.loan_info_select_staff_size_title
        val data = mUserMenuList.staffSize
        showMenu(f, menuVm, data, view, desc)
    }

    private var levelOfPosition: (Fragment, BaseMenuViewModel?, TextView) -> Unit =
        { f, menuVm, view ->
            val desc = R.string.loan_info_select_level_of_position_title
            val data = mUserMenuList.levelOfPosition
            showMenu(f, menuVm, data, view, desc)
        }

    private var salaryRange: (Fragment, BaseMenuViewModel?, TextView) -> Unit = { f, menuVm, view ->
        val desc = R.string.loan_info_select_salary_range_title
        val data = mUserMenuList.salaryRange
        showMenu(f, menuVm, data, view, desc)
    }

    private val functionList = arrayListOf(
        gender,
        language,
        edu,
        ms,
        purpose,

        occupation,

        companyIndustry,
        jobNature,
        staffSize,
        levelOfPosition,
        salaryRange
    )

    fun showMenu(index: Int, fragment: Fragment, menuVm: BaseMenuViewModel?, view: TextView) {
        viewModelScope.launch {
            try {
                checkMenuData().await()
                mCurrentIndex = index
                if (index in 0 until functionList.size) {
                    functionList[index].invoke(fragment, menuVm, view)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun showMenu(
        fragment: Fragment, menuVm: BaseMenuViewModel?
        , data: List<UserType>?
        , view: TextView, loanInfoSelectEducationTitle: Int
    ) {

        viewModelScope.launch {
            val act = ActivityStack.currentActivity()
            if (data != null && act != null && !act.isFinishing && !act.isDestroyed) {
                val isOkResult = checkMustPermission(fragment)
                val isOk = isOkResult.await()
                var popupWindow: BasePopupWindow? = null;
                menuVm?.callbackData = { it ->
                    popupWindow?.dismiss()
                    val userType = data[it]
                    view.text = userType.info
                    if (mCurrentIndex >= 0 && mCurrentIndex < mViewData.selectNetData.size) {
                        mViewData.selectNetData[mCurrentIndex].set(userType.state.toInt())
                    }


                    if (mCurrentIndex == 5) {
                        //occupation = it
                        val visible =
                            if (userType.state.toInt() == NO_WORK) View.GONE else View.VISIBLE
                        writeWork.set(visible)
                    }

                    if (mCurrentIndex == 6) {
                        mJobNature = userType.jobs
                    }

                    Unit
                }
                if (isOk && menuVm != null) {
                    data?.let { data ->
                        val a = data.mapIndexed { i, d -> MenuData(d.info, i, false) }
                        menuVm.title = view.context.getString(loanInfoSelectEducationTitle)
                        menuVm.listData.clear()
                        menuVm.listData.addAll(a)
                        popupWindow = BasePopupWindow(act, menuVm, view, x217)
                        popupWindow?.show(x_187, x23)
                    }
                }
            }
        }
    }

    private fun checkMenuData(): Deferred<Unit> {
        return viewModelScope?.async {
            if (!this@ProfileInfoViewModel::mUserMenuList.isInitialized) {
                val result =
                    reqApi(UserInfoService::class.java, { queryListInfo() }, isShowLoading = true)
                result.data?.let {
                    mUserMenuList = it
                }
            }
        }
    }

    fun checkMustPermission(et: EditText, fragment: Fragment) {
        if (et.isFocusable) {
            viewModelScope.launch {
                val a = checkMustPermission(fragment)
                a.await()
            }
        }
    }

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
            mViewData.whatsApp.set(whatsapp)
            mViewData.anotherPhone.set(anotherPhone)
            if (!TextUtil.isEmpty(educationStr)) {
                mViewData.eduQualifier.set(education)
            }
            //mBinding.writeUserInfoEdu.text = educationStr
        }
    }

    //手机通信录联系人。
    private var cacheContactInfo: String? = null
    private var isFirstLoadContact: Boolean = true
    fun collectContactInfo() {
        if (AndroidPermissions.hasPermissions(
                ContextProvider.app,
                Manifest.permission.READ_CONTACTS
            )
        ) {
            if (isFirstLoadContact) {
                isFirstLoadContact = false
                thread(true) {
                    ActivityStack.currentActivity()?.let {
                        if (!it.isDestroyed || !it.isFinishing) {
                            val contacts = PhoneUtil.getContactsList(it)
                            cacheContactInfo =
                                Base64.encode(GsonUtils.toJsonString(contacts).toByteArray())
                        }
                    }
                }
            }
        }
    }

    fun localPause() {

    }

    fun commitBeforeCheckPermission(fragment: Fragment) {
        mPermissionModel.requestPermission(fragment)
    }

    fun commitUserInfoData(reqCode: Int) {
        if (reqCode == PermissionCheckModel.PERMISSION_DATA_COMMIT) {
            collectContactInfo()
            FirseBaseEventUtils.trackEvent(StatEventTypeName.PERSON_INFO_COMMIT)
        }
    }

    @get:Bindable
    var workSince: String? = null
        set(workSince) {
            field = workSince
        }
    var commitWorkSince: String? = ""

    fun choseWorkDateClick(fragment: Fragment, view: View) {
        var now = Calendar.getInstance()
        var dpd = newInstance(
            { _, year, monthOfYear, dayOfMonth ->
                workSince = "$dayOfMonth-${(monthOfYear + 1)}-$year"
                commitWorkSince = "$year-${monthOfYear + 1}-$dayOfMonth"
            },
            now.get(Calendar.YEAR), // Initial year selection
            now.get(Calendar.MONTH), // Initial month selection
            now.get(Calendar.DAY_OF_MONTH) // Inital day selection
        )
        dpd.maxDate = now
        dpd.show(fragment.childFragmentManager, "Datepickerdialog")
    }




}



