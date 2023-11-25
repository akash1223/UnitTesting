package com.example.lysnclient.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.example.lysnclient.R
import com.example.lysnclient.adapters.AdapterUserDashboard
import com.example.lysnclient.databinding.FragmentUserBinding
import com.example.lysnclient.model.DashboardUser
import com.example.lysnclient.viewmodel.HomeDashboardViewModel
import java.util.*

class UserFragment(private val homeDashboardViewModel: HomeDashboardViewModel) : BaseFragment() {

    private lateinit var userBinding: FragmentUserBinding
    private lateinit var mAdapter: AdapterUserDashboard
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        userBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_user, container, false
        )
        return userBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        setup()
        super.onActivityCreated(savedInstanceState)
    }

    override fun setup() {
        userBinding.lifecycleOwner = this
        userBinding.viewModel = homeDashboardViewModel
        mView = userBinding.dashboardUserLayout

        val listOfYouScreen = ArrayList<DashboardUser>()
        listOfYouScreen.add(
            DashboardUser(
                R.drawable.ic_icon_placeholder,
                R.string.user_consultations_title,
                R.string.user_consultations_sub_title
            )
        )
        listOfYouScreen.add(
            DashboardUser(
                R.drawable.ic_icon_placeholder_two,
                R.string.user_message_title,
                R.string.user_message_sub_title
            )
        )
        listOfYouScreen.add(
            DashboardUser(
                R.drawable.ic_icon_placeholder_three,
                R.string.user_wellbeing_tracker_title,
                R.string.user_wellbeing_tracker_sub_title
            )
        )
        listOfYouScreen.add(
            DashboardUser(
                R.drawable.ic_icon_placeholder_four,
                R.string.user_Assessments_title,
                R.string.user_Assessments_sub_title
            )
        )

        listOfYouScreen.add(
            DashboardUser(
                R.drawable.ic_icon_placeholder,
                R.string.logout,
                R.string.logout_subtitle
            )
        )

        homeDashboardViewModel.listOfYouScreen = listOfYouScreen
        mAdapter =
            AdapterUserDashboard(requireActivity(), listOfYouScreen, homeDashboardViewModel)
        userBinding.myAdapter = mAdapter
    }

    companion object {
        @JvmStatic
        fun newInstance(homeDashboardViewModel: HomeDashboardViewModel) =
            UserFragment(homeDashboardViewModel).apply {

            }
    }
}