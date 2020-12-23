package com.hc.load

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hc.load.databinding.FragmentLoanHistoryBinding
import com.hc.uicomponent.GridItemDecoration
import com.hc.uicomponent.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_loan_history.*

class LoanHistoryFragment : BaseFragment<FragmentLoanHistoryBinding>(R.layout.fragment_loan_history) {




    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        order_rv?.apply {
            layoutManager = LinearLayoutManager(context)
            val decoration = GridItemDecoration.newBuilder().spanCount(1)
                .horizontalDivider(ColorDrawable(ContextCompat.getColor(context, R.color.colorDiv)),
                    context.resources.getDimensionPixelSize(R.dimen.menu_item_divider), false
                ).build()

            addItemDecoration(decoration)
            adapter = LoanOrderAdapter()
            val adapters = (adapter as LoanOrderAdapter).loanListData
            adapters.add(HistoryData())
            adapters.add(HistoryData())
            adapters.add(HistoryData())
            adapters.add(HistoryData())
            adapter?.notifyDataSetChanged()
        }

    }

    inner class LoanOrderAdapter(var loanListData: MutableList<HistoryData> = mutableListOf()) :
        RecyclerView.Adapter<HistoryHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_loan_history_item, parent, false)
            return HistoryHolder(view)
        }

        override fun onBindViewHolder(holder: HistoryHolder, position: Int) {
            holder.bindData(loanListData[position])
        }

        override fun getItemCount(): Int {
            return loanListData.size
        }
    }

    inner class HistoryHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bindData(loadData: HistoryData) {

        }
    }

    inner class HistoryData {
        var state: Int = 0
    }

}