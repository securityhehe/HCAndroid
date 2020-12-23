package com.hc.permission

import android.annotation.SuppressLint
import android.app.Activity
import android.view.View
import androidx.databinding.BindingAdapter
import androidx.fragment.app.Fragment
object PermissionAdapter {

    //Manifest.permission.CAMERA,//
//Manifest.permission.WRITE_EXTERNAL_STORAGE,//
//Manifest.permission.READ_PHONE_STATE,//
//Manifest.permission.ACCESS_FINE_LOCATION,//
//Manifest.permission.READ_CONTACTS,//
//Manifest.permission.CALL_PHONE,//
//Manifest.permission.READ_SMS//
    @SuppressLint("ClickableViewAccessibility")
    @BindingAdapter(
        value = ["activity", "reqCode", "rationale", "isShowRationale", "permissionArray"],
        requireAll = false
    )
    @JvmStatic
    fun req(
        view: View
        , activity: Activity
        , reqCode: Int
        , rationale: String?
        , isShowRationale: Boolean? = true
        , permissionArray: Array<out String?>
    ) {

        view.setOnClickListener {
            val list = permissionArray.filterNotNull()
            val array = list.toTypedArray()
            AndroidPermissions.requestPermissions(
                activity,
                rationale ?: "",
                isShowRationale ?: true,
                reqCode,
                *array
            )
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @BindingAdapter(
        value = ["fragment", "reqCode", "rationale", "isShowRationale", "permissionArray"],
        requireAll = false
    )
    @JvmStatic
    fun req(
        view: View
        , activity: Fragment
        , reqCode: Int
        , rationale: String?
        , isShowRationale: Boolean? = true
        , permissionArray: Array<out String?>
    ) {

        view.setOnClickListener {
            println("req -> permission")
            val list = permissionArray.filterNotNull()
            val array = list.toTypedArray()
            AndroidPermissions.requestPermissions(
                activity,
                rationale ?: "",
                isShowRationale ?: true,
                reqCode,
                *array
            )
        }
    }
}