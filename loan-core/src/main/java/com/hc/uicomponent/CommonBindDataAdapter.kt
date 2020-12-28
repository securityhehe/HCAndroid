package com.hc.uicomponent

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.databinding.BindingAdapter
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.hc.uicomponent.config.Constants
import com.hc.uicomponent.provider.CommonProvider
import frame.utils.ConverterUtil
import frame.utils.RegularUtil

object CommonBindDataAdapter {

    @SuppressLint("ClickableViewAccessibility")
    @BindingAdapter("touchOther")
    @JvmStatic
    fun touchOther(view: View, onTouchListener: View.OnTouchListener?) {
        view.setOnTouchListener { view, evnet ->
            val a = onTouchListener?.onTouch(view, evnet)
            a ?: false
        }
    }

    @BindingAdapter(
        value = ["url", "title", "content", "linkText", "linkColor"],
        requireAll = false
    )
    @JvmStatic
    fun bindTextAndJumpWebViewRes(
        view: TextView
        , urlText: String
        , titleText: String
        , contentText: String
        , linkTextStr: String
        , linkColorInt: Int

    ) {
        val span = SpannableString(contentText)
        val linkStart = contentText.indexOf(linkTextStr)
        if (linkStart != -1) {
            val linkEnd = linkStart + linkTextStr.length
            span.setSpan(
                UrlClickSpan(urlText, titleText),
                linkStart,
                linkEnd,
                Spanned.SPAN_MARK_MARK
            )
            span.setSpan(
                ForegroundColorSpan(linkColorInt),
                linkStart,
                linkEnd,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            );
        }
        view.text = span
        view.movementMethod = LinkMovementMethod.getInstance()
    }


    @BindingAdapter(
        value = ["src", "defaultImage", "errorImage", "skipCache", "noAnimation", "sizeImgWidth", "sizeImgHeight"],
        requireAll = false
    )
    @JvmStatic
    open fun setImage(
        imageView: ImageView,
        path: String?,
        defaultImage: Drawable?,
        errorImage: Drawable?,
        skipCache: Boolean,
        noAnimation: Boolean,
        sizeImgWidth: Int?,
        sizeImgHeight: Int?
    ): Unit {
        val context = imageView.context

        if (TextUtils.isEmpty(path)) {
            // 加载默认图片
            if (defaultImage != null) {
                imageView.setImageDrawable(defaultImage)
            } else {
                imageView.setImageDrawable(errorImage)
            }
        } else {
            if (RegularUtil.isInteger(path)) {
                val opt = RequestOptions()
                errorImage ?: opt.error(errorImage)
                defaultImage ?: opt.placeholder(defaultImage)
                opt.skipMemoryCache(skipCache)
                if (sizeImgHeight != null && sizeImgWidth != null) {
                    opt.override(sizeImgHeight)
                }
                val transitionOptions = DrawableTransitionOptions().dontTransition()
                // 加载资源图片
                if (noAnimation) {
                    Glide.with(context)
                        .load(ConverterUtil.getInteger(path))
                        .apply(opt)
                        .thumbnail(0.1f)
                        .into(imageView)
                } else {
                    errorImage?.let {
                        Glide.with(context).load(ConverterUtil.getInteger(path))
                            .apply(opt)
                            .transition(transitionOptions)
                            .thumbnail(0.1f)
                            .into(imageView)
                    }
                }
            }
        }
    }

    @BindingAdapter(
        value = ["navId","url","title"],
        requireAll = false
    )
    @JvmStatic
    fun jumpPage(view:View,navId:Int,url:String?,title:String?){
        view.setOnClickListener{
            val opt = NavOptions.Builder()
                .setEnterAnim(R.anim.anim_right_to_middle)
                .setLaunchSingleTop(false)
                .setPopExitAnim(R.anim.anim_middle_to_right)
                .build()
            val bundle =  bundleOf(Pair("title",title),Pair("link",url))
            Navigation.findNavController(view).navigate(navId,bundle,opt)
        }
    }

    @BindingAdapter("colorState")
    @JvmStatic
    fun convertColor(view: TextView, state:Int) {
        val a = when(state){
            Constants.NUMBER_10 -> R.color.loan_info_color_f46524
            Constants.NUMBER_20 -> R.color.loan_info_color_2ee100
            Constants.NUMBER_30 -> R.color.loan_info_color_666666
            else -> R.color.loan_info_color_d800c8
        }

        view.setTextColor(ContextCompat.getColor(view.context,a))
    }

    @BindingAdapter("clearOnFocusAndDispatch")
    @JvmStatic
    fun clearOnFocusAndDispatch(view: EditText, listener: View.OnFocusChangeListener?) {
        view.onFocusChangeListener = View.OnFocusChangeListener { focusedView, hasFocus ->
            if (hasFocus) {

            } else {
                listener?.onFocusChange(focusedView, hasFocus)
            }
        }
    }

    @BindingAdapter("hideKeyboardOnInputDone")
    @JvmStatic fun hideKeyboardOnInputDone(view: EditText, enabled: Boolean) {
        if (!enabled) return
        val listener = TextView.OnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                view.clearFocus()
                val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
            }
            false
        }
        view.setOnEditorActionListener(listener)
    }


}

class UrlClickSpan constructor(var urlText: String, var titleText: String) : ClickableSpan() {
    override fun onClick(widget: View) {
        CommonProvider.instance?.getWebViewNavId()?.let {
            val bundle = bundleOf(Pair("title", titleText), Pair("link", urlText))
            Navigation.findNavController(widget).navigate(it, bundle)
        }
    }
}



