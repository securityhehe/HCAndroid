package com.hc.load.view

import android.content.Context
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout

import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.hc.load.R
import com.hc.uicomponent.utils.EditTextFormat
import com.hc.uicomponent.utils.ScreenAdapterUtils
import com.hc.uicomponent.utils.TextUtil

/**
 * @Author : ZhouWei
 * @TIME : 2020/3/2 12:27
 * @DESC : 编辑框 & 删除键
 */
class EditDelLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : LinearLayout(context, attrs) {

    private lateinit var editTextContent: AppCompatEditText
    private val editHint: String?
    private val maxLen: Int
    private val isOnlyNumber: Boolean
    private val isAddBlank: Boolean
    private val addBlankNum: Int

    private var clearBtn: ImageButton? = null

    val isInputEmpty: Boolean
        get() = TextUtil.isEmpty(editTextContent.text!!.toString())

    var inputContent: String
        get() = editTextContent.text!!.toString()
        set(content) = editTextContent.setText(content)

    init {
        val typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.EditViewDelLayout)
        editHint = typedArray.getString(R.styleable.EditViewDelLayout_edit_hint)
        maxLen = typedArray.getInteger(R.styleable.EditViewDelLayout_edit_max_len, Integer.MAX_VALUE)
        isOnlyNumber = typedArray.getBoolean(R.styleable.EditViewDelLayout_edit_only_number, false)
        isAddBlank = typedArray.getBoolean(R.styleable.EditViewDelLayout_edit_add_blank, false)
        addBlankNum = typedArray.getInt(R.styleable.EditViewDelLayout_edit_add_blank_num, 4)

        typedArray.recycle()

        initView(context)
    }

    private fun initView(context: Context) {
        orientation = LinearLayout.VERTICAL

        val inflateView = LayoutInflater.from(context).inflate(R.layout.layout_input_ed_clear, null)
        editTextContent = inflateView.findViewById(R.id.in_edit_content)
        editTextContent.hint = editHint
        editTextContent.setHintTextColor(ContextCompat.getColor(context, R.color.C_999999))
        editTextContent.setTextColor(ContextCompat.getColor(context, R.color.C_333333))
        editTextContent.textSize = 16f
        editTextContent.setSingleLine()
        editTextContent.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(maxLen))
        //不能输入表情
        EditTextFormat.addFilter(editTextContent, EditTextFormat.getEmojiExcludeFilter())

        if (isOnlyNumber) {
//            EditTextFormat.addFilter(editTextContent, EditTextFormat.getNumberFilter())
            editTextContent.inputType = InputType.TYPE_CLASS_NUMBER
            if (isAddBlank) {
                editTextContent.addBlankTextListener(false,spacesNum = addBlankNum)
            }
        }

        editTextContent.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                clearBtn!!.visibility = if (editTextContent.text == null || TextUtil.isEmpty(editTextContent.text!!.toString())) View.GONE else View.VISIBLE
            }

            override fun afterTextChanged(s: Editable) {

            }
        })

        clearBtn = inflateView.findViewById(R.id.in_edit_content_clear)
        clearBtn!!.setOnClickListener { v -> editTextContent.setText("") }

        val layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ScreenAdapterUtils.dp2px(getContext(), 40))
        addView(inflateView, layoutParams)
    }

    fun getEditText(): AppCompatEditText {
        return editTextContent
    }
}

/**
 * 控制EditText每隔指定位数加上空格分隔
 */
fun EditText.addBlankTextListener(isOnlyNeedTxt:Boolean, spacesNum:Int = 4, block:((String)->Unit)?= null): Unit {
    this.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            if (isOnlyNeedTxt && block != null) block(this@addBlankTextListener.text.toString())
        }

        override fun afterTextChanged(s: Editable) {
            if (isOnlyNeedTxt) return
            val str = s.toString().trim { it <= ' ' }.replace(" ", "")
            var result = ""
            if (str.length >= spacesNum) {
                this@addBlankTextListener.removeTextChangedListener(this)
                for (i in 0 until str.length) {
                    result += str[i]
                    if ((i + 1) % spacesNum == 0) {
                        result += " "
                    }
                }
                if (result.endsWith(" ")) {
                    result = result.substring(0, result.length - 1)
                }
                this@addBlankTextListener.setText(result)
                this@addBlankTextListener.addTextChangedListener(this)
                this@addBlankTextListener.setSelection(this@addBlankTextListener.text.length)//焦点到输入框最后位置

                if (block != null) {
                    block(removeBlank(result))
                }
            }else{
                if (block != null) {
                    block(removeBlank(str))
                }
            }

        }
    })
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


fun removeBlank(str:String): String {
    return str.trim { it <= ' ' }.replace(" ", "")
}

