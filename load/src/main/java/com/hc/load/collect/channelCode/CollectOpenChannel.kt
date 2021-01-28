package com.hc.load.collect.channelCode


import com.android.installreferrer.api.InstallReferrerClient
import com.android.installreferrer.api.InstallReferrerStateListener
import com.hc.load.BuildConfig
import com.hc.load.net.LoanInfoService
import com.hc.uicomponent.call.reqApi
import com.hc.uicomponent.config.Constants
import com.hc.uicomponent.provider.ContextProvider
import com.hc.uicomponent.utils.TextUtil
import com.hc.uicomponent.utils.mmkv
import frame.utils.DeviceUtil
import frame.utils.NetWorkGetMacUtils

/**
 * 统计用户的渠道来源
 */
class CollectOpenChannel {

    /**********************************  Play Install Referrer Library start *****************************/

    private var isHasUploadOpenAmount = mmkv().decodeBool(Constants.IS_HAS_STAT_OPEN_AMOUNT, false)

    /**
     * 上传统计渠道码
     */
    fun uploadChannelCode() {
        //保证在没有上传成功过   &&  要在连接回调后继续执行逻辑
        if (!isHasUploadOpenAmount && isHasConnectedReferrered) {
            //上传GP统计渠道
            if (!TextUtil.isEmpty(getFromGpUtmSource)) {
                statOpenAmount(getFromGpUtmSource!!)
                return
            }
            //TODO 这个地方我后期增加的
            else {
                statOpenAmount(BuildConfig.CHANNEL_NAME)
            }
            //获取Firebase-link并统计打开渠道信息
            //uploadOpenAmountByFirebaseLink()
        }
    }

    /**
     * 上传GP渠道码
     */
    private fun statOpenAmount(channelName: String) {
        //在没有上传成功过GP渠道
        if (!isHasUploadOpenAmount) {
            println("---- [gp] ---- open amount--$channelName")
            reqApi(serviceClass = LoanInfoService::class.java,
                block = { getOpenAmount(channelName, DeviceUtil.getDeviceId(ContextProvider.app), NetWorkGetMacUtils.getMacAddress(ContextProvider.app)) },
                successCallback = {
                    println("---- [gp] ---- open amount success--$channelName")
                    isHasUploadOpenAmount = true //记录不要再上传了
                    mmkv().encode(Constants.IS_HAS_STAT_OPEN_AMOUNT, true)
                },
                apiFailure = {
                    isHasUploadOpenAmount = false
                    false
                },
                ioExCallback = {
                    isHasUploadOpenAmount = false
                    false
                }

            )
        }
    }

    private var isHasConnectedReferrered = false//是否已经连接完毕了
    private var mReferrerClient: InstallReferrerClient? = null

    /**
     * 与谷歌商店建立连接
     */
    fun connectGooglePlayRefrerrer() {
        if (!isHasUploadOpenAmount) {
            mReferrerClient = InstallReferrerClient.newBuilder(ContextProvider.app).build()
            mReferrerClient!!.startConnection(installReferrerStateListener)
        }
    }

    /**
     * 注册InstallReferrer监听器
     */
    private val installReferrerStateListener = object : InstallReferrerStateListener {
        override fun onInstallReferrerSetupFinished(responseCode: Int) {
            when (responseCode) {
                InstallReferrerClient.InstallReferrerResponse.OK -> {//Connection established
                    //获取GP渠道信息
                    getGpChannelMessage()

                    isHasConnectedReferrered = true
                    uploadChannelCode()
                }
                else -> {
                    isHasConnectedReferrered = true
                    uploadChannelCode()
                }
            }
        }

        override fun onInstallReferrerServiceDisconnected() {
            isHasConnectedReferrered = true
            uploadChannelCode()
        }
    }

    /**
     * Google Play获取安装来源数据
     */
    private var getFromGpUtmSource: String? = null
    private fun getGpChannelMessage() {
        try {
            mReferrerClient?.run {
                val response = this.installReferrer
                var installReferrer = response.installReferrer
                installReferrer?.run {
                    for (data in this.split("&".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()) {
                        val split = data.split("=".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                        if (split.size == 2) {
                            if ("utm_source" == split[0]) {
                                if (!TextUtil.isEmpty(split[1])) {
                                    getFromGpUtmSource = if (split[1] == "google-play") {
                                        BuildConfig.CHANNEL_NAME
                                    } else {
                                        split[1]
                                    }
                                    mmkv().encode(Constants.STATE_OPEN_CHANNEL, getFromGpUtmSource)
                                }
                                return
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
        }
    }

    fun disconnectGooglePlayRefrerrer() {
        if (mReferrerClient != null && mReferrerClient!!.isReady) {
            println("----over---end--")
            mReferrerClient!!.endConnection()
        }
    }
    /**********************************  Play Install Referrer Library end *****************************/

}