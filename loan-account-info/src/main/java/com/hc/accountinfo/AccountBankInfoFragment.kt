package com.hc.accountinfo

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import com.hc.accountinfo.databinding.FragmentBankInfoBinding
import com.hc.accountinfo.vm.BankInfoViewModel
import com.hc.uicomponent.annotation.BindViewModel
import com.hc.uicomponent.base.BaseFragment
import com.hc.uicomponent.config.Constants
import com.hc.uicomponent.menu.BaseMenuViewModel
import kotlinx.android.synthetic.main.fragmnet_kyc_info.*

class AccountBankInfoFragment : BaseFragment<FragmentBankInfoBinding>(R.layout.fragment_bank_info){


    @BindViewModel
    var mSupplyInfoViewModel: BankInfoViewModel? = null
    @BindViewModel
    var baseMenuVm: BaseMenuViewModel? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val isCreditFinish = arguments?.getBoolean(Constants.STATE) ?: false
        mFragmentBinding.apply {

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