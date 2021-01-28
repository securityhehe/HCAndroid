package com.hc.accountinfo
import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentActivity
import com.hc.accountinfo.databinding.FragmentProfileInputInfoBinding
import com.hc.accountinfo.vm.ProfileInfoViewModel
import com.hc.data.formKey
import com.hc.permission.AndroidPermissions
import com.hc.uicomponent.annotation.BindViewModel
import com.hc.uicomponent.base.BaseFragment
import com.hc.uicomponent.config.Constants
import com.hc.uicomponent.menu.BaseMenuViewModel
import com.hc.uicomponent.provider.ContextProvider
import com.hc.uicomponent.stack.ActivityStack
import com.hc.uicomponent.utils.FirseBaseEventUtils
import com.hc.uicomponent.utils.LocationUtils
import com.hc.uicomponent.utils.StatEventTypeName

class AccountProfileInputFragment : BaseFragment<FragmentProfileInputInfoBinding>(R.layout.fragment_profile_input_info),AndroidPermissions.PermissionCallbacks{

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
        vm?.initBaseInfoViewModel(this@AccountProfileInputFragment)
        vm?.baseVm?.let {
            if(it.enableChangeFrom){
                it.formKey = (arguments?.getString(formKey)?.toInt()) ?: 0
                it.enableChangeFrom = false
            }
        }
        val isCreditFinish = arguments?.getBoolean(Constants.STATE) ?: false
        mFragmentBinding.isCreditFinish =isCreditFinish
        vm?.reqUserInfo(isCreditFinish)
    }

    override fun onResume() {
        super.onResume()
        FirseBaseEventUtils.trackEvent(StatEventTypeName.PERSON_INFO_PAGE)
        LocationUtils.getInstance().resetGpsLocationUpdates(requireContext())
    }

    override fun onPause() {
        super.onPause()
        LocationUtils.getInstance().stopLocationUpdates(requireContext())
        LocationUtils.getInstance().stopGps()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        AndroidPermissions.onRequestPermissionsResult(requestCode,permissions,grantResults,this)
    }

    override fun onAllPermissionGranted(requestCode: Int) {
        vm?.commitUserInfoData(this,requestCode)
    }

}