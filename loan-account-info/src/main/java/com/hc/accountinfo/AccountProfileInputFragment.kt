package com.hc.accountinfo
import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentActivity
import com.hc.accountinfo.databinding.FragmentProfileInputInfoBinding
import com.hc.accountinfo.vm.ProfileInfoViewModel
import com.hc.uicomponent.annotation.BindViewModel
import com.hc.uicomponent.base.BaseFragment
import com.hc.uicomponent.menu.BaseMenuViewModel
import com.hc.uicomponent.provider.ContextProvider
import com.hc.uicomponent.stack.ActivityStack
import com.hc.uicomponent.utils.LocationUtils

class AccountProfileInputFragment : BaseFragment<FragmentProfileInputInfoBinding>(R.layout.fragment_profile_input_info){

    @BindViewModel
    var vm: ProfileInfoViewModel? = null

    @BindViewModel
    var menuVm :BaseMenuViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityStack.currentActivity()?.let {
            LocationUtils.getInstance().initLocation(ContextProvider.app,it as FragmentActivity)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mFragmentBinding.vm = vm
        mFragmentBinding.menuVM = menuVm
        mFragmentBinding.lanFragment = this
        //vm?.reqUserInfo()
    }

}