package com.tools.network.callback

import java.util.*

interface IHttpParamProvider {
    val curRuntimeEvn: Boolean
    var loanUrl: String

    companion object {
        @JvmStatic
        var instance: IHttpParamProvider? = null
    }

    fun addCommonHttpParam(map: TreeMap<String, String>)

    fun addSigParam(map: TreeMap<String, String>):String

    fun getKey():String

    fun getToken():String

    fun getUserId():String
    fun getUserIdKey():String



}