package com.hc.setting

import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.hc.uicomponent.base.BaseActivity
import com.test.setting.R
import com.test.setting.databinding.LoanSettingLayoutBinding
import kotlinx.android.synthetic.main.loan_setting_layout.*

class SettingActivity : BaseActivity<LoanSettingLayoutBinding>(R.layout.loan_setting_layout) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initItem()
    }

    private fun initItem() {
        val languageSetting = language_setting?.findViewById<TextView>(R.id.item)
        val checkVersion = check_version?.findViewById<TextView>(R.id.item)
        val feedback = feedback?.findViewById<TextView>(R.id.item)
        languageSetting?.setCompoundDrawablesRelativeWithIntrinsicBounds(R.mipmap.loan_setting_language,
            0,
            0,
            0
        )
        checkVersion?.setCompoundDrawablesRelativeWithIntrinsicBounds(
            R.mipmap.loan_setting_version,
            0,
            0,
           0
        )
        feedback?.setCompoundDrawablesRelativeWithIntrinsicBounds(
            R.mipmap.loan_setting_feedback,
            0,
            0,
           0
        )
        languageSetting?.setText(R.string.loan_setting_language_text)
        checkVersion?.setText(R.string.loan_setting_version_text)
        feedback?.setText(R.string.loan_setting_feedback_text)
        findViewById<View>(R.id.common_back).setOnClickListener{
            finish()
        }


    }
}