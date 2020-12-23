object AppVersion {
    const val appVersionName = "01.0001"
    const val appVersionCode = 100
}

object Version {
    //platform version
    const val target_sdk = 29
    const val min_sdk = 21
    const val compile_sdk = 29
    const val build_tools = "29.0.0"
    const val gradle = "3.6.0"
    const val kotlin = "1.3.72"
    const val anko = "0.10.0"
    const val apt = "1.8"
    const val detekt = "1.0.0"
    const val kotlin_ext = "1.2.0";
}

object LibVersion {
    const val af: String = "5.0.0"
    const val zxing: String = "3.3.0"
    const val speech = "0.48.0-alpha"

    //LibVersion
    const val multidex = "2.0.1"
    const val appcompat = "1.1.0"
    const val recyclerview = "1.1.0"
    const val play_service = "16.0.1"

    const val facebook = "5.11.0"
    const val room = "2.2.5"
    const val glide = "4.6.1"
    const val fragment = "1.2.5"
    const val lifecycle = "2.2.0"
    const val constraint_layout = "1.1.3"
    const val cardview = "1.0.0"
    const val material = "1.1.0"
    const val parcelable = "1.1.0"

    const val firebase_ad = "17.2.1"
    const val firebase_messaging = "17.3.3"

    const val google_api = "1.22.0"
    const val google_server = "4.3.3"
    const val google_server_people = "v1-rev139-1.22.0"
}

object Config {
    const val isDebug = true
    //config version
    const val versionCode=2
    const val versionName="1.0.0"
    //config channel
    const val channelName="Google_Play_Loan" //用于直接连接手机安装下使用此渠道名称，其他情况使用多渠道打包工具生成的release包

    //config local test env params
    const val isTestEnv = false
    const val isTestURL = "http://172.16.20.161:8080/"
    //config is private package for app opening amount
    const val isPrivatePackage = false
    const val mmkvpasswd = "hc_mmkv";
}
