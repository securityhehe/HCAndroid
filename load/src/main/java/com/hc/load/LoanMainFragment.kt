package com.hc.load

import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import com.hc.data.MenuData
import com.hc.data.NavContents
import com.hc.data.OrderStateEnum.*
import com.hc.data.mall.DiversionProduct
import com.hc.data.mall.GoodsSx
import com.hc.data.mall.IListData
import com.hc.data.user.UserType
import com.hc.load.databinding.FragmentLoanInputMoneyLayoutBinding
import com.hc.load.view.LoanProductView
import com.hc.load.vm.LoanViewModel
import com.hc.load.vm.LogicData
import com.hc.permission.AndroidPermissions
import com.hc.uicomponent.annotation.BindViewModel
import com.hc.uicomponent.base.jumpDeepLikPage
import com.hc.uicomponent.config.Constants
import com.hc.uicomponent.menu.BaseMenuViewModel
import com.hc.uicomponent.menu.BasePopupWindow
import com.hc.uicomponent.provider.ContextProvider
import com.hc.uicomponent.stack.ActivityStack
import com.hc.uicomponent.utils.ScreenAdapterUtils
import com.timmy.tdialog.TDialog
import frame.utils.StringFormat
import kotlinx.android.synthetic.main.fragment_loan_input_money_layout.*
import org.jetbrains.anko.bundleOf
import org.jetbrains.anko.find

class LoanMainFragment : PermissionBaseFragment<FragmentLoanInputMoneyLayoutBinding>(R.layout.fragment_loan_input_money_layout) {

    companion object {
        const val RECEIVE = 0x01
        const val REJECT = 0x02
        const val STATUS_RECEIVE = 0x01
        const val STATUS_ODER_UNDER_REVIEW = 0x02
        const val STATUS_DISBURSED = 0x03
        const val DIVERSION_TYPE_TOP = "1"
        const val DIVERSION_TYPE_MORE = "2"
        const val READ_SMS_PERMISSION = 1
        const val BASE_PERMISSION = 2
    }

    private val x100 = ScreenAdapterUtils.dp2px(ContextProvider.app, 100)
    private val x23 = ScreenAdapterUtils.dp2px(ContextProvider.app, 23)
    private val x187 = ScreenAdapterUtils.dp2px(ContextProvider.app, -70)

    @BindViewModel
    var mLoanMainViewModel: LoanViewModel? = null

    @BindViewModel
    var baseMenuModel: BaseMenuViewModel? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mLoanMainViewModel?.apply {
            mFragmentBinding.vm = this
            reqHomeData(view, false)
            setData()
            initEvent()
        }

        mFragmentBinding.run {
            record.setOnClickListener {
                NavHostFragment.findNavController(this@LoanMainFragment).navigate(Uri.parse("navigation://loan/history/order"))
            }
            message.setOnClickListener {
                NavHostFragment.findNavController(this@LoanMainFragment).navigate(Uri.parse("navigation://loan/message"))
            }
        }
    }

    private fun initEvent() {
        mFragmentBinding.run {
            moreProduct.setOnClickListener {
                mLoanMainViewModel?.logicData?.moreData?.value?.let {
                    showDiversionDialog(it)
                }
            }
        }
    }

    private fun LoanViewModel.setData() {
        logicData.run {

            mOrderFlowMapStateData.observe(viewLifecycleOwner, Observer {
                setOrderMapFlowState(it)
            })

            mControlOrderVisibleOrGone.observe(viewLifecycleOwner, Observer {
                mFragmentBinding.order.orderContainer.visibility = it
            })

            mOrderViewControl.observe(viewLifecycleOwner, Observer {
                mLoanMainViewModel?.let { model ->
                    setOrderView(this, it, model)
                }
            })

            //控制order的展示和隐藏。
            mControlProductVisibleOrGone.observe(viewLifecycleOwner, Observer {
                mFragmentBinding.productView.visibility = it
            })

            mProductList.observe(viewLifecycleOwner, Observer {
                setProductList(it)
            })
            mControlMoreBtnVisibleOrGone.observe(viewLifecycleOwner, Observer {
                moreProductContainer.visibility = it
            })

        }
    }

    //审核拒绝
    private fun toAuditReject(time: String) {
        context?.let {
            mFragmentBinding.run {
                loanStatusFl.background = ContextCompat.getDrawable(it, R.drawable.loan_audit_reject_bg)
                loanStatusDesc.text = String.format(getString(R.string.loan_audit_reject_text), time)
                loanStatusDesc.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    0,
                    R.drawable.loan_audit_reject_ic,
                    0,
                    0
                )
                loanStatusDesc.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary))
            }
        }
    }

    //订单逾期
    private fun toBeOverdue(day: String, money: String) {
        context?.let {
            mFragmentBinding.run {
                loanStatusFl.background = ContextCompat.getDrawable(it, R.drawable.loan_audit_reject_bg)
                loanStatusDesc.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.loan_status_be_overdue, 0, 0)
                loanStatusDesc.text = String.format(getString(R.string.loan_order_to_overdue_text), "$day Days(s)", "₹$money")
                loanStatusDesc.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary))
            }
        }
    }

    //订单续期
    private fun toRenewal() {
        context?.let {
            mFragmentBinding.run {
                loanStatusFl.background = ContextCompat.getDrawable(it, R.drawable.loan_audit_reject_bg)
                loanStatusDesc.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.loan_renewal, 0, 0)
                loanStatusDesc.text = String.format(getString(R.string.loan_order_to_renewal_text))
                loanStatusDesc.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary))
            }
        }
    }

    //失效订单状态
    private fun toInvalidReject() {
        context?.let {
            mFragmentBinding.run {
                loanStatusFl.background = ContextCompat.getDrawable(it, R.drawable.loan_audit_reject_bg)
                loanStatusDesc.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0)
                loanStatusDesc.text = getString(R.string.loan_audit_invalid_text)
                val a: ViewGroup.MarginLayoutParams = loanStatusDesc.layoutParams as ViewGroup.MarginLayoutParams
                a.topMargin = resources.getDimensionPixelOffset(R.dimen.laon_top_text_30)
                loanStatusDesc.layoutParams = a
                loanStatusDesc.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary))
            }
        }
    }

    //订单关闭
    private fun toOrderClose() {
        context?.let {
            mFragmentBinding.run {
                loanStatusFl.background = ContextCompat.getDrawable(it, R.drawable.loan_audit_reject_bg)
                loanStatusDesc.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0)
                loanStatusDesc.text = getString(R.string.loan_order_close_text)
                loanStatusDesc.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary))
            }
        }
    }


    //订单还款中提示。6
    private fun toRepaymentProcessing() {
        setLoanViewState(STATUS_RECEIVE)
        context?.let {
            mFragmentBinding.run {
                loanStatusFl.background = ContextCompat.getDrawable(it, R.drawable.loan_audit_reject_bg)
                loanStatusDesc.text = getString(R.string.loan_status_to_repayment_text)
                loanStatusDesc.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.loan_repayment_processing, 0, 0)
            }
        }
    }

    //状态转换方法
    private fun setLoanViewState(level: Int = 0) {
        val isOpenReceive = (level >= STATUS_RECEIVE)
        mFragmentBinding.run {
            loanAppReceive.isEnabled = isOpenReceive
            loanGo1.isEnabled = isOpenReceive
            val isOpenOrderReview = (level >= STATUS_ODER_UNDER_REVIEW)
            loanOrderReview.isEnabled = isOpenOrderReview
            loanGo2.isEnabled = isOpenOrderReview
            val isOpenDisbursed = (level >= STATUS_DISBURSED)
            lanDisbursed.isEnabled = isOpenDisbursed
        }
    }

    //没有下单状态。
    private fun toReceived() {
        context?.let {
            mFragmentBinding.run {
                loanStatusFl.background = ContextCompat.getDrawable(it, R.drawable.loan_status_received_bg)
                loanStatusDesc.text = getString(R.string.loan_status_received_text)
                loanStatusDesc.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0)
                setLoanViewState()
            }
        }
    }

    //预审
    private fun toPrevCredit() {
        context?.let {
            mFragmentBinding.run {
                loanStatusFl.background = ContextCompat.getDrawable(it, R.drawable.loan_audit_reject_bg)
                loanStatusDesc.text = getString(R.string.loan_prev_credit)
                loanStatusDesc.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.loan_repayment_processing, 0, 0)
            }
        }
    }

    //审核中
    private fun toRenewalSelect() {
        context?.let {
            mFragmentBinding.run {
                loanStatusFl.background = ContextCompat.getDrawable(it, R.drawable.loan_status_received_bg)
                loanStatusDesc.text = getString(R.string.loan_status_received_text)
                loanStatusDesc.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0)
                setLoanViewState(STATUS_RECEIVE)
            }
        }
    }

    //订单审核通过，已经打款。
    private fun toOrderUnderReview() {
        context?.let {
            mFragmentBinding.run {
                loanStatusFl.background = ContextCompat.getDrawable(it, R.drawable.loan_status_received_bg)
                loanStatusDesc.text = getString(R.string.loan_status_received_text)
                loanStatusDesc.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0)
                setLoanViewState(STATUS_ODER_UNDER_REVIEW)
            }
        }
    }

    //等待还款。
    private fun toOrderRepay() {
        context?.let {
            mFragmentBinding.run {
                loanStatusFl.background = ContextCompat.getDrawable(it, R.drawable.loan_status_received_bg)
                loanStatusDesc.text = getString(R.string.loan_status_received_text)
                loanStatusDesc.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0)
                setLoanViewState(STATUS_DISBURSED)
            }
        }
    }

    //显示产品弹出窗口。
    private fun showDiversionDialog(list: MutableList<IListData>?) {
        list?.let {
            val view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_loan_diversion_more, null, false)
            val builder = TDialog.Builder((activity as FragmentActivity).supportFragmentManager).setDialogView(view)
            builder.setScreenWidthAspect(activity, 1f)
            builder.setScreenHeightAspect(activity, 1f)
            builder.setGravity(Gravity.CENTER)
            builder.setCancelableOutside(false)
            builder.setDimAmount(0.0f)

            builder.setOnViewClickListener { _, _, tDialog ->
                tDialog.dismissAllowingStateLoss()
            }
            builder.setOnKeyListener { _, _, _ ->
                false
            }
            view.find<LoanProductView>(R.id.loanProduct).setData(it)
            val dialog = builder.create()
            dialog.show()
        }
    }


    private fun setOrderMapFlowState(it: String?) {
        mFragmentBinding.fl.visibility = View.GONE
        when (it) {
            INVALID.state -> {
                toInvalidReject()
            }
            CLOSE.state -> {
                toOrderClose()
            }
            AUTO_REFUSED.state -> {
                toAuditReject(mLoanMainViewModel?.logicData?.mainDataRec?.nextTime ?: "")
            }
            OVERDUE.state -> {
                val a = mLoanMainViewModel?.logicData?.mainDataRec?.orderRepay
                toBeOverdue(a?.penaltyDay ?: "0", a?.penaltyAmout ?: "0")
            }
            RENEWAL.state -> {
                toRenewal()
            }
            RENEWAL_ING.state,
            REPAY_ING.state -> {
                toRepaymentProcessing()
            }
            CREDIT_VERIFIY_LOADING.state -> {  //征信认证中, 调整到问卷调查
                mFragmentBinding.fl.visibility = View.VISIBLE
                mFragmentBinding.title.text = getString(R.string.loan_credit_ing)
                mFragmentBinding.btn.text = getString(R.string.loan_submit)
                mFragmentBinding.btn.setOnClickListener {
                    val url = mLoanMainViewModel?.logicData?.mainDataRec?.orderInfo?.id
                    url?.let {
                        val title = resources.getString(R.string.order_credit_ask_question_title)
                        mLoanMainViewModel?.jumpWebFragment(mFragmentBinding.root, title, url)
                    }
                }
            }
            CASH_FAIL.state -> { //提现失败。更改银行卡。
                mFragmentBinding.fl.visibility = View.VISIBLE
                mFragmentBinding.title.text = getString(R.string.loan_change_bank)
                mFragmentBinding.btn.text = getString(R.string.loan_change_bank_btn)
                mFragmentBinding.btn.setOnClickListener {
                    mLoanMainViewModel?.jumpDeepLikPage(mFragmentBinding.root, null, NavContents.loanBank)
                }
            }
            SUBMIT_NO_COMMIT_ORDER.state -> {
                toReceived()
            }
            SUBMIT_ORDER_SUCCESS.state,
            FIRST_REVIEW_ING.state,
            AUTO_REVIEW_ING.state,
            MANUAL_REVIEW_ING.state,
            AUTO_REVIEW_PASS.state,
            MANUAL_REVIEW_PASS.state,
            WAIT_CASH.state,
            CASH_ING.state,
            CASH_FAIL.state -> {
                if (CASH_FAIL.state == it) {
                    if (mLoanMainViewModel?.logicData?.isBindBankFlag == true) {
                        toOrderUnderReview()
                    }
                } else {
                    toOrderUnderReview()
                }
            }
            REPAY.state -> {
                toOrderRepay()
            }
            else -> {

            }
        }
    }

    private fun LogicData.setProductList(it: MutableList<IListData>) {
        if (mControlProductVisibleOrGone.value == View.VISIBLE) {
            it.forEach {
                if (it is GoodsSx) {
                    val orderStatus = mLoanMainViewModel?.logicData?.orderStatus
                    it.isCanBuy = !(MANUAL_REFUSED.state == orderStatus || AUTO_REFUSED.state == orderStatus)
                }
            }
            productView.setData(it).callback = object : LoanProductView.Callback {
                //显示产品列表。
                override fun selectPrice(price: IListData, priceTv: TextView, iv: ImageView) {
                    choosePrice(price, priceTv, iv)
                }

                //下单，
                override fun getNowMoney(price: IListData, money: String) {
                    mLoanMainViewModel
                    if (price.getChangeMoney()) {
                        if (price is GoodsSx) {
                            if (price.choseAmount == 0.0) {
                                price.choseAmount = price.maxAmount
                            }
                            mLoanMainViewModel?.saveGetNowMoney(price)

                            reqSmsPermissionAndGetNowMoney()
                        }
                    } else {
                        //导流app下单。
                        if (price is DiversionProduct) {
                            mLoanMainViewModel?.handleDiversionClick(price, DIVERSION_TYPE_TOP, requireActivity());
                        }
                    }
                }
            }
        }
    }

    private fun setOrderView(logicData1: LogicData, it: String?, loanViewModel: LoanViewModel) {
        if (logicData1.mControlOrderVisibleOrGone.value == View.VISIBLE) {
            mFragmentBinding.order.run {
                mLoanMainViewModel?.logicData?.mainDataRec?.apply {

                    val moneyFlag = (it == RENEWAL.state || it == OVERDUE.state || REPAY.state == it || BAD.state == it)
                    //title显示。
                    if (moneyFlag) {
                        moneyTitle.text = getString(R.string.loan_repay_amount)
                        if (this.orderInfo?.stages ?: 0 > Constants.NUMBER_1) {
                            var sumTotalMoney = 0.0
                            if (this.orderStages.isNotEmpty()) {
                                sumTotalMoney += this.orderStages[0].totalRepayment
                            }
                            money.text = StringFormat.showMoneyWithSymbol(requireContext(), sumTotalMoney.toString())
                        } else {
                            money.text = StringFormat.showMoneyWithSymbol(requireContext(), this.orderRepay.totalRepayment.toString())
                        }
                    } else {
                        moneyTitle.text = getString(R.string.loan_amount)
                        money.text = StringFormat.showMoneyWithSymbol(requireContext(), this.orderInfo?.goodsPrice)
                    }

                    val ifShowPaymentText =
                        (REPAY.state == it || OVERDUE.state == it || RENEWAL.state == it || REPAY_ING.state == it || RENEWAL_ING.state == it)
                    if (ifShowPaymentText) {
                        dateTitle.text = getString(R.string.pay_repayment_date)
                        date.text = orderStages[0].repayTimeStr
                    } else {
                        val args = StringFormat.integerFormat(this.orderInfo?.timeLimit ?: "")
                        val format = resources.getString(R.string.loan_duration_day)
                        date.text = String.format(format, args)
                        dateTitle.text = getString(R.string.loan_duration)
                    }

                    val isShowBtnRepay = (it == RENEWAL.state || it == OVERDUE.state || REPAY.state == it || BAD.state == it)
                    val isShowBtnDelay = loanViewModel.logicData.mainDataRec?.isDeplay == Constants.NUMBER_1
                    val isShowBtnContainer = isShowBtnRepay || isShowBtnDelay
                    //还款按钮按钮显示。
                    loanRepay.visibility = if (isShowBtnRepay) View.VISIBLE else View.GONE
                    loanDelay.visibility = if (isShowBtnDelay) View.VISIBLE else View.GONE
                    container.visibility = if (isShowBtnContainer) View.VISIBLE else View.GONE

                    loanRepay.setOnClickListener{
                       ContextProvider.mNavIdProvider?.getPlanNavId()?.let {
                           Navigation.findNavController(loanRepay).navigate(it )
                       }
                    }
                    loanDelay.setOnClickListener{
                        ContextProvider.mNavIdProvider?.getPlanNavId()?.let {
                            val argument = bundleOf(Pair(Constants.flag,true))
                            Navigation.findNavController(loanRepay).navigate(it, argument)
                        }
                    }

                    val isShowBillDetail = (WAIT_SIGN.state == it || WAIT_CASH.state == it || CASH_FAIL.state == it)
                    val isShowRepayment = (RENEWAL.state == it || OVERDUE.state == it || REPAY.state == it || BAD.state == it||REPAY_ING.state == it||RENEWAL_ING.state == it)
                    when {
                        isShowBillDetail -> {
                            billDetails.setText(R.string.bill_details)
                            billDetails.visibility = View.VISIBLE
                        }
                        isShowRepayment -> {
                            billDetails.setText(R.string.repayment_plan)
                            billDetails.visibility = View.VISIBLE
                        }
                        else -> {
                            billDetails.visibility = View.GONE
                        }
                    }

                    billDetails.setOnClickListener {
                        if (isShowBillDetail) {
                            ContextProvider.mNavIdProvider?.getOrderDetail()?.let {
                                val mut = (this.orderInfo?.stages ?: 0 > Constants.NUMBER_1)
                                val argument = bundleOf(Pair(Constants.ORDER_NUM, this.orderInfo?.id ?: ""), Pair(Constants.STATE, mut))
                                Navigation.findNavController(billDetails).navigate(it, argument)
                            }
                        } else {
                            ContextProvider.mNavIdProvider?.getPlanNavId()?.let {
                                Navigation.findNavController(loanRepay).navigate(it)
                            }
                        }
                    }

                    //底部文案状态展示。
                    val isGoneKeepTips = (SUBMIT_ORDER_SUCCESS.state == it)
                            || AUTO_REVIEW_ING.state == it
                            || MANUAL_REVIEW_ING.state == it
                            || AUTO_REVIEW_PASS.state == it
                            || MANUAL_REVIEW_PASS.state == it
                            || WAIT_CASH.state == it
                            || CASH_ING.state == it
                            || (CASH_FAIL.state == it && mLoanMainViewModel?.logicData?.isBindBankFlag == true)

                    if (isGoneKeepTips) {
                        tip.visibility = View.VISIBLE
                    } else {
                        tip.visibility = View.GONE
                    }
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        AndroidPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    //显示价格改变弹出窗口,
    private fun choosePrice(price: IListData?, priceTv: TextView, iv: ImageView) {
        if (price == null) return
        val borrowMoneyList = mutableListOf<Double>()
        if (price.getChangeMoney()) {
            if (price is GoodsSx) {
                var startAmount = price.minAmount
                val endMaxAmount = price.maxAmount
                var isEqu = false
                while (startAmount <= endMaxAmount) {
                    if (startAmount == endMaxAmount) {
                        isEqu = true
                        borrowMoneyList.add(endMaxAmount)
                        break
                    } else {
                        borrowMoneyList.add(startAmount)
                        startAmount += price.increment
                    }
                }
                if (!isEqu) {
                    borrowMoneyList.add(endMaxAmount)
                }
                if (borrowMoneyList.size > 0) {
                    borrowMoneyList.reverse()
                    showMenu(borrowMoneyList, -1, priceTv, iv) {
                        price.choseAmount = it
                    }
                }
            }
        }
    }

    private fun showMenu(data: List<Double>?, titleRes: Int, view: TextView, iv: ImageView, callback: (Double) -> Unit) {
        val act = ActivityStack.currentActivity()
        if (data != null && act != null && !act.isFinishing && !act.isDestroyed) {
            var popupWindow: BasePopupWindow?
            baseMenuModel?.apply {
                val menuData = data.mapIndexed { i, d ->
                    val a = UserType(null, d.toString(), d.toString(), null, "", "")
                    MenuData(a, i, false)
                }
                if (titleRes == -1) {
                    ""
                } else {
                    title = view.context.getString(titleRes) ?: ""
                }
                listData.clear()
                listData.addAll(menuData)
                popupWindow = BasePopupWindow(act, this, iv, x100)
                popupWindow?.show(x187, x23)
                callbackData = { it ->
                    popupWindow?.dismiss()
                    callback.invoke(it.menuInfo.info.toDouble())
                    view.text = StringFormat.showMoneyWithSymbol(requireContext(), it.menuInfo.info)
                    Unit
                }
            }
        }
    }

    override fun callPermissionOk() {
        super.callPermissionOk()
        mLoanMainViewModel?.commitOrder(mFragmentBinding.root)
    }

}


