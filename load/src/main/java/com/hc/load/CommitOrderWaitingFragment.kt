package com.hc.load

import android.os.Bundle
import android.view.View
import com.hc.load.databinding.FragmentLoanLoadingBinding
import com.hc.load.vm.CommitOrderLoadingViewModel
import com.hc.uicomponent.annotation.BindViewModel
import com.hc.uicomponent.base.BaseFragment
import com.hc.uicomponent.config.Constants

class CommitOrderWaitingFragment : BaseFragment<FragmentLoanLoadingBinding>(R.layout.fragment_loan_loading) {

    @BindViewModel
    var mCommitLoadingViewModel: CommitOrderLoadingViewModel? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mFragmentBinding.run {
            val number = arguments?.getString(Constants.ORDER_NUM)
            number?.let {
                mCommitLoadingViewModel?.startCountDownLogic(text2,it)
            }
        }
    }
}