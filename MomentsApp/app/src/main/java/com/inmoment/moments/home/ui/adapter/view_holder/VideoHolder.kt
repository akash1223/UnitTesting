package com.inmoment.moments.home.ui.adapter.view_holder

import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.inmoment.moments.R
import com.inmoment.moments.databinding.FeedsVideoItemRowBinding
import com.inmoment.moments.databinding.LayoutFeedsReadMoreSectionBinding
import com.inmoment.moments.home.model.Feed
import com.inmoment.moments.home.ui.ExoPlayerRecyclerView
import com.inmoment.moments.home.ui.adapter.FeedsAdapter


class VideoHolder(
    val binding: FeedsVideoItemRowBinding,
    interfaceMomentAction: FeedsAdapter.InterfaceMomentAction
) : FeedsBaseHolder(
    binding.root, interfaceMomentAction,
    binding.layoutBottomSection,
    binding.topSection as LayoutFeedsReadMoreSectionBinding
) {

    private val videoPlayIV: ImageView
    private val feedsVV: FrameLayout


    init {
        view.tag = this
        videoPlayIV = view.findViewById(R.id.videoPlayIV)
        feedsVV = view.findViewById(R.id.feedsVV)
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
        if ((recyclerView is ExoPlayerRecyclerView)) {
            recyclerView.prepareVideo(position, this)
        }
        videoPlayIV.setOnClickListener {
            videoPlayIV.visibility = View.GONE
            if (recyclerView is ExoPlayerRecyclerView) {
                recyclerView.playVideo(position)
            }
        }
        feedsVV.setOnClickListener {
            if (recyclerView is ExoPlayerRecyclerView) {
                recyclerView.onPausePlayer()
            }
        }
    }
}