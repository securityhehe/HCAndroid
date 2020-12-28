package com.hc.main

import android.os.Bundle
import android.view.View
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.hc.main.app.LoanBase
import com.hc.main.vm.MainViewModel
import com.hc.uicomponent.annotation.BindViewModel
import com.hc.uicomponent.base.BaseActivity
import com.test.app.R
import com.test.app.databinding.ActivityMainBinding

class MainActivity : BaseActivity<ActivityMainBinding>(R.layout.activity_main) {

    @BindViewModel
    var mHomeViewModel: MainViewModel?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        LoanBase.initVideoChat(applicationContext)
        super.onCreate(savedInstanceState)
        val navController = findNavController(R.id.nav_host_fragment)
        mBinding?.apply {
            vm = mHomeViewModel
            navView.setupWithNavController(navController)
        }
        mHomeViewModel?.apply {
            checkLogin(this@MainActivity)
        }
    }
}
