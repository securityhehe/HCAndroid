package com.hc.load

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hc.load.databinding.FragmentLoanListBinding
import com.hc.uicomponent.base.BaseFragment


class LoanMessageListFragment : BaseFragment<FragmentLoanListBinding>(R.layout.fragment_loan_list) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        /**
        rv?.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = ListMessageAdapter()
            val data = (adapter as ListMessageAdapter).messageListData
            data.add(LoanMessage("1"))
            data.add(LoanMessage("1"))
            data.add(LoanMessage("1"))
            data.add(LoanMessage("1"))
            data.add(LoanMessage("1"))
        }**/
    }

    class ListMessageAdapter() : RecyclerView.Adapter<ListViewHolder>() {
        var messageListData = mutableListOf<LoanMessage>()
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_loan_message_item, parent, false)
            return ListViewHolder(view)
        }

        override fun getItemCount(): Int {
            return messageListData.size
        }

        override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
            holder.bind(messageListData[position])
        }

    }

    class ListViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(data: LoanMessage) {

        }
    }

    data class LoanMessage(var data: String)

}


