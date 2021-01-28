package com.hc.load


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hc.load.databinding.FragmentLoanBillDetailBinding
import com.hc.uicomponent.base.BaseFragment
import com.hc.uicomponent.config.Constants

class BillDetailFragment : BaseFragment<FragmentLoanBillDetailBinding>(R.layout.fragment_loan_bill_detail){

    //public static final String ORDER_COMMIT_GOODS_SX = "order_commit_goods_sx";
    //public static final String ORDER_COMMIT_AUTO_ENTRY_PAGE = "order_commit_auto_entry_page";
    //public static final String ORDER_NBFC_IMG_URL = "order_nbfc_img_url";
    //public static final String ORDER_COMMIT_GET_CERTIFY_BANK_STATE = "order_commit_get_certify_bank_state";
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {

            it.getSerializable(Constants.ORDER_COMMIT_AUTO_ENTRY_PAGE);
        }
        mFragmentBinding.apply {
            if(rv.adapter == null  ){
               rv.adapter = OrderAdapter()
            }
        }
    }


    class OrderAdapter: RecyclerView.Adapter<OrderHolder>() {
        var orderInfo = mutableListOf<Pair<String,String>>()
        fun setData() {
            
        }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderHolder {
            return OrderHolder(LayoutInflater.from(parent.context).inflate(R.layout.loan_item_details,parent, false))
        }

        override fun getItemCount(): Int {
            return 10;
        }

        override fun onBindViewHolder(holder: OrderHolder, position: Int) {
            TODO("Not yet implemented")
        }

    }

    class OrderHolder (view :View): RecyclerView.ViewHolder(view){

    }
}