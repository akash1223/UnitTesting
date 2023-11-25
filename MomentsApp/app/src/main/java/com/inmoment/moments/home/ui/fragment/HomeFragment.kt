package com.inmoment.moments.home.ui.fragment

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.inmoment.moments.R
import com.inmoment.moments.databinding.FragmentHomeBinding
import com.inmoment.moments.framework.common.AppConstants
import com.inmoment.moments.framework.common.AppConstants.NAV_BACK_PROGRAM
import com.inmoment.moments.framework.common.AppConstants.VISIBLE_THRESHOLD
import com.inmoment.moments.framework.common.Logger
import com.inmoment.moments.framework.datamodel.ActivityLogResponseData
import com.inmoment.moments.framework.datamodel.CollectionModel
import com.inmoment.moments.framework.datamodel.MomentsResponseData
import com.inmoment.moments.framework.dto.Error
import com.inmoment.moments.framework.extensions.setSafeOnClickListener
import com.inmoment.moments.framework.persist.SharedPrefsInf
import com.inmoment.moments.framework.persist.SharedPrefsInf.Companion.PREF_CONTENT_SHARED
import com.inmoment.moments.framework.ui.BaseFragment
import com.inmoment.moments.framework.utils.ShareContentCallBackReceiver
import com.inmoment.moments.home.ActivityLogEnum
import com.inmoment.moments.home.MomentType
import com.inmoment.moments.home.model.Feed
import com.inmoment.moments.home.ui.HomeViewModel
import com.inmoment.moments.home.ui.adapter.FeedsAdapter
import com.inmoment.moments.menu.MenuFragmentDirections
import com.inmoment.moments.program.model.Program
import com.inmoment.moments.reward.ui.RewardFragmentDirections
import com.lysn.clinician.utility.extensions.getHtmlSpannedString
import com.lysn.clinician.utility.extensions.scrollToTop
import com.wootric.androidsdk.Wootric
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject


@AndroidEntryPoint
class HomeFragment : BaseFragment() {
    private var savedMomentsData = ArrayList<MomentsResponseData>()
    private var savedAccountName: String = ""
    lateinit var feedsAdapter: FeedsAdapter
    private lateinit var mBinding: FragmentHomeBinding
    private var momentType: String? = null
    private var loading = false
    private var pageEnd = false
    val TAG1 = "HomeViewModel"
    private var tickReceiver: BroadcastReceiver? = null

    @Inject
    lateinit var sharedPrefsInf: SharedPrefsInf

    companion object {
        fun newInstance() = HomeFragment()
    }

    private val homeViewModel: HomeViewModel by activityViewModels()

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable("MomentsListResponseData", savedMomentsData)
        outState.putString("savedAccountName", savedAccountName)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = baseBinding(
            R.layout.fragment_home,
            inflater,
            container,
            FragmentHomeBinding::class.java
        )
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pageEnd = false
        loading = false

        mBinding.toolbar.findViewById<ImageView>(R.id.iv_menu).setOnClickListener {
            mNavController.navigate(R.id.action_UserProfileFragment)
        }

        mBinding.floatingUpArrowIV.setOnClickListener {
            mBinding.feedsRV.smoothScrollToPosition(0)
        }
        setupList()
        setTimeChangeBroadcast()
        if (homeViewModel.feedDadaList.value.isNullOrEmpty()) {
            homeViewModel.isProgramDbExist.observe(viewLifecycleOwner) {
                if (it) {
                    getCollection()
                    if (homeViewModel.activeSavedView.value != null)
                        getMoments(true)
                    else {
                        momentType = homeViewModel.getMomentType()
                        homeViewModel.removeAllAndAddItem(listOf(), AppConstants.EMPTY_VALUE)
                    }
                } else {
                    getProgramData()
                }
            }
        } else if (homeViewModel.feedDadaList.value!!.size == 1) {
            momentType = homeViewModel.getMomentType()
            showEmptyListErrorMessage(
                homeViewModel.getMomentType(),
                homeViewModel.feedDadaList.value!!
            )
        }

        fragmentCallBack()
    }

    private fun setupList() {
        feedsAdapter = FeedsAdapter(
            this.requireActivity(),
            homeViewModel,
            mBinding.feedsRV,
            viewLifecycleOwner,
            sharedPrefsInf
        )

        feedsAdapter.setMomentActionListener(momentActionClick())
        val recycleLayoutManager = LinearLayoutManager(requireContext())
        mBinding.feedsRV.apply {
            layoutManager = recycleLayoutManager
            adapter = feedsAdapter
            addOnScrollListener(onRecycleScrollListener(recycleLayoutManager))
        }
        homeViewModel.selectedProgram.observe(viewLifecycleOwner) {
            it?.let { feedsAdapter.setSelectedProgram(it) }
        }
        mBinding.feedsRV.setMediaObjects(feedsAdapter.currentList)
        homeViewModel.activeSavedView.observe(viewLifecycleOwner)
        {
            if (it != null) {
                feedsAdapter.setSelectedSavedView(it)
            }
        }
    }

    private fun fragmentCallBack() {

        homeViewModel.feedDadaList.observe(viewLifecycleOwner)
        {
            feedsAdapter.submitList(it.toList())
            momentType?.let { it1 -> showEmptyListErrorMessage(it1, it.toList()) }
        }

        mNavController.currentBackStackEntry?.savedStateHandle?.getLiveData<Program>(
            NAV_BACK_PROGRAM
        )?.observe(viewLifecycleOwner)
        {

            feedsAdapter.setSelectedProgram(it)
            getSavedViews()
            mNavController.currentBackStackEntry?.savedStateHandle?.remove<Program>(NAV_BACK_PROGRAM)
        }
        mNavController.currentBackStackEntry?.savedStateHandle?.getLiveData<MomentType>(
            AppConstants.NAV_BACK_MOMENT_TYPE
        )?.observe(viewLifecycleOwner)
        {
            homeViewModel.dataSourceType.value = it
            if (homeViewModel.activeSavedView.value != null) {
                getCollection()
                getMoments(true)
            }
            mNavController.currentBackStackEntry?.savedStateHandle?.remove<MomentType>(AppConstants.NAV_BACK_MOMENT_TYPE)
        }
        mNavController.currentBackStackEntry?.savedStateHandle?.getLiveData<ActivityLogResponseData>(
            AppConstants.NAV_BACK_REWARD
        )?.observe(viewLifecycleOwner)
        {

            homeViewModel.addActivityLog(it)
            showRewardSubmitSnackBar()
            mNavController.currentBackStackEntry?.savedStateHandle?.remove<ActivityLogResponseData>(
                AppConstants.NAV_BACK_REWARD
            )
        }
        mNavController.currentBackStackEntry?.savedStateHandle?.getLiveData<CollectionModel>(
            AppConstants.NAV_BACK_ADD_TO_FAV
        )?.observe(viewLifecycleOwner)
        {
            homeViewModel.addToCollectionOrUpdateCollection(it)
            logActivityAction(
                ActivityLogEnum.COLLECTION,
                "Added to \"${it.label}\" collection",
                homeViewModel.getSavedActionFeedInfo()
            )
            showAddToAnotherCollectionSnackBar(it)
            mNavController.currentBackStackEntry?.savedStateHandle?.remove<CollectionModel>(
                AppConstants.NAV_BACK_ADD_TO_FAV
            )
        }

    }


    private fun getProgramData() {

        showLoading()
        val savedViewsData = homeViewModel.getPrograms()
        savedViewsData.successLiveData?.observe(viewLifecycleOwner) { it ->
            homeViewModel.storeProgramAndAccountToDB(it)
            getSavedViews()
        }
        savedViewsData.errorLiveData?.observe(viewLifecycleOwner) {
            hideLoading()
            showError(R.string.error_getting_program_data)
        }
    }

    private fun getSavedViews() {

        showLoading()
        momentType = homeViewModel.getMomentType()
        val savedViewsData = homeViewModel.getSavedViews()
        savedViewsData.successLiveData?.observe(viewLifecycleOwner) {
            try {
                if (it.isNotEmpty()) {
                    homeViewModel.storeSavedViewsData(it)
                    getCollection()
                    getMoments(true)
                } else {
                    hideLoading()
                    homeViewModel.removeAllAndAddItem(listOf(), AppConstants.EMPTY_VALUE)
                    homeViewModel.storeSavedViewsData(listOf())
                }
            } catch (ex: Exception) {
                hideLoading()
                showError(R.string.error_getting_saved_views_data)
            }

        }
        savedViewsData.errorLiveData?.observe(viewLifecycleOwner) {
            hideLoading()
            showError(R.string.error_getting_saved_views_data)
        }
    }

    /*
      Get collection -> Used to check is feed is in favorite (Heart Icon)
    * */
    private fun getCollection() {
        val collectionCallback = homeViewModel.getCollection()
        collectionCallback.successLiveData?.observe(viewLifecycleOwner) {
            homeViewModel.setCollection(it)

            if (!loading) {
                feedsAdapter.notifyDataSetChanged()
            }
        }
        collectionCallback.errorLiveData?.observe(viewLifecycleOwner) {

        }
    }

    private fun getMoments(refresh: Boolean = false) {

        loading = true
        if (refresh) {
            showLoading()
            momentType = homeViewModel.getMomentType()
            mBinding.feedsRV.scrollToTop()
        } else {
            homeViewModel.enableLoaderFooter()
        }
        val momentCallback = homeViewModel.getMoment(refresh)
        momentCallback.successLiveData?.observe(viewLifecycleOwner) {
            if (refresh) hideLoading() else homeViewModel.disableLoaderFooter()
            if (it.isNullOrEmpty()) {
                pageEnd = true
            }
            val feeds = homeViewModel.convertMomentResponseToFeed(it)
            if (refresh) {
                wooticSdkSetUp()
                homeViewModel.removeAllAndAddItem(feeds, homeViewModel.getMomentsTitle())
            } else {
                homeViewModel.addItems(feeds)
            }

            loading = false
        }
        momentCallback.errorLiveData?.observe(viewLifecycleOwner) {
            if (refresh) hideLoading() else homeViewModel.disableLoaderFooter()
            showError(R.string.error_getting_moments_data)
            loading = false
        }
        getUserProfile()
    }

    private fun getUserProfile() {
        val userData = homeViewModel.getUserInfo()
        userData.successLiveData?.observe(
            viewLifecycleOwner
        ) {
            it.data?.userProfiles?.get(0)?.let { it1 ->
                homeViewModel.saveUserData(
                    it1.firstName!!,
                    it1.lastName!!,
                    it1.id!!
                )
            }
        }
    }


    private fun showEmptyListErrorMessage(momentType: String, list: List<Feed>) {

        when {
            list.size > 2 -> {
                mBinding.emptySavedViewError.visibility = View.GONE
                mBinding.emptyCollectionError.visibility = View.GONE
            }
            momentType == MomentType.SAVED_VIEWS.value -> {
                mBinding.emptySavedViewError.visibility = View.VISIBLE
            }
            momentType == MomentType.COLLECTION.value -> {
                mBinding.emptyCollectionError.visibility = View.VISIBLE
            }
        }

    }

    private fun getMomentActivityLog(experienceId: String) {

        val savedViewsData = homeViewModel.getMomentActivityLog(experienceId)
        savedViewsData.successLiveData?.observe(viewLifecycleOwner) {
            homeViewModel.updateActivityLog(
                it,
                experienceId
            )
        }
        savedViewsData.errorLiveData?.observe(viewLifecycleOwner) {
            homeViewModel.updateActivityLog(
                listOf(),
                experienceId
            )
        }
    }

    private fun logActivityAction(action: ActivityLogEnum, message: String, experienceId: String) {
        val callback =
            homeViewModel.logActivity(action, message, experienceId)
        callback.successLiveData?.observe(viewLifecycleOwner) { it ->
            homeViewModel.addActivityLog(it)
        }
        callback.errorLiveData?.observe(viewLifecycleOwner) {
            Logger.i(TAG1, "logActivityAction Error=>" + it)
        }
    }


    private fun momentActionClick() = object : FeedsAdapter.InterfaceMomentAction {
        override fun onShareActionClick(feed: Feed, imageFileUri: Uri, position: Int) {

            val receiver = Intent(context, ShareContentCallBackReceiver::class.java)
            receiver.action = AppConstants.ACTION_CONTENT_SHARE_SUCCESSFULLY
            val pendingIntent =
                PendingIntent.getBroadcast(context, 0, receiver, PendingIntent.FLAG_UPDATE_CURRENT)
            feed.experienceId?.let { it1 -> homeViewModel.saveActionClickFeedInfo(it1) }
            val intent = Intent(Intent.ACTION_SEND)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.putExtra(Intent.EXTRA_STREAM, imageFileUri)
            intent.putExtra(Intent.EXTRA_SUBJECT, "Check out this feedback!")
            intent.type = "image/png"
            requireActivity().startActivityFromFragment(
                this@HomeFragment, Intent.createChooser(
                    intent,
                    "Share with", pendingIntent.intentSender
                ), 101
            )
            Logger.d(TAG, "Shared feeds data")
        }

        override fun onRewardActionClick(feed: Feed, position: Int) {
            feed.experienceId?.let { homeViewModel.saveActionClickFeedInfo(it) }
            val direction = RewardFragmentDirections.actionRewardFragment(feed)
            mNavController.navigate(direction)
        }

        override fun onActivityActionClick(feed: Feed, position: Int) {
            feed.experienceId?.let { homeViewModel.saveActionClickFeedInfo(it) }
            feed.experienceId?.let { getMomentActivityLog(it) }
        }

        override fun onAddToFavouriteActionClick(feed: Feed, position: Int) {
            addToFavourite(feed)
        }

        override fun onRemoveFromFavouriteActionClick(feed: Feed, position: Int) {
            removeFromFavourite(feed)
        }
    }

    private fun addToFavourite(feed: Feed) {

        val callback = homeViewModel.addToFavourite(feed, getString(R.string.my_favorites))
        callback.successLiveData?.observe(viewLifecycleOwner) {
            showAddToFavouriteSnackBar(feed)
            homeViewModel.addToCollectionOrUpdateCollection(it.data.collections)
            feed.experienceId?.let { it1 ->
                logActivityAction(
                    ActivityLogEnum.COLLECTION,
                    "Added to \"${getString(R.string.my_favorites)}\" collection",
                    it1
                )
            }
        }
        callback.errorLiveData?.observe(viewLifecycleOwner) {
            if (it.errorCode == Error.RETURN_FROM_VIEW_MODEL) {
                showAddToFavouriteSnackBar(feed)
            } else {
                // Error condition for undo collection
            }
        }
    }

    private fun removeFromFavourite(feed: Feed) {

        val position = homeViewModel.getFeedPosition(feed)!!
        val callback = homeViewModel.removeFromCollection(feed)
        feed.activityLogCount = feed.activityLogCount!! + 1
        feed.experienceId?.let { homeViewModel.removeItem(it) }
        callback.successLiveData?.observe(viewLifecycleOwner) {
            feed.experienceId?.let { it1 ->
                logActivityAction(
                    ActivityLogEnum.COLLECTION,
                    "Deleted from \"${it.data.collections.label}\" collection",
                    it1
                )
            }
            homeViewModel.addToCollectionOrUpdateCollection(it.data.collections)
            showRemoveFromFavouriteSnackBar(feed, position, it.data.collections)
        }
        callback.errorLiveData?.observe(viewLifecycleOwner) {
            showError(R.string.error_delete_favorite)
        }
    }

    private fun showRemoveFromFavouriteSnackBar(
        feed: Feed,
        position: Int,
        collections: CollectionModel
    ) {
        val customSnackView: View = layoutInflater.inflate(
            R.layout.layout_add_to_favorite_dialog,
            mBinding.root.findViewById(android.R.id.content)
        )
        val btnAction = customSnackView.findViewById<Button>(R.id.btn_action)
        customSnackView.findViewById<TextView>(R.id.txt_collection_name).text =
            context?.getHtmlSpannedString(R.string.deleted_favorite_message, collections.label)
        btnAction.text = getString(R.string.undo)
        var snackBar: Snackbar? = null
        btnAction.setSafeOnClickListener {
            snackBar?.dismiss()
            val callback = homeViewModel.addToFavourite(feed, collections.label)
            callback.successLiveData?.observe(viewLifecycleOwner) {
                homeViewModel.addToCollectionOrUpdateCollection(it.data.collections)
                homeViewModel.addItem(feed, position)
                feed.experienceId?.let { it1 ->
                    logActivityAction(
                        ActivityLogEnum.COLLECTION,
                        "Added to \"${collections.label}\" collection",
                        it1
                    )
                }
            }
        }
        snackBar = showCardViewSnackBar(mBinding.root, customSnackView, 5000)
    }

    private fun showRewardSubmitSnackBar() {
        val customSnackView: View = layoutInflater.inflate(
            R.layout.layout_rewared_success_dialog,
            mBinding.root.findViewById(android.R.id.content)
        )
        showCardViewSnackBar(mBinding.root, customSnackView)
    }

    private fun showAddToFavouriteSnackBar(feed: Feed) {
        val customSnackView: View = layoutInflater.inflate(
            R.layout.layout_add_to_favorite_dialog,
            mBinding.root.findViewById(android.R.id.content)
        )
        val btnAction = customSnackView.findViewById<Button>(R.id.btn_action)
        customSnackView.findViewById<TextView>(R.id.txt_collection_name).text =
            context?.getHtmlSpannedString(R.string.add_to_my_favorite)

        btnAction.text = getString(R.string.title_collections)
        var snackBar: Snackbar? = null
        btnAction.setSafeOnClickListener {
            snackBar?.dismiss()
            // Navigate to collection screen
            feed.experienceId?.let { it1 -> homeViewModel.saveActionClickFeedInfo(it1) }
            val direction =
                MenuFragmentDirections.actionMenuFragment(feed, MomentType.COLLECTION.value)
            mNavController.navigate(direction)
        }
        snackBar = showCardViewSnackBar(mBinding.root, customSnackView, 5000)
    }

    private fun showAddToAnotherCollectionSnackBar(collections: CollectionModel) {
        hideLoading()
        val customSnackView: View = layoutInflater.inflate(
            R.layout.layout_add_to_favorite_dialog,
            mBinding.root.findViewById(android.R.id.content)
        )
        val btnAction = customSnackView.findViewById<Button>(R.id.btn_action)
        customSnackView.findViewById<TextView>(R.id.txt_collection_name).text =
            context?.getHtmlSpannedString(R.string.add_to_another_collection, collections.label)

        btnAction.text = getString(R.string.view_collection)
        var snackBar: Snackbar? = null

        btnAction.setOnClickListener {
            // Navigate to collection screen
            snackBar?.dismiss()
            val direction =
                MenuFragmentDirections.actionMenuFragment(menu = MomentType.COLLECTION.value)
            mNavController.navigate(direction)
        }
        snackBar = showCardViewSnackBar(mBinding.root, customSnackView, 5000)
    }


    private fun onRecycleScrollListener(recycleLayoutManager: LinearLayoutManager) =
        object : RecyclerView.OnScrollListener() {
            override fun onScrolled(
                recyclerView: RecyclerView,
                dx: Int, dy: Int
            ) {
                val visibleItemCount = recycleLayoutManager.childCount
                val totalItemCount = recycleLayoutManager.itemCount
                val lastVisibleItem =
                    recycleLayoutManager.findLastVisibleItemPosition()
                val firstCompleteVisibleItem =
                    recycleLayoutManager.findFirstCompletelyVisibleItemPosition()
                val lastCompleteVisibleItem =
                    recycleLayoutManager.findLastCompletelyVisibleItemPosition()
                homeViewModel.addToMomentReadList(firstCompleteVisibleItem, lastCompleteVisibleItem)
                if (lastVisibleItem + 1 > visibleItemCount && visibleItemCount < totalItemCount - 1) {
                    mBinding.floatingUpArrowIV.visibility = View.VISIBLE
                    Logger.i(
                        "$TAG=>onScrolled",
                        "onScrolledtotalItemCount=>$totalItemCount" + " \n lastVisibleItem + VISIBLE_THRESHOLD=>${lastVisibleItem + VISIBLE_THRESHOLD} "
                    )
                    Logger.i(
                        "$TAG=>onScrolled",
                        "totalItemCount=>$totalItemCount" + " \n loading->${!loading}  pageEnd->${!pageEnd}"
                    )

                    if (!loading && !pageEnd
                        && totalItemCount <= (lastVisibleItem + VISIBLE_THRESHOLD)
                    ) {
                        Logger.i(TAG, "totalItemCount =>getMoments")
                        getMoments()
                    }
                } else {
                    mBinding.floatingUpArrowIV.visibility = View.GONE
                }

            }
        }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 101 && sharedPrefsInf.get(PREF_CONTENT_SHARED, false)) {
            Logger.d(TAG, "Shared feeds data")
            logActivityAction(
                ActivityLogEnum.SHARE,
                "Shared this moment",
                homeViewModel.getSavedActionFeedInfo()
            )
        }
    }

    override fun onPause() {
        super.onPause()
        tickReceiver?.let {
            requireContext().unregisterReceiver(it)
        }
        if (::feedsAdapter.isInitialized) {
            feedsAdapter.pausePlayer()
        }
    }

    override fun onResume() {
        super.onResume()
        tickReceiver?.let {
            requireContext().registerReceiver(
                it,
                IntentFilter(Intent.ACTION_TIME_TICK)
            )
           // homeViewModel.markMomentRead()
        }
        if (::feedsAdapter.isInitialized) {
            feedsAdapter.resumePlayer()
        }
    }

    override fun onStop() {
        super.onStop()
        try {
            if (::feedsAdapter.isInitialized) {
                feedsAdapter.pausePlayer()
            }
        } catch (ex: Exception) {
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        tickReceiver = null
        if (::feedsAdapter.isInitialized) {
            feedsAdapter.releasePlayer()
        }
    }

    fun wooticSdkSetUp() {
        val wootric = Wootric.init(
            requireActivity(),
            "cd14f8ccab1f887882d1039780e125afa3f356df7d9cc3e58b70f85259fc6f8c",
            "NPS-71899b45"
        );
        val loginSessionInfo = homeViewModel.getLoginSessionDetails()
        wootric.setEndUserEmail(loginSessionInfo.first);
        wootric.setEndUserCreatedAt(loginSessionInfo.second);
        wootric.setSurveyColor(R.color.colorPrimary)
        wootric.setScoreColor(R.color.colorPrimary)

        // Use only for testing
        if (homeViewModel.activeSavedView.value?.savedViewId == "Test: MW Google Sheet")
            wootric.setSurveyImmediately(true);
        wootric.survey();
    }

    fun setTimeChangeBroadcast() {
        // homeViewModel.markMomentRead()
        if (tickReceiver == null) {
            tickReceiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context, intent: Intent) {
                    if (intent.action!!.compareTo(Intent.ACTION_TIME_TICK) == 0) {
                        //   homeViewModel.markMomentRead()
                    }
                }
            }
            requireContext().registerReceiver(
                tickReceiver as BroadcastReceiver,
                IntentFilter(Intent.ACTION_TIME_TICK)
            )
        }
    }

}