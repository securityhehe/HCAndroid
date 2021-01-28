package com.hc.load


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.hc.data.order.OrderBillRec
import com.hc.load.databinding.FragmentLoanBillDetailBinding
import com.hc.load.databinding.ItemOrderBillMultiplePeriodBinding
import com.hc.load.vm.BillViewModel
import com.hc.uicomponent.annotation.BindViewModel
import com.hc.uicomponent.base.BaseFragment
import com.hc.uicomponent.config.Constants
import com.hc.uicomponent.provider.ContextProvider
import com.hc.uicomponent.utils.dynamicAddChildView
import com.hc.uicomponent.utils.txt
import frame.utils.StringFormat
import kotlinx.android.synthetic.main.fragment_loan_bill_detail.*
import kotlinx.android.synthetic.main.fragment_loan_input_money_layout.*

class BillDetailFragment : BaseFragment<FragmentLoanBillDetailBinding>(R.layout.fragment_loan_bill_detail) {

    @BindViewModel
    var billViewModel: BillViewModel? = null
    var isMultiOrder: Boolean = false
    var orderId: String = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            orderId = it.getString(Constants.ORDER_NUM, "")
            isMultiOrder = it.getBoolean(Constants.STATE)
        }
        billViewModel?.apply {
            mOrderBillRec.observe(viewLifecycleOwner, Observer {
                reqBillAndUpdateUI(it)
            })
            showBillList(orderId)
        }
    }

    private fun reqBillAndUpdateUI(orderList: List<OrderBillRec>?) {
        orderList?.let { list ->
            if (list.isNotEmpty()) {
                mFragmentBinding.run {
                    val orderBillRec = list[0]
                    on.value.text = orderBillRec.orderNo
                    val requireContext = requireContext()
                    la.value.text = StringFormat.showMoneyWithSymbol(requireContext, orderBillRec.loanAmount)
                    ld.value.text = String.format(getString(R.string.pay_order_loan_dur_day), orderBillRec.loanDuration)
                    tsf.value.text = StringFormat.showMoneyWithSymbol(requireContext, orderBillRec.technicalServiceFee)
                    gst.value.text = StringFormat.showMoneyWithSymbol(requireContext, orderBillRec.gst)
                    cp.value.text = StringFormat.showMoneyWithSymbol(requireContext, orderBillRec.commissionPayment)
                    ayg.value.text = StringFormat.showMoneyWithSymbol(requireContext, orderBillRec.amountYouGet)
                    tvInterest.text = StringFormat.showMoneyWithSymbol(requireContext, orderBillRec.interest)
                    var totalAmount: Double = 0.00
                    list.forEach {
                        totalAmount += it.repaymentAmount
                    }
                    repaymentMoney.text = StringFormat.showMoneyWithSymbol(requireContext, "$repaymentMoney")
                }
                val layoutId = R.layout.item_order_bill_multiple_period
                dynamicAddChildView<OrderBillRec, ItemOrderBillMultiplePeriodBinding>(mFragmentBinding.ll, layoutId, list) { binding, index, item ->
                    binding.index = index
                    binding.item = item
                }
            }
        }
    }

}