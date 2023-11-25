package com.inmoment.moments.home.ui.adapter

import android.annotation.SuppressLint
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.inmoment.moments.R
import com.inmoment.moments.databinding.FeedsAudioItemRowBinding
import com.inmoment.moments.databinding.FeedsCommentsItemRowBinding
import com.inmoment.moments.databinding.FeedsImagesItemRowBinding
import com.inmoment.moments.databinding.FeedsVideoItemRowBinding
import com.inmoment.moments.framework.common.*
import com.inmoment.moments.framework.datamodel.SavedViewsListResponseData
import com.inmoment.moments.framework.persist.SharedPrefsInf
import com.inmoment.moments.home.MomentType
import com.inmoment.moments.home.model.Feed
import com.inmoment.moments.home.ui.ExoPlayerRecyclerView
import com.inmoment.moments.home.ui.HomeViewModel
import com.inmoment.moments.home.ui.adapter.view_holder.*
import com.inmoment.moments.home.ui.fragment.HomeUiAction
import com.inmoment.moments.program.model.Program
import com.inmoment.moments.view.adapter.inflate


class FeedsAdapter(
    private val context: FragmentActivity,
    private val viewModel: HomeViewModel,
    private val recyclerView: RecyclerView,
    mLifecycleOwner: LifecycleOwner,
    val sharedPrefsInf: SharedPrefsInf
) : ListAdapter<Feed, RecyclerView.ViewHolder>(FeedDiffCallback()) {


    private var lifecycleOwner: LifecycleOwner = mLifecycleOwner

    lateinit var savedViewsListResponseData: SavedViewsListResponseData
    var defaultProgram: Program? = null


    lateinit var interfaceMomentAction: InterfaceMomentAction
    private var inflater: LayoutInflater = LayoutInflater.from(context)

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (holder is FirstFeedsHolder) {
            defaultProgram?.let {
                getItem(position)?.let { it1 ->
                    holder.bindData(
                        context,
                        it1, it
                    )
                }
            }
        } else if (holder is FeedsBaseHolder) {
            getItem(position)?.let {
                if (holder is VideoHolder || holder is AudioHolder) {
                    Logger.d("FeedsAdapterNew", "Video and Audio Item Position =>$position")
                }
                holder.bindData(
                    context,
                    it,
                    position,
                    itemCount,
                    recyclerView,
                    sharedPrefsInf.get(
                        SharedPrefsInf.PREF_MOMENT_TYPE,
                        MomentType.SAVED_VIEWS.value
                    )
                )
            }
        }
    }

    fun setMomentActionListener(interfaceMomentAction: InterfaceMomentAction) {
        this.interfaceMomentAction = interfaceMomentAction
    }

    override fun getItemViewType(position: Int): Int {
        when (getItem(position)?.rowType) {
            FIRST_ROW -> return FIRST_ROW
            VIDEO_ROW -> return VIDEO_ROW
            IMAGES_ROW -> return IMAGES_ROW
            COMMENT_ROW -> return COMMENT_ROW
            AUDIO_ROW -> return AUDIO_ROW
            FOOTER_ROW -> return FOOTER_ROW
            CAUGHT_UP -> return CAUGHT_UP
        }
        return super.getItemViewType(position)
    }


    @SuppressLint("InflateParams")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        when (viewType) {
            FIRST_ROW -> {
                val view = parent.inflate(R.layout.feeds_first_item_row, false)
                return FirstFeedsHolder(view)
            }
            VIDEO_ROW -> {
                val binding = FeedsVideoItemRowBinding.inflate(inflater)
                val homeUiAction = HomeUiAction(viewModel, context)
                binding.viewModel = viewModel
                binding.homeAction = homeUiAction
                binding.lifecycleOwner = lifecycleOwner
                binding.savedView = savedViewsListResponseData
                return VideoHolder(binding, interfaceMomentAction)
            }
            IMAGES_ROW -> {
                val binding = FeedsImagesItemRowBinding.inflate(inflater)
                val homeUiAction = HomeUiAction(viewModel, context)
                binding.viewModel = viewModel
                binding.homeAction = homeUiAction
                binding.lifecycleOwner = lifecycleOwner
                binding.savedView = savedViewsListResponseData
                return ImagesHolder(binding, interfaceMomentAction)
            }
            COMMENT_ROW -> {
                val binding = FeedsCommentsItemRowBinding.inflate(inflater)
                val homeUiAction = HomeUiAction(viewModel, context)
                binding.viewModel = viewModel
                binding.homeAction = homeUiAction
                binding.lifecycleOwner = lifecycleOwner
                binding.savedView = savedViewsListResponseData
                return CommentsHolder(binding, interfaceMomentAction)
            }
            AUDIO_ROW -> {
                val binding = FeedsAudioItemRowBinding.inflate(inflater)
                val homeUiAction = HomeUiAction(viewModel, context)
                binding.viewModel = viewModel
                binding.homeAction = homeUiAction
                binding.lifecycleOwner = lifecycleOwner
                binding.savedView = savedViewsListResponseData
                return AudioHolder(binding, interfaceMomentAction)
            }
            CAUGHT_UP -> {
                val view = parent.inflate(R.layout.feeds_you_are_caught_up, false)
                return CaughtUpViewHolder(view, recyclerView)
            }
            else -> {
                val view = parent.inflate(R.layout.feeds_footer_item_row, false)
                return FooterViewHolder(view)
            }
        }
    }


    fun pausePlayer() {
        if (recyclerView is ExoPlayerRecyclerView) {
            recyclerView.onPausePlayer()
        }
    }

    fun resumePlayer() {
        if (recyclerView is ExoPlayerRecyclerView) {
            recyclerView.onPausePlayer()
        }
    }

    fun releasePlayer() {
        if (recyclerView is ExoPlayerRecyclerView) {
            recyclerView.releasePlayer()
        }
    }

    fun setSelectedProgram(program: Program) {
        defaultProgram = program
        notifyItemChanged(0)
    }

/*
    fun updateActivityLog(activityLog: List<ActivityLogModel>, position: Int) {
        val feed = feedList[position]
        feed?.activitiesLogModel.addAll(activityLog)
        if (feed != null) {
            feed.loadingEffect = false
        }
        //(recyclerView.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(position,0)
        notifyItemChanged(position)
    }

    fun addActivityLog(activityLog: ActivityLogModel) {
        var feed: Feed? = null
        var feedPosition = 0
        feedList.forEachIndexed { index, loopFeed ->
            if(loopFeed.experienceId == activityLog.experienceId)
            {
                feed = loopFeed
                feedPosition =index
                return@forEachIndexed
            }
        }
        feed?.let{
            it.activityLogCount = it.activityLogCount!! + 1

            if (!it.activitiesLogModel.isNullOrEmpty()) {
                it.activitiesLogModel.add(activityLog)
            }
            it.activityLogClick = true
        }
       notifyDataSetChanged()

    }*/

    interface InterfaceMomentAction {
        fun onShareActionClick(feed: Feed, imageFileUri: Uri, position: Int)
        fun onRewardActionClick(feed: Feed, position: Int)
        fun onActivityActionClick(feed: Feed, position: Int)
        fun onAddToFavouriteActionClick(feed: Feed, position: Int)
        fun onRemoveFromFavouriteActionClick(feed: Feed, position: Int)
    }

    /*

     fun addItems(list: List<Feed>) {
         feedList.addAll(feedList.lastIndex,list)
         notifyDataSetChanged()
     }
     fun addItem(feed: Feed,position: Int) {
         feedList.add(position,feed)
         notifyItemInserted(position)
         if(feedList.size==2) {
             val listEndView = Feed()
             listEndView.rowType = CAUGHT_UP
             feedList.add(listEndView)
             notifyItemInserted(position+1)
         }
         notifyDataSetChanged()
     }
     fun removeItem(position: Int) {
         feedList.removeAt(position)
         notifyItemRemoved(position)
         if(feedList.size==2)
         {
             feedList.removeAt(position)
             notifyItemRemoved(position)
         }
         notifyDataSetChanged()

     }

     fun enableLoaderFooter() {
         val feed = feedList.last()
         feed.rowType = FOOTER_ROW
         notifyItemChanged(feedList.lastIndex)
     }

     fun disableLoaderFooter() {
         val feed = feedList.last()
         feed.rowType = CAUGHT_UP
         notifyItemChanged(feedList.lastIndex)
     }

     fun removeAllAndAddItem(list: List<Feed>?) {

         list?.let {
             feedList.clear()
             feedList.addAll(it)
             if(list.size > 1) {
                 val listEndView = Feed()
                 listEndView.rowType = CAUGHT_UP
                 feedList.add(listEndView)
             }
         }
         notifyDataSetChanged()
     }*/
    /* fun addCatchUP() {
         val listEndView = Feed()
         listEndView.rowType = CAUGHT_UP
         feedList.add(listEndView)
         notifyItemInserted(feedList.size-1)
     }
 */

    override fun getItemCount() = currentList.size
    fun setSelectedSavedView(data: SavedViewsListResponseData) {
        if (!data.isNonDxDataSource) {
            data.enableCase = false
            data.enableCollection = false
            data.enableReward = false
        }
        /* if (BuildConfig.DEBUG) {
             data.enableCase = true
             data.enableCollection = true
             data.enableReward = true
         }*/

        savedViewsListResponseData = data
    }

    class FeedDiffCallback : DiffUtil.ItemCallback<Feed>() {
        override fun areItemsTheSame(oldItem: Feed, newItem: Feed): Boolean {

            val oldItemString = oldItem.experienceId
            val newItemString = newItem.experienceId
            val isEqual = oldItemString == newItemString

            return isEqual
        }

        override fun areContentsTheSame(oldItem: Feed, newItem: Feed): Boolean {
            return oldItem == newItem
        }
    }
}