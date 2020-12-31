package com.hc.accountinfo.vm.viewdata

import android.view.View
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableInt
import com.hc.accountinfo.vm.ProfileInfoViewModel
import com.hc.accountinfo.vm.ProfileInfoViewModel.Companion.BUSINESS
import com.hc.accountinfo.vm.ProfileInfoViewModel.Companion.NO_WORK
import com.hc.accountinfo.vm.ProfileInfoViewModel.Companion.SALARIED_FULL_TIME
import com.hc.accountinfo.vm.ProfileInfoViewModel.Companion.SALARIED_PART_TIME
import com.hc.accountinfo.vm.ProfileInfoViewModel.Companion.SELF_EMPLOYED
import com.hc.data.StringUtil
import com.hc.data.user.UserInfoExt
import com.hc.data.user.UserInfoSub
import com.hc.data.user.UserType
import com.hc.uicomponent.LoanObservableField
import com.hc.uicomponent.utils.TextUtil

class ViewUserInfoData {

    val mUserInfo: UserInfoSub by lazy {
        UserInfoSub()
    }

    var firstNameA = LoanObservableField<String>().setCallT {
        mUserInfo.firstNamePan = it
        checkUserInfo()
    }

    var middleName = LoanObservableField<String>().setCallT {
        mUserInfo.middleNamePan = it
        checkUserInfo()
    }

    var lastName = LoanObservableField<String>().setCallT {
        mUserInfo.lastNamePan = it
        checkUserInfo()
    }

    //选择相关数据。
    var gender = LoanObservableField<UserType>().setCallT {
        mUserInfo.sex = it.state.toInt()
        checkUserInfo()
    }
    var language = LoanObservableField<UserType>().setCallT {
        mUserInfo.language = it.state.toInt()
        checkUserInfo()
    }
    var eduQualifier = LoanObservableField<UserType>().setCallT {
        mUserInfo.education = it.state.toInt()
        checkUserInfo()
    }
    var maritalStatus = LoanObservableField<UserType>().setCallT {
        mUserInfo.marryState = it.state.toInt()
        checkUserInfo()
    }
    var purpose = LoanObservableField<UserType>().setCallT {
        mUserInfo.purpose = it.state.toInt()
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
    var writeWork = ObservableInt(View.GONE)

    var occ = LoanObservableField<UserType>().setCallT {
        mUserInfo.occupation = it.state.toInt()

        val state = it.state.toInt()
        if (state == NO_WORK) {
            writeWork.set(View.GONE)
            isShowJobAndLevelOfPosition.set(View.GONE)
        } else {
            writeWork.set(View.VISIBLE)
        }
        if (state == SELF_EMPLOYED || state == BUSINESS) {
            isShowJobAndLevelOfPosition.set(View.GONE)
        }
        if (state == SALARIED_FULL_TIME || state == SALARIED_PART_TIME) {
            isShowJobAndLevelOfPosition.set(View.VISIBLE)
        }
        checkWork()
    }

    var workFullName = LoanObservableField<String>().setCallT {
        mUserInfo.companyName = it
        checkWork()
    }

    var workEmail = LoanObservableField<String>().setCallT {
        mUserInfo.companyEmail = it
        checkWork()
    }

    //工作时间-2019-2000
    var workSince = LoanObservableField<String>().setCallT {
        mUserInfo.workingSince = it
        checkWork()
    }

    var companyTel = LoanObservableField<String>().setCallT {
        mUserInfo.companyTel = it
        checkWork()
    }

    var salaryRange = LoanObservableField<UserType>().setCallT {
        mUserInfo.salaryRange = it.state.toInt()
        checkWork()
    }

    // if (mWorkInfoLogic.isCheckEmail)
    var emailCode = LoanObservableField<String>().setCallT {
        mUserInfo.emailCode = it
        checkWork()
    }

    var companyEmail = LoanObservableField<String>().setCallT {

        mUserInfo.companyEmail = it
        checkWork()
    }

    var companyIndustry = LoanObservableField<UserType>().setCallT {

        if (!it.state.isNullOrEmpty()) {
            mUserInfo.companyIndustry = it.state.toInt()
        }
        checkWork()
    }

    var staffSize = LoanObservableField<UserType>().setCallT {

        if (!it.state.isNullOrEmpty()) {
            mUserInfo.salaryRange = it.state.toInt()
        }
        checkWork()
    }

    //mWorkInfoLogic.isShowJobAndLevelOfPosition.get()
    var jobNature = LoanObservableField<UserType>().setCallT {

        if (!it.state.isNullOrEmpty()) {
            mUserInfo.jobNature = it.state.toInt()
        }
        checkWork()
    }

    //mWorkInfoLogic.isShowJobAndLevelOfPosition.get()
    var levelOfPosition = LoanObservableField<UserType>().setCallT {
        if (!it.state.isNullOrEmpty()) {
            mUserInfo.levelOfPosition = it.state.toInt()
        }
        checkWork()
    }

    //控制是否显示发送验证码的页面
    var isShowSmsCodeInput = ObservableBoolean(false)

    var isCheckEmail = LoanObservableField<Boolean>().setCallT {
        checkWork()
    }

    private fun checkWork() {
        val b = condition1() || condition2() || condition3()
        btnIsEnable.set(b)
    }

    fun condition1(): Boolean {
        return (occ.get()?.state?.toInt() == ProfileInfoViewModel.NO_WORK)
    }

    fun condition2(): Boolean {
        val get = occ.get()?.state?.toInt()
        return ((get == ProfileInfoViewModel.SELF_EMPLOYED
                || get == ProfileInfoViewModel.BUSINESS) && !(TextUtil.isExistEmpty(workFullName.get(), workEmail.get(), mWorkSinceText, companyTel.get())
                || salaryRange.get() == null
                || companyIndustry.get() == null
                || staffSize.get() == null) && if (isCheckEmail.get() == true) !TextUtil.isEmpty(
            emailCode.get()
        ) else true)
    }

    fun condition3(): Boolean {
        val get = occ.get()?.state?.toInt()
        return ((get == ProfileInfoViewModel.SALARIED_FULL_TIME || get == ProfileInfoViewModel.SALARIED_PART_TIME)
                && !(TextUtil.isExistEmpty(workFullName.get(), workEmail.get(), mWorkSinceText, companyTel.get())
                || salaryRange.get() == null
                || companyIndustry.get() == null
                || staffSize.get() == null
                || jobNature.get() == null
                || levelOfPosition.get() == null) && if (isCheckEmail.get() == true) !TextUtil.isEmpty(emailCode.get()) else true)
    }

    var mWorkSinceText: String? = ""
    var btnIsEnable = ObservableBoolean(false)

    //当前认证状态(控制工作元素显隐-如，选择下拉菜单，是否可以编辑邮箱)
    var isCreditFinish = ObservableBoolean(false)

    //是否可以编辑工作信息的时间选择
    var isEditable = ObservableBoolean(false)

    //控制是否可以编辑填写的邮箱（在发送验证码过程中不能编辑 || 认证完成不能编辑） -->
    var isCannotEditEmail = ObservableBoolean(false)


    //控制是否显示Job Nature & Level Of Position -->
    var isShowJobAndLevelOfPosition = ObservableInt(View.GONE)

    val selectNetData =
        arrayListOf(gender, language, eduQualifier, maritalStatus, purpose, occ, companyIndustry, jobNature, staffSize, levelOfPosition, salaryRange)

    val dataTextList = arrayListOf(firstNameA, middleName, lastName, anotherPhone, facebook, whatsApp, workFullName, companyEmail, companyTel, workSince)

}

fun UserInfoExt.getIntKey(): MutableList<String> {
    return mutableListOf(
        "${sex}_${sexStr}"
        , "${language}_${languageStr}"
        , "${education}_${educationStr}"
        , "${marryState}_${marryStateStr}"
        , "${purpose}_${purposeStr}"
        , "${userWorkInfoModel?.occupation}_${userWorkInfoModel?.occupationStr}"
        , "${userWorkInfoModel?.companyIndustry}_${userWorkInfoModel?.companyIndustryStr}"
        , "${userWorkInfoModel?.jobNature}_${userWorkInfoModel?.jobNatureStr}"
        , "${userWorkInfoModel?.staffSize}_${userWorkInfoModel?.staffSizeStr}"
        , "${userWorkInfoModel?.levelOfPosition}_${userWorkInfoModel?.levelOfPositionStr}"
        , "${userWorkInfoModel?.salaryRange}_${userWorkInfoModel?.salaryRangeStr}"
    )
}

fun UserInfoExt.getTextKey(): MutableList<String> {
    return mutableListOf(
        "$firstNamePan"
        , "$middleNamePan"
        , "$lastNamePan"

        , "$anotherPhone"
        , "$facebook"
        , "$whatsapp"

        , "${userWorkInfoModel?.companyEmail}"
        , "${userWorkInfoModel?.companyName}"
        , "${userWorkInfoModel?.companyTel}"
        , "${userWorkInfoModel?.workingSince}"

    )
}
