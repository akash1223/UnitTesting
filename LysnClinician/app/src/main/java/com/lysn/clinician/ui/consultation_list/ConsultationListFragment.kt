package com.lysn.clinician.ui.consultation_list

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.lysn.clinician.R
import com.lysn.clinician.databinding.FragmentConsultationListBinding
import com.lysn.clinician.http.Resource
import com.lysn.clinician.model.ConsultationDetails
import com.lysn.clinician.model.ConsultationsDetailsResponse
import com.lysn.clinician.ui.base.BaseFragment
import com.lysn.clinician.ui.consultation_details.ConsultationDetailsActivity
import com.lysn.clinician.utils.BundleConstants
import com.lysn.clinician.utils.MixPanelData
import com.lysn.clinician.utility.extensions.color
import com.lysn.clinician.utility.extensions.scrollToTop
import com.lysn.clinician.utils.PreferenceUtil
import com.xwray.groupie.*
import kotlinx.android.synthetic.main.toolbar_shadow_layout.*
import org.json.JSONObject
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber


class ConsultationListFragment : BaseFragment() {

    private val mViewModel  by sharedViewModel<ConsultationListViewModel>()
    private lateinit var mConsultationListBinding: FragmentConsultationListBinding
    private val mPreferenceUtil: PreferenceUtil by inject()
    //RecycleView Variables
    private lateinit var groupAdapter: GroupAdapter<GroupieViewHolder>
    private val sectionReadyToJoin = Section()
    private val sectionUpcoming = Section()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mConsultationListBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_consultation_list, container, false
        )
        mConsultationListBinding.lifecycleOwner = this
        return mConsultationListBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setup()
        observeData()
    }

    override fun setup() {


        mNavController.currentDestination?.label?.let { setToolbarTitle(it.toString()) }
        recycleViewSetUp()
        toolbarButtonSetUp()
        mixPanelScreenVisitedEvent(MixPanelData.CONSULTATION_LIST_VIEW_SHOWN_EVENT)

    }

    private fun recycleViewSetUp() {
        groupAdapter = GroupAdapter<GroupieViewHolder>().apply {
            sectionReadyToJoin.setHideWhenEmpty(true)
            sectionReadyToJoin.setHeader(ConsultationListHeaderItem(R.string.consultation_ready_to_join))
            add(sectionReadyToJoin)

            sectionUpcoming.setHeader(ConsultationListHeaderItem(R.string.upcoming_consultations))
            sectionUpcoming.setHideWhenEmpty(true)
            add(sectionUpcoming)
        }
        mConsultationListBinding.recyclerConsultationList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = groupAdapter
        }
        // pull to refresh setup
        mConsultationListBinding.swipeRefresh.setOnRefreshListener {
            mViewModel.fetchConsultations()
        }
        mConsultationListBinding.swipeRefresh.setColorSchemeColors(requireContext().color(R.color.colorPrimary))
    }

    private fun toolbarButtonSetUp() {
        iv_back.setImageResource(R.drawable.ic_help)
        iv_back.contentDescription = context?.getString(R.string.accessibility_home_help)

        iv_menu.setImageResource(R.drawable.ic_notification_border)
        iv_menu.visibility = View.VISIBLE
        iv_back.contentDescription = context?.getString(R.string.accessibility_home_notification)

        iv_back.setOnClickListener {
            // Perform Action
        }
        iv_menu.setOnClickListener {
            // Perform Action
        }
    }

    private fun observeData() {

        mViewModel.getConsultations().observe(
            viewLifecycleOwner,
            Observer { response ->

                if (mConsultationListBinding.swipeRefresh.isRefreshing) {
                    if (response.status != Resource.Status.LOADING)
                        mConsultationListBinding.swipeRefresh.isRefreshing = false
                } else {
                    if (response.status == Resource.Status.LOADING) showLoading() else hideLoading()
                }

                when (response.status) {
                    Resource.Status.SUCCESS -> {
                        response.data?.let { setDataToUI(it) }
                    }
                    Resource.Status.NO_INTERNET -> {
                        showNoInternetMaterialDialog()
                    }
                    Resource.Status.ERROR -> {
                        response.message?.let { showSnackBar(mConsultationListBinding.toolbar, it,true) }
                    }
                    else->{
                        response.message?.let { showSnackBar(mConsultationListBinding.toolbar, it,true) }
                    }
                }
            })
        mViewModel.getReadyToJoin().observe(viewLifecycleOwner, Observer {

            val listItem: List<ConsultationListItem> =
                it.map { m -> ConsultationListItem(m, onItemClickListener,true) }
            sectionReadyToJoin.update(listItem)
            mConsultationListBinding.recyclerConsultationList.scrollToTop()
        })
        mViewModel.getUpcomingConsultation().observe(viewLifecycleOwner, Observer {
            val listItem: List<ConsultationListItem> =
                it.map { m -> ConsultationListItem(m, onItemClickListener) }
            sectionUpcoming.update(listItem)
        })

    }

    private fun setDataToUI(consultationsDetailsResponse: ConsultationsDetailsResponse) {
        prepareReadyToJoinList(consultationsDetailsResponse)
        prepareUpcomingList(consultationsDetailsResponse)

        if (consultationsDetailsResponse.canJoinIds.isEmpty() &&
            consultationsDetailsResponse.upcomingIds.isEmpty()
        ) {
            mConsultationListBinding.recyclerConsultationList.visibility = View.GONE
            mConsultationListBinding.txtEmptyConsultation.visibility = View.VISIBLE

        } else {
            mConsultationListBinding.recyclerConsultationList.visibility =View.VISIBLE
            mConsultationListBinding.txtEmptyConsultation.visibility = View.GONE
        }
    }

    private fun prepareReadyToJoinList(consultationsDetailsResponse: ConsultationsDetailsResponse) {
        val readyToJoin: List<ConsultationDetails>? =
            consultationsDetailsResponse?.byId?.filterKeys { key ->
                consultationsDetailsResponse.canJoinIds.contains(key)
            }?.map { x -> x.value }

        readyToJoin?.forEach {
            it.timerDisplayName=it.statusForClientDisplay
            it.timerStatus=it.status
        }

        if (readyToJoin?.size!! > 0) {
            mViewModel.setReadyToJoin(readyToJoin)
            Timber.d("Timber    IN prepareReadyToJoinList")
            setTimeChangeBroadcast(object : InterfaceTimeChange{
                override fun timeChanged() {
                    mViewModel.timerChange()
                }
            })
        } else {
            mViewModel.setUpcomingConsultation(listOf())
            sectionReadyToJoin.update(listOf())
        }
    }

    private fun prepareUpcomingList(consultationsDetailsResponse: ConsultationsDetailsResponse) {
        val upcomingData: List<ConsultationDetails>? =
            consultationsDetailsResponse?.byId?.filter {
                consultationsDetailsResponse.upcomingIds.contains(it.key) && !consultationsDetailsResponse.canJoinIds.contains(
                    it.key
                )
            }?.map { x -> x.value }

        if (upcomingData?.size!! > 0) {
            mViewModel.setUpcomingConsultation(upcomingData)
        } else {
            mViewModel.setUpcomingConsultation(listOf())
            sectionUpcoming.update(listOf())
        }
    }

    private val onItemClickListener = OnItemClickListener { item, view ->
        val consultationListItem: ConsultationListItem = item as ConsultationListItem;
        val intent = Intent(requireContext(), ConsultationDetailsActivity::class.java)
        val bundle = Bundle()
        bundle.putParcelable(BundleConstants.KEY_CONSULTATION_DATA, consultationListItem.consultationDetails)
        intent.putExtras(bundle)

        val properties = JSONObject()
        properties.put(MixPanelData.KEY_SCREEN_NAME, "Consultation List")
        properties.put(MixPanelData.KEY_CONSULTATION_ID, consultationListItem.consultationDetails.id)
        properties.put(MixPanelData.KEY_CONSULTATION_TYPE, consultationListItem.consultationDetails.type)
        properties.put(MixPanelData.KEY_CONSULTATION_STATUS, consultationListItem.consultationDetails.status)
        properties.put(MixPanelData.KEY_CONSULTATION_DURATION, consultationListItem.consultationDetails.duration)
        MixPanelData.getInstance(requireActivity())
            .addEvent(properties,MixPanelData.CONSULTATION_LIST_ITEM_CLICKED_EVENT)
        requireActivity().startActivity(intent)
    }

    override fun onResume() {
        if(mPreferenceUtil.getValue(PreferenceUtil.REFRESH_CONSULTATION_LIST,false))
        {
            mPreferenceUtil.putValue(PreferenceUtil.REFRESH_CONSULTATION_LIST,false)
            mViewModel.fetchConsultations()
        }
        super.onResume()
    }
}