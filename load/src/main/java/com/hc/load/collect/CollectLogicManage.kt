package com.hc.load.collect


import com.hc.load.collect.channelCode.CollectOpenChannel
import com.hc.load.collect.sms.CollectSmsInfo
import com.hc.load.vm.CollectAppInfo
import com.hc.uicomponent.utils.LocationUtils

/**
 * 负责配置启动指定的非正流程的逻辑
 *
 *  manage.build()
 *        .addLocation()
 *        .addCollectAppList()
 *        .addCollectChannelCode()
 *
 *  注: add表示启用并调用业务逻辑!
 */
class CollectLogicManage {

    //////////////////////////////收集APP Info///////////////////////////////////
    val collectAppLogic : CollectAppInfo by lazy {
        CollectAppInfo()
    }
    //////////////////////////////收集APP SMS///////////////////////////////////
    fun collectSmsInfo() {
        CollectSmsInfo.collectSmsInfo()
    }

    //////////////////////////////收集APP OPEN CHANNEL//////////////////////////
    val collectOpenChannelLogic : CollectOpenChannel by lazy {
        CollectOpenChannel()
    }

    //////////////////////////////收集定位-经纬度信息//////////////////////////
    val locationUtils: LocationUtils by lazy {
        LocationUtils.getInstance()
    }

}