package com.hc.load.vm

import com.hc.data.common.CommonDataModel
import com.hc.data.utils.DeviceInfoUtil.getInstallAppInfo
import com.hc.uicomponent.config.Constants
import com.hc.uicomponent.provider.ContextProvider
import com.hc.uicomponent.stack.ActivityStack
import com.hc.uicomponent.utils.GsonUtils
import com.hc.uicomponent.utils.TextUtil
import com.hc.uicomponent.utils.mmkv
import frame.utils.DateUtil
import java.util.*
import kotlin.concurrent.thread

class CollectAppInfo {
    //处于已登录状态时,但<尚未标记过上传记录> 或者 <第二天后>, 需上传
    private var isCollectLoading = false //标识收集状态
    private var isCollectOnce = true     //控制在应用内仅仅执行收集一次的标记
    private var mCollectAppInfoList: String? = ""

    fun collectAppInfo() {
        if (CommonDataModel.mLoggedIn) {
            val isHasUploadAppList = mmkv().decodeBool(Constants.CUR_DAY_HAS_UPLOAD_APP_LIST, false)
            val curDate = DateUtil.getDate(Date())
            val lastDate = mmkv().decodeString(Constants.COLLECT_APP_LIST_FINISH_DATE, curDate)
            //有且仅当列表数据为空 或者 到了next一天的情况下，再次收集数据。
            if (!isHasUploadAppList || DateUtil.getBetweenDay(lastDate, curDate) > 0) {
                if (isCollectOnce) {
                    isCollectOnce = false
                    childThread2CollectAppData()
                }
            }
        }
    }

    private fun childThread2CollectAppData() {
        thread(true) {
            isCollectLoading = true//收集中
            ActivityStack.currentActivity()?.let {
                isCollectLoading = false//收集完成
                val info = getInstallAppInfo(ContextProvider.app, null)
                mCollectAppInfoList = GsonUtils.toJsonString(info)
                mCollectAppInfoList?.let {
                    mmkv().encode(Constants.COLLECT_APP_LIST_FINISH_DATE, DateUtil.getDate(Date()))
                }
                isCollectOnce = true //防止next一天无法重新统计APP列表数据
            }
        }
    }

    //是否已经收集完毕
    fun isHasCollectLoading() = isCollectLoading

    //获取收集的app信息
    fun getCollectAppInfo() = if (TextUtil.isEmpty(mCollectAppInfoList)) "" else mCollectAppInfoList

}