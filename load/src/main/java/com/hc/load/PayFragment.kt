package com.hc.load

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewStub
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import com.hc.load.databinding.FragmentRealPayBinding
import com.hc.load.databinding.VsRealPayHistoryInfoBinding
import com.hc.load.view.MyViewStubProxy
import com.hc.load.vm.PayViewModel
import com.hc.load.vm.RealPayViewClick
import com.hc.uicomponent.annotation.BindViewModel
import com.hc.uicomponent.base.BaseFragment
import com.hc.uicomponent.config.Constants
import kotlinx.android.synthetic.main.fragment_real_pay.*

class PayFragment : BaseFragment<FragmentRealPayBinding>(R.layout.fragment_real_pay) {

    var payAmount: String = ""
    var commission: String = ""
    var email: String = ""
    var orderId: String = ""
    var payToken: String = ""
    var payNotifyURL: String = ""
    var appId: String = ""
    var companyId: Int = 0

    @BindViewModel
    lateinit var realPayViewModel: PayViewModel

    lateinit var realPayViewClick: RealPayViewClick
    private lateinit var historyPayVsProxy: MyViewStubProxy
    private lateinit var historyPayVsListener: ViewStub.OnInflateListener
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.run {
            payAmount = this.getString(Constants.PRICE) ?: ""
            commission = this.getString(Constants.COMMISSION) ?: ""
            email = this.getString(Constants.EMAIL) ?: ""
            orderId = this.getString(Constants.ORDER_ID) ?: ""
            payToken = this.getString(Constants.PAY_TOKEN) ?: ""
            payNotifyURL = this.getString(Constants.PAY_NOTIFY_URL) ?: ""
            appId = this.getString(Constants.PAY_APP_ID) ?: ""
            companyId = this.getInt(Constants.COMPANY_ID)
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, backCallback)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handleViewLogic()
        injectBindingViewClick()
    }

    private fun injectBindingViewClick() {
        mFragmentBinding.run {
            payViewClick = RealPayViewClick(this@PayFragment, this, realPayViewModel).also {
                realPayViewClick = it
            }
            payAmount = this@PayFragment.payAmount
            commission = this@PayFragment.commission
            context = this.root.context
        }
    }

    private fun handleViewLogic() {
        //历史支付记录
        realPayViewModel.reqPayHistory {
            historyPayVsListener = ViewStub.OnInflateListener { _, inflated ->
                val realHistoryPayBinding = DataBindingUtil.bind<VsRealPayHistoryInfoBinding>(inflated)!!
                // 动态添加历史支付Item View
                realPayViewClick.dynamicAddHistoryItem(realHistoryPayBinding, it)
            }
            if (!this@PayFragment::historyPayVsProxy.isInitialized) {
                historyPayVsProxy = MyViewStubProxy(in_pay_history_info_vs)
                historyPayVsProxy.setOnInflateListener(historyPayVsListener)
                historyPayVsProxy.viewStub!!.inflate()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        realPayViewClick.onActivityResult(orderId, payAmount, requestCode, resultCode, data)
    }


    private val backCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (payment_webview.visibility == View.VISIBLE) {
                payment_webview.visibility = View.GONE
                sv_root.visibility = View.VISIBLE
                return
            }
            Navigation.findNavController(mFragmentBinding.root).navigateUp()
        }
    }

}