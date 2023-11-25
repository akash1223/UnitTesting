package com.inmoment.moments.home.ui.adapter.view_holder

import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.inmoment.moments.databinding.FeedsCommentsItemRowBinding
import com.inmoment.moments.databinding.LayoutFeedsReadMoreSectionBinding
import com.inmoment.moments.home.model.Feed
import com.inmoment.moments.home.ui.adapter.FeedsAdapter


class CommentsHolder(
    val binding: FeedsCommentsItemRowBinding,
    private val interfaceMomentAction: FeedsAdapter.InterfaceMomentAction
) : FeedsBaseHolder(
    binding.root, interfaceMomentAction,
    binding.layoutBottomSection,
    binding.layoutTopSection as LayoutFeedsReadMoreSectionBinding,

) {


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
    }
}