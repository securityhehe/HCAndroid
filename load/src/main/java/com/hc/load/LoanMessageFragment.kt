package com.hc.load

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.hc.load.databinding.FragmentLoanMessageLayoutBinding
import com.hc.uicomponent.base.BaseFragment
import com.hc.uicomponent.tab.TabsView.OnTabsItemClickListener
import kotlinx.android.synthetic.main.fragment_loan_message_layout.*


class LoanMessageFragment : BaseFragment<FragmentLoanMessageLayoutBinding>(R.layout.fragment_loan_message_layout) {

    private val mTitles = ArrayList<String>()
    private var mFragment = mutableListOf<Fragment>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initTitle()
        initFragment()
    }

    private fun initFragment() {
        val orderMessage = LoanMessageListFragment()
        val repayMessage = LoanMessageListFragment()
        mFragment.add(orderMessage)
        mFragment.add(repayMessage)
    }

    private fun initTitle() {
        mTitles.add(getString(R.string.loan_order_messge_title))
        mTitles.add(getString(R.string.loan_announcement_title))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //设置pagerTabStrip的一些需要属性
        pager?.adapter = MessagePagerAdapter()
        val order = getString(R.string.loan_order_messge_title)
        val anno = getString(R.string.loan_announcement_title)
        pagerTab.setTabs(order,anno)
        pagerTab.setOnTabsItemClickListener(object : OnTabsItemClickListener {
            override fun onClick(view: View?, position: Int) {
                pager.setCurrentItem(position, true)
            }
        })
        pager.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageSelected(position: Int) {
                pagerTab.setCurrentTab(position, true)
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
        pager?.adapter?.notifyDataSetChanged()
    }


    inner class MessagePagerAdapter :
        FragmentStatePagerAdapter(childFragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        override fun getItem(position: Int): Fragment {
            return mFragment[position]
        }

        override fun getCount(): Int {
            return mFragment.size
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return mTitles[position]
        }

    }

}