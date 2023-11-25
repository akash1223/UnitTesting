package com.inmoment.moments.home.ui.adapter.view_holder

import android.annotation.SuppressLint
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.inmoment.moments.R
import com.inmoment.moments.databinding.FeedsAudioItemRowBinding
import com.inmoment.moments.databinding.LayoutFeedsReadMoreSectionBinding

import com.inmoment.moments.home.model.Feed
import com.inmoment.moments.home.ui.ExoPlayerRecyclerView
import com.inmoment.moments.home.ui.adapter.FeedsAdapter


class AudioHolder(
    val binding: FeedsAudioItemRowBinding,
    private val interfaceMomentAction: FeedsAdapter.InterfaceMomentAction
) : FeedsBaseHolder(
    binding.root, interfaceMomentAction,
    binding.layoutBottomSection,
    binding.topSection as LayoutFeedsReadMoreSectionBinding
) {

    protected val playControlIV: ImageView = view.findViewById(R.id.playControlIV)
    protected val seekBarTotalDuration: TextView = view.findViewById(R.id.seekBarTotalDuration)
    protected val seekbar: SeekBar = view.findViewById(R.id.seekBar)
    protected val rewindIV: ImageView = view.findViewById(R.id.rewindIV)
    protected val forwardIV: ImageView = view.findViewById(R.id.forwardIV)

    @SuppressLint("ClickableViewAccessibility")
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
            recyclerView.prepareAudio(feed, position, this)
        }
        seekbar.setOnTouchListener { _, _ -> false }
        if (recyclerView is ExoPlayerRecyclerView) {
            if (recyclerView.audioPlaying) {
                playControlIV.setImageResource(R.drawable.ic_pause)
            } else {
                playControlIV.setImageResource(R.drawable.ic_play_control)
            }
        }

        playControlIV.setOnClickListener {
            if (recyclerView is ExoPlayerRecyclerView) {
                recyclerView.playAudio(feed, position, this)
            }
        }
        rewindIV.setOnClickListener {
            if (recyclerView is ExoPlayerRecyclerView && recyclerView.audioPlaying) {
                recyclerView.rewindAudio()
            }
        }

        forwardIV.setOnClickListener {
            if (recyclerView is ExoPlayerRecyclerView && recyclerView.audioPlaying) {
                recyclerView.forwardAudio()
            }
        }
    }
}