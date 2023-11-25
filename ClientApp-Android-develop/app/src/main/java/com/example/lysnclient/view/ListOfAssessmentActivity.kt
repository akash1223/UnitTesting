package com.example.lysnclient.view

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.example.lysnclient.R
import com.example.lysnclient.adapters.AdapterAssessmentList
import com.example.lysnclient.databinding.ActivityListOfAssessmentBinding
import com.example.lysnclient.http.ResponseStatus
import com.example.lysnclient.utils.AppConstants
import com.example.lysnclient.utils.MixPanelData
import com.example.lysnclient.viewmodel.ListOfAssessmentViewModel
import kotlinx.android.synthetic.main.view_toolbar.view.*
import org.koin.android.viewmodel.ext.android.viewModel

class ListOfAssessmentActivity : BaseActivity() {
    private lateinit var mAdapter: AdapterAssessmentList
    private lateinit var activityAssessmentBinding: ActivityListOfAssessmentBinding
    private val viewModel: ListOfAssessmentViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_of_assessment)
        setup()
    }

    override fun setup() {
        activityAssessmentBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_list_of_assessment)
        mView = activityAssessmentBinding.assessmentListLayout
        activityAssessmentBinding.lifecycleOwner = this
        activityAssessmentBinding.layoutToolbar.toolbar_title.text =
            getString(R.string.title_assessment)
        setSupportActionBar(activityAssessmentBinding.layoutToolbar.toolbar)
        supportActionBar?.title=AppConstants.EMPTY_VALUE
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        mAdapter = AdapterAssessmentList(this, ArrayList(), viewModel)
        activityAssessmentBinding.myAdapter = mAdapter

        showLoading()
        viewModel.listOfAssessmentLiveData.observe(this, Observer {
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
                ResponseStatus.UNAUTHORIZED_TOKEN_EXPIRED -> {
                    showSnackMsg(it.message)
                }
                else -> {
                    showSnackMsg(it.message)
                }
            }
        })
        viewModel.navigateToDetailObservable.observe(this, Observer {
            if (it != null && it > -1)
                startActivity(
                    Intent(this, AssessmentDetailActivity::class.java).putExtra(
                        AppConstants.INTENT_ASSESSMENT_ID,
                        it
                    )
                )
        })
        MixPanelData.getInstance(this).addEvent(MixPanelData.eventVisitedAssessmentList)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_assessment_list, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                super.onBackPressed()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
