package com.hc.login

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import com.hc.login.databinding.FragmentLoginPhoneCodeLayoutBinding
import com.hc.login.vm.LoginVM
import com.hc.uicomponent.base.BaseFragment

class InputCheckCodeFragment:BaseFragment<FragmentLoginPhoneCodeLayoutBinding>(R.layout.fragment_login_phone_code_layout){

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mFragmentBinding.vm = ViewModelProvider(NavHostFragment.findNavController(this).getViewModelStoreOwner(R.id.loginModelNavContainer)).get(LoginVM::class.java)
    }
}