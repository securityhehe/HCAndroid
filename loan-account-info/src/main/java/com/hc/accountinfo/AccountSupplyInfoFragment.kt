package com.hc.accountinfo

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import com.hc.accountinfo.databinding.FragmentSupplementaryInfoBinding
import com.hc.accountinfo.vm.SupplyInfoViewModel
import com.hc.permission.AndroidPermissions
import com.hc.uicomponent.annotation.BindViewModel
import com.hc.uicomponent.base.BaseFragment
import com.hc.uicomponent.config.Constants
import com.hc.uicomponent.menu.BaseMenuViewModel
import kotlinx.android.synthetic.main.fragmnet_kyc_info.*

class AccountSupplyInfoFragment : BaseFragment<FragmentSupplementaryInfoBinding>(R.layout.fragment_supplementary_info),AndroidPermissions.PermissionCallbacks{


    @BindViewModel
    var mSupplyInfoViewModel: SupplyInfoViewModel? = null
    @BindViewModel
    var baseMenuVm: BaseMenuViewModel? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val isCreditFinish = arguments?.getBoolean(Constants.STATE) ?: false
        mFragmentBinding.apply {
            this.isCreditFinish = isCreditFinish
            this.vm = mSupplyInfoViewModel
            this.baseVm = baseMenuVm
            this.fm = this@AccountSupplyInfoFragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(this, backCallback)
        mSupplyInfoViewModel?.reqReShowSupplementInfo(true)
    }

    private val backCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            mFragmentBinding.vm?.apply {
                back(title)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        AndroidPermissions.onRequestPermissionsResult(requestCode,permissions,grantResults, this)
    }

    override fun onAllPermissionGranted(requestCode: Int) {
        super.onAllPermissionGranted(requestCode)
        mSupplyInfoViewModel?.onAllPermissionGranted(this@AccountSupplyInfoFragment,requestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        mSupplyInfoViewModel?.onActivityResult(requestCode,resultCode,data)
    }




}
