
package com.hc.accountinfo.vm.viewoperator

import android.content.Context
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import androidx.databinding.InverseMethod
import android.view.View
import android.widget.EditText
import android.widget.TextView
import com.hc.data.user.UserType


/**
 * The [EditText] that controls the number of sets is using two-way Data Binding. Applying a
 * 2-way expression to the `android:text` attribute of the EditText triggers an update on every
 * keystroke. This is an alternative implementation that uses a [View.OnFocusChangeListener]
 * instead.
 *
 * `numberOfSetsAttrChanged` creates a listener that triggers when the focus is lost
 *
 * `hideKeyboardOnInputDone` (in a different file) will clear focus when the `Done` action on
 * the keyboard is dispatched, triggering `numberOfSetsAttrChanged`.
 */
object MenuDataConvertAdapters {

    /**
     * Needs to be used with [NumberOfSetsConverters.setArrayToString].
     */
    @BindingAdapter("menuValue")
    @JvmStatic fun setMenuValue(view: TextView, value: String) {
        view.setText(value)
    }

    /**
     * Called when the [InverseBindingListener] of the `numberOfSetsAttrChanged` binding adapter
     * is notified of a change.
     *
     * Used with the inverse method of [NumberOfSetsConverters.setArrayToString], which is
     * [NumberOfSetsConverters.stringToSetArray].
     */
    @InverseBindingAdapter(attribute = "menuValue")
    @JvmStatic fun getMenuValue(editText: TextView): String {
        return editText.text.toString()
    }

    /**
     * That this Binding Adapter is not defined in the XML. "AttrChanged" is a special
     * suffix that lets you manage changes in the value, using two-way Data Binding.
     *
     * Note that setting a [View.OnFocusChangeListener] overrides other listeners that might be set
     * with `android:onFocusChangeListener`. Consider supporting both in the same binding adapter
     * with `requireAll = false`. See [android.databinding.adapters.CompoundButtonBindingAdapter]
     * for an example.
     */
    @BindingAdapter("menuValuesAttrChanged")
    @JvmStatic fun setListener(view: EditText, listener: InverseBindingListener?) {
        view.onFocusChangeListener = View.OnFocusChangeListener { focusedView, hasFocus ->
            val textView = focusedView as TextView
            if (hasFocus) {
                // Delete contents of the EditText if the focus entered.
                textView.text = ""
            } else {
                // If the focus left, update the listener
                listener?.onChange()
            }
        }
    }
}

/**
 * Converters for the number of sets attribute.
 */
object NumberOfSetsConverters {

    /**
     * Used with `numberOfSets` to convert from array to String.
     */
    @InverseMethod("stringToInt")
    @JvmStatic fun intToString(context: Context, userType: UserType): String {
        return userType.info

    }

    /**
     * This is the Inverse Method used in `numberOfSets`, to convert from String to array.
     *
     * Note that Context is passed
     */
    @JvmStatic fun stringToInt(unused: Context,  userType: UserType): Int  {
       return userType.state.toInt()
    }
}
