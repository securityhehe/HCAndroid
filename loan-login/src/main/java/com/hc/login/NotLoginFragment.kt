package com.hc.login

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import com.hc.login.databinding.FragmentNotLoginLayoutBinding
import com.hc.login.vm.LoginVM
import com.hc.uicomponent.annotation.BindViewModel
import com.hc.uicomponent.base.BaseFragment
import com.hc.uicomponent.base.BaseViewModel

class NotLoginFragment : BaseFragment<FragmentNotLoginLayoutBinding>(R.layout.fragment_not_login_layout) {

    @BindViewModel
    var viewModel: LoginVM? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mFragmentBinding.vm = viewModel
    }

    override fun <M : BaseViewModel> createGetViewModel(clazz: Class<M>): M {
        return ViewModelProvider(NavHostFragment.findNavController(this).getViewModelStoreOwner(R.id.loginModelNavContainer)).get(clazz)
    }
}