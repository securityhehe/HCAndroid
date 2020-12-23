package com.hc.accountinfo

import android.os.Bundle
import android.view.View
import com.hc.accountinfo.R.layout.fragment_account_info
import com.hc.accountinfo.databinding.FragmentAccountInfoBinding
import com.hc.accountinfo.vm.LoanSettingViewModel
import com.hc.uicomponent.annotation.BindViewModel
import com.hc.uicomponent.base.BaseFragment


class AccountSettingFragment : BaseFragment<FragmentAccountInfoBinding>(fragment_account_info) {

    @BindViewModel
    var mSettingVm :LoanSettingViewModel?=null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mFragmentBinding?.apply {
            vm = mSettingVm
            vm?.changeHead()
        }
    }

}