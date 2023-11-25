package com.inmoment.moments.home.ui.adapter.view_holder

import android.view.View
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.inmoment.moments.R
import com.inmoment.moments.databinding.FeedsImagesItemRowBinding
import com.inmoment.moments.databinding.LayoutFeedsReadMoreSectionBinding
import com.inmoment.moments.home.model.Feed
import com.inmoment.moments.home.ui.adapter.FeedsAdapter
import com.inmoment.moments.home.ui.adapter.ImagePagerAdapter


class ImagesHolder(
    val binding: FeedsImagesItemRowBinding,
    private val interfaceMomentAction: FeedsAdapter.InterfaceMomentAction
) : FeedsBaseHolder(
    binding.root, interfaceMomentAction,
    binding.layoutBottomSection,
    binding.topSection as LayoutFeedsReadMoreSectionBinding
) {
    private val viewPager: ViewPager
    private val tabLayout: TabLayout
    private val imgView: View

    init {
        view.tag = this
        viewPager = view.findViewById(R.id.viewPager)
        tabLayout = view.findViewById(R.id.tabDots)
        imgView = view
    }

    override fun bindData(
        context: FragmentActivity,
        feed: Feed,
        position: Int,
        feedsListSize: Int,
        recyclerView: RecyclerView,
        momentType : String
    ) {
        binding.feed = feed
        binding.layoutBottomSection.lifecycleOwner = binding.lifecycleOwner
        baseBindData(context, feed, position, feedsListSize, recyclerView,momentType)

        feed.images?.let {
            if (it.isNotEmpty()) {
                viewPager.adapter = ImagePagerAdapter(imgView.context, it)
                if (it.size > 1) {
                    tabLayout.visibility = View.VISIBLE
                    tabLayout.setupWithViewPager(viewPager, true)
                } else {
                    tabLayout.visibility = View.GONE
                }
            }
        }
    }
}