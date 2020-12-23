package com.hc.load

import android.app.Dialog
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hc.load.databinding.FragmentLoanInputMoneyLayoutBinding
import com.hc.uicomponent.BaseDialog
import com.hc.uicomponent.BaseDialog.Callback
import com.hc.uicomponent.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_loan_input_money_layout.*
import java.io.Serializable

class LoanMainFragment : BaseFragment<FragmentLoanInputMoneyLayoutBinding>(R.layout.fragment_loan_input_money_layout) {

    companion object {
        const val RECEIVE = 0x01
        const val REJECT = 0x02
        const val STATUS_RECEIVE = 0x01
        const val STATUS_ODER_UNDER_REVIEW= 0x02
        const val STATUS_DISBURSED= 0x03

    }

    private var mLoanMainViewModel: LoanViewModel? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_loan_input_money_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loan_recycler?.apply {
            layoutManager = LinearLayoutManager(context)
            val a = mutableListOf<LoadData>()
            a.add(LoadData())
            a.add(LoadData())
            a.add(LoadData())
            adapter = LoanAdapter(a)
            adapter?.notifyDataSetChanged()
        }

        record?.setOnClickListener{
            NavHostFragment.findNavController(this).navigate(Uri.parse("navigation://loan/history/order"))
        }

        message?.setOnClickListener{
            
            NavHostFragment.findNavController(this).navigate(Uri.parse("navigation://loan/message"))
        }
    }

    inner class LoanAdapter(private var loanListData: MutableList<LoadData> = mutableListOf()) :
        RecyclerView.Adapter<LoadHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LoadHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_loan_input_money_item, parent, false)
            return LoadHolder(view)
        }

        override fun onBindViewHolder(holder: LoadHolder, position: Int) {
            holder.bindData(loanListData[position])
        }

        override fun getItemCount(): Int {
            return loanListData.size
        }
    }

    inner class LoadHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val mMoney = view.findViewById<TextView>(R.id.money)
        private val mButton: Button = view.findViewById(R.id.getMoney)
        private val mMoneyDesc: TextView = view.findViewById(R.id.moneyDesc)
        fun bindData(loadData: LoadData) {
            mButton.isSelected = (loadData.status != REJECT)
            mButton.setOnClickListener {
                showDialog()
            }
        }
    }

    class LoadData : Serializable {
        var status: Int = RECEIVE
    }

    fun showDialog() {
        context?.let {
            BaseDialog(it)
                .setData("Not qualified ! Please apply on 8 October , 2019.", null, "Got it")
                .setCallback(object : Callback {
                    override fun confirm(d: Dialog?) {
                        d?.dismiss()
                    }
                }).show()
        }
    }

    /**
     * 跳转到审核中页面。
     */
    fun toReceived() {
        setLoanViewState(STATUS_RECEIVE)
        context?.let {
            loan_status_fl?.background =
                ContextCompat.getDrawable(it, R.drawable.loan_status_received_bg)
            loan_status_desc?.text = getString(R.string.loan_status_received_text)
            loan_status_desc?.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0)
            setLoanViewState()
        }
    }


    /**
     * 调整到拒绝页面。
     */
    fun toAuditReject() {
        context?.let {
            loan_status_fl?.background =
                ContextCompat.getDrawable(it, R.drawable.loan_audit_reject_bg)
            loan_status_desc?.text = getString(R.string.loan_audit_reject_text)
            loan_status_desc?.setCompoundDrawablesRelativeWithIntrinsicBounds(
                0,
                R.drawable.loan_audit_reject_ic,
                0,
                0
            )
            loan_status_desc?.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary))
        }
    }

    /**
     * 跳转到审核失效页面
     */
    fun toInvalidReject() {
        context?.let {
            loan_status_fl?.background =
                ContextCompat.getDrawable(it, R.drawable.loan_audit_reject_bg)
            loan_status_desc?.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0)
            loan_status_desc?.text = getString(R.string.loan_audit_invalid_text)
            loan_status_desc?.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary))
        }
    }

    /***
     * 跳转到订到关闭页面
     */

    fun toOrderClose() {
        context?.let {
            loan_status_fl?.background =
                ContextCompat.getDrawable(it, R.drawable.loan_audit_reject_bg)
            loan_status_desc?.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0)
            loan_status_desc?.text = getString(R.string.loan_order_close_text)
            loan_status_desc?.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary))
        }
    }

    /**
     * 待还款状态
     */
    fun toOrderUnderReview() {
        context?.let {
            loan_status_fl?.background =
                ContextCompat.getDrawable(it, R.drawable.loan_status_received_bg)
            loan_status_desc?.text = getString(R.string.loan_status_received_text)
            loan_status_desc?.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0)
            setLoanViewState(STATUS_ODER_UNDER_REVIEW)
        }
    }

    /**
     * 逾期
     */
    private  fun toBeOverdue(){
        context?.let {
            loan_status_fl?.background =
                ContextCompat.getDrawable(it, R.drawable.loan_audit_reject_bg)
            loan_status_desc?.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.loan_status_be_overdue, 0, 0)
            loan_status_desc?.text = String.format(getString(R.string.loan_order_to_overdue_text),"1 Days(s)","₹50")
            loan_status_desc?.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary))
        }
    }

    /**
     * 续期中
     */

    private  fun toRenewal(){
        context?.let {
            loan_status_fl?.background =
                ContextCompat.getDrawable(it, R.drawable.loan_audit_reject_bg)
            loan_status_desc?.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.loan_renewal, 0, 0)
            loan_status_desc?.text = String.format(getString(R.string.loan_order_to_renewal_text))
            loan_status_desc?.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary))
        }
    }

    /**
     * 还款续期选择
     */
    private fun toRenewalSelect(){
        setLoanViewState(STATUS_RECEIVE)
        context?.let {
            loan_status_fl?.background =
                ContextCompat.getDrawable(it, R.drawable.loan_status_received_bg)
            loan_status_desc?.text = getString(R.string.loan_status_received_text)
            loan_status_desc?.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0)
            setLoanViewState()
        }
    }

    /**
     * 还款处理中
     */
    private fun toRepaymentProcess(){
        setLoanViewState(STATUS_RECEIVE)
        context?.let {
            loan_status_fl?.background = ContextCompat.getDrawable(it, R.drawable.loan_audit_reject_bg)
            loan_status_desc?.text = getString(R.string.loan_status_to_repayment_text)
            loan_status_desc?.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.loan_repayment_processing, 0, 0)
        }
    }

    /**
     * 状态转换方法
     */
    private fun setLoanViewState(level: Int = 0) {
        val isOpenReceive = (level >= STATUS_RECEIVE)
        loan_app_receive?.isEnabled = isOpenReceive
        loan_go_1?.isEnabled = isOpenReceive
        val isOpenOrderReview = (level >= STATUS_ODER_UNDER_REVIEW)
        loan_order_review?.isEnabled = isOpenOrderReview
        loan_go_2?.isEnabled = isOpenOrderReview
        val isOpenDisbursed = (level >= STATUS_DISBURSED)
        lan_disbursed.isEnabled = isOpenDisbursed
    }

}

