package com.hc.accountinfo

import android.os.Bundle
import android.view.View
import androidx.navigation.Navigation
import com.hc.accountinfo.databinding.FragmentPermissionResultBinding
import com.hc.uicomponent.base.BaseFragment
import com.hc.uicomponent.config.Constants
import com.hc.uicomponent.utils.FacebookEventUtils

class AccountAuthResultFragment : BaseFragment<FragmentPermissionResultBinding>(R.layout.fragment_permission_result) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.getString(Constants.STATE)?.toIntOrNull()?.let { it ->
            when (it) {
                Constants.NUMBER_10 -> {//failure
                    mFragmentBinding.run {
                        fail.visibility = View.VISIBLE
                        success.visibility = View.GONE
                    }
                }
                Constants.NUMBER_20 -> {//success
                    mFragmentBinding.run {
                        fail.visibility = View.GONE
                        success.visibility = View.VISIBLE
                    }
                    FacebookEventUtils.setFacebookEvent(requireContext(), FacebookEventUtils.PURCHASE)
                }
            }
            mFragmentBinding.run {
                successBack.setOnClickListener { view ->
                    Navigation.findNavController(view).navigate(R.id.loan_info_auth_result_to_auth_center)
                }
                failBack.setOnClickListener { view ->
                    Navigation.findNavController(view).navigate(R.id.loan_info_auth_result_to_auth_center)
                }

                gotoApply.setOnClickListener { view ->
                    Navigation.findNavController(view).navigate(R.id.loan_info_auth_result_to_profile_info)
                }
            }
        }
    }

}
