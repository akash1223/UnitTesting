package com.inmoment.moments.menu.saved_views.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.appcretor.wiproexercise.ui.base.GenericListAdapter
import com.inmoment.moments.R
import com.inmoment.moments.databinding.ListItemSavedViewsBinding
import com.inmoment.moments.framework.common.AppConstants
import com.inmoment.moments.framework.datamodel.SavedViewsListResponseData
import com.inmoment.moments.framework.persist.SharedPrefsInf
import com.inmoment.moments.framework.ui.BaseFragment
import com.inmoment.moments.home.MomentType
import com.inmoment.moments.menu.saved_views.SavedViewsViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SaveViewsFragment : BaseFragment() {

    private val savedViewsViewModel: SavedViewsViewModel by viewModels()
    private lateinit var genericRecycleAdapter: GenericListAdapter<SavedViewsListResponseData?, ListItemSavedViewsBinding?>

    @Inject
    lateinit var sharedPrefsInf: SharedPrefsInf

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_save_views, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setRecycleViewData(view)
        setData()
    }

    private fun setRecycleViewData(view: View) {
        view.findViewById<RecyclerView>(R.id.recycler_saved_views).apply {
            layoutManager = LinearLayoutManager(requireContext())
            genericRecycleAdapter = object :
                GenericListAdapter<SavedViewsListResponseData?, ListItemSavedViewsBinding?>(
                    requireContext()
                ) {
                override val layoutResId: Int
                    get() = R.layout.list_item_saved_views

                override fun onBindData(
                    model: SavedViewsListResponseData?,
                    position: Int,
                    dataBinding: ListItemSavedViewsBinding?
                ) {
                    if (dataBinding != null) {
                        if (model?.savedViewSource == null)
                            model?.savedViewSource = "XI"
                        if (savedViewsViewModel.visitedSavedViewList.contains(model?.savedViewId))
                            model?.isVisitedSavedView = true
                        dataBinding.item = model
                    }
                }

                override fun onItemClick(model: SavedViewsListResponseData?, position: Int) {

                    model?.let {
                        if (savedViewsViewModel.storeSavedViewsData(model)) {
                            mNavController.previousBackStackEntry?.savedStateHandle?.set(
                                AppConstants.NAV_BACK_MOMENT_TYPE,
                                MomentType.SAVED_VIEWS
                            )
                        }
                    }
                    mNavController.popBackStack()
                }
            }
            adapter = genericRecycleAdapter
        }
    }

    private fun setData() {

        savedViewsViewModel.getSavedViewsFromDB().observe(viewLifecycleOwner) {
            if (!it.isNullOrEmpty()) {
                genericRecycleAdapter.submitList(it)
            }
        }
        val savedViewsData = savedViewsViewModel.getSavedViewsInfo()
        savedViewsData.successLiveData?.observe(viewLifecycleOwner) {
            savedViewsViewModel.storeSelectedSavedViewsData(it)
        }
        savedViewsData.errorLiveData?.observe(viewLifecycleOwner) {
            showError(R.string.error_getting_saved_views_data)
        }
    }
}
