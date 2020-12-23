package com.hc.login

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import com.hc.login.databinding.FragmentPermissionDescBinding
import com.hc.login.vm.LoginVM
import com.hc.permission.AndroidPermissions
import com.hc.uicomponent.base.BaseFragment

class LoanFragmentPermission :BaseFragment<FragmentPermissionDescBinding>(R.layout.fragment_permission_desc), AndroidPermissions.PermissionCallbacks {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mFragmentBinding.vm = ViewModelProvider(NavHostFragment.findNavController(this).getViewModelStoreOwner(R.id.loginModelNavContainer)).get(LoginVM::class.java)
        mFragmentBinding.vFragment = this@LoanFragmentPermission
    }

    private val backCallback = object:OnBackPressedCallback(true){
        override fun handleOnBackPressed() {
            mFragmentBinding.vm?.apply {
                gotoMainPage(mFragmentBinding.nextBtn)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(this,backCallback)
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        AndroidPermissions.onRequestPermissionsResult(requestCode,permissions,grantResults)
    }


    //kycPage
    override fun onDoneRequestPermission(requestCode: Int) {
        super.onDoneRequestPermission(requestCode)
        println("onDoneRequestPermission->>")
        mFragmentBinding.vm?.toKycPage(mFragmentBinding.nextBtn)
    }




}