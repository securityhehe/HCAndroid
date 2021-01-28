package com.hc.accountinfo

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.hc.accountinfo.databinding.FragmentBankInfoBinding
import com.hc.accountinfo.vm.BankInfoViewModel
import com.hc.uicomponent.annotation.BindViewModel
import com.hc.uicomponent.base.BaseFragment
import com.hc.uicomponent.base.CommonDataViewModel
import com.hc.uicomponent.config.Constants
import com.hc.uicomponent.menu.BaseMenuViewModel
import kotlinx.android.synthetic.main.fragmnet_kyc_info.*

class AccountBankInfoFragment : BaseFragment<FragmentBankInfoBinding>(R.layout.fragment_bank_info) {

    @BindViewModel
    var mSupplyInfoViewModel: BankInfoViewModel? = null

    @BindViewModel
    var mBaseMenuVm: BaseMenuViewModel? = null
    var mCommonModel : CommonDataViewModel?=null
    var isChangeBank:Boolean? = false
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val isCreditFinish = arguments?.getBoolean(Constants.STATE) ?: false
        mCommonModel = ViewModelProvider(requireActivity()).get(CommonDataViewModel::class.java)
        isChangeBank = arguments?.getBoolean(Constants.IS_CHANGE_BANK,false)
        mFragmentBinding.apply {
            this.vm = mSupplyInfoViewModel
            this.baseMenuVm = mBaseMenuVm
            this.isCreditFinish = isCreditFinish
        }
        mSupplyInfoViewModel?.showBankDetailInfo()
        mSupplyInfoViewModel?.let {
            viewLifecycleOwner.lifecycle.addObserver(mSupplyInfoViewModel as LifecycleObserver)
        }
        mSupplyInfoViewModel?.data?.observe(viewLifecycleOwner, Observer {
            if(isChangeBank == true){
                mCommonModel?.bankData?.value = it
            }
        })
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