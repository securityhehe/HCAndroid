package com.hc.setting

import android.os.Bundle
import com.hc.setting.vm.SettingViewModel
import com.hc.uicomponent.annotation.BindViewModel
import com.hc.uicomponent.base.BaseActivity
import com.test.setting.R
import com.test.setting.databinding.FeedbackSuggestionsBinding

class FeedbackActivity:BaseActivity<FeedbackSuggestionsBinding>(R.layout.feedback_suggestions){
    @BindViewModel
    var model:SettingViewModel?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding?.let {
           it.vm = model
        }
    }
}
