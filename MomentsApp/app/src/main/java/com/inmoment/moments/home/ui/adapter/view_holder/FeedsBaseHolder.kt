package com.inmoment.moments.home.ui.adapter.view_holder

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.text.*
import android.util.TypedValue
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.ShimmerFrameLayout
import com.inmoment.moments.R
import com.inmoment.moments.databinding.LayoutFeedsBottomSectionBinding
import com.inmoment.moments.databinding.LayoutFeedsReadMoreSectionBinding
import com.inmoment.moments.framework.extensions.setSafeOnClickListener
import com.inmoment.moments.home.MomentType
import com.inmoment.moments.home.helper.CustomTypefaceSpan
import com.inmoment.moments.home.model.Feed
import com.inmoment.moments.home.ui.adapter.ActivityLogAdapter
import com.inmoment.moments.home.ui.adapter.FeedsAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException


abstract class FeedsBaseHolder(
    val view: View,
    private val interfaceMomentAction: FeedsAdapter.InterfaceMomentAction,
    private val actionBinding: LayoutFeedsBottomSectionBinding,
    private val topSectionBinding: LayoutFeedsReadMoreSectionBinding
) : RecyclerView.ViewHolder(view) {

    private val context: Context = view.context
    private val feedMessageTV: TextView = view.findViewById(R.id.feedMessageTV)

    private val llExperienceScore: LinearLayout = view.findViewById(R.id.ll_experience_score)
    private val txtExperienceScoreNumber: TextView =
        view.findViewById(R.id.txt_experience_score_number)
    private val txtExperienceScoreMsg: TextView = view.findViewById(R.id.txt_experience_score_msg)
    private val dividerLine: View = view.findViewById(R.id.divider_line)

    private val readMore: TextView = view.findViewById(R.id.readMoreTV)
    private val feedDateTimeDetailTV: TextView = view.findViewById(R.id.feedDateTimeDetailTV)
    private val feedLocationDetailTV: TextView = view.findViewById(R.id.feedLocationDetailTV)
    private val activityCountIV: TextView = view.findViewById(R.id.ivActivityLog)
    private val activityLogRV: RecyclerView = view.findViewById(R.id.activityRV)
    private val activityCountTV: TextView = view.findViewById(R.id.activityCountTV)
    private val btnShare: ImageView = view.findViewById(R.id.ivShare)
    private val btnRewards: ImageView = view.findViewById(R.id.ivRewards)
    private val feedsCardView: CardView = view.findViewById(R.id.feedsCV)
    private val chkLike: ImageButton = view.findViewById(R.id.chkLike)

    private val shimmerFrameLayout: ShimmerFrameLayout = view.findViewById(R.id.shimmerFrameLayout)

    fun baseBindData(
        context: FragmentActivity,
        feed: Feed,
        position: Int,
        feedsListSize: Int,
        recyclerView: RecyclerView,
        momentType: String

    ) {
        collapseActivityLog()
        setOnClickActivityCount(feed, position)
        setFeedNumberWithIcon(feed)
        setFeedsMessageText(context, feed)
        // handleCaughtUpFunctionality(feedsListSize, position, feed, recyclerView)
        ellipsizeFeedsText()
        setOnClickShare(
            context, feed,
            position
        )
        if (activityLogRV.adapter == null) {
            val linearLayoutManager = LinearLayoutManager(
                context,
                LinearLayoutManager.VERTICAL,
                false
            )

            activityLogRV.layoutManager = linearLayoutManager
            activityLogRV.adapter = object :
                ActivityLogAdapter(context, feed.activitiesLogModel) {}

            // activityLogRV.addLineDividerDecorator(R.color.darkGrayishAzure)
            activityLogRV.addItemDecoration(
                DividerItemDecoration(context, LinearLayoutManager.VERTICAL)
            )
        } else {
            val activityAdapter = activityLogRV.adapter as ActivityLogAdapter
            activityAdapter.addItems(feed.activitiesLogModel)

        }

        activityCountTV.text = context.getString(
            R.string.number_of_activities,
            feed.activityLogCount
        )
        actionBinding.feed = feed
        topSectionBinding.feed =feed
        actionBinding.executePendingBindings()
        setOnClickRewards(feed, position)
        setOnClickFavourite(context, feed, position, momentType)
        handleReadMore(feed)
    }

    private fun setOnClickFavourite(
        context: FragmentActivity,
        feed: Feed,
        position: Int,
        momentType: String
    ) {

        chkLike.setSafeOnClickListener {
            if(momentType== MomentType.SAVED_VIEWS.value) {

                if (!(actionBinding.viewModel?.favoriteList?.contains(feed.experienceId))!!) {
                    chkLike.setImageDrawable(
                        ContextCompat.getDrawable(
                            context,
                            R.drawable.collections_selected
                        )
                    )
                   chkLike.animate().scaleX(1.4f).scaleY(1.4f).setDuration(200).withEndAction {
                       chkLike.animate().scaleX(1f).scaleY(1f).duration = 200
                   }
                }
               interfaceMomentAction.onAddToFavouriteActionClick(feed, position)
            }
            else
            {
                interfaceMomentAction.onRemoveFromFavouriteActionClick(feed, position)
            }
        }
    }

    internal fun setOnClickActivityCount(
        feed: Feed,
        position: Int
    ) {

        activityCountIV.setSafeOnClickListener {
            if (feed.activityLogClick) {
                feed.activityLogClick = false
                actionBinding.feed = feed
            } else {
                if (feed.activityLogCount!! > 0 && feed.activitiesLogModel.size <= 0) {
                    interfaceMomentAction.onActivityActionClick(feed, position)
                    feed.loadingEffect = true
                }
                feed.activityLogClick = true
            }
            actionBinding.feed = feed
            actionBinding.executePendingBindings()
        }
        /* if(feed.activityLogCount!=null && feed.activityLogCount!! >0) {

             activityCountIV.visibility = View.VISIBLE
             activityCountIV.text = feed.activityLogCount.toString()


             if (feed.activityLogClick && feed.activitiesLogModel.isNotEmpty()) {
                 activityCountTV.visibility = View.VISIBLE
                 activityLogRV.visibility = View.VISIBLE
                 shimmerFrameLayout.visibility = View.GONE

             }
             if(!feed.activityLogClick || activityLogRV.visibility == View.VISIBLE)
             {
                 shimmerFrameLayout.visibility = View.GONE
             }
             activityCountIV.setOnSingleClickListener{

                 if (feed.activityLogClick) {
                     feed.activityLogClick = false
                     activityCountTV.visibility = View.GONE
                     activityLogRV.visibility = View.GONE
                     shimmerFrameLayout.visibility = View.GONE
                 } else {


                     if (feed.activitiesLogModel.isEmpty() || activityLogRV.adapter == null) {
                         interfaceMomentAction.onActivityActionClick(feed, position)
                         // shimmerFrameLayout.visibility = View.VISIBLE
                     }
                     else {
                         feed.activityLogClick = true
                         activityLogRV.visibility = View.VISIBLE
                         activityCountTV.visibility = View.VISIBLE
                         shimmerFrameLayout.visibility = View.GONE

                     }
                 }

             }

         }*/
    }


    abstract fun bindData(
        context: FragmentActivity,
        feed: Feed,
        position: Int,
        feedsListSize: Int,
        recyclerView: RecyclerView,
        momentType: String
    )

    internal fun collapseActivityLog() {
        activityCountTV.visibility = View.GONE
        activityLogRV.visibility = View.GONE
    }

    internal fun setOnClickShare(
        context: FragmentActivity,
        feed: Feed,
        position: Int
    ) {
        btnShare.setOnClickListener {


            if (readMore.isVisible)
                readMore.performClick()
            val layoutBottomSection = view.findViewById<View>(R.id.layout_bottom_section)
            CoroutineScope(Dispatchers.Main).launch {

                feedsCardView.radius = 0f
                layoutBottomSection.visibility = View.GONE
                delay(40)
                val viewBitmap = getScreenShot(feedsCardView)
                val imageUri = saveBitmapToFileProvider(context, viewBitmap)
                layoutBottomSection.visibility = View.VISIBLE
                delay(40)
                feedsCardView.radius = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    8f,
                    view.context.resources.displayMetrics
                )
                interfaceMomentAction.onShareActionClick(feed, imageUri, position)
            }

        }
    }

    internal fun setOnClickRewards(
        feed: Feed,
        position: Int
    ) {
        btnRewards.setOnClickListener {

            interfaceMomentAction.onRewardActionClick(feed, position)
        }
    }

    private fun getScreenShot(view: View): Bitmap {
        val returnedBitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(returnedBitmap)
        val bgDrawable = view.background
        if (bgDrawable != null) bgDrawable.draw(canvas)
        else canvas.drawColor(Color.WHITE)
        view.draw(canvas)
        return returnedBitmap
    }

    private fun saveBitmapToFileProvider(context: FragmentActivity, bitmap: Bitmap): Uri {
        //---Save bitmap to external cache directory---//
        //get cache directory
        val cachePath = File(context.externalCacheDir, "my_images/")
        cachePath.mkdirs()

        //create png file
        val file = File(cachePath, "Image_123.png")
        val fileOutputStream: FileOutputStream
        try {
            fileOutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)
            fileOutputStream.flush()
            fileOutputStream.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        //get file uri
        return FileProvider.getUriForFile(
            context,
            context.packageName + ".provider",
            file
        )

    }

    /* private fun shareImage(context: FragmentActivity, imageFileUri: Uri) {
         //create a intent
         val intent = Intent(Intent.ACTION_SEND)
         intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
         intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
         intent.putExtra(Intent.EXTRA_STREAM, imageFileUri)
         intent.type = "image/png"
         context.startActivity(Intent.createChooser(intent, "Share with"))
     }*/


    @Suppress("DEPRECATION")
    private fun fromHtml(spannableStringBuilder: String): Spanned {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(spannableStringBuilder, Html.FROM_HTML_MODE_LEGACY)
        } else {
            Html.fromHtml(spannableStringBuilder)
        }
    }

    private fun setFeedNumberWithIcon(feed: Feed) {
        var sentimentStringValue: String

        /*llExperienceScore,  txtExperienceScoreNumber,  txtExperienceScoreMsg,  dividerLine*/
        var sentiment = 9999
        if (feed.sentiment != null) {
            sentiment = Math.round(feed.sentiment)
            sentimentStringValue = sentiment.toString()
        } else
            sentimentStringValue = "-"
        var lightColor: Int
        var darkColor: Int
        when {
            sentiment == 9999 -> {
                lightColor = view.context.resources.getColor(android.R.color.transparent)
                darkColor = view.context.resources.getColor(R.color.non_experience_light)

            }
            sentiment > 0 -> {
                lightColor = view.context.resources.getColor(R.color.positive_experience_light)
                darkColor = view.context.resources.getColor(R.color.positive_experience_dark)
            }
            sentiment < 0 -> {
                lightColor = view.context.resources.getColor(R.color.negative_experience_light)
                darkColor = view.context.resources.getColor(R.color.negative_experience_dark)
            }
            else -> {
                lightColor = view.context.resources.getColor(R.color.neutral_experience_light)
                darkColor = view.context.resources.getColor(R.color.neutral_experience_dark)
            }

        }
        txtExperienceScoreNumber.text = sentimentStringValue
        txtExperienceScoreNumber.setTextColor(darkColor)
        txtExperienceScoreMsg.setTextColor(darkColor)
        dividerLine.setBackgroundColor(darkColor)
        llExperienceScore.setBackgroundColor(lightColor)

    }

    internal fun setFeedsMessageText(
        context: FragmentActivity,
        feed: Feed
    ) {
        val commentText = feed.text?.trim()
        val spannableStringBuilder = SpannableStringBuilder(commentText)
        if (!feed.tagAnnotations.isNullOrEmpty()) {
            for (tagAnnotation in feed.tagAnnotations) {

                if (commentText != null && commentText.isNotEmpty() && tagAnnotation.beginIndex != null && tagAnnotation.endIndex != null) {
                    val typeface = ResourcesCompat.getFont(context, R.font.ibm_plex_sans_semi_bold)
                    spannableStringBuilder.setSpan(
                        CustomTypefaceSpan(typeface!!),
                        tagAnnotation.beginIndex,
                        tagAnnotation.endIndex,
                        Spannable.SPAN_INCLUSIVE_INCLUSIVE
                    )
                }
            }
        }

        if (spannableStringBuilder.isNotEmpty()) feedMessageTV.text = spannableStringBuilder
        else feedMessageTV.text = commentText
    }


/*fun handleCaughtUpFunctionality(
    position: Int,
    feedsListSize: Int,
    feed: Feed,
    recyclerView: RecyclerView
) {
    val extraPadding = view.findViewById<View>(R.id.extra_padding)
    val youAreCaughtUpButton = view.findViewById<Button>(R.id.youAreCaughtUpButton)
    val visibleItemCount = recyclerView.layoutManager?.childCount
    val totalItemCount = recyclerView.layoutManager?.itemCount
    if (position == feedsListSize - 1 && visibleItemCount!! < totalItemCount!! - 1) {
        extraPadding.visibility = View.VISIBLE
        youAreCaughtUpButton.visibility = View.VISIBLE
    } else {
        extraPadding.visibility = View.GONE
        youAreCaughtUpButton.visibility = View.GONE
    }
    youAreCaughtUpButton.setOnClickListener {
        recyclerView.smoothScrollToPosition(0)
    }
}*/

    fun handleReadMore(feed: Feed) {

        val dateTimeDetails = feed.getTimeAgo(feed.dateTime)
        val locationDetails = feed.location
        if (dateTimeDetails != null && dateTimeDetails.isNotEmpty()) feedDateTimeDetailTV.text =
            dateTimeDetails
        if (locationDetails.isNotEmpty()) feedLocationDetailTV.text = locationDetails
        readMore.setOnClickListener {
            readMore.visibility = View.GONE
            feedMessageTV.maxLines = Int.MAX_VALUE
            feedMessageTV.ellipsize = null
        }
    }

    fun ellipsizeFeedsText() {
        feedMessageTV.post {
            val lines: Int = feedMessageTV.lineCount
            when {
                lines > 4 -> {
                    readMore.visibility = View.VISIBLE
                    feedMessageTV.setLines(4)
                    feedMessageTV.ellipsize = TextUtils.TruncateAt.END
                }
                else -> {
                    feedMessageTV.setLines(lines)
                    readMore.visibility = View.GONE
                    feedMessageTV.ellipsize = null
                }
            }

        }
    }
}
