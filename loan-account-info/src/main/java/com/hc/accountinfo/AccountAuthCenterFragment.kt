package com.hc.accountinfo

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import com.hc.accountinfo.databinding.FragmentAccountProfileBinding
import com.hc.accountinfo.vm.AuthCenterViewModel
import com.hc.uicomponent.annotation.BindViewModel
import com.hc.uicomponent.base.BaseFragment
import kotlinx.android.synthetic.main.fragmnet_kyc_info.*

class AccountAuthCenterFragment : BaseFragment<FragmentAccountProfileBinding>(R.layout.fragment_account_profile) {

    @BindViewModel
    var mAuthCenterVM: AuthCenterViewModel? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mFragmentBinding.apply {
            vm = mAuthCenterVM
            vm?.initBaseInfoViewModel(this@AccountAuthCenterFragment)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(this, backCallback)
    }

    private val backCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            mFragmentBinding.vm?.apply {
                back(title)
            }
        }
    }


}
