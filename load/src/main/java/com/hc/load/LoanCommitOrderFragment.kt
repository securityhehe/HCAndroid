package com.hc.load

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.blankj.utilcode.util.NetworkUtils
import com.blankj.utilcode.util.StringUtils
import com.blankj.utilcode.util.ToastUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.hc.data.MenuData
import com.hc.data.NavContents
import com.hc.data.mall.CheckOrder
import com.hc.data.mall.GoodsSx
import com.hc.data.mall.IListData
import com.hc.data.mall.PlatformExt
import com.hc.data.order.OrderBillRec
import com.hc.data.user.UserType
import com.hc.load.databinding.DialogStagItemBinding
import com.hc.load.databinding.FragmentLoanCommitOrderLayoutBinding
import com.hc.load.epoch.callback.ObservableObject
import com.hc.load.epoch.utils.EpochLogicUtils
import com.hc.load.utils.LocalManageUtil
import com.hc.load.vm.CollectAppInfo
import com.hc.load.vm.CommitOrderViewModel
import com.hc.uicomponent.base.CommonDataViewModel
import com.hc.uicomponent.annotation.BindViewModel
import com.hc.uicomponent.base.BaseViewModel
import com.hc.uicomponent.base.jumpDeepLikPage
import com.hc.uicomponent.config.Constants
import com.hc.uicomponent.menu.BaseMenuViewModel
import com.hc.uicomponent.menu.BasePopupWindow
import com.hc.uicomponent.provider.CommonProvider
import com.hc.uicomponent.provider.ContextProvider
import com.hc.uicomponent.stack.ActivityStack
import com.hc.uicomponent.utils.*
import frame.utils.StringFormat
import java.io.File
import java.io.FileInputStream
import java.math.BigDecimal
import java.util.*

class LoanCommitOrderFragment : PermissionBaseFragment<FragmentLoanCommitOrderLayoutBinding>(R.layout.fragment_loan_commit_order_layout), java.util.Observer {

    @BindViewModel
    var orderViewModel: CommitOrderViewModel? = null
    var mCollectAppInfo: CollectAppInfo? = null
    var gsx: GoodsSx? = null
    var isAuthJoinPage: Boolean = false
    var indexImgPath: String? = ""
    var isCertifyBank: Boolean = false

    @BindViewModel
    var baseMenuModel: BaseMenuViewModel? = null
    private val x100 = ScreenAdapterUtils.dp2px(ContextProvider.app, 100)
    private val x23 = ScreenAdapterUtils.dp2px(ContextProvider.app, 23)
    private val x187 = ScreenAdapterUtils.dp2px(ContextProvider.app, -70)

    init {
        ObservableObject.getInstance().addObserver(this)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycle.addObserver(orderViewModel as LifecycleObserver)
        arguments?.apply {
            val sx = this.getSerializable(Constants.ORDER_COMMIT_GOODS_SX)
            sx?.let {
                if (sx is GoodsSx) {
                    gsx = sx
                }
            }
            isAuthJoinPage = this.getBoolean(Constants.ORDER_COMMIT_AUTO_ENTRY_PAGE)

            indexImgPath = this.getString(Constants.ORDER_NBFC_IMG_URL)
            isCertifyBank = this.getBoolean(Constants.ORDER_COMMIT_GET_CERTIFY_BANK_STATE)
        }

        mCollectAppInfo = CollectAppInfo()
        mCollectAppInfo?.collectAppInfo()

        orderViewModel?.apply {
            checkOrderData.observe(viewLifecycleOwner, Observer {
                showAgreement(gsx, it)
                updateOrderCommitPage(it)

            })
            mGotoWaitingPage.observe(viewLifecycleOwner, Observer { data ->
                ContextProvider.mNavIdProvider?.getCommitOrderLoadingNavId()?.let {
                    val data = bundleOf(Pair(Constants.ORDER_NUM, data))
                    Navigation.findNavController(view).navigate(it, data)
                }
            })
        }
        mFragmentBinding.run {
            ivNbfcImg.visibility = View.GONE
            //金额选择。
            changeMoney.setOnClickListener {
                choosePrice(gsx, tvLoanMoney, changeMoney)
            }

            commit.setOnClickListener {
                orderCommitClick()
            }
            bankEdit.setOnClickListener {
                modifyBankCardNoClick(view)
            }
        }
        callCheckOrder()
    }

    //跳转到修改银行卡认证界面，重新填写银行卡信息
    private fun modifyBankCardNoClick(view: View) {
        val url = String.format(NavContents.loanBank, true);
        orderViewModel?.jumpDeepLikPage(view, null, url)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val view = ViewModelProvider(requireActivity()).get(CommonDataViewModel::class.java)
        view.bankData.observe(this, Observer {
            it?.let {
                mFragmentBinding.tvBankNo.text = it
                mFragmentBinding.bankEdit.visibility = View.GONE
                isCertifyBank = true
            }
        })
    }

    private fun updateOrderCommitPage(checkOrder: CheckOrder) {
        gsx?.run {

            //注意:自动进入此页面默认选择的值应为maxAmount
            mFragmentBinding.run {
                //如果复贷用户的银行卡信息已处于[已认证]状态，则不需要修改
                if (isAuthJoinPage) {
                    bankEdit.visibility = if (!isCertifyBank) {
                        View.VISIBLE
                    } else {
                        View.GONE
                    }
                }

                //借款金额
                tvLoanMoney.text = StringFormat.showMoneyWithSymbol(requireContext(), "${gsx?.choseAmount}")

                //借款期限
                tvLoanDuration.text = String.format(resources.getString(R.string.mall_order_submit_loan_duration), "${gsx?.borrowDays}")

                //借款日利率
                stageTotalIncludeInterestLabel.text = String.format(resources.getString(R.string.dialog_mall_order_total_interest), "${gsx?.dayRate}")

                //协议显示与点击事件的设置
                showAgreement(gsx, checkOrder)

                //显示银行卡信息
                checkOrder.bankCard?.run {
                    tvBankNo.text = bankNo
                }

                //动态控制相关费用初始化数据以及收缩效果
                val platformExt = checkOrder.platformExt
                showLoanAndFeeDetails(platformExt)

                //显示分期信息、显示总利息、显示应还金额
                showStageInfoAndTotalInterestAndRepayAmount(checkOrder)

                //显示NBFC图片
                ivNbfcImg.visibility = View.GONE
                indexImgPath?.run {
                    ivNbfcImg.loadImageWithCallBack(this) {
                        ivNbfcImg.visibility = View.VISIBLE
                        ivNbfcImg.setImageBitmap(it)
                    }
                }

            }
        }
    }

    private fun callCheckOrder() {
        gsx?.run {
            val goodsPrice = if (isAuthJoinPage) {
                maxAmount
            } else {
                choseAmount
            }
            orderViewModel?.checkOrder(goodsPrice, id)
        }
    }

    private fun showAgreement(goodsSx: GoodsSx?, checkOrder: CheckOrder?) {
        if (goodsSx == null || checkOrder == null) return
        val spannable = SpannableString(getString(R.string.dialog_mall_order_commit_agreement_tip2))
        //设置loan agreement协议的点击事件
        val loanAgreementSpannableClick = object : ClickableSpan() {
            override fun onClick(widget: View) {
                if (!TextUtil.isEmpty(checkOrder.contentUrlV1)) {
                    CommonProvider.instance?.getWebViewNavId()?.let {
                        val a = resources.getString(R.string.mall_order_submit_loan_agreement)
                        baseMenuModel?.jumpWebFragment(mFragmentBinding.root,a, checkOrder.contentUrlV1 ?: "")
                    }
                }
            }

            override fun updateDrawState(ds: TextPaint) {
                ds.isUnderlineText = false
            }
        }

        //设置sanction letter的点击事件
        val sanctionLetterSpannableClick = object : ClickableSpan() {
            override fun onClick(widget: View) {
                if (!TextUtil.isEmpty(checkOrder.contentUrlV2)) {
                    CommonProvider.instance?.getWebViewNavId()?.let {
                        val a = resources.getString(R.string.mall_order_submit_sanction_letter)
                        baseMenuModel?.jumpWebFragment(mFragmentBinding.root, a,checkOrder.contentUrlV2 ?: "")
                    }
                }
            }

            override fun updateDrawState(ds: TextPaint) {
                ds.isUnderlineText = false
            }
        }

        /** 定义协议对应的下标，用于控制颜色范围&点击范围 **/
        val loanStart = 73
        val loanEnd = 93
        val loanLetterStart = 97
        val loanLetterEnd = 118
        spannable.setSpan(loanAgreementSpannableClick, loanStart, loanEnd, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
        spannable.setSpan(sanctionLetterSpannableClick, loanLetterStart, loanLetterEnd, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)

        spannable.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.colorPrimary)),
            loanStart,
            loanEnd,
            Spannable.SPAN_EXCLUSIVE_INCLUSIVE
        )
        spannable.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.colorPrimary)),
            loanLetterStart,
            loanLetterEnd,
            Spannable.SPAN_EXCLUSIVE_INCLUSIVE
        )

        mFragmentBinding.tvLoanAgreement2.apply {
            text = spannable
            movementMethod = LinkMovementMethod.getInstance()
        }
    }


    private fun showLoanAndFeeDetails(platformExt: PlatformExt) {
        mFragmentBinding.run {
            // 1 展示，2 隐藏，3 不展示
            when (platformExt.isShow) {
                1 -> {
                    groupFees.visibility = View.VISIBLE
                    ivFeesSwitch.visibility = View.VISIBLE
                    ivFeesSwitch.animate().rotation(0f)
                }
                2 -> {
                    groupFees.visibility = View.GONE
                    ivFeesSwitch.visibility = View.VISIBLE
                    ivFeesSwitch.animate().rotation(-90f)
                }
                3 -> {
                    groupFees.visibility = View.GONE
                    ivFeesSwitch.visibility = View.GONE
                }
            }
            ivFeesSwitch.setOnClickListener {
                if (groupFees.visibility == View.VISIBLE) {
                    groupFees.visibility = View.GONE
                    ivFeesSwitch.animate().rotation(-90f)
                } else {
                    groupFees.visibility = View.VISIBLE
                    ivFeesSwitch.animate().rotation(0f)
                }
            }
            if (platformExt.isShow == Constants.NUMBER_1 || platformExt.isShow == Constants.NUMBER_2) {
                if (LocalManageUtil.judyIsEnglishType(requireContext())) {
                    tvTechFeeLabel.text = platformExt.technicalServiceFeeEn
                    tvGstFeeLabel.text = platformExt.gstEn
                    tvCommissionFeeLabel.text = platformExt.commissionPaymentEn
                } else {
                    tvTechFeeLabel.text = platformExt.technicalServiceFeeIn
                    tvGstFeeLabel.text = platformExt.gstIn
                    tvCommissionFeeLabel.text = platformExt.commissionPaymentIn
                }
            }
        }

    }

    /**
     * 显示分期信息、 所需费用
     */
    private fun showStageInfoAndTotalInterestAndRepayAmount(checkOrder: CheckOrder) {
        checkOrder.run {
            orderBills?.let {
                if (it.isNotEmpty()) {
                    val orderBill = it[0]
                    val context = requireContext()
                    mFragmentBinding.run {
                        tvTechFeeMoney.text = StringFormat.showMoneyWithSymbol(context, orderBill.technicalServiceFee)
                        tvGstFeeMoney.text = StringFormat.showMoneyWithSymbol(context, orderBill.gst)
                        tvCommissionFeeMoney.text = StringFormat.showMoneyWithSymbol(context, orderBill.commissionPayment)
                        tvAmountMoney.text = StringFormat.showMoneyWithSymbol(context, orderBill.amountYouGet)
                        // 多期、单期通过 checkOrder.orderBills 的个数
                        if (it.size > 1) {
                            stagItemRoot.removeAllViews()
                            dynamicAddChildView<OrderBillRec, DialogStagItemBinding>(stagItemRoot, R.layout.dialog_stag_item, it) { bind, _, data ->
                                bind.item = data
                            }
                        }
                        tvStageInterestMoney.text = StringFormat.showMoneyWithSymbol(context, orderBill.interest)
                        tvStageRepaymentMoney.text = StringFormat.showMoneyWithSymbol(context, orderBill.loanAmount)
                    }
                }
            }

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        ObservableObject.getInstance().deleteObserver(this)
        viewLifecycleOwner.lifecycle.removeObserver(orderViewModel as LifecycleObserver)
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
                    title = view.context.getString(titleRes)
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

    fun orderCommitClick() {
        reqSmsPermissionAndGetNowMoney()
    }

    override fun callPermissionOk() {
        super.callPermissionOk()
        if (!NetworkUtils.isConnected()) {
            ToastUtils.showShort(R.string.app_access_server_error)
            return
        }

        if (!mFragmentBinding.orderCommitCheck.isChecked) {
            ToastUtils.showShort(R.string.dialog_main_order_commit_uncheck_tip)
            return
        }

        gsx?.run {
            if (this.choseAmount == 0.0) {
                ToastUtils.showShort(R.string.dialog_mall_order_commit_amount_more_than_0_tip)
                return@run
            }
            //埋点统计
            dataStatistics()
            //提交订单
            orderViewModel?.commitOrder(this)
        }
    }

    //埋点统计
    private fun dataStatistics() {
        FacebookEventUtils.logPurchase(requireContext(), BigDecimal.ZERO, Currency.getInstance("USD"), null)
        AppsFlyerUtils.setAppsFlyerEvent(AppsFlyerUtils.APPSFLYER_EVENT_PURCHASE)
    }


    //epoch data receive
    override fun update(o: Observable?, arg: Any?) {
        EpochLogicUtils.update(o, arg)
    }
}

fun BaseViewModel.jumpWebFragment(view: View, title: String, link: String) {

    CommonProvider.instance?.getWebViewNavId()?.let {
        val bundle = bundleOf(Pair("link", link), Pair("title", title))
        Navigation.findNavController(view).navigate(it, bundle)

    }

}

fun AppCompatImageView.loadImageWithCallBack(imgUrl: String?, onReadyAction: (Bitmap) -> Unit) {
    if (StringUtils.isEmpty(imgUrl)) {
        return
    }
    Glide.with(this).downloadOnly().load(imgUrl).listener(object : RequestListener<File> {
        override fun onLoadFailed(
            e: GlideException?,
            model: Any?,
            target: Target<File>?,
            isFirstResource: Boolean
        ): Boolean {
            return false
        }

        override fun onResourceReady(
            resource: File?,
            model: Any?,
            target: Target<File>?,
            dataSource: DataSource?,
            isFirstResource: Boolean
        ): Boolean {
            kotlin.runCatching {
                val fis = FileInputStream(resource)
                val bmp = BitmapFactory.decodeStream(fis)
                onReadyAction(bmp)
            }
            return false
        }
    }).preload()
}
