package com.hc.login.bind

import android.annotation.SuppressLint
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.hc.login.R
import com.hc.login.generated.callback.OnClickListener

object LoginModelBindAdapter {

    @BindingAdapter(value = ["loginModelCode", "loginModelNumber"], requireAll = true)
    @JvmStatic
    fun bindTv(tv: TextView, phoneCode: String, phoneNum: String) {
        tv.text = String.format(
            tv.context.getString(R.string.login_phone_hint_msg),
            phoneCode,
            phoneNum
        )
    }
}