package com.lysn.clinician.utils


import android.content.Context
import com.lysn.clinician.BuildConfig
import com.mixpanel.android.mpmetrics.MixpanelAPI
import org.json.JSONObject
import java.util.*

class MixPanelData private constructor(context: Context) {

    private var mixPanelInstance: MixpanelAPI =
        MixpanelAPI.getInstance(context, BuildConfig.MIX_PANEL_TOKEN)

    fun addEvent(eventName: String) {
        mixPanelInstance.track(eventName)
    }

    fun addEvent(jsonData: JSONObject, eventName: String) {
        mixPanelInstance.track(eventName, jsonData)
    }

    fun addEvent(key: String, value: String, eventName: String) {
        val props = JSONObject()
        props.put(key, value)
        mixPanelInstance.track(eventName, props)
    }

    fun flushMixPanel() {
        mixPanelInstance.flush()
    }

    fun createProfile(email: String, mobile: String) {

        mixPanelInstance.identify(email)
        val properties = JSONObject()
        properties.put(KEY_EMAIL, email)
        properties.put(KEY_MOBILE, mobile)
        properties.put(KEY_TIMEZONE, TimeZone.getDefault())
        mixPanelInstance.people.set(properties)

        val superProperties = JSONObject()
        superProperties.put(KEY_EMAIL, email)
        superProperties.put(KEY_MOBILE, mobile)
        mixPanelInstance.registerSuperProperties(superProperties)
    }

    fun createProfile(email: String) {

        mixPanelInstance.identify(email)
        val properties = JSONObject()
        properties.put(KEY_EMAIL, email)
        properties.put(KEY_TIMEZONE, TimeZone.getDefault())
        mixPanelInstance.people.set(properties)

        val superProperties = JSONObject()
        superProperties.put(KEY_EMAIL, email)
        mixPanelInstance.registerSuperProperties(superProperties)
    }

    companion object : SingletonHolder<MixPanelData, Context>(::MixPanelData) {

        // Screen Events
        const val WELCOME_VIEW_SHOWN_EVENT = "WelCome View Shown"
        const val SIGN_IN_VIEW_SHOWN_EVENT = "SignIn View Shown"
        const val SIGN_IN_COMPLETED_EVENT = "SignIn Completed"
        const val CONSULTATION_DETAILS_VIEW_SHOWN_EVENT = "Consultation details View Shown"
        const val JOIN_CONSULTATION_VIEW_SHOWN_EVENT = "Join consultation view Shown"
        const val TERMS_AND_CONDITION_VIEW_SHOWN_EVENT = "Terms & Conditions  View Shown"
        const val CONSULTATION_LIST_VIEW_SHOWN_EVENT = "Consultation List View Shown"
        const val VIDEO_SESSION_VIEW_SHOWN_EVENT = "Video Calling View Shown"
        const val FORGOT_PASSWORD_VIEW_SHOWN_EVENT = "Forgot Password"
        const val FORGOT_PASSWORD_VIEW_DISMISSED_EVENT = "Forgot Password Dismissed"
        const val SIGN_UP_VIEW_DISMISSED_EVENT = "Sign Up Dismissed"
        const val SETTING_VIEW_SHOWN_EVENT = "Settings View Shown"


        // Button click events
        const val REGISTER_WITH_LYSN_BUTTON_CLICKED_EVENT = "Register with Lysn Button Click"
        const val LOGIN_BUTTON_CLICKED_EVENT = "Login Button Click"
        const val SIGN_IN_BUTTON_CLICKED_EVENT = "SignIn Button Click"
        const val CANCEL_CONSULTATION_BUTTON_CLICKED_EVENT = "Cancel Consultation Button Click"
        const val JOIN_CONSULTATION_BUTTON_CLICKED_EVENT = "Join Consultation Button Click"
        const val TERMS_AND_CONDITION_REVIEW_BUTTON_CLICKED_EVENT = "Review Button Click"
        const val TERMS_AND_CONDITION_ACCEPT_BUTTON_CLICKED_EVENT = "Accept Terms Button Click"
        const val CONSULTATION_LIST_ITEM_CLICKED_EVENT = "Consultation List Item Click"
        const val END_CONSULTATION_BUTTON_CLICKED_EVENT = "End Consultation Button Click"
        const val EXIT_CONSULTATION_BUTTON_CLICKED_EVENT = "Exit Consultation Button Click"
        const val CAMERA_BUTTON_CLICKED_EVENT = "Camera Button Click"
        const val MIC_BUTTON_CLICKED_EVENT = "Mic Button Click"
        const val VOLUME_BUTTON_CLICKED_EVENT = "Volume Button Click"
        const val SWITCH_CAMERA_BUTTON_CLICKED_EVENT = "Switch camera Button Click"
        const val CANCELLATION_POLICY_BUTTON_CLICKED_EVENT = "Cancellation Policy Button Click"
        const val RESCHEDULE_CONSULTATION_BUTTON_CLICKED_EVENT = "Reschedule Consultation Button Click"
        const val LOGOUT_BUTTON_CLICKED_EVENT = "Logout Button Click"
        const val SAVE_BUTTON_CLICKED_EVENT = "Save Button Click"


        //Consultation List Screen Event
        const val KEY_CONSULTATION_ID = "ConsultationID"
        const val KEY_CONSULTATION_TYPE = "ConsultationType"
        const val KEY_CONSULTATION_STATUS = "ConsultationStatus"
        const val KEY_CONSULTATION_DURATION = "ConsultationDuration"

        //Join Consultation Session Screen Event
        const val KEY_CAMERA_STATUS = "Camera Status"
        const val KEY_VOLUME_STATUS = "Volume Status"
        const val KEY_MIC_STATUS = "Mic Status"
        const val SWITCH_CAMERA_STATUS = "Switch Camera Status"
        const val KEY_ROOM_NAME = "Room Name"
        const val KEY_PARTICIPANT_ID = "Participant ID"
        const val ROOM_CONNECTED = "Room connected successfully"
        const val CHAT_CONNECTED = "Chat connected successfully"
        const val CHAT_CONNECTION_SHUTDOWN = "Chat connection shutdown"
        const val ROOM_CONNECTION_FAILURE = "Room connection failure"
        const val CHAT_CONNECTION_FAILURE = "Chat connection failure"

        const val ACTIVATED = "Activated"
        const val DEACTIVATED = "Deactivated"
        const val SPEAKER = "Speaker"
        const val EAR_PIECE = "Ear piece"
        const val FRONT_CAMERA = "Front Camera"
        const val BACK_CAMERA = "Back Camera"
        const val KEY_EMAIL = "email"
        const val KEY_MOBILE = "phone"
        const val KEY_TIMEZONE = "timezone"
        const val KEY_SCREEN_NAME = "ScreenName"
        const val BUTTON_TITLE = "ButtonTitle"
        const val KEY_USER_NAME = "UserName"

        //Setting Screen Events
        const val EMAIL_NOTIFICATION_SELECTED = "Email notification selected"
        const val SMS_NOTIFICATION_SELECTED = "SMS notification selected"
        const val ALLOW_NOTIFICATION_EVENT = "Allow notification event"
        const val YES = "Yes"
        const val NO = "No"


    }
}