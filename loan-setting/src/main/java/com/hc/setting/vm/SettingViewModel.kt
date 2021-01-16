package com.hc.setting.vm

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableField
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.viewModelScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import com.blankj.utilcode.util.ToastUtils
import com.google.gson.Gson
import com.hc.data.common.CommonDataModel
import com.hc.data.update.Update
import com.hc.setting.CommonService
import com.hc.setting.DeviceInfoUtil
import com.hc.setting.FeedbackActivity
import com.hc.uicomponent.BuildConfig
import com.hc.uicomponent.base.BaseViewModel
import com.hc.uicomponent.call.reqApi
import com.hc.uicomponent.config.Constants
import com.hc.uicomponent.provider.ContextProvider
import com.hc.uicomponent.stack.ActivityStack
import com.hc.uicomponent.utils.DialogUtils
import com.hc.uicomponent.utils.DialogUtils.createBaseDialogBind
import com.hc.uicomponent.utils.FirseBaseEventUtils
import com.hc.uicomponent.utils.StatEventTypeName
import com.hc.uicomponent.utils.TextUtil
import com.test.setting.R
import com.test.setting.databinding.DialogUpdateLayoutBinding
import com.tools.network.callback.AppResultCode
import frame.utils.ChannelUtil
import frame.utils.ConverterUtil
import kotlinx.coroutines.launch
import org.json.JSONObject
import kotlin.reflect.KParameter

class SettingViewModel : BaseViewModel() {

    private fun jump2UpdateApp(context: Context, downloadUrl: String?) {
        val intent = Intent(Intent.ACTION_VIEW)
        if (TextUtil.isEmpty(downloadUrl)) {
            intent.data = Uri.parse("market://details?id=" + context.packageName)
            if (intent.resolveActivity(context.packageManager) != null) {
                context.startActivity(intent)
            } else { //No app marketplace, we will jump to Google Play through a browser
                intent.data = Uri.parse("https://play.google.com/store/apps/details?id=" + context.packageName)
                if (intent.resolveActivity(context.packageManager) != null) { //有浏览器
                    context.startActivity(intent)
                }
            }
        } else {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.data = Uri.parse(downloadUrl)
            if (intent.resolveActivity(context.packageManager) != null) {
                context.startActivity(intent)
            }
        }
    }


    private fun checkAppUpdate(context: Context, isAutoUpdate: Boolean) {
        viewModelScope.launch {
            val a = reqApi(CommonService::class.java, { checkUpdate() })
            println("update:${a.data.toString()}")
            var updateRec: Update? = null
            try {
                val jsonObject = JSONObject(a.data)
                if (!jsonObject.has(ChannelUtil.getChannel(ContextProvider.app, BuildConfig.CHANNEL_NAME))) {
                    if (!isAutoUpdate) ToastUtils.showShort(ContextProvider.getString(R.string.version_channel_no_match))
                    return@launch;
                }
                val jsonArray = jsonObject.getJSONArray(ChannelUtil.getChannel(ContextProvider.app, BuildConfig.CHANNEL_NAME))
                for (i in 0 until jsonArray.length()) {
                    val o = jsonArray.getJSONObject(i)
                    val android = o.getJSONObject("android") ?: continue
                    updateRec = Gson().fromJson(android.toString(), Update::class.java)
                    break
                }

                updateRec?.let {
                    val isUpdateState: Int = isNeedUpdate(updateRec.minVersion, updateRec.latestVersion)

                    if (isUpdateState == Constants.NUMBER_0) {
                        ToastUtils.showShort(R.string.version_last_version)
                    } else {
                        val bindView = createViewDataBinding(context, it.latestVersion, it.appDescribe, (isUpdateState == Constants.NUMBER_1), it.downloadUrl)
                        DialogUtils.showTips(context, bindView)
                    }
                }
                if (updateRec != null) {

                } else {
                    if (!isAutoUpdate) ToastUtils.showShort(ContextProvider.getString(R.string.version_channel_no_match))
                }
            } catch (e: Exception) {
            }
        }
    }


    private fun isNeedUpdate(minVersion: String?, lastVersion: String?): Int {
        if (minVersion == null || lastVersion == null) {
            return 0
        }
        val updateLength = 3
        val local = DeviceInfoUtil.getVersionName(ContextProvider.app).replace("[^\\d.]+", "").split("\\.")
        val remoteMin = minVersion.replace("[^\\d.]+".toRegex(), "").split("\\.".toRegex()).toTypedArray()
        val remoteMax = lastVersion.replace("[^\\d.]+".toRegex(), "").split("\\.".toRegex()).toTypedArray()
        var isForceUpdate = false
        if (local.size == updateLength && remoteMin.size == updateLength && remoteMax.size == updateLength) {
            for (i in 0 until updateLength) {
                if (!isForceUpdate && ConverterUtil.getInteger(local[i]) < ConverterUtil.getInteger(remoteMin[i])) { //force update
                    return 1 //force update
                } else {
                    if (ConverterUtil.getInteger(local[i]) > ConverterUtil.getInteger(remoteMin[i])) { //> minVersion
                        isForceUpdate = true
                    }
                    if (ConverterUtil.getInteger(local[i]) > ConverterUtil.getInteger(remoteMax[i])) { //>maxVersion
                        return 0 //no update
                    } else {
                        if (ConverterUtil.getInteger(local[i]) < ConverterUtil.getInteger(remoteMax[i])) { //<maxVersion
                            return 2 // no force update
                        }
                    }
                }
            }
        }
        return 0
    }

    //create upgrade dialog
    private fun createViewDataBinding(context: Context, lastVersion: String?, des: String?, isForce: Boolean, downloadUrl: String?): ViewDataBinding {
        val tipsView = DataBindingUtil.inflate<DialogUpdateLayoutBinding>(LayoutInflater.from(context), R.layout.dialog_update_layout, null, false)
        FirseBaseEventUtils.trackEvent(StatEventTypeName.APP_UPGRADE)
        tipsView.content = des
        tipsView.lastVersion = lastVersion
        tipsView.isShowCancelBtn = !isForce
        tipsView.cancel.setOnClickListener {
            tipsView.root.tag?.apply {
                if (this is DialogFragment) {
                    this.dismissAllowingStateLoss()
                }
            }
        }
        tipsView.upgrade.setOnClickListener {
            FirseBaseEventUtils.trackEvent(StatEventTypeName.APP_UPGRADE, null)
            jump2UpdateApp(it.context, downloadUrl)
        }
        return tipsView
    }

    var message = ObservableField<String>()

    fun commitFeedBack(view: View) {
        val opinion = message.get()
        if (TextUtil.isEmpty(opinion)) {
            ToastUtils.showShort(view.resources.getString(R.string.feedback_content))
        } else {
            viewModelScope.launch {
                var result = reqApi(CommonService::class.java, { adviceFeedBack(opinion) })
                ToastUtils.showShort(result.msg)
            }
        }
    }

    fun doAction(fm: Fragment, idIndex: Int) {
        val context = fm.requireContext()
        when (idIndex) {
            0 -> {
                checkAppUpdate(context, false)
            }
            1 -> {
                val intent = Intent(context, FeedbackActivity::class.java);
                context.startActivity(intent);
            }
            2 -> {
                val a = createBaseDialogBind(context, context.resources.getString(R.string.logout_tip), true) {
                    ContextProvider.mNavIdProvider?.getToLogInActionId()?.let { target ->
                        ActivityStack.currentActivity()?.let {
                            CommonDataModel.clearUser(fm.requireContext())
                            NavHostFragment.findNavController(fm).navigate(target)
                        }
                    }
                }
                a?.let {
                    DialogUtils.showCenterTips(context, it)
                }

            }
        }
    }
}