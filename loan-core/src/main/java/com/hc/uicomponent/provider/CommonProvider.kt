package com.hc.uicomponent.provider

interface CommonProvider {
    companion object {
        var instance: CommonProvider? = null
    }
    fun getWebViewNavId():Int
}