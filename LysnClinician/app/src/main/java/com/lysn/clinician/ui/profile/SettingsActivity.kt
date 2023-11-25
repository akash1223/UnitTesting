package com.lysn.clinician.ui.profile

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.google.gson.Gson
import com.lysn.clinician.R
import com.lysn.clinician.databinding.ActivitySettingsBinding
import com.lysn.clinician.http.Resource
import com.lysn.clinician.model.AllowNotificationRequestData
import com.lysn.clinician.model.UserProfileResponse
import com.lysn.clinician.ui.base.BaseActivity
import com.lysn.clinician.utils.MixPanelData
import com.lysn.clinician.utils.PreferenceUtil
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.toolbar_layout.view.*
import org.json.JSONObject
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.lang.Exception

/**
 * This class is used for update allow notification settings and  show timezone
 */
class SettingsActivity : BaseActivity() {
    private val mViewModel: SettingsViewModel by viewModel()
    private val mPreferenceUtil: PreferenceUtil by inject()
    private lateinit var mBinding: ActivitySettingsBinding
    private var mAllowNotificationRequestData = AllowNotificationRequestData()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        mBinding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_settings
        ) as ActivitySettingsBinding

        setup()
        setObserver()

    }

    override fun setup() {
        mBinding.lifecycleOwner = this
        mBinding.viewModel = mViewModel
        mBinding.toolbar.txt_title.text = getString(R.string.settings_text)

        try {
            val userProfileData: UserProfileResponse = Gson().fromJson(mPreferenceUtil.getUserProfile(), UserProfileResponse::class.java)
            mBinding.checkboxEmailNotification.isChecked = userProfileData.user.sendEmailReminders
            mBinding.checkboxSmsNotification.isChecked = userProfileData.user.sendSmsReminders
            mBinding.txtTimezone.text = userProfileData.user.timezone
            mAllowNotificationRequestData.user.send_email_reminders = userProfileData.user.sendEmailReminders
            mAllowNotificationRequestData.user.send_sms_reminders = userProfileData.user.sendSmsReminders

        }catch (e : Exception){
            e.printStackTrace()
        }


        mBinding.checkboxEmailNotification.setOnCheckedChangeListener { _, isChecked ->

            mAllowNotificationRequestData.user.send_email_reminders = isChecked
            val properties = JSONObject()
            if (isChecked) {
                properties.put(MixPanelData.EMAIL_NOTIFICATION_SELECTED, MixPanelData.YES)

            } else {
                properties.put(MixPanelData.EMAIL_NOTIFICATION_SELECTED, MixPanelData.NO)
            }
            MixPanelData.getInstance(this)
                .addEvent(
                    properties,
                    MixPanelData.ALLOW_NOTIFICATION_EVENT
                )
        }

        mBinding.checkboxSmsNotification.setOnCheckedChangeListener { _, isChecked ->

            mAllowNotificationRequestData.user.send_sms_reminders = isChecked
            val properties = JSONObject()
            if (isChecked) {
                properties.put(MixPanelData.SMS_NOTIFICATION_SELECTED, MixPanelData.YES)

            } else {
                properties.put(MixPanelData.SMS_NOTIFICATION_SELECTED, MixPanelData.NO)

            }
            MixPanelData.getInstance(this)
                .addEvent(
                    properties,
                    MixPanelData.ALLOW_NOTIFICATION_EVENT
                )
        }

        mBinding.toolbar.iv_back?.let {
            it.setOnClickListener {
                finish()
            }
        }

        mixPanelScreenVisitedEvent(MixPanelData.SETTING_VIEW_SHOWN_EVENT)

    }

    private fun setObserver() {
        mViewModel.onSaveSettingsObservable.observe(this, Observer {
            mViewModel.saveSettings(mAllowNotificationRequestData)
                .observe(this, Observer { response ->

                    when (response.status) {
                        Resource.Status.LOADING ->
                            showLoading()

                        Resource.Status.SUCCESS -> {
                            hideLoading()
                            mPreferenceUtil.putValue(PreferenceUtil.USER_PROFILE_PREFERENCE_KEY,Gson().toJson(response.data))
                            showSnackBar(
                                root_layout,
                                getString(R.string.notification_setting_change_success_message),
                                true
                            )
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

            mixPanelButtonClickEvent(
                mBinding.btnSave,
                MixPanelData.SAVE_BUTTON_CLICKED_EVENT,
                this
            )
        })
    }


}