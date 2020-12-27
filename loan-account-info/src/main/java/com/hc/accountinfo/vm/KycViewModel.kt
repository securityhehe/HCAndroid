package com.hc.accountinfo.vm

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.view.View
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.ToastUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.hc.accountinfo.R
import com.hc.accountinfo.api.IUserInfoService
import com.hc.accountinfo.api.UserInfoService
import com.hc.data.common.CommonDataModel
import com.hc.data.formPermissionPage
import com.hc.data.user.KycUserInfo
import com.hc.permission.AndroidPermissions
import com.hc.uicomponent.LoanObservableField
import com.hc.uicomponent.base.BaseViewData
import com.hc.uicomponent.base.BaseViewModel
import com.hc.uicomponent.base.PageBase
import com.hc.uicomponent.call.reqApi
import com.hc.uicomponent.config.RELEASE_PHOTO_PATH_APP
import com.hc.uicomponent.config.TEMP_PHOTO_PATH_FOR_APP
import com.hc.uicomponent.provider.ContextProvider
import com.hc.uicomponent.stack.ActivityStack
import com.hc.uicomponent.utils.FirseBaseEventUtils
import com.hc.uicomponent.utils.StatEventTypeName
import com.liveness.dflivenesslibrary.DFProductResult
import com.liveness.dflivenesslibrary.liveness.DFSilentLivenessActivity
import com.tools.network.callback.SignUtil
import com.tools.network.utils.FileUploadUtil
import com.wildma.idcardcamera.camera.IDCardCamera
import com.wildma.idcardcamera.camera.IDCardCamera.*
import frame.utils.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileInputStream

class KycViewModel : BaseViewModel() {
    companion object {
        private val LIVE_FACE_IMG_NAME = Base64.encode("faceImg.png".toByteArray())
        const val SCAN_FACE = 4
    }

    init {
        CommonDataModel.TEMP_PHOTO_PATH = TEMP_PHOTO_PATH_FOR_APP + CommonDataModel.mTokenData?.userId + File.separator
        CommonDataModel.RELEASE_PHOTO_PATH = RELEASE_PHOTO_PATH_APP + CommonDataModel.mTokenData?.userId+ File.separator
    }

    var baseVm: BaseAuthCenterInfo? = null
    var viewData = ViewData()
    var viewOperator = ViewOperator()
    private var isOnlyScanFaceFlag = false
    private var checkFaceResult = false
    private var mReqCode: String? = null
    private val kycBean: KycUserInfo by lazy {
        KycUserInfo()
    }

    fun initBaseInfoViewModel(fragment: Fragment) {
        val viewModelStoreOwner = NavHostFragment.findNavController(fragment).getViewModelStoreOwner(R.id.loan_info_model_container)
        baseVm = ViewModelProvider(viewModelStoreOwner).get((BaseAuthCenterInfo::class.java))
        println("center===> $baseVm")
    }

    override fun back(view: View) {
        if (baseVm?.formKey == formPermissionPage) {
            gotoAuthCenterPage(view)
        } else {
            super.back(view)
        }
    }


    fun gotoAuthCenterPage(view: View) {
        Navigation.findNavController(view).navigate(R.id.loan_info_model_kyc_auth_center)
    }

    //控制是否显示aab
    fun showKycInfo(fragment: Fragment, isCreditFinish: Boolean) {
        viewModelScope.launch {
            val userInfo = reqApi(
                UserInfoService::class.java,
                { queryUserBasicInfo()},
                isCancelDialog = false
            )
            userInfo.data?.runCatching {
                val isVisible = if (this.aadhaarSign) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
                viewData.isShowAAbArea.set(isVisible)
                val aadAndPanImgAllNotEmpty = !TextUtil.isExistEmpty(frontImg, backImg, panImg)
                val panImgNotEmpty = !TextUtil.isExistEmpty(panImg)
                val isCanReadCache =
                    isCreditFinish || if (this.aadhaarSign) aadAndPanImgAllNotEmpty else panImgNotEmpty
                val frontBackExistEmpty = TextUtil.isExistEmpty(frontImg, backImg)
                val pinNotEmpty = !TextUtil.isEmpty(panImg)
                val pinEmpty = !TextUtil.isEmpty(panImg)
                val isShowPanImageFlag =
                    !isCreditFinish && pinNotEmpty && frontBackExistEmpty && this.aadhaarSign || pinEmpty

                val baseSavePath = CommonDataModel.RELEASE_PHOTO_PATH
                if (this.aadhaarSign) {
                    val saveFrontFilePath = baseSavePath + ID_CARD_FRONT_IMG
                    loadImage(
                        fragment,
                        saveFrontFilePath,
                        isCanReadCache,
                        viewData.aadCardFrontPhotoPath,
                        frontImg,
                        ID_CARD_FRONT_IMG
                    )

                    val saveBackFilePath = baseSavePath + ID_CARD_BACK_IMG
                    loadImage(
                        fragment,
                        saveBackFilePath,
                        isCanReadCache,
                        viewData.aadCardBackPhotoPath,
                        backImg,
                        ID_CARD_BACK_IMG
                    )
                }

                if (!isShowPanImageFlag) {
                    val savePinPath = baseSavePath + PAN_CARD_FORNT_IMG
                    loadImage(
                        fragment,
                        savePinPath,
                        isCanReadCache,
                        viewData.pinCardFrontPhotoPath,
                        panImg,
                        PAN_CARD_FORNT_IMG
                    )
                }


                if (isCreditFinish) {//20 & 30 -> done
                    val saveLivePath = baseSavePath + LIVE_FACE_IMG_NAME
                    loadImage(
                        fragment,
                        saveLivePath,
                        isCanReadCache,
                        viewData.faceRecPhotoPath,
                        livingImg,
                        LIVE_FACE_IMG_NAME
                    )
                    viewData.isEnable.set(false)
                    viewData.setDataAuthOk()
                } else { //10 & 40 no done
                    if (this.aadhaarSign) {
                        if (aadAndPanImgAllNotEmpty) {
                            viewData.onlyEnableScanFace()
                            isOnlyScanFaceFlag = true
                        }
                    } else {
                        if (panImgNotEmpty) {
                            isOnlyScanFaceFlag = true
                            viewData.onlyEnableScanFace()
                        }
                    }
                }
            }
        }
    }


    private fun loadImage(
        fragment: Fragment,
        saveFilePath: String,
        isCanReadCache: Boolean,
        photoObserverPath: ObservableField<String>,
        imageUrl: String,
        idCardFrontImg: String
    ) {
        saveFilePath.let { saveFilePath ->
            checkFileIsOk(isCanReadCache, saveFilePath,
                fileOk = {
                    photoObserverPath.set(saveFilePath)
                },
                fileError = {
                    photoObserverPath.set(imageUrl)
                    if (!TextUtil.isEmpty(imageUrl)) {
                        loadImage(fragment, imageUrl, idCardFrontImg)
                    }
                }
            )
        }
    }


    private fun checkFileIsOk(
        isCanReadCache: Boolean = false,
        filePath: String,
        fileOk: () -> Unit,
        fileError: () -> Unit
    ) {
        val hasPermissions = AndroidPermissions.hasPermissions(
            ContextProvider.app,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        val isPathEmpty = !TextUtil.isEmpty(filePath)
        val fileExists = FileUtils.isFileExists(filePath)
        if (isCanReadCache && isPathEmpty && fileExists && hasPermissions) {
            fileOk
        } else {
            fileError
        }
    }

    private fun loadImage(fragment: Fragment, imageUrl: String?, imageName: String) {

        if (fragment.isDetached) {
            return
        }
        Glide.with(fragment).downloadOnly().load(imageUrl).listener(object : RequestListener<File> {
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<File>?,
                isFirstResource: Boolean
            ): Boolean {
                return false
            }

            override fun onResourceReady(
                resource: File?,
                model: Any?,
                target: Target<File>?,
                dataSource: DataSource?,
                isFirstResource: Boolean
            ): Boolean {
                kotlin.runCatching {
                    val fis = FileInputStream(resource)
                    val bmp = BitmapFactory.decodeStream(fis)
                    val saveSuccessPath = FileUtil.saveFile(
                        ContextProvider.app,
                        CommonDataModel.RELEASE_PHOTO_PATH,
                        imageName,
                        bmp
                    )
                    println("--->KYC本地没有对应图片，进行缓存，缓存状态--->$saveSuccessPath  , $imageName")
                }
                return false
            }
        }).preload()
    }



    private var currentWhoOperator: Int = -1
    fun doOperatorAuth(fragment: Fragment, whoOperator: Int) {
        currentWhoOperator = whoOperator
        if (whoOperator < SCAN_FACE) {
            create(fragment).openCamera(whoOperator)
        } else if (whoOperator == SCAN_FACE) {
            viewModelScope.launch {
                var faceSdkType = reqApi(UserInfoService::class.java, block = { getFaceSdkType() })
                faceSdkType.data?.run {
                    if (this == 2) {
                        val intent = Intent()
                        val act = fragment.requireActivity()
                        intent.setClass(
                            fragment.requireActivity(),
                            DFSilentLivenessActivity::class.java
                        )
                        intent.putExtra(DFSilentLivenessActivity.KEY_ANTI_HACK, true)
                        intent.putExtra(DFSilentLivenessActivity.KEY_DETECT_IMAGE_RESULT, true)
                        intent.putExtra(
                            DFSilentLivenessActivity.KEY_HINT_MESSAGE_HAS_FACE,
                            act.getString(R.string.kyc_string_liveness_has_face_and_holdstill_hint)
                        )
                        intent.putExtra(
                            DFSilentLivenessActivity.KEY_HINT_MESSAGE_NO_FACE,
                            act.getString(R.string.kyc_string_liveness_no_face_hint)
                        )
                        intent.putExtra(
                            DFSilentLivenessActivity.KEY_HINT_MESSAGE_FACE_NOT_VALID,
                            act.getString(R.string.kyc_liveness_face_not_valid_hint)
                        )
                        act.startActivityForResult(intent, SCAN_FACE)
                    }
                }
            }
        }
    }



    fun onActivityResultPhoto(requestCode: Int, resultCode: Int, intentData: Intent?) {
        if (resultCode == RESULT_CODE) {
            val photoPath = getImagePath(intentData)
            if (!StringUtil.isEmpty(photoPath)) {
                viewModelScope.launch {
                    kycBean.file = File(photoPath)//pan back
                    ActivityStack.currentActivity()?.let {
                        kycBean.deviceId = DeviceUtil.getDeviceId(it)
                    }
                    when (requestCode) {
                        TYPE_IDCARD_FRONT -> //ad front
                            kycBean.reqCode = null
                        TYPE_IDCARD_BACK -> //ad back
                            kycBean.reqCode = mReqCode
                        TYPE_PAN_FRONT -> //pan back
                            kycBean.reqCode = mReqCode
                    }

                    /** group request params **/
                    val groupReqMap = ReflectUtils.getFieldValue(kycBean)
                    val headParams = SignUtil.getInstance().addCommonParamsAndSign(groupReqMap)
                    val reqBodyMap = FileUploadUtil.getRequestMap(groupReqMap)

                    var reqResult = reqApi(IUserInfoService::class.java, block = {
                        when (requestCode) {
                            TYPE_IDCARD_BACK -> {// ad back
                                checkAdBackImg(headParams, reqBodyMap)
                            }
                            TYPE_PAN_FRONT -> {// pan card
                                checkPanFrontImg(headParams, reqBodyMap)
                            }
                            else -> {//ad front
                                checkAdFrontImg(headParams, reqBodyMap)
                            }
                        }
                    })

                    reqResult.run {
                        when (requestCode) {
                            TYPE_IDCARD_FRONT -> {
                                data?.run {
                                    mReqCode = this.toString()
                                    viewData.aadCardFrontPhotoPath.set(photoPath)
                                }
                            }
                            TYPE_IDCARD_BACK -> {
                                viewData.aadCardBackPhotoPath.set(photoPath)
                            }
                            TYPE_PAN_FRONT -> { //pan back
                                if (viewData.isShowAAbArea.get() == View.GONE) {
                                    data?.run {
                                        mReqCode = this.toString()
                                    }
                                }
                                viewData.pinCardFrontPhotoPath.set(photoPath)
                            }
                        }
                        viewData.isClickableScanFace.set(false)
                    }
                }
            }
        }
    }


    fun onActivityResultForAuthFace(
        fragment: Fragment,
        requestCode: Int,
        resultCode: Int,
        intentData: Intent?
    ) {
        val activity = fragment.requireActivity()
        activity ?: return
        if (activity is PageBase) {
            activity.showLoadingDialog()
        }
        try {
            if (fragment.isDetached) {
                return
            }
            if (Activity.RESULT_OK == resultCode && requestCode == SCAN_FACE) {

                if (intentData != null) {
                    val dfResult: DFProductResult =
                        intentData.getSerializableExtra(DFSilentLivenessActivity.KEY_RESULT_LIVENESS_IMG) as DFProductResult
                    val imageResults = dfResult.livenessImageResults
                    if (imageResults != null && imageResults.isNotEmpty()) {
                        val imgByte = imageResults[0].detectImage//face image

                        var saveSuccPath = FileUtil.saveFile(
                            ContextProvider.app,
                            CommonDataModel.TEMP_PHOTO_PATH,
                            LIVE_FACE_IMG_NAME,
                            imgByte
                        )
                        viewData.faceRecPhotoPath.set(saveSuccPath)
                        checkFaceResult = dfResult.isAntiHackPass
                        viewData.isClickableScanFace.set(!dfResult.isAntiHackPass)
                        if (checkFaceResult) FirseBaseEventUtils.trackEvent(StatEventTypeName.KYC_ANTI_HACK_SUCCESS_NUMS) else FirseBaseEventUtils.trackEvent(
                            StatEventTypeName.KYC_ANTI_HACK_FAILURE_NUMS
                        )
                    }
                }
            }
        } catch (e: Exception) {
        } finally {
            if (activity is PageBase) {
                activity.dismissLoadingDialog()
            }
        }
    }

    fun uploadKycInfo(btn:View,fragment: Fragment)  {
        if (!checkFaceResult){
            ToastUtils.showShort(R.string.kyc_face_scan_failure)
            return
        }

        kotlin.runCatching {
            FirseBaseEventUtils.trackEvent(StatEventTypeName.KYC_INFO_COMMIT)
            kycBean.reqCode = mReqCode
            kycBean.file = File(viewData.faceRecPhotoPath.get()?:"")
            kycBean.deviceId = DeviceUtil.getDeviceId(fragment.requireContext())
            val map = ReflectUtils.getFieldValue(kycBean)
            viewModelScope.launch {
                val reqResult = reqApi(
                    UserInfoService::class.java, {
                        val head = SignUtil.getInstance().addCommonParamsAndSign(map)
                        val params = FileUploadUtil.getRequestMap(map)
                        checkFaceImg(head, params)
                    }, isCancelDialog = false
                )
                val tempFile = File(CommonDataModel.TEMP_PHOTO_PATH)
                val exists = tempFile.exists()
                val b = tempFile.listFiles() != null
                val notEmpty = tempFile.listFiles()?.isNotEmpty() ?: false
                if (exists && b && notEmpty) {
                    val savePhotoJob = viewModelScope.async(Dispatchers.IO) {
                        println("--->KYC提交完毕后，处理图片复制工作--->")
                        if (!isOnlyScanFaceFlag) {
                            FileUtil.deleteDir(File(RELEASE_PHOTO_PATH_APP))//清除掉release目录中的缓存图片文件
                        }
                        FileUtil.copyDir(
                            CommonDataModel.TEMP_PHOTO_PATH,
                            CommonDataModel.RELEASE_PHOTO_PATH
                        )//src,dest 复制临时照片到缓存目录
                        FileUtil.deleteDir(File(CommonDataModel.TEMP_PHOTO_PATH))//清除掉用户的临时照片
                    }
                    savePhotoJob.await()
                }
                println("--->KYC提交完毕后，处理图片复制工作，都已完成！--->")
                //提交完成
                ToastUtils.showShort(reqResult.msg)
                back(btn)
            }
        }
    }


    inner class ViewOperator {
        fun commitFrontImage(fragment: Fragment)  {
            FirseBaseEventUtils.trackEvent(StatEventTypeName.KYC_AADHAAR_FRONT_CLICK)
            reqOperationPermission(fragment, TYPE_IDCARD_FRONT)
        }

        fun commitBackImage(view: Fragment) {
            FirseBaseEventUtils.trackEvent(StatEventTypeName.KYC_AADHAAR_BACK_CLICK)
            if (!viewData.isExistFrontCard()) {
                ToastUtils.showShort(R.string.kyc_scan_back_error_tip)
                return
            }
            reqOperationPermission(view, TYPE_IDCARD_BACK)
        }


        fun commitPinImage(view: Fragment) {
            if (viewData.isShowAAbArea.get() == View.VISIBLE) {
                if (!viewData.isExistFrontAndBackCard()) {
                    ToastUtils.showShort(R.string.kyc_scan_pan_error_tip)
                    return
                }
            }
            FirseBaseEventUtils.trackEvent(StatEventTypeName.KYC_PAN_FRONT_CLICK)
            reqOperationPermission(view, IDCardCamera.TYPE_PAN_FRONT)
        }

        fun commitFaceClick(view: Fragment)  {
            if (viewData.isShowAAbArea.get() == View.VISIBLE) {
                if (!viewData.isExistAdAndPanCard()) {
                    ToastUtils.showShort(R.string.kyc_scan_face_error_tip)
                    return
                }
            }

            if (viewData.isShowAAbArea.get() == View.VISIBLE) {
                if (!viewData.isExistAdAndPanCard()) {
                    ToastUtils.showShort(R.string.kyc_scan_face_error_tip)
                    return
                }
            } else {
                if (!viewData.isExistPanCard()) {
                    ToastUtils.showShort(R.string.kyc_scan_face_error_tip_no_aadhaar)
                    return
                }
            }
            FirseBaseEventUtils.trackEvent(StatEventTypeName.KYC_PHOTO_CLICK)
            reqOperationPermission(view, SCAN_FACE)
        }

        private fun reqOperationPermission(fragment: Fragment, whoOperation: Int) {
            AndroidPermissions.requestPermissions(
                fragment,
                "null",
                false,
                whoOperation,
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        }
    }
    inner class ViewData : BaseViewData() {

        //aab -》aab认证区域
        var isShowAAbArea = ObservableInt(View.GONE)
        var isOnlyEnableScanFace = ObservableBoolean(false)
        var isClickableScanFace = ObservableBoolean(true)
        var isShowAuthCommitBtn = ObservableInt(View.GONE)

        fun onlyEnableScanFace() {
            viewData.isEnable.set(false)
            viewData.isOnlyEnableScanFace.set(true)
        }

        fun setDataAuthOk() {
            isOnlyEnableScanFace.set(true)
            isClickableScanFace.set(false)
            isShowAuthCommitBtn.set(View.VISIBLE)
        }


        var aadCardFrontPhotoPath:LoanObservableField<String> = LoanObservableField("").setCallT {
            check()
        }

        var aadCardBackPhotoPath: LoanObservableField<String> = LoanObservableField("").setCallT {
            check()
        }


        var pinCardFrontPhotoPath: LoanObservableField<String> = LoanObservableField("").setCallT {
            check()
        }

        var faceRecPhotoPath: LoanObservableField<String> = LoanObservableField("").setCallT {
            check()
        }

        private fun check() {
            isEnable.set(
                if (isShowAAbArea.get() == View.VISIBLE) {
                    !(TextUtil.isExistEmpty(
                        aadCardFrontPhotoPath.get(),
                        aadCardBackPhotoPath.get(),
                        pinCardFrontPhotoPath.get(),
                        faceRecPhotoPath.get()
                    ))
                } else {
                    !(TextUtil.isExistEmpty(pinCardFrontPhotoPath.get(), faceRecPhotoPath.get()))
                }
            )
        }

        fun isExistFrontCard(): Boolean = !TextUtil.isEmpty(aadCardFrontPhotoPath.get())

        fun isExistFrontAndBackCard(): Boolean =
            isExistFrontCard() && (!TextUtil.isExistEmpty(aadCardBackPhotoPath.get()))

        fun isExistAdAndPanCard(): Boolean =
            isExistFrontAndBackCard() && !TextUtil.isExistEmpty(pinCardFrontPhotoPath.get())

        fun isExistPanCard(): Boolean = !TextUtil.isExistEmpty(pinCardFrontPhotoPath.get())
    }

}