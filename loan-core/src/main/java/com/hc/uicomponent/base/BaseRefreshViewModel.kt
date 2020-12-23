package com.hc.uicomponent.base


open class BaseRefreshViewModel<T> : BaseViewModel() {
/**
    private var isResetReqHeader = false
    private var isRefreshViewModel = false

    private var mShowViewType: ShowViewType? = null

    private var isRefresh: RefreshLoadType = RefreshLoadType.DEFAULT

    //控制是否可以刷新
    var isRefreshFlag = ObservableBoolean(false)

    //控制是否可以加载更多
    var isLoadMoreFlag = ObservableBoolean(false)

    var retryLoadListener: PlaceholderLayout.OnReloadListener? = null



    private fun callResultUpdatePlaceHolderUI(callType: CallResultType, response: Response<T>? = null): Unit {
        val curAct = ActivityStack.currentActivity()
        if (curAct == null || curAct.isFinishing) return

        //复位刷新header和footer
        resetRefreshHeader(callType)
        //更新占位图
        when (callType) {
            CallResultType.ResponseSuccess -> {
                response!!.run {
                    if (isResetReqHeader) {
                        val showEmptyMap = onCallbackReqSuccess(response.body()!!) //数据刷新成功回调。
                        showEmptyMap.forEach {
                            val isShowEmpty = it.key
                            var pageState = it.value
                            pageState = if (isShowEmpty) {
                                if (pageState == -1) SUCCESS else pageState
                            } else {
                                SUCCESS
                            }
                            statusPlaceFlag.set(pageState)
                        }
                        //请求成功后,再去启用[刷新 || 加载更多]功能
                        if (isRefreshViewModel) {
                            isRefreshFlag.set(true)
                            isLoadMoreFlag.set(true)
                        }

                    }
                }
            }
            CallResultType.ResponseFail -> {
                //用于决定在没有数据情况下：页面保持状态
                if (isReqDataSucc) {
                    statusPlaceFlag.set(SUCCESS)
                } else {
                    statusPlaceFlag.set(if (onCallbackNetFailure()) PlaceholderLayout.PageState.ERROR else SUCCESS)
                }

            }
            CallResultType.FailureIOEX -> {
                //为了保证在请求异常情况下能够保持原来的界面状态，故不更新界面！
                //如果原来没有加载成功，则显示网络异常
                if (onCallbackNetFailure()) {
                    if (!NetworkUtils.isConnected()) {
                        if (isReqDataSucc) {
                            statusPlaceFlag.set(SUCCESS)
                        } else {
                            statusPlaceFlag.set(PlaceholderLayout.PageState.NO_NETWORK)
                        }
                    } else {
                        if (this.isReqDataSucc) {
                            statusPlaceFlag.set(SUCCESS)
                        } else {
                            statusPlaceFlag.set(PlaceholderLayout.PageState.ERROR)
                        }
                    }
                } else {
                    statusPlaceFlag.set(SUCCESS)
                }
            }
            CallResultType.FailureOther -> {
                if (isReqDataSucc) {
                    statusPlaceFlag.set(SUCCESS)
                } else {
                    statusPlaceFlag.set(PlaceholderLayout.PageState.ERROR)
                }
            }
        }
    }

    open fun onCallbackReqSuccess(result: T?): Map<Boolean, Int> {// 这个方法用于控制加载成功的界面显示状态逻辑
        return mapOf(true to -1)
    }

    open fun onCallbackNetFailure(): Boolean {
        return true
    }


    private fun resetRefreshHeader(showViewType: CallResultType) {
        if (!isRefreshViewModel) {
            return
        }
        when (mShowViewType) {
            ShowViewType.PlaceViewRefresh -> {
                val condition1 = (this.isResetReqHeader)
                val condition2 = getStopCondition(showViewType)
                if (condition1 || condition2) {
                    when (isRefresh) {
                        RefreshLoadType.StopRefresh -> {
                            stopRefreshData()
                        }
                        RefreshLoadType.StopLoadMore -> {
                            stopLoadMoreData()
                        }
                    }
                }
            }
        }
    }

    private fun getStopCondition(showViewType: CallResultType): Boolean {
        return (CallResultType.ResponseFail == showViewType
                || CallResultType.FailureIOEX == showViewType
                || CallResultType.FailureOther == showViewType
                && !this.isResetReqHeader)
    }

    open fun stopLoadMoreData() {

    }

    open fun stopRefreshData() {

    }

*/
}
