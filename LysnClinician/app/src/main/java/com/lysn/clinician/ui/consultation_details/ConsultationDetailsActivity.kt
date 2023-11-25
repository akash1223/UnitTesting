package com.lysn.clinician.ui.consultation_details

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.lysn.clinician.BuildConfig
import com.lysn.clinician.R
import com.lysn.clinician.databinding.ActivityConsultationDetailsBinding
import com.lysn.clinician.http.Resource
import com.lysn.clinician.model.ConsultationDetails
import com.lysn.clinician.ui.base.BaseActivity
import com.lysn.clinician.ui.base.BaseFragment
import com.lysn.clinician.ui.consultation_list.ConsultationListViewModel
import com.lysn.clinician.ui.join_consultation.JoinConsultationActivity
import com.lysn.clinician.utility.ConsultationStatusCode
import com.lysn.clinician.utils.BundleConstants
import com.lysn.clinician.utils.LocalizeTextProvider
import com.lysn.clinician.utils.MixPanelData
import com.lysn.clinician.utils.PreferenceUtil
import kotlinx.android.synthetic.main.activity_consultation_details.*
import kotlinx.android.synthetic.main.toolbar_layout.view.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


/**
 * This class shows consultation details
 */
class ConsultationDetailsActivity : BaseActivity() {

    private val mPreferenceUtil: PreferenceUtil by inject()
    private val mViewModel: ConsultationDetailsViewModel by viewModel()
    private lateinit var mBinding: ActivityConsultationDetailsBinding
    private var isSessionCancel: Boolean = false
    private lateinit var mConsultationDetails : ConsultationDetails

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_consultation_details
        ) as ActivityConsultationDetailsBinding

        try {
            intent?.getParcelableExtra<ConsultationDetails>(BundleConstants.KEY_CONSULTATION_DATA).let{
                if (it != null) {
                    mConsultationDetails = it
                }
            }
            setup()
            setObserver()
        }catch (e: Exception) {
            showToast(
                LocalizeTextProvider(this).getSomethingWrongMessage(),
                true
            )
            finish()
        }
    }

    override fun setup() {

        mBinding.toolbar.txt_title.text = getString(R.string.consultation_details)
        mBinding.lifecycleOwner = this
        mBinding.viewModel = mViewModel
        mBinding.statusColor = ConsultationStatusCode(this)
        setDataToViewModel(mConsultationDetails)

        // screen visited mix panel event
        mConsultationDetails.client?.let {
            mixPanelScreenVisitedEvent(
                MixPanelData.CONSULTATION_DETAILS_VIEW_SHOWN_EVENT,
                it.firstName + " " + it.lastName,
                this
            )
        }

        mBinding.toolbar.iv_back?.let {
            it.setOnClickListener {
                finish()
            }
        }

        mBinding.txtViewProfile.setOnClickListener {
            showSnackBar(
                root_layout,
                getString(R.string.coming_soon),
                true
            )
        }

        btn_join_consultation.setOnClickListener {
            PreferenceUtil(this).clearVideoSessionPref()
            val intent = Intent(this, JoinConsultationActivity::class.java)
            val bundle = Bundle()
            bundle.putParcelable(BundleConstants.KEY_CONSULTATION_DATA, mConsultationDetails)
            intent.putExtras(bundle)
            startActivity(intent)
        }

        mBinding.txtCancellationPolicy.setOnClickListener {
            val uri: Uri = Uri.parse(BuildConfig.TERMS_CONDITIONS_URL)
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)

            mixPanelButtonClickEvent(
                mBinding.txtCancellationPolicy,
                MixPanelData.CANCELLATION_POLICY_BUTTON_CLICKED_EVENT,
                this
            )
        }

        mBinding.txtRescheduleConsultation.setOnClickListener {
            val uri: Uri = Uri.parse(BuildConfig.WEB_BASE_URL)
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)

            mixPanelButtonClickEvent(
                mBinding.txtRescheduleConsultation,
                MixPanelData.RESCHEDULE_CONSULTATION_BUTTON_CLICKED_EVENT,
                this
            )
        }

    }

    private fun setDataToViewModel(consultationDetails: ConsultationDetails?) {
        if (consultationDetails != null) {
            consultationDetails.timerDisplayName = consultationDetails.statusForClientDisplay
            consultationDetails.timerStatus = consultationDetails.status
            mViewModel.addConsultationData(consultationDetails)
            if (consultationDetails.canJoin) {
                setTimeChangeBroadcast(object : BaseFragment.InterfaceTimeChange {
                    override fun timeChanged() {
                        mViewModel.timerChange()
                    }
                })
            }
        }
    }

    private fun setObserver() {
        mViewModel.onCancelConsultationObservable.observe(this, Observer {
            showAlert()

            // cancel consultation button clicked event
            mixPanelButtonClickEvent(
                    mBinding.txtCancelConsultation,
            MixPanelData.CANCEL_CONSULTATION_BUTTON_CLICKED_EVENT,
            this
            )
        })
    }

    private fun showAlert() {
        MaterialAlertDialogBuilder(this, R.style.ThemeOverlay_App_MaterialAlertDialog)
            .setTitle(getString(R.string.cancel_consultation_dialog_title))
            .setMessage(getString(R.string.cancel_consultation_description))

            .setNeutralButton(getString(R.string.never_mind_text))
            { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(getString(R.string.confirm_cancellation_text))
            { _, _ ->
                cancelConsultation()
            }
            .show()
    }

    private fun cancelConsultation() {
        mViewModel.cancelConsultation().observe(this, Observer { response ->

            when (response.status) {

                Resource.Status.LOADING ->
                    showLoading()

                Resource.Status.SUCCESS -> {
                    hideLoading()
                    setDataToViewModel(response.data?.consultation)
                    showSnackBar(
                        root_layout,
                        getString(R.string.cancel_consultation_success_message),
                        true
                    )
                    mPreferenceUtil.putValue(PreferenceUtil.REFRESH_CONSULTATION_LIST,true)
                    btn_join_consultation.text = getString(R.string.book_client_again)
                    btn_join_consultation.isClickable = false
                    txt_cancel_consultation.visibility = View.GONE
                    isSessionCancel = true
                }

                Resource.Status.NO_INTERNET -> {
                    hideLoading()
                    showNoInternetMaterialDialog()
                }

                Resource.Status.ERROR -> {
                    hideLoading()
                    showSnackBar(root_layout, response.message.toString(), true)
                }

                else -> {
                    hideLoading()
                    showSnackBar(root_layout, response.message.toString(), true)
                }
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}