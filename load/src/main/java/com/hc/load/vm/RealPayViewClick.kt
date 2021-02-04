package com.hc.load.vm

import android.content.Intent
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import com.bigkoo.pickerview.TimePickerView
import com.blankj.utilcode.util.KeyboardUtils
import com.blankj.utilcode.util.ToastUtils
import com.hc.data.CUR_RUNTIME_EVN
import com.hc.data.common.CommonDataModel
import com.hc.data.order.CFPayInfo
import com.hc.data.user.UserType
import com.hc.load.PayFragment
import com.hc.load.R
import com.hc.load.databinding.*
import com.hc.load.utils.CashFree2PayUtils
import com.hc.load.utils.RazorPayClientUtils
import com.hc.load.utils.ViewAnimUtils
import com.hc.load.view.addBlankTextListener
import com.hc.load.view.removeBlank
import com.hc.uicomponent.provider.ContextProvider
import com.hc.uicomponent.utils.*

import com.razorpay.PaymentData
import frame.utils.DateUtil
import frame.utils.RegularUtil
import kotlinx.android.synthetic.main.fragment_real_pay.*
import java.math.BigDecimal
import java.util.*

/**
 * @Author : ZhouWei
 * @TIME   : 2020/6/15 10:24
 * @DESC   : 处理实际支付的点击事件
 */
class RealPayViewClick(val act: PayFragment,
                       val binding: FragmentRealPayBinding,
                       val realPayViewModel:PayViewModel
): RazorPayClientUtils.RazorPayResultListener{
    /**
     * ---------------------------------------
     * 区分开选择历史支付和输入的支付方式的逻辑：
     * 1.统一使用标识：isHasChose 是否已经存在选择支付项
     * 2.使用 isUseHistoryMethod 标识当前所处支付方式（使用历史支付 or  其他支付方式）
     * 3.切换两者时需重置对方的状态
     * ---------------------------------------
     *
     * 其他三种支付方式的处理逻辑：
     * 1.统一使用标识：isHasChose 是否已经存在选择支付项
     * 2.使用变量 curChosePayMethod 缓存当前已选择的某个方式枚举类型，没有选择为null值
     * ---------------------------------------
     */

    //触发点击支付历史记录item的监听器
    var handleHistoryClickListener: View.OnClickListener
    var lastHistoryPayInfoBinding: ItemRealPayHistoryInfoBinding ?= null

    //触发点击支付方式中收缩布局的按钮监听器
    var handleShrinkClickListener: View.OnClickListener

    //表示当前是否存在选择支付方式
    var isHasChose: Boolean = false
    //用于区分当前选择的是历史支付方式，还是其他支付方式，默认为0(没选择任何支付选项)
    var isUseHistoryMethod :Int = 0 //0:默认，1：使用历史UPI支付,2：使用历史Card支付,3:使用其他支付
    //表示当前选中的支付方式
    var curChosePayMethod: PayViewModel.PayMethodEnum? = null

    /** 定义三种支付方式对应的-> [ Item 布局] 和 [ 输入项布局] 对应的 binding对象**/
    lateinit var upiBinding: IncludePayMethodItemBinding //item binding
    lateinit var upiInputBinding: VsUpiInputContentBinding // item child layout binding
    lateinit var upiRootView: View // item child root layout view

    lateinit var netbankBinding: IncludePayMethodItemBinding
    lateinit var netbankInputBinding: VsNetbankingInputContentBinding
    lateinit var netBankRootView: View

    lateinit var creditCardBinding: IncludePayMethodItemBinding
    lateinit var creditCardInputBinding: VsCreditCardInputContentBinding
    lateinit var creditRootView: View


    companion object{
        // 选择的支付公司类型
        const val PAY_CO_CASH_FREE = 0x1
        const val PAY_CO_RAZOR_PAY = 0x2

        //##支付方式1-upi；2-bank;3-Credit [用于回传支付数据<payType>支付类型值 以及 区分选择哪种历史支付类型的逻辑判断]
        const val PAY_UPI = 1
        const val PAY_NETBINKING = 2
        const val PAY_CARD = 3

        //##选择支付方式:1-历史UPI支付方式；1-历史CARD支付方式；3-其他支付方式 [用于记录当前所选择是哪种支付途径]
        const val USE_DEFAULT_PAY = 0 //默认值(没选择任何支付选项)
        const val USE_HISTORY_UPI_PAY = 1
        const val USE_HISTORY_CARD_PAY = 2
        const val USE_OTHTER_PAY = 3
    }

    //选中某个历史支付类型时缓存对应的账户支付数据
    var curChoseUpiHistoryPayData: CFPayInfo?= null
    var curChoseCardHistoryPayData:CFPayInfo?= null

    init {
        handleHistoryClickListener = View.OnClickListener {
            handleHistoryClickLogic(it)
        }
        handleShrinkClickListener = View.OnClickListener {
            handleShrinkClickSwapLogic(it)
        }

        binding.cfPayRoot.setOnClickListener { KeyboardUtils.hideSoftInput(act.requireActivity()) }

        //初始化-设置支付方式Enum
        dynamicInitPayMethod()

        //初始化CashFree-SDK部分不要请求参数
        when (act.companyId) {
            PAY_CO_CASH_FREE -> {
                initCashFreeSdkRequestParams()
            }
            PAY_CO_RAZOR_PAY -> {
                initRazorPayRequestParams()
            }
        }
    }

    private fun handleHistoryClickLogic(it: View) {
        DataBindingUtil.bind<ItemRealPayHistoryInfoBinding>(it)?.run {
            val binding = this

            resetHistoryCheckState(this)
            lastHistoryPayInfoBinding = binding

            val isChecked = binding.inPayHistoryCb.isChecked
            if (isChecked){//open->close
                curChoseUpiHistoryPayData = null
                curChoseCardHistoryPayData = null
                isHasChose = false
                isUseHistoryMethod = USE_DEFAULT_PAY
            }else{
                isHasChose = true
            }

            (it.tag as CFPayInfo).run {
                when (this.payType) {
                    PAY_UPI -> {
                        if (!isChecked) {//close->open
                            curChoseUpiHistoryPayData = this
                            swapHistoryAndOtherPayState(true, USE_HISTORY_UPI_PAY)
                        }
                        binding.inPayHistoryCb.isChecked = !isChecked
                    }
                    PAY_CARD -> {
                        if (!isChecked) {
                            println("--->Card---isChecked")
                            curChoseCardHistoryPayData = this
                            swapHistoryAndOtherPayState(true, USE_HISTORY_CARD_PAY)
                        }
                        binding.inPayHistoryCb.isChecked = !isChecked
                    }
                }
            }
        }
    }

    private fun handleShrinkClickSwapLogic(it: View) {
        if (it.tag is PayViewModel.PayMethodEnum) {
            resetVsLayout2HideState(it.tag as PayViewModel.PayMethodEnum)//重置非当前Vs页面的为隐藏状态
            swapHistoryAndOtherPayState(false, USE_OTHTER_PAY)
            when (it.tag as PayViewModel.PayMethodEnum) {
                PayViewModel.PayMethodEnum.UPI -> {
                    if (it.parent.parent is ViewGroup) {
                        if (!this@RealPayViewClick::upiBinding.isInitialized) {//-未实例化 - OPEN
                            //-获取databinding对象
                            upiBinding = (it.parent.parent as ViewGroup).tag as IncludePayMethodItemBinding
                            //-获取vs对象并设置监听器
                            upiBinding.inPayMethodUpiVs.setOnInflateListener { stub, inflated ->
                                //vs-upi元素
                                upiInputBinding = DataBindingUtil.bind(inflated)!!

                                //设置当前选中的状态
                                setChoseState(true, PayViewModel.PayMethodEnum.UPI)
                                //控制按钮旋转
                                ViewAnimUtils.roateViewAngle(upiBinding.inPayShrinkIb, 0f, 90f)
                            }
                            //-实例化vs
                            if (!upiBinding.inPayMethodUpiVs.isInflated) {
                                upiRootView = upiBinding.inPayMethodUpiVs.viewStub!!.inflate()

                            }
                        } else {//--已经实例化 -CLOSE
                            handleHasInitShrinkStateSwapLogic(PayViewModel.PayMethodEnum.UPI)
                        }
                    }
                }

                PayViewModel.PayMethodEnum.NETBanking -> {
                    if (it.parent.parent is ViewGroup) {
                        if (!this@RealPayViewClick::netbankBinding.isInitialized) {//-未实例化 - OPEN
                            //-获取databinding对象
                            netbankBinding = (it.parent.parent as ViewGroup).tag as IncludePayMethodItemBinding
                            //-获取vs对象并设置监听器
                            netbankBinding.inPayMethodNetbankingVs.setOnInflateListener { stub, inflated ->
                                //vs-netbanking card元素
                                netbankInputBinding = DataBindingUtil.bind(inflated)!!

                                netbankInputBinding.netBankClickListener = handleNetBankClickListener

                                //设置当前选中的状态
                                setChoseState(true, PayViewModel.PayMethodEnum.NETBanking)
                                //控制按钮旋转
                                ViewAnimUtils.roateViewAngle(netbankBinding.inPayShrinkIb, 0f, 90f)
                            }
                            //-实例化vs
                            if (!netbankBinding.inPayMethodNetbankingVs.isInflated) {
                                netBankRootView = netbankBinding.inPayMethodNetbankingVs.viewStub!!.inflate()
                            }
                        } else {//--已经实例化 -CLOSE
                            handleHasInitShrinkStateSwapLogic(PayViewModel.PayMethodEnum.NETBanking)
                        }
                    }
                }

                PayViewModel.PayMethodEnum.CreditCard -> {
                    if (it.parent.parent is ViewGroup) {
                        if (!this@RealPayViewClick::creditCardBinding.isInitialized) {//-未实例化 - OPEN
                            //-获取databinding对象
                            creditCardBinding = (it.parent.parent as ViewGroup).tag as IncludePayMethodItemBinding
                            //-获取vs对象并设置监听器
                            creditCardBinding.inPayMethodCreditCardVs.setOnInflateListener { stub, inflated ->
                                //vs-credit card元素
                                creditCardInputBinding = DataBindingUtil.bind(inflated)!!

                                creditCardInputBinding.cardNo.getEditText().addBlankTextListener(false)

                                creditCardInputBinding.cardMmYy.setOnClickListener {
                                    showChoseCreditExpiryDate()
                                }

                                //设置当前选中的状态
                                setChoseState(true, PayViewModel.PayMethodEnum.CreditCard)
                                //控制按钮旋转
                                ViewAnimUtils.roateViewAngle(creditCardBinding.inPayShrinkIb, 0f, 90f)
                            }
                            //-实例化vs
                            if (!creditCardBinding.inPayMethodCreditCardVs.isInflated) {
                                creditRootView = creditCardBinding.inPayMethodCreditCardVs.viewStub!!.inflate()
                            }
                        } else {//--已经实例化 -CLOSE
                            handleHasInitShrinkStateSwapLogic(PayViewModel.PayMethodEnum.CreditCard)
                        }
                    }
                }
            }
        }
    }

    ///--------- 封装
    /**
     * 处理点击切换已经实例化三种支付布局的状态切换
     */
    private fun handleHasInitShrinkStateSwapLogic(payMethodEnum: PayViewModel.PayMethodEnum) {
        val isChosePayMethodNoNull = curChosePayMethod != null
        var shrinkBtn : View?= null
        when(payMethodEnum){
            PayViewModel.PayMethodEnum.UPI ->{
                if (isChosePayMethodNoNull && payMethodEnum != curChosePayMethod){
                    isHasChose = false
                    upiRootView.setVisibilty(true)
                } else {
                    //非切换vs布局时控制显隐
                    upiRootView.visibility = if (isHasChose) View.GONE else View.VISIBLE
                }
                shrinkBtn = upiBinding.inPayShrinkIb
            }

            PayViewModel.PayMethodEnum.NETBanking -> {
                if (isChosePayMethodNoNull && payMethodEnum != curChosePayMethod){
                    isHasChose = false
                    netBankRootView.setVisibilty(true)
                } else {
                    //非切换vs布局时控制显隐
                    netBankRootView.visibility = if (isHasChose) View.GONE else View.VISIBLE
                }
                shrinkBtn = netbankBinding.inPayShrinkIb
            }

            PayViewModel.PayMethodEnum.CreditCard -> {
                if (isChosePayMethodNoNull && payMethodEnum != curChosePayMethod){
                    isHasChose = false
                    creditRootView.setVisibilty(true)
                } else {
                    //非切换vs布局时控制显隐
                    creditRootView.visibility = if (isHasChose) View.GONE else View.VISIBLE
                }
                shrinkBtn = creditCardBinding.inPayShrinkIb
            }
        }
        //控制按钮旋转
        ViewAnimUtils.roateViewAngle(shrinkBtn, if (isHasChose) 90f else 0f, if (isHasChose) 0f else 90f)

        //设置当前选中的状态
        setChoseState(!isHasChose, if (isHasChose) null else payMethodEnum)
    }

    /**
     * 初始化CashFree-SDK部分不要请求参数
     */
    private lateinit var cashFreeParamsBuilder : CashFree2PayUtils.CFBuilder
    private fun initCashFreeSdkRequestParams() {
        cashFreeParamsBuilder = CashFree2PayUtils.CFBuilder()
            .setAppId(act.appId)
            .setCustomerEmail(act.email)
            .setCustomerPhone(CommonDataModel.mTokenData?.mobile)
            .setOrderAmount(act.payAmount)
            .setOrderId(act.orderId)
            .setToken(act.payToken)
            .setNotifyUrl(act.payNotifyURL)
            .setTestEnv(CUR_RUNTIME_EVN)
            .setIsCallNativeAppForUpi(false)
    }

    /**
     * 初始化CashFree-SDK部分不要请求参数
     */
    private lateinit var razorPayBuilder: RazorPayClientUtils.RazorPayBuilder
    private fun initRazorPayRequestParams() {
        val a1 = BigDecimal(act.payAmount)
        val b1 = BigDecimal("100")
        val result: BigDecimal = a1.multiply(b1) // 相乘结果
        val one = BigDecimal("1")
        // Razor 需要一个整数值，相当于将两位有效的小数位乘100
        val payAmount: String = result.divide(one, 2, BigDecimal.ROUND_HALF_UP).toInt().toString() //保留1位数

        // 也可以使用下面的方法
        // payAmount = String.valueOf((long)(Double.parseDouble(amount) * 10 * 10));
        razorPayBuilder = RazorPayClientUtils.RazorPayBuilder()
            .setListener(this)
            .setAppId(act.appId)
            .setCustomerEmail(act.email)
            .setCustomerPhone(CommonDataModel.mTokenData?.mobile)
            .setOrderAmount(payAmount)
            .setOrderId(act.orderId)
            .setToken(act.payToken)
            .setNotifyUrl(act.payNotifyURL)
            .setTestEnv(CUR_RUNTIME_EVN)
            .setIsCallNativeAppForUpi(false)
    }

    /**
     * 控制显示信用卡的过期时间
     */
    lateinit var curChoseCreditMMYYDate:String
    lateinit var curChoseCreditDate: Date
    private fun showChoseCreditExpiryDate() {
        val pvTime = TimePickerView.Builder(act.requireActivity(), TimePickerView.OnTimeSelectListener { date, v ->
            curChoseCreditDate = date
            val yyMM = DateUtil.getDate(date, DateUtil.Format.MONTH.value)
            creditCardInputBinding.cardMmYy.text = yyMM
            curChoseCreditMMYYDate = yyMM

        }).setType(booleanArrayOf(true, true, false, false, false, false))
            .setLabel("Year", "Month", null, null, null, null)
            .setSubmitText(ContextProvider.getString(R.string.in_dialog_know))//
            .setCancelText(ContextProvider.getString(R.string.dialog_cancel))//
            .setSubmitColor(ContextCompat.getColor(act.requireContext(),R.color.colorPrimary))//
            .setCancelColor(ContextCompat.getColor(act.requireContext(), R.color.C_666666))//
            .build()
        if (this@RealPayViewClick::curChoseCreditDate.isInitialized){
            val ca = Calendar.getInstance()
            ca.time = curChoseCreditDate
            pvTime.setDate(ca)
        }else{
            val ca = Calendar.getInstance()
            ca.add(Calendar.YEAR,1)
            pvTime.setDate(ca)
        }
        pvTime.show()
    }

    /**
     * 设置三种支付方式 对应 当前选择的支付标识
     */
    private fun setChoseState(isChose: Boolean, payMethod: PayViewModel.PayMethodEnum? = null): Unit {
        this.isHasChose = isChose
        this.curChosePayMethod = payMethod
        act.in_cash_sdk_btn.setVisibilty(!(PayViewModel.PayMethodEnum.NETBanking == payMethod && this.isHasChose))
    }

    /**
     * 控制子支付布局的显隐切换(非历史View)
     */
    private fun resetVsLayout2HideState(curChosedEnum: PayViewModel.PayMethodEnum): Unit {//基于view1开展后切换到view2展开情况！
        if (curChosePayMethod != null) {
            if (curChosedEnum != curChosePayMethod) {
                when (curChosedEnum) {
                    PayViewModel.PayMethodEnum.UPI -> {
                        if (this@RealPayViewClick::netBankRootView.isInitialized) {
                            hideRootView(netBankRootView)
                        }
                        if (this@RealPayViewClick::creditRootView.isInitialized) {
                            hideRootView(creditRootView)
                        }
                        when (curChosePayMethod) {
                            PayViewModel.PayMethodEnum.NETBanking -> ViewAnimUtils.roateViewAngle(netbankBinding.inPayShrinkIb, 90f, 0f)
                            PayViewModel.PayMethodEnum.CreditCard -> ViewAnimUtils.roateViewAngle(creditCardBinding.inPayShrinkIb, 90f, 0f)
                        }
                    }
                    PayViewModel.PayMethodEnum.NETBanking -> {
                        if (this@RealPayViewClick::upiRootView.isInitialized) {
                            hideRootView(upiRootView)
                        }
                        if (this@RealPayViewClick::creditRootView.isInitialized) {
                            hideRootView(creditRootView)
                        }
                        when (curChosePayMethod) {
                            PayViewModel.PayMethodEnum.UPI -> ViewAnimUtils.roateViewAngle(upiBinding.inPayShrinkIb, 90f, 0f)
                            PayViewModel.PayMethodEnum.CreditCard -> ViewAnimUtils.roateViewAngle(creditCardBinding.inPayShrinkIb, 90f, 0f)
                        }
                    }
                    PayViewModel.PayMethodEnum.CreditCard -> {
                        if (this@RealPayViewClick::upiRootView.isInitialized) {
                            hideRootView(upiRootView)
                        }
                        if (this@RealPayViewClick::netBankRootView.isInitialized) {
                            hideRootView(netBankRootView)
                        }
                        when (curChosePayMethod) {
                            PayViewModel.PayMethodEnum.UPI -> ViewAnimUtils.roateViewAngle(upiBinding.inPayShrinkIb, 90f, 0f)
                            PayViewModel.PayMethodEnum.NETBanking -> ViewAnimUtils.roateViewAngle(netbankBinding.inPayShrinkIb, 90f, 0f)
                        }
                    }
                }
            }
        }
    }

    private fun hideRootView(vararg views: View): Unit {
        views.forEach {
            it.visibility = View.GONE
        }
    }

    private fun swapHistoryAndOtherPayState(isUseHistoryPay:Boolean,chosePayType:Int): Unit {
        if (isUseHistoryPay){//切换到历史支付方式—>重置其他支付方式的选中状态
            isUseHistoryMethod = chosePayType

            when(curChosePayMethod){
                PayViewModel.PayMethodEnum.UPI -> {
                    //重置其他支付状态
                    if (this@RealPayViewClick::upiRootView.isInitialized) {
                        hideRootView(upiRootView)
                        if (this.curChosePayMethod!=null) controlRoateView(upiBinding.inPayShrinkIb)
                    }
                }
                PayViewModel.PayMethodEnum.NETBanking -> {
                    if (this@RealPayViewClick::netBankRootView.isInitialized) {
                        hideRootView(netBankRootView)
                        if (this.curChosePayMethod!=null) controlRoateView(netbankBinding.inPayShrinkIb)
                    }
                }
                PayViewModel.PayMethodEnum.CreditCard -> {
                    if (this@RealPayViewClick::creditRootView.isInitialized) {
                        hideRootView(creditRootView)
                        if (this.curChosePayMethod!=null) controlRoateView(creditCardBinding.inPayShrinkIb)
                    }
                }
            }
            //其他支付方式置为null
            this.curChosePayMethod = null
        }else{//切换到其他支付方式->重置历史支付方式的选中状态
            if (isUseHistoryMethod == USE_HISTORY_UPI_PAY || isUseHistoryMethod == USE_HISTORY_CARD_PAY){
                isHasChose = !isHasChose
            }
            resetHistoryCheckState()

            isUseHistoryMethod = chosePayType
            curChoseUpiHistoryPayData = null
            curChoseCardHistoryPayData = null
        }
    }

    private fun resetHistoryCheckState(curPayHistoryBinding:ItemRealPayHistoryInfoBinding?=null){
        KeyboardUtils.hideSoftInput(act.requireActivity())
        act.in_cash_sdk_btn.setVisibilty(true)

        if (lastHistoryPayInfoBinding != null && lastHistoryPayInfoBinding != curPayHistoryBinding){
            lastHistoryPayInfoBinding!!.inPayHistoryCb.isChecked = false
            when(isUseHistoryMethod){
                USE_HISTORY_UPI_PAY -> {
                    curChoseUpiHistoryPayData = null
                }
                USE_HISTORY_CARD_PAY -> {
                    curChoseCardHistoryPayData = null
                }
            }
            isUseHistoryMethod = USE_DEFAULT_PAY
            lastHistoryPayInfoBinding = null
        }
    }

    private fun controlRoateView(roateView: View): Unit {
        ViewAnimUtils.roateViewAngle(roateView, 90f, 0f)
    }

    /**
     *  触发点击NetBanking5个选项时监听器
     */
    private var handleNetBankClickListener: View.OnClickListener = View.OnClickListener {
        when(it){
            netbankInputBinding.sbi->{
                println("--sbi 2 sdk")
                if (act.companyId == PAY_CO_CASH_FREE) {
                    cashFreeParamsBuilder
                        .setPayMethod(CashFree2PayUtils.CashFreePayMethodEnum.NET_BANKING)
                        .setNetBankingCode("3005")
                        .build().doPayment(act.requireActivity())
                } else if (act.companyId == PAY_CO_RAZOR_PAY) {
                    razorPayBuilder
                        .setPayMethod(RazorPayClientUtils.RazorPayMethodEnum.NET_BANKING)
                        .setNetBankingCode("BARB_R")
                        .build().doPayment(act.requireActivity())
                }

            }
            netbankInputBinding.icic->{
                println("--icic 2 sdk")
                if (act.companyId == PAY_CO_CASH_FREE) {
                    cashFreeParamsBuilder
                        .setPayMethod(CashFree2PayUtils.CashFreePayMethodEnum.NET_BANKING)
                        .setNetBankingCode("3022")
                        .build().doPayment(act.requireActivity())
                } else if (act.companyId == PAY_CO_RAZOR_PAY) {
                    razorPayBuilder
                        .setPayMethod(RazorPayClientUtils.RazorPayMethodEnum.NET_BANKING)
                        .setNetBankingCode("ICIC")
                        .build().doPayment(act.requireActivity())
                }

            }
            netbankInputBinding.hdfc->{
                println("--hdfc 2 sdk")
                if (act.companyId == PAY_CO_CASH_FREE) {
                    cashFreeParamsBuilder
                        .setPayMethod(CashFree2PayUtils.CashFreePayMethodEnum.NET_BANKING)
                        .setNetBankingCode("3021")
                        .build().doPayment(act.requireActivity())
                } else if (act.companyId == PAY_CO_RAZOR_PAY) {
                    razorPayBuilder
                        .setPayMethod(RazorPayClientUtils.RazorPayMethodEnum.NET_BANKING)
                        .setNetBankingCode("HDFC")
                        .build().doPayment(act.requireActivity())
                }

            }
            netbankInputBinding.axis->{
                println("--axis 2 sdk")
                if (act.companyId == PAY_CO_CASH_FREE) {
                    cashFreeParamsBuilder
                        .setPayMethod(CashFree2PayUtils.CashFreePayMethodEnum.NET_BANKING)
                        .setNetBankingCode("3003")
                        .build().doPayment(act.requireActivity())
                } else if (act.companyId == PAY_CO_RAZOR_PAY) {
                    razorPayBuilder
                        .setPayMethod(RazorPayClientUtils.RazorPayMethodEnum.NET_BANKING)
                        .setNetBankingCode("UTIB")
                        .build().doPayment(act.requireActivity())
                }

            }
            netbankInputBinding.moreBank->{
                println("--moreBank 2 sdk")
                reqNetBankdingInfoEnum {
                    DialogUtils.showCashFreeBankListDialog(act.requireActivity(),it,object : DialogUtils.NetBankingChoseListener{
                        override fun choseBank(bankCode: String) {
                            println("-- chose bank 2 pay--->")
                            if (act.companyId == PAY_CO_CASH_FREE) {
                                cashFreeParamsBuilder
                                    .setPayMethod(CashFree2PayUtils.CashFreePayMethodEnum.NET_BANKING)
                                    .setNetBankingCode(bankCode)
                                    .build().doPayment(act.requireActivity())
                            } else if (act.companyId == PAY_CO_RAZOR_PAY) {
                                razorPayBuilder
                                    .setPayMethod(RazorPayClientUtils.RazorPayMethodEnum.NET_BANKING)
                                    .setNetBankingCode(bankCode)
                                    .build().doPayment(act.requireActivity())
                            }
                        }
                    })
                }
            }
        }
    }

    /**
     * req all select info
     */
    //cache netbanking data list
    private lateinit var netbankingList:List<UserType>
    private fun reqNetBankdingInfoEnum(showMoreBank:(List<UserType>)-> Unit) {
        if (this@RealPayViewClick::netbankingList.isInitialized){
            showMoreBank(netbankingList)
        }else {
            realPayViewModel.reqNetBankdingInfoEnum(act.companyId == PAY_CO_CASH_FREE){
                netbankingList = it

                DialogUtils.showCashFreeBankListDialog(act.requireActivity(),it,object : DialogUtils.NetBankingChoseListener{
                    override fun choseBank(bankCode: String) {
                        println("-- chose bank 2 pay--->")
                        if (act.companyId == PAY_CO_CASH_FREE) {
                            cashFreeParamsBuilder
                                .setPayMethod(CashFree2PayUtils.CashFreePayMethodEnum.NET_BANKING)
                                .setNetBankingCode(bankCode)
                                .build().doPayment(act.requireActivity())
                        } else if (act.companyId == PAY_CO_RAZOR_PAY) {
                            razorPayBuilder
                                .setPayMethod(RazorPayClientUtils.RazorPayMethodEnum.NET_BANKING)
                                .setNetBankingCode(bankCode)
                                .build().doPayment(act.requireActivity())
                        }

                    }
                })
            }
        }
    }

    /**
     * #触发cashFree支付流程#
     */
    fun cashFreeSdk2PayClick(payNow: View): Unit {
        if (isHasChose) {
            //选择历史支付方式UPI处理逻辑
            if (isUseHistoryMethod == USE_HISTORY_UPI_PAY) {
                println("--->>pay call history upi...")
                curChoseUpiHistoryPayData?.run {
                    if (act.companyId == PAY_CO_CASH_FREE) {
                        cashFreeParamsBuilder
                            .setPayMethod(CashFree2PayUtils.CashFreePayMethodEnum.UPI_METHOD)
                            .setUpiAccount(this.upiAccount)//这个参数时历史参数
                            .build().doPayment(act.requireActivity())
                    } else if (act.companyId == PAY_CO_RAZOR_PAY) {
                        razorPayBuilder
                            .setPayMethod(RazorPayClientUtils.RazorPayMethodEnum.UPI_METHOD)
                            .setUpiAccount(this.upiAccount)
                            .build().doPayment(act.requireActivity())
                    }

                }
            }
            //选择历史支付方式CARD处理逻辑
            else if (isUseHistoryMethod == USE_HISTORY_CARD_PAY){
                println("--->>pay call history card...")
                curChoseCardHistoryPayData?.run {
                    if(!TextUtil.isEmpty(this.cardNoMmYy) && this.cardNoMmYy!!.split("-").size == 2){
                        val cardExpiryMMYY = this.cardNoMmYy!!.split("-")
                        println("--->cardExpiryMMyy-->" + cardExpiryMMYY[0] + "," + cardExpiryMMYY[1])
                        if (act.companyId == PAY_CO_CASH_FREE) {
                            cashFreeParamsBuilder
                                .setPayMethod(CashFree2PayUtils.CashFreePayMethodEnum.CREDIT_CARD)
                                .setCreditCardNo(removeBlank(this.cardNo!!))
                                .setCreditCardHodler(this.cardHolderName)
                                .setCreditCardExpiryYY(cardExpiryMMYY[1])
                                .setCreditCardExpiryMM(cardExpiryMMYY[0])
                                .setCreditCardCvv(this.cardCvv)
                                .build().doPayment(act.requireActivity())
                        } else if (act.companyId == PAY_CO_RAZOR_PAY) {
                            razorPayBuilder
                                .setPayMethod(RazorPayClientUtils.RazorPayMethodEnum.CREDIT_CARD)
                                .setCreditCardNo(removeBlank(this.cardNo!!))
                                .setCreditCardHolder(this.cardHolderName)
                                .setCreditCardExpiryYY(cardExpiryMMYY[1])
                                .setCreditCardExpiryMM(cardExpiryMMYY[0])
                                .setCreditCardCvv(this.cardCvv)
                                .build().doPayment(act.requireActivity())
                        }

                    }
                }
            }
            //选择其他支付方式处理逻辑
            else if(isUseHistoryMethod == USE_OTHTER_PAY){
                when (curChosePayMethod) {
                    PayViewModel.PayMethodEnum.UPI -> {
                        if (upiInputBinding.payUpiEd.isInputEmpty) {
                            ToastUtils.showShort(R.string.pay_method_parasm_input_complete)
                        } else {
                            println("--->>pay call upi...")
                            if (act.companyId == PAY_CO_CASH_FREE) {
                                cashFreeParamsBuilder
                                    .setPayMethod(CashFree2PayUtils.CashFreePayMethodEnum.UPI_METHOD)
                                    .setUpiAccount(upiInputBinding.payUpiEd.inputContent)
                                    .build().doPayment(act.requireActivity())
                            } else if (act.companyId == PAY_CO_RAZOR_PAY) {
                                razorPayBuilder
                                    .setPayMethod(RazorPayClientUtils.RazorPayMethodEnum.UPI_METHOD)
                                    .setUpiAccount(upiInputBinding.payUpiEd.inputContent)
                                    .build().doPayment(act.requireActivity())
                            }

                        }
                    }
                    PayViewModel.PayMethodEnum.CreditCard -> {
                        val isVaildMMYY = creditCardInputBinding.cardMmYy.text.split("-").size == 2
                        if (TextUtil.isAllEmpty(removeBlank(creditCardInputBinding.cardNo.inputContent),
                                creditCardInputBinding.cardHolderName.inputContent,
                                creditCardInputBinding.cardMmYy.text.toString(),
                                creditCardInputBinding.cardCvv.inputContent) || !isVaildMMYY) {
                            ToastUtils.showShort(R.string.pay_method_parasm_input_complete)
                        } else {
                            println("--->>pay call credit card...")
                            val cardExpiryMMYY = creditCardInputBinding.cardMmYy.text.split("-")
                            // 校验银行卡号是否有效（规则校验）
                            val cardNumber = removeBlank(creditCardInputBinding.cardNo.inputContent)
                            if (RegularUtil.isValidCardNumber(cardNumber)) {
                                when (act.companyId) {
                                    PAY_CO_CASH_FREE -> cashFreeParamsBuilder
                                        .setPayMethod(CashFree2PayUtils.CashFreePayMethodEnum.CREDIT_CARD)
                                        .setCreditCardNo(cardNumber)
                                        .setCreditCardHodler(creditCardInputBinding.cardHolderName.inputContent)
                                        .setCreditCardExpiryYY(cardExpiryMMYY[1])
                                        .setCreditCardExpiryMM(cardExpiryMMYY[0])
                                        .setCreditCardCvv(creditCardInputBinding.cardCvv.inputContent)
                                        .build().doPayment(act.requireActivity())
                                    PAY_CO_RAZOR_PAY ->
                                        razorPayBuilder
                                            .setPayMethod(RazorPayClientUtils.RazorPayMethodEnum.CREDIT_CARD)
                                            .setCreditCardNo(cardNumber)
                                            .setCreditCardHolder(creditCardInputBinding.cardHolderName.inputContent)
                                            .setCreditCardExpiryYY(cardExpiryMMYY[1])
                                            .setCreditCardExpiryMM(cardExpiryMMYY[0])
                                            .setCreditCardCvv(creditCardInputBinding.cardCvv.inputContent)
                                            .build().doPayment(act.requireActivity())
                                }
                            } else {
                                ToastUtils.showShort("Please check if your bank card number is correct!")
                            }
                        }
                    }
                }
            }
        } else {
            ToastUtils.showShort(R.string.pay_no_select_method)
        }
    }

    /**
     * 接收CF-SDK支付处理回调
     */
    fun onActivityResult(orderId:String,orderAmount:String,requestCode: Int, resultCode: Int, data: Intent?) {
        /////
        if (data == null || data.extras == null) {
            Navigation.findNavController(act.cf_pay_root).navigateUp()
            return
        }
        val bundle = data.extras
        if (bundle != null) {
            println("================>>支付返回的参数 S<<============")
            for (key in bundle.keySet()) {
                if (bundle.getString(key) != null) {
                    println(key + " : " + bundle.getString(key))
                }
            }
            println("================>>支付返回的参数 E<<============")
            val txStatus = bundle.getString("txStatus")

            if (TextUtil.isEmpty(bundle.getString("referenceId"))) {
                println("----状态不为SUCCESS->Finish")
                Navigation.findNavController(act.cf_pay_root).navigateUp()
                return
            }

            //## 获取相应的支付回调数据
            val paymentMode =
                bundle.getString(/*CFPaymentService.PARAM_PAYMENT_MODES*/"paymentMode")
            val referenceId = bundle.getString("referenceId")
            val txMsg = bundle.getString("txMsg")
            val txTime = bundle.getString("txTime")

            var payInfo = ""
            //##支付方式1-upi；2-bank;3-Credit
            var payType: Int = 0
            //##支付账号
            var payAccount: String = ""
            when (curChosePayMethod) {
                PayViewModel.PayMethodEnum.UPI -> {
                    payType = PAY_UPI
                    payAccount = upiInputBinding.payUpiEd.inputContent
                    payInfo = GsonUtils.toJsonString(
                        CFPayInfo(
                            payType = PAY_UPI,
                            upiAccount = upiInputBinding.payUpiEd.inputContent
                        )
                    )


                }
                PayViewModel.PayMethodEnum.NETBanking -> {
                    payType = PAY_NETBINKING
                }
                PayViewModel.PayMethodEnum.CreditCard -> {
                    payType = PAY_CARD
                    payAccount = removeBlank(creditCardInputBinding.cardNo.inputContent)

                    val cfCardPayInfo = CFPayInfo(
                        payType = PAY_CARD,
                        cardNo = removeBlank(creditCardInputBinding.cardNo.inputContent),
                        cardCvv = creditCardInputBinding.cardCvv.inputContent,
                        cardHolderName = creditCardInputBinding.cardHolderName.inputContent,
                        cardNoMmYy = creditCardInputBinding.cardMmYy.text.toString()
                    )

                    payInfo = GsonUtils.toJsonString(cfCardPayInfo)

                }
            }
            // 如果选择的是历史记录的情况下
            if (curChosePayMethod == null) {
                when (isUseHistoryMethod) {
                    USE_HISTORY_UPI_PAY -> {
                        payType = PAY_UPI
                        payAccount = curChoseUpiHistoryPayData?.upiAccount!!
                        payInfo = GsonUtils.toJsonString(curChoseUpiHistoryPayData)

                    }

                    USE_HISTORY_CARD_PAY -> {
                        payType = PAY_CARD
                        payAccount = curChoseCardHistoryPayData?.cardNo!!
                        payInfo = GsonUtils.toJsonString(curChoseCardHistoryPayData)

                    }
                }
            }

            if (!TextUtil.isEmpty(data.getStringExtra("txMsg"))) {
                ToastUtils.showShort("Reason：" + data.getStringExtra("txMsg"))
            }

            println(
                "发送到服务器支付回调参数=" + orderId + ",\t"
                        + orderAmount + ",\t"
                        + paymentMode + ",\t"
                        + referenceId + ",\t"
                        + txMsg + ",\t"
                        + txStatus + ",\t"
                        + txTime + ",\t"
                        + payInfo + ",\t"
                        + payType + ",\t"
                        + payAccount
            )

            /////
            realPayViewModel.onActivityResult(
                orderId,
                orderAmount,

                paymentMode,
                referenceId,
                txMsg,
                txStatus,
                txTime,
                payInfo,
                payType,
                payAccount,

                requestCode,
                resultCode,
                data
            )
        }
    }

    /**
     * RazorPay 支付成功回调
     */
    override fun onSucceeded(orderId:String,orderAmount:String,razorpayPaymentId: String, paymentData: com.razorpay.PaymentData?) {
        //## 获取相应的支付回调数据
        val paymentMode = ""
        val txMsg = ""
        val txTime = ""
        var payInfo = ""
        val txStatus = "SUCCESS"

        //##支付方式1-upi；2-bank;3-Credit
        var payType = 0
        //##支付账号
        var payAccount : String = ""
        try {
            when (curChosePayMethod) {
                PayViewModel.PayMethodEnum.UPI -> {
                    payType = PAY_UPI
                    payAccount = upiInputBinding.payUpiEd.inputContent
                    payInfo = if (isUseHistoryMethod == USE_HISTORY_UPI_PAY) {
                        GsonUtils.toJsonString(curChoseUpiHistoryPayData)
                    } else {
                        GsonUtils.toJsonString(
                            CFPayInfo(
                                payType = PAY_UPI,
                                upiAccount = upiInputBinding.payUpiEd.inputContent
                            )
                        )
                    }
                }
                PayViewModel.PayMethodEnum.NETBanking -> {
                    payType = PAY_NETBINKING
                }
                PayViewModel.PayMethodEnum.CreditCard -> {
                    payType = PAY_CARD
                    payAccount = removeBlank(creditCardInputBinding.cardNo.inputContent)

                    val cfCardPayInfo = CFPayInfo(
                        payType = PAY_CARD,
                        cardNo = removeBlank(creditCardInputBinding.cardNo.inputContent),
                        cardCvv = creditCardInputBinding.cardCvv.inputContent,
                        cardHolderName = creditCardInputBinding.cardHolderName.inputContent,
                        cardNoMmYy = creditCardInputBinding.cardMmYy.text.toString()
                    )
                    payInfo = if (isUseHistoryMethod == USE_HISTORY_CARD_PAY) {
                        GsonUtils.toJsonString(curChoseCardHistoryPayData)
                    } else {
                        GsonUtils.toJsonString(cfCardPayInfo)
                    }
                }
            }
            // 如果选择的是历史记录的情况
            if (curChosePayMethod == null) {
                when (isUseHistoryMethod) {
                    USE_HISTORY_UPI_PAY -> {
                        payType = PAY_UPI
                        payAccount = curChoseUpiHistoryPayData?.upiAccount!!
                        payInfo = GsonUtils.toJsonString(curChoseUpiHistoryPayData)

                    }

                    USE_HISTORY_CARD_PAY -> {
                        payType = PAY_CARD
                        payAccount = curChoseCardHistoryPayData?.cardNo!!
                        payInfo = GsonUtils.toJsonString(curChoseCardHistoryPayData)

                    }
                }
            }
        }catch (e :Exception){

        }
        /////
        realPayViewModel.onSucceeded(
            orderId,
            orderAmount,
            //
            paymentMode,
            txMsg,
            txStatus,
            txTime,
            payInfo,
            payType,
            payAccount,
            //
            razorpayPaymentId,
            paymentData)
    }

    /**
     * RazorPay 支付失败回调
     */
    override fun onFailed(description: String?, paymentData: PaymentData?) {
        ToastUtils.showShort(R.string.pay_failed)
        Navigation.findNavController(act.in_pay_custon_content_view).navigateUp()
    }

    /**
     * 动态添加历史支付记录
     */
    fun dynamicAddHistoryItem(realHistoryPayBinding: VsRealPayHistoryInfoBinding, datas: MutableList<CFPayInfo>) {
        realHistoryPayBinding.run {
            val historyVg = this.inHistoryViewGroup
            dynamicAddChildView<CFPayInfo, ItemRealPayHistoryInfoBinding>(historyVg, R.layout.item_real_pay_history_info,datas){
                    binding,index,data ->
                val rootLayoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ScreenAdapterUtils.dp2px(act.requireContext(),50))
                binding.root.layoutParams = rootLayoutParams
                //
                binding.item = data
                binding.index = index
                binding.root.tag = data
                binding.root.setOnClickListener(handleHistoryClickListener)
            }
        }
    }

    /**
     * 动态添加支付方式
     */
    private fun dynamicInitPayMethod() {
        val payMethods = listOf<PayViewModel.PayMethodEnum>(PayViewModel.PayMethodEnum.UPI, PayViewModel.PayMethodEnum.NETBanking, PayViewModel.PayMethodEnum.CreditCard)

        dynamicAddChildView<PayViewModel.PayMethodEnum, IncludePayMethodItemBinding>(act.in_pay_method_container,R.layout.include_pay_method_item,payMethods){
            binding,index,data ->
            binding.item = data
            binding.index = index
            binding.itemChildListener = handleShrinkClickListener
            binding.root.tag = binding
        }
    }

}


fun View.setVisibilty(isShowView:Boolean) {
    this.visibility = if (isShowView) View.VISIBLE else View.GONE
}
