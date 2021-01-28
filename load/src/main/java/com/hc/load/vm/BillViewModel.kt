package com.hc.load.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.hc.data.order.OrderBillRec
import com.hc.load.net.LoanInfoService
import com.hc.uicomponent.base.BaseViewModel
import com.hc.uicomponent.call.reqApi
import kotlinx.coroutines.launch

class BillViewModel : BaseViewModel() {

    var mOrderBillRec = MutableLiveData<List<OrderBillRec>?>()

    fun showBillList(orderId:String) {
        viewModelScope.launch {
            val reqResult = reqApi(LoanInfoService::class.java,{ getBillList(orderId) })
            reqResult.data?.run {
               mOrderBillRec.value = this
            }
        }
    }
}