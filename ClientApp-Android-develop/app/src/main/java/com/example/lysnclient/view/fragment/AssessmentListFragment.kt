package com.example.lysnclient.view.fragment

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.example.lysnclient.R
import com.example.lysnclient.adapters.AdapterAssessmentList
import com.example.lysnclient.databinding.FragmentAssessmentListBinding
import com.example.lysnclient.http.ResponseStatus
import com.example.lysnclient.utils.AppConstants
import com.example.lysnclient.utils.MixPanelData
import com.example.lysnclient.view.AssessmentDetailActivity
import com.example.lysnclient.view.HomeActivity
import com.example.lysnclient.viewmodel.ListOfAssessmentViewModel
import kotlinx.android.synthetic.main.view_toolbar.view.*
import org.koin.android.viewmodel.ext.android.viewModel

/**
 * This class is used for display list of assessment type
 */
class AssessmentListFragment : BaseFragment() {
    private lateinit var assListFragmentBinding: FragmentAssessmentListBinding
    private lateinit var mAdapter: AdapterAssessmentList
    private val viewModel: ListOfAssessmentViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        assListFragmentBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_assessment_list, container, false
        )
        setHasOptionsMenu(true)
        return assListFragmentBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setup()
    }

    override fun setup() {
        assListFragmentBinding.lifecycleOwner = this
        mView = assListFragmentBinding.assListFragmentLayout

        assListFragmentBinding.toolbarTitle.text =
            getString(R.string.title_assessment)
        val activity = activity as? HomeActivity
        activity?.setSupportActionBar(assListFragmentBinding.toolbar)
        activity?.supportActionBar?.title = AppConstants.EMPTY_VALUE
        activity?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        activity?.supportActionBar?.setDisplayHomeAsUpEnabled(true)

        mAdapter = AdapterAssessmentList(requireActivity(), ArrayList(), viewModel)
        assListFragmentBinding.myAdapter = mAdapter
        showLoading()
        viewModel.listOfAssessmentLiveData.observe(viewLifecycleOwner, Observer {
            hideLoading()
            when (it.status) {
                ResponseStatus.SUCCESS -> {
                    if (it.apiResponse.isNullOrEmpty()) showSnackMsg(getString(R.string.no_assessment_available))
                    else mAdapter.setDataList(it.apiResponse ?: ArrayList())
                }
                ResponseStatus.NO_INTERNET -> {
                    showNoInternetDialog()
                }
                ResponseStatus.FAILURE -> {
                    showSnackMsg(it.message)
                }
                ResponseStatus.BAD_PARAMS -> {
                    showSnackMsg(it.message)
                }
                else -> {
                    showSnackMsg(it.message)
                }
            }
        })
        viewModel.navigateToDetailObservable.observe(viewLifecycleOwner, Observer {
            if (it != null && it > -1)
                startActivity(
                    Intent(requireActivity(), AssessmentDetailActivity::class.java).putExtra(
                        AppConstants.INTENT_ASSESSMENT_ID,
                        it
                    )
                )
        })
        MixPanelData.getInstance(requireActivity())
            .addEvent(MixPanelData.eventVisitedAssessmentList)
    }

    override fun onDestroyView() {
        val activity = activity as? HomeActivity
        activity?.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        setHasOptionsMenu(false)
        super.onDestroyView()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_assessment_list, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                requireActivity().onBackPressed()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        @JvmStatic
        fun newInstance() = AssessmentListFragment().apply { }
    }
}