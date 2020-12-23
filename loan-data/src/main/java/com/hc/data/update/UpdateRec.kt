package com.hc.data.update

data class Update(
    var appType: String?,
    var latestVersion: String?,
    var minVersion: String,
    var downloadUrl: String?,
    var appSize: String?,
    var appDescribe: String?
)