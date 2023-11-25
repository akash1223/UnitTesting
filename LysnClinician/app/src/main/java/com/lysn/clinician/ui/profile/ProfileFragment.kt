package com.lysn.clinician.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.lysn.clinician.R
import com.lysn.clinician.databinding.FragmentProfileBinding
import com.lysn.clinician.http.Resource
import com.lysn.clinician.ui.AuthenticateActivity
import com.lysn.clinician.ui.base.BaseFragment
import com.lysn.clinician.utils.MixPanelData
import com.lysn.clinician.utils.PreferenceUtil
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.toolbar_shadow_layout.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * This class used for user profile and handle settings,logout functionality
 */
class ProfileFragment : BaseFragment() {
    private val mViewModel: ProfileViewModel by viewModel()
    private lateinit var mProfileBinding: FragmentProfileBinding
    private val mPreferenceUtil: PreferenceUtil by inject()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mProfileBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_profile, container, false
        )
        mProfileBinding.lifecycleOwner = this
        return mProfileBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setup()
        toolbarButtonSetUp()
        setObserver()
    }

    override fun setup() {
        mNavController.currentDestination?.label?.let { setToolbarTitle(it.toString()) }
        mProfileBinding.lifecycleOwner = this
        mProfileBinding.viewModel = mViewModel
        mProfileBinding.lifecycleOwner = viewLifecycleOwner

        constraint_settings.setOnClickListener {
            startActivity(Intent(requireActivity(),SettingsActivity::class.java))
        }

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

    private fun setObserver() {
        mViewModel.onLogoutObservable.observe(viewLifecycleOwner, Observer {
            showLogoutAlert()
        })
    }


    private fun showLogoutAlert() {
        MaterialAlertDialogBuilder(requireActivity(), R.style.ThemeOverlay_App_MaterialAlertDialog)
            .setTitle(getString(R.string.logout_text))
            .setMessage(getString(R.string.logout_confirmation_message))

            .setNegativeButton(getString(R.string.cancel_text))
            { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(getString(R.string.confirm_text))
            { _, _ ->
                logoutUser()
                mixPanelButtonClickEvent(
                    mProfileBinding.txtViewLogout,
                    MixPanelData.LOGOUT_BUTTON_CLICKED_EVENT,
                    requireActivity()
                )
            }
            .show()
    }

    private fun logoutUser() {
        mViewModel.logoutUser().observe(viewLifecycleOwner, Observer { response ->
            if (response.status == Resource.Status.LOADING) {
                showLoading()
            } else {
                hideLoading()
                mPreferenceUtil.clearAll()
                val intent = Intent(requireActivity(), AuthenticateActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
                requireActivity().finish()
            }
        })


    }
}