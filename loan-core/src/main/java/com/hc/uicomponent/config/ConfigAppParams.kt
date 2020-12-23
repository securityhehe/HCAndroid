package com.hc.uicomponent.config

import com.hc.uicomponent.BuildConfig


/**
 * requst service key
 */
const val REQ_SERVICE_KEY = "F892EA6DCD5D2519AB637F0BFA674026"

/**
 * device info tongdun
 */
val TD_DEVICE_URL = if (BuildConfig.IS_APP_DEBUG) "https://idfptest.tongdun.net" else "https://idfp.tongdun.net"

/**
 * AppsFlyer
 */
const val APPS_FLYER_KEY = "e3W7igvGrwCvGUGnzE4eve"


/**
 * epoch appId & appSecret
 */
const val EPOCH_APP_ID =  "nAzItRntv1i3vU6"
const val EPOCH_SECRET =  "N6E61d17T556UhfFTioQu21CMFII5pZc"
const val EPOCH_POST_URL =  "https://sdk.epoch-api.com"