package com.hc.login

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import com.hc.login.databinding.FragmentLoginPhoneLayoutBinding
import com.hc.login.vm.LoginVM
import com.hc.uicomponent.base.BaseFragment

class PhoneLoginFragment : BaseFragment<FragmentLoginPhoneLayoutBinding>(R.layout.fragment_login_phone_layout) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mFragmentBinding.vm = ViewModelProvider(NavHostFragment.findNavController(this).getViewModelStoreOwner(R.id.loginModelNavContainer)).get(LoginVM::class.java)
        val model:LoginVM? = mFragmentBinding.vm
        model?.isUpdateLanguageUI?.observe(this.viewLifecycleOwner, Observer {
            val ad = mFragmentBinding.menu.mBindLayout?.adapter
            ad?.notifyDataSetChanged()
        })
    }

}