package com.hc.accountinfo

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.provider.FontsContractCompat.FontRequestCallback.RESULT_OK
import androidx.fragment.app.Fragment
import com.hc.accountinfo.databinding.FragmnetKycInfoBinding
import com.hc.accountinfo.vm.KycViewModel
import com.hc.data.formKey
import com.hc.permission.AndroidPermissions
import com.hc.uicomponent.annotation.BindViewModel
import com.hc.uicomponent.base.BaseFragment
import com.hc.uicomponent.config.Constants
import com.hc.uicomponent.provider.ContextProvider
import com.hc.uicomponent.utils.DialogUtils
import com.hc.uicomponent.utils.disableChildViewClickEvent
import com.wildma.idcardcamera.camera.IDCardCamera

class AccountKycFragment : BaseFragment<FragmnetKycInfoBinding>(R.layout.fragmnet_kyc_info) ,
    AndroidPermissions.PermissionCallbacks {

    @BindViewModel
    var vm: KycViewModel? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mFragmentBinding.vm = vm
        mFragmentBinding.vm?.apply {
            initBaseInfoViewModel(this@AccountKycFragment)
            baseVm?.let {
                if(it.enableChangeFrom){
                    it.formKey = (arguments?.getString(formKey)?.toInt()) ?: 0
                    it.enableChangeFrom = false
                }
            }
            disableChildViewClickEvent(mFragmentBinding.rootView)
           val authState  =  arguments?.getBoolean(Constants.STATE)?:false
            vm?.showKycInfo(this@AccountKycFragment,authState)
        }
        mFragmentBinding.fm = this@AccountKycFragment
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(this, backCallback)
    }


    private val backCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            mFragmentBinding.vm?.apply {
                gotoAuthCenterPage(mFragmentBinding.cardDesc)
            }
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: Array<out String>) {
        super.onPermissionsDenied(requestCode, perms)
        DialogUtils.showPermissionDialog(requireActivity(),deniedPassionTip = ContextProvider.mPermissionsRationaleProvider?.getRationaleText(requireContext(),*perms)?:"")
    }

    override fun onAllPermissionGranted(requestCode: Int) {
        super.onAllPermissionGranted(requestCode)
        vm?.doOperatorAuth(this,requestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == IDCardCamera.RESULT_CODE) {
            vm?.onActivityResultPhoto(requestCode, resultCode, data)
        }else if(resultCode == Activity.RESULT_OK ) {
            vm?.onActivityResultForAuthFace(this@AccountKycFragment,requestCode, resultCode, data)
        }
    }


}