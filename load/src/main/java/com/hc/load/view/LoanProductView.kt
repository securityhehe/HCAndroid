package com.hc.load.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hc.data.mall.IListData
import com.hc.load.R
import com.hc.uicomponent.weight.NoDoubleClickButton
import frame.utils.StringFormat

class LoanProductView : FrameLayout {
    var rv: RecyclerView? = null
    var callback: Callback? = null
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)
    init {
        LayoutInflater.from(context).inflate(R.layout.loan_product_view, this, true)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        rv = findViewById(R.id.productList)
        rv?.run {
            this.layoutManager = LinearLayoutManager(context)
            if (this.adapter == null) {
                this.adapter = LoanAdapter()
            }
        }
    }


    inner class LoanAdapter(private var loanListData: MutableList<IListData> = mutableListOf()) : RecyclerView.Adapter<LoadHolder>() {

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

        fun setData(it: MutableList<IListData>) {
            loanListData.clear()
            loanListData.addAll(it)
            this.notifyDataSetChanged()
        }
    }

    inner class LoadHolder(var view: View) : RecyclerView.ViewHolder(view) {

        private val mMoney = view.findViewById<TextView>(R.id.money)
        private val loanDuration: TextView = view.findViewById(R.id.loanDuration)
        private val dayInterest: TextView = view.findViewById(R.id.dayInterest)
        private val getMoneyBtn: NoDoubleClickButton = view.findViewById(R.id.getMoney)
        private val changeMoney: ImageView = view.findViewById(R.id.changeMoney)
        private val platformName: TextView = view.findViewById(R.id.platformName)

        fun bindData(loadData: IListData) {
            mMoney.text = loadData.getMaxAmount()
            loanDuration.text = loadData.getLoanDuration(view.context)
            dayInterest.text = loadData.getDayInterest(view.context)
            getMoneyBtn.isEnabled = loadData.getCanBuyFlag()
            changeMoney.visibility = if (loadData.getChangeMoney()) {
                View.VISIBLE
            } else {
                View.GONE
            }
            platformName.visibility = if (loadData.getShowPlatformName()) {
                View.VISIBLE
            } else {
                View.GONE
            }
            platformName.text = loadData._getPlatformName()
            changeMoney.setOnClickListener {
                callback?.selectPrice(loadData,mMoney,changeMoney)
            }
            getMoneyBtn.setOnClickListener{
                callback?.getNowMoney(loadData, mMoney.text.toString())
            }

        }
    }

    fun setData(data: MutableList<IListData>): LoanProductView {
        rv?.let {
            (it.adapter as LoanAdapter).setData(data)
        }
        return this
    }

    interface Callback {
        fun selectPrice(price: IListData,priceTv:TextView, view:ImageView) {}
        fun getNowMoney(price:IListData,money:String) {}
    }


}