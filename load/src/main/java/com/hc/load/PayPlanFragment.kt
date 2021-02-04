package com.hc.load

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Html
import android.view.View
import android.view.ViewTreeObserver
import android.widget.LinearLayout
import android.widget.ScrollView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.hc.data.OrderStateEnum.*
import com.hc.data.mall.MainDataRec
import com.hc.data.mall.OrderStage
import com.hc.data.order.PlatformOrder
import com.hc.load.databinding.FragmentRepaymentPlanLayoutBinding
import com.hc.load.databinding.PaymentLayoutMultiplePeriodBinding
import com.hc.load.databinding.PaymentLayoutMultiplePeriodItemBinding
import com.hc.load.databinding.PaymentLayoutSinglePeriodBinding
import com.hc.load.utils.ViewAnimUtils
import com.hc.load.vm.PayPlanViewModel
import com.hc.uicomponent.annotation.BindViewModel
import com.hc.uicomponent.base.BaseFragment
import com.hc.uicomponent.config.Constants
import com.hc.uicomponent.placeView.PlaceholderLayout
import com.hc.uicomponent.provider.ContextProvider
import com.hc.uicomponent.utils.FirseBaseEventUtils
import com.hc.uicomponent.utils.StatEventTypeName
import com.hc.uicomponent.utils.TextUtil
import com.hc.uicomponent.utils.dynamicAddChildView
import frame.utils.DateUtil
import frame.utils.StringFormat
import kotlinx.android.synthetic.main.payment_layout_order.*

class PayPlanFragment : BaseFragment<FragmentRepaymentPlanLayoutBinding>(R.layout.fragment_repayment_plan_layout, true) {

    //if click delay button to scoll 2 layout buttom
    //标识是否来源于其他平台的订单
    private var swap = 0
    private var isMultiplePeriodFlag: Boolean = false //is multiple period flag : true=>多期 ,false==>单期
    private var platformOrderData: PlatformOrder? = null
    private var isCallPayLoadingStatus = false
    private var isClickDelayFlag = false
    private var isRefreshLayout = false
    private var isDrawReqDataFinish = false
    private var orderRepaySingleBinding: PaymentLayoutSinglePeriodBinding? = null
    private var orderRepayMultipleBinding: PaymentLayoutMultiplePeriodBinding? = null
    private var globalListener: ViewTreeObserver.OnGlobalLayoutListener? = null
    private var multipleItemViewHeightList = mutableListOf<Int>()
    private var mainOrder: MainDataRec? = null

    @BindViewModel
    var mModel: PayPlanViewModel? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.run {
            isClickDelayFlag = getBoolean(Constants.flag) ?: false
            val obj = getSerializable(Constants.platform)
            obj.let {
                if (obj is PlatformOrder) {
                    platformOrderData = obj
                }
            }

        }

        initView(view)
        mModel?.payOrderInfo?.observe(viewLifecycleOwner, Observer {
            mainOrder = it
            setData()
        })
    }

    private fun initView(view: View) {
        // initPlaceHolder(view)
        initContent(view)
    }

    private fun initPlaceHolder(view: View) {
        mFragmentBinding.run {
            place.setContentView(nsv)
            place.status = PlaceholderLayout.LOADING
            place.setOnReloadListener {
                mModel?.reqOrderData()
            }
        }
    }

    private fun initContent(view: View) {
        mFragmentBinding.run {
            singlePeriodPayVs.setOnInflateListener { _, inflated ->
                globalListener = ViewTreeObserver.OnGlobalLayoutListener {
                    if (this@PayPlanFragment.isClickDelayFlag) nsv.fullScroll(ScrollView.FOCUS_DOWN)
                    if (isDrawReqDataFinish) nsv.viewTreeObserver.removeOnGlobalLayoutListener(globalListener)
                }
                nsv.viewTreeObserver.addOnGlobalLayoutListener(globalListener)
                orderRepaySingleBinding = DataBindingUtil.bind(inflated)
                isDrawReqDataFinish = true
                orderRepaySingleBinding?.paySingleRepayBtn?.setOnClickListener {
                    FirseBaseEventUtils.trackEvent(StatEventTypeName.SINGLE_PERIOD_PAY_CLICK)
                    mModel?.reqPayTypeAndCalcPayAmount(view, false)
                }

                bindSignalOrderData()
            }


            multiplePeriodPayVs.setOnInflateListener { _, inflated ->
                globalListener = ViewTreeObserver.OnGlobalLayoutListener {
                    if (this@PayPlanFragment.isClickDelayFlag) nsv.fullScroll(ScrollView.FOCUS_DOWN)
                    val multipleParentLayout = orderRepayMultipleBinding?.payMultipleRepayGroupLayout
                    multipleParentLayout?.let {
                        for (index in 0 until it.childCount) {
                            val multipleItemChildView = it.getChildAt(index).findViewById<LinearLayout>(R.id.repay_multiple_period_shrink_layout)
                            multipleItemChildView.visibility = View.GONE
                        }
                    }
                    if (isDrawReqDataFinish) nsv.viewTreeObserver.removeOnGlobalLayoutListener(globalListener)
                    nsv.viewTreeObserver.addOnGlobalLayoutListener(globalListener)
                    orderRepayMultipleBinding = DataBindingUtil.bind(inflated)
                    orderRepayMultipleBinding?.run {
                        payMultipleRepayShrinkImgBtn.setOnClickListener {
                            val multiLayout = this.payMultipleRepayGroupLayout
                            if (multiLayout.childCount > 0) {
                                val fl = if (swap % 2 == 0) 0f else 90f
                                val fl1 = if (swap % 2 == 0) 90f else 0f
                                ViewAnimUtils.roateViewAngle(view, fl, fl1)
                                for (index in 0 until multiLayout.childCount) {
                                    val multipleItemChildView =
                                        multiLayout.getChildAt(index).findViewById<LinearLayout>(R.id.repay_multiple_period_shrink_layout)
                                    if (swap % 2 == 0) {
                                        if (multipleItemViewHeightList.isEmpty() || index > multipleItemViewHeightList.size - 1) {
                                            multipleItemViewHeightList.add(multipleItemChildView.height)
                                        }
                                    }
                                    if (isRefreshLayout) {
                                        multipleItemViewHeightList[index] = multipleItemChildView.height
                                    }
                                    multipleItemChildView.visibility = View.VISIBLE
                                    val i = if (swap % 2 != 0) multipleItemViewHeightList[index] else 0
                                    val i1 = if (swap % 2 != 0) 0 else multipleItemViewHeightList[index]
                                    ViewAnimUtils.changeViewHeightForAnim(multipleItemChildView, i, i1)
                                }
                            }
                            swap++
                            isRefreshLayout = false
                        }
                        payMultipleRepayBtn.setOnClickListener {
                            FirseBaseEventUtils.trackEvent(StatEventTypeName.MULTIPLE_PERIOD_PAY_CLICK)
                            mModel?.reqPayTypeAndCalcPayAmount(view, false)
                        }
                        payContainer.btn.run {
                            this.setOnClickListener {
                                mModel?.reqPayTypeAndCalcPayAmount(view, isClickDelayFlag)
                            }
                        }
                    }
                    bindMainOrderData()
                }
            }

        }
    }

    override fun onResume() {
        super.onResume()
        if (isCallPayLoadingStatus) {
            return
        }
        if (platformOrderData != null) {
            mModel?.groupPlatformOrderInfo2MainDataRec(platformOrderData!!)
        } else {
            mModel?.reqOrderData()
        }
    }

    private fun bindSignalOrderData() {
        val requireContext = requireContext()
        //bind view data
        orderRepaySingleBinding?.run {
            val orderRepay = mainOrder?.orderRepay
            amount.text = StringFormat.showMoneyWithSymbol(requireContext, orderRepay?.amount)
            interest.text = StringFormat.showMoneyWithSymbol(requireContext, orderRepay?.interestSum)
            val overZero = StringFormat.isOverZero(orderRepay?.penaltyAmout)
            val more = OVERDUE.state == mainOrder?.orderInfo?.state
            overdueContainer.visibility = doVisible(overZero && more)
            overdueFines.text = StringFormat.showMoneyWithSymbol(requireContext, orderRepay?.penaltyAmout)

            val isShowDisCount = StringFormat.isOverZero(orderRepay?.amountRelief)
            discountContainer.visibility = doVisible(isShowDisCount)
            amountRelief.text = StringFormat.showMoneyWithNegativeSymbol(requireContext, orderRepay?.amountRelief)

            val isShowSettlementContainer = StringFormat.isOverZero(orderRepay?.amountPaid)
            settlementContainer.visibility = doVisible(isShowSettlementContainer)
            amountPaid.text = StringFormat.showMoneyWithNegativeSymbol(requireContext, orderRepay?.amountPaid)
            val isShowBtn =
                REPAY_ING.state == mainOrder?.orderInfo?.state || RENEWAL_ING.state == mainOrder?.orderInfo?.state
            paySingleRepayBtn.visibility = if (isShowBtn) {
                View.GONE
            } else {
                View.VISIBLE
            }
        }
    }

    @SuppressLint("ResourceAsColor")
    private fun bindMainOrderData() {
        val requireContext = requireContext()

        orderRepayMultipleBinding?.run {
            val orderInfo = mainOrder?.orderInfo
            val isColorSelect = REPAY_ING.state == orderInfo?.state || RENEWAL_ING.state == orderInfo?.state
            val color = if (isColorSelect) R.color.C_F46524 else R.color.C_2488F4
            hintText.setTextColor(color)
            val isShowOverdue = REPAY_ING.state == orderInfo?.state || RENEWAL_ING.state == orderInfo?.state
            stateImage.setBackgroundResource(if (isShowOverdue) R.drawable.loan_repay_signal_order else R.drawable.loan_repay_more_order)
        }
        val parentGroup = orderRepayMultipleBinding?.payMultipleRepayGroupLayout
        parentGroup?.let {
            it.removeAllViews()
            val layoutId = R.layout.payment_layout_multiple_period_item
            val data = mainOrder?.orderStages
            data?.let {
                dynamicAddChildView<OrderStage, PaymentLayoutMultiplePeriodItemBinding>(parentGroup, layoutId, data) { binding, index, data ->
                    binding.run {
                        emi.text = ContextProvider.getString(R.string.pay_mutliple_emi, index)
                        repayTime.text = DateUtil.getFormat2DateStr(DateUtil.getFormat2Date(data.repayTime, DateUtil.Format.DATE), DateUtil.Format.DATE)
                        amount.text = StringFormat.showMoneyWithSymbol(requireContext, data.amount)
                        interest.text = StringFormat.showMoneyWithSymbol(requireContext, data.interest)
                        penaltyAmount.text = StringFormat.showMoneyWithSymbol(requireContext, data.penaltyAmount)
                        val isShowOverdue = StringFormat.isOverZero(data.penaltyAmount)
                        overdueContainer.visibility = doVisible(isShowOverdue)
                        val isShowAmountRelief = StringFormat.isOverZero(data.amountRelief)
                        discountAmountContainer.visibility = doVisible(isShowAmountRelief)
                        amountRelief.text = StringFormat.showMoneyWithNegativeSymbol(requireContext, data.amountRelief)
                        val isShowAmountPaid = StringFormat.isOverZero(data.amountPaid)
                        settlementContainer.visibility = doVisible(isShowAmountPaid)
                        settlement.text = StringFormat.showMoneyWithNegativeSymbol(requireContext, data.amountPaid)
                        totalRepayment.text = StringFormat.showMoneyWithSymbol(requireContext, "${data.totalRepayment}")
                    }
                    isDrawReqDataFinish = true
                }
            }
        }
    }

    private fun doVisible(isShowAmountPaid: Boolean): Int {
        return if (isShowAmountPaid) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    private fun setData() {
        mainOrder?.let { order ->
            val moneyValue = if (this.isMultiplePeriodFlag) {
                var sumTotalMoney = 0.0
                order.orderStages.let {
                    if (order.orderStages.isNotEmpty()) {
                        sumTotalMoney += it[0].totalRepayment
                    }
                }
                sumTotalMoney
            } else {
                mainOrder?.orderRepay?.totalRepayment
            }

            val state = mainOrder?.orderInfo?.state
            val isNormal = REPAY.state == state || RENEWAL.state == state
            val isRepaying = REPAY_ING.state == state || RENEWAL_ING.state == state
            val isBad = OVERDUE.state == state || BAD.state == state

            mFragmentBinding.run {
                head.run {
                    image.visibility = View.VISIBLE
                    tvDate.visibility = View.VISIBLE
                    when {
                        isNormal -> {
                            image.setImageResource(R.drawable.loan_ic_completed)
                            tvDate.setText(R.string.pay_normal_text)
                        }
                        isRepaying -> {
                            image.setImageResource(R.drawable.loan_ic_closed)
                            tvDate.setText(R.string.pay_processing_text)
                        }
                        isBad -> {
                            image.setImageResource(R.drawable.loan_overdue)
                            val date = ContextProvider.getString(R.string.pay_overdue_text, StringFormat.integerFormat(mainOrder?.orderRepay?.penaltyDay))
                            tvDate.text = date
                        }
                        else -> {
                            image.visibility = View.GONE
                            tvDate.visibility = View.GONE
                        }
                    }

                    moneyTitle.text = when {
                        isRepaying -> {
                            getString(R.string.pay_total_real_pay)
                        }
                        mainOrder?.orderInfo?.stages ?: 0 > 1 -> {
                            getString(R.string.pay_repayment_amount)
                        }
                        else -> {
                            getString(R.string.pay_total_real_pay)
                        }
                    }

                    order.orderStages.let {
                        if (it.isNotEmpty()) {
                            date.text = DateUtil.getFormat2DateStr(DateUtil.getFormat2Date(it[0].repayTimeStr, DateUtil.Format.DATE), DateUtil.Format.DATE)
                        }
                    }
                    money.text = StringFormat.showMoneyWithSymbol(requireContext(), "$moneyValue")
                    orderContainer.run {
                        orderNumber.text = order.orderInfo?.orderNo
                        orderDate.text =
                            DateUtil.getFormat2DateStr(DateUtil.getFormat2Date(order.orderInfo?.createTime, DateUtil.Format.DATE), DateUtil.Format.DATE)
                    }

                    payContainer.run {
                        val iShowRepayBtn = order.isDeplay == Constants.NUMBER_1
                        this.root.visibility = if (iShowRepayBtn) View.VISIBLE else View.GONE

                        val formatArgs = StringFormat.addingNumbers(requireContext(), order.deplayFee, order.deplayGstFee)
                        val formatArgs1 = StringFormat.integerFormat("${order.deplayDay}")
                        val hint = ContextProvider.getString(R.string.pay_detail_delay_tip, formatArgs, formatArgs1)
                        val title = Html.fromHtml(hint)
                        titleDesc.text = title

                        fee.text = StringFormat.showMoneyWithSymbol(requireContext(), "${order.deplayFee}")
                        gst.text = StringFormat.showMoneyWithSymbol(requireContext(), "${order.deplayGstFee}")
                        val isShow = REPAY_ING.state == order.orderInfo?.state || RENEWAL_ING.state == order.orderInfo?.state
                        btn.visibility = if (isShow) View.GONE else View.VISIBLE
                    }

                    isMultiplePeriodFlag = (order.orderInfo?.stages ?: 0 > Constants.NUMBER_1)

                    singlePeriodPayVs.run {
                        if (!isMultiplePeriodFlag) {
                            if (orderRepaySingleBinding != null) {
                                bindSignalOrderData()
                            } else {
                                mFragmentBinding.singlePeriodPayVs.viewStub?.inflate()
                            }
                        }
                    }
                    multiplePeriodPayVs.run {
                        if (isMultiplePeriodFlag) {
                            if (orderRepayMultipleBinding != null) {
                                bindMainOrderData()
                            } else {
                                mFragmentBinding.multiplePeriodPayVs.viewStub?.inflate()
                            }
                        }
                    }
                    //显示NBFC图片
                    ivNbfcImg.let {
                        mainOrder?.indexImgPath?.apply {
                            ivNbfcImg.loadImageWithCallBack(this) {
                                ivNbfcImg.visibility = View.VISIBLE
                                ivNbfcImg.setImageBitmap(it)
                            }
                        }
                        if (TextUtil.isEmpty(mainOrder?.indexImgPath)) {
                            ivNbfcImg.visibility = View.GONE
                        }
                    }
                }
            }
        }

    }

}

