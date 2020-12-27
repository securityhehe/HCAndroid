package com.hc.uicomponent.utils
import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Rect
import android.net.Uri
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.provider.ContactsContract
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.*
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.hc.uicomponent.provider.ContextProvider

/**
 * 字符串中空格去掉
 */
fun removeBlank(str:String): String {
    return str.trim { it <= ' ' }.replace(" ", "")
}



fun EditText.addTextListener(block:(String)->Unit): Unit {
    var et = this
    this.addTextChangedListener(object :TextWatcher{
        override fun afterTextChanged(p0: Editable?) {
        }

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            block(et.text.toString())
        }

    })
}

fun Activity.showToast(msg:String): Unit {
    Toast.makeText(this,msg, Toast.LENGTH_SHORT).show()
}


fun FragmentActivity.addAndShowHideFragment(containerId: Int, descFrag: Fragment, hideFrag: ArrayList<Fragment>) {
    if (descFrag != null) {
        var isAddFrag = this.supportFragmentManager.findFragmentByTag(descFrag.javaClass.simpleName)
        if (isAddFrag == null) {
            val transaction = this.supportFragmentManager.beginTransaction()
            transaction.add(containerId, descFrag, descFrag.javaClass.simpleName)
            for (hide in hideFrag) {
                transaction.hide(hide)
            }
            transaction.show(descFrag)
            transaction.commitAllowingStateLoss()
        }
    }
}

/** 控制ScrollView包含EditText的布局与软键盘之间的处理关系 start**/
fun listener(act: FragmentActivity, scrollView: ScrollView, marginTop: Int): ViewTreeObserver.OnGlobalLayoutListener {
    return ViewTreeObserver.OnGlobalLayoutListener {
        val r = Rect()
        //获取当前界面可视部分
        act.window.decorView.getWindowVisibleDisplayFrame(r)
        //获取屏幕的高度
        val screenHeight = act.window.decorView.rootView.height
        //此处就是用来获取键盘的高度的， 在键盘没有弹出的时候 此高度为0 键盘弹出的时候为一个正数
        val heightDifference = screenHeight - r.bottom

        scrollView.viewTreeObserver.removeOnGlobalLayoutListener(listener)

        //软键盘弹出来了
        if (heightDifference > 0) {
            val location = IntArray(2)
            scrollView.getLocationOnScreen(location)

            var params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, location[1])
            params.topMargin = marginTop
            scrollView.layoutParams = params
        } else {
            var params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            params.topMargin = marginTop
            scrollView.layoutParams = params
        }

        var msg = Message.obtain()
        msg.obj = scrollView
        msg.what = 0
        myHandler.sendMessage(msg)
    }
}

var listener: ViewTreeObserver.OnGlobalLayoutListener? = null

private var myHandler: Handler = object : Handler(Looper.getMainLooper()) {
    override fun handleMessage(msg: Message) {
        msg.obj?.run {
            var scrollView = this as ScrollView
            scrollView.viewTreeObserver.addOnGlobalLayoutListener(listener)
        }
    }
}

/** 控制ScrollView包含EditText的布局与软键盘之间的处理关系 end **/


fun EditText.addFocusTextListener(block:()->Unit) {
    setOnFocusChangeListener { v,hasFocus ->
        if (hasFocus) block()
    }
}


/**
 * 判断Activity是否Destroy
 */
fun isDestroyForAct(activity: AppCompatActivity) :Boolean {
    return activity.isFinishing ||  activity.isDestroyed
}

fun TextView.txt(): String {
    return this.text.toString()
}

/**
 * 如果字符串为null，则返回“”,否则：返回值本身
 */
fun isNull2Str(str:String?): String{
    if (str == null) {
        return ""
    }
    return str
}

fun <T:View>View.getViewById(viewId:Int): T {
    return this.findViewById<T>(viewId)
}

fun View.OnClickListener.addClickListener(vararg view:View): Unit {
    view.forEach {
        it.setOnClickListener(this)
    }
}

fun addClickListener(vararg view:View,clickLogic:(View)->Unit): Unit {
    view.forEach { it ->
        it.setOnClickListener {
            clickLogic(it)
        }
    }
}

/**
 * 跳转到浏览器
 */
fun FragmentActivity.jump2BrowserLoadUrl(url:String, isFinish:Boolean = false): Unit {
    val intent = Intent(Intent.ACTION_VIEW)
    intent.addCategory(Intent.CATEGORY_BROWSABLE)
    intent.data = Uri.parse(url)
    this.startActivity(intent)
    if (isFinish)this.finish()
}


/**
 * open contact 2 chose contact
 */
fun FragmentActivity.startNativeContact(requestCode: Int) {
    val intent = Intent()
    intent.action = Intent.ACTION_PICK
    intent.data = ContactsContract.Contacts.CONTENT_URI
    startActivityForResult(intent, requestCode)
}

fun View.setStatusMarginTop(marginTop:Int): Unit {
    (this.layoutParams as ViewGroup.MarginLayoutParams).run {
        topMargin = getStatusBarHeight() + ScreenAdapterUtils.dp2px(context,marginTop)
    }
}

fun View.setStatusHeight(): Unit {
    this.layoutParams = (this.layoutParams as ViewGroup.MarginLayoutParams).also {
        it.height = getStatusBarHeight()
    }
}


/**
 * 获取TextView的文本
 */
fun TextView.toTxt() = this.text.toString()

/**
 * 状态栏属性标识
 */
private const val STATUS_BAR_HEIGHT_RES_NAME = "status_bar_height"

/**
 * 计算状态栏高度高度
 * getStatusBarHeight
 */
fun getStatusBarHeight(): Int {
    return getInternalDimensionSize(Resources.getSystem(), STATUS_BAR_HEIGHT_RES_NAME)
}

private fun getInternalDimensionSize(res: Resources, key: String): Int {
    var result = 0
    val resourceId = res.getIdentifier(key, "dimen", "android")
    if (resourceId > 0) {
        result = res.getDimensionPixelSize(resourceId)
    }
    return result
}

/**
 * translate view from x1 2 x2
 * @param transView
 * @param translate
 */
fun translate(transView: View?, vararg translate: Float) {
    val tranlate = ObjectAnimator.ofFloat(transView, "translationX", *translate)
    tranlate.duration = 200
    tranlate.interpolator = AccelerateDecelerateInterpolator()
    tranlate.start()
}


/**
 * 获取SD卡的根目录
 */
fun getSDPath(): String? {
    var sdDir: String? = null
    // 判断sd卡是否存在
    val sdCardExist = Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
    if (sdCardExist) {
        sdDir = Environment.getExternalStorageDirectory().absolutePath
    }
    return sdDir?:""
}


/**
 * 判断Activity是否Destroy
 */
fun isDestroyForAct(activity: FragmentActivity) :Boolean {
    return activity.isFinishing ||  activity.isDestroyed
}

fun arouteWithBundleParams(activity:FragmentActivity,intentData:Intent? = null,resultCode:Int = -1) {
    if (intentData != null){
        activity.setResult(resultCode,intentData)
    }
    activity.finish()
}

fun <T,B: ViewDataBinding> dynamicAddChildView(parentGroup: LinearLayout, @LayoutRes layoutId:Int, datas:List<T>, block:(B, Int, T)->Unit) {
    parentGroup.removeAllViews()
    parentGroup.orientation = LinearLayout.VERTICAL
    if (datas.isNotEmpty()) {
        for (i in datas.indices) {
            val binding = DataBindingUtil.inflate<B>(
                LayoutInflater.from(ContextProvider.app),
                layoutId,
                null,//TODO 这个地方不要写 parentGroup ,报错：The specified child already has a parent. You must call removeView() on the child's parent first.
                false
            )
            block(binding,i+1,datas[i])
            //addView
            parentGroup.addView(binding.root)
        }
    }
}

