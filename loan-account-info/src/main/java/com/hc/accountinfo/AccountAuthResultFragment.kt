package com.hc.accountinfo

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import com.hc.accountinfo.databinding.FragmentAccountProfileBinding
import com.hc.accountinfo.databinding.FragmentPermissionSuccessBinding
import com.hc.accountinfo.vm.AuthCenterViewModel
import com.hc.uicomponent.annotation.BindViewModel
import com.hc.uicomponent.base.BaseFragment
import kotlinx.android.synthetic.main.fragmnet_kyc_info.*

class AccountAuthResultFragment : BaseFragment<FragmentPermissionSuccessBinding>(R.layout.fragment_permission_success) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

}
