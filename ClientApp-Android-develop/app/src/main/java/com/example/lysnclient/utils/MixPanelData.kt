package com.example.lysnclient.utils

import android.content.Context
import com.example.lysnclient.BuildConfig
import com.example.lysnclient.utils.Utilities.getDeviceTimeZone
import com.mixpanel.android.mpmetrics.MixpanelAPI
import org.json.JSONObject
import java.util.*

class MixPanelData private constructor(context: Context) {

    private val mixPanelInstance: MixpanelAPI =
        MixpanelAPI.getInstance(context, BuildConfig.MIX_PANEL_TOKEN)

    fun addEvent(jsonData: JSONObject, eventName: String) {
        mixPanelInstance.track(eventName, jsonData)
    }

    fun addEvent(key: String, value: String, eventName: String) {
        val props = JSONObject()
        props.put(key, value)
        mixPanelInstance.track(eventName, props)
    }

    fun alias() {
        mixPanelInstance.reset()
        mixPanelInstance.alias(mixPanelInstance.distinctId, null)
    }

    fun addEvent(eventName: String) {
        mixPanelInstance.track(eventName)
    }

    fun flushMixPanel() {
        mixPanelInstance.flush()
    }

    /// It creates alias to track events with the given email
    fun mapEvents(email: String) {
        mixPanelInstance.alias(email, mixPanelInstance.distinctId)
    }

    fun createProfile(email: String, mobile: String) {
        mixPanelInstance.identify(email)
        val properties = JSONObject()
        properties.put(KEY_EMAIL, email)
        properties.put(KEY_MOBILE, mobile)
        properties.put(KEY_TIMEZONE, getDeviceTimeZone())
        mixPanelInstance.people.set(properties)

        val superProperties = JSONObject()
        superProperties.put(KEY_EMAIL, email)
        superProperties.put(KEY_MOBILE, mobile)
        superProperties.put(KEY_TIMEZONE, getDeviceTimeZone())
        mixPanelInstance.registerSuperProperties(superProperties)
    }

    companion object : SingletonHolder<MixPanelData, Context>(::MixPanelData) {
        const val eventUserAuthorization = "User Authorization"
        const val eventPasswordValidation = "Password Validated"

        const val eventRequestOTP = "OTP Generated"
        const val eventOTPVerified = "OTP Verified"
        const val eventSignUpCompleted = "Sign Up Completed"
        const val eventSignInCompleted = "Sign In Completed"
        const val eventReGenerateOtp = "Sign Up - OTP Regenerated"
        const val eventForgotPasswordVisited = "Visited Forgot-Password Page"
        const val eventStartedAssessment = "Started Assessment"
        const val eventLandedAssessmentReview = "Landed to Assessment Review"
        const val eventCompletedAssessment = "Completed Assessment"

        const val eventLandingWizard = "Landed to Wizard"
        const val eventCompleteWizard = "Visited Wizard Completely"
        const val eventVisitedAssessmentList = "Visited Assessment List"

        const val eventOpenSingleChoiceQue = "Landed to Single Choice Question"
        const val eventCompletedSingleChoiceQue = "Completed Single Choice Question"
        const val eventEditedSingleChoiceQue = "Edited Single Choice Question"
        const val eventStoppedAssessment = "Stopped Assessment"

        const val eventOpenHomeTab = "Landed to Home Tab"
        const val eventOpenSearchTab = "Tapped Search Tab"
        const val eventOpenLearnTab = "Tapped Learn Tab"
        const val eventOpenUserTab = "Landed to User Tab"
        const val eventEditedSingleChoiceQueInReview =
            "Edited Single Choice Question – During Review"
        const val eventLandedToFindPsychologistScreen = "Landed to Find Psychologist Screen"
        const val eventLandedToWellBeingTrackerScreen = "Landed to Wellbeing Tracker"
        const val eventStartedWellBeingTrackerScreen = "Started Wellbeing Tracker"

        const val eventWBTQuestionVisited = "Landed to Question - Wellbeing Tracker"
        const val eventWBTQuestionAnswered = "Answer to Question - Wellbeing Tracker"
        const val eventWBTAnswerEdited = "Edited Answer to Question – Wellbeing Tracker"
        const val eventStopWBTQuestion = "Stopped Wellbeing Tracker"
        const val eventLandedToAboutLysnWellBeingScreen = "Landed to “About Lysn Wellbeing” page"
        const val eventStartedWBTOnAboutLysnWellBeingScreen = "Started Wellbeing Tracker from “About Lysn Wellbeing” page"
        const val eventSignOut = "Sign Out"
        const val eventSessionExpired = "Session Expired"

        const val KEY_EMAIL = "email"
        const val KEY_MOBILE = "phone"
        const val KEY_TIMEZONE = "timezone"
        const val KEY_ASSESSMENT_TITLE = "Title"
        const val KEY_QUESTION = "Question"
        const val KEY_ANSWER = "Answer"
        const val KEY_PREVIOUS_ANSWER = "Previous-Answer"
        const val KEY_New_ANSWER = "New-Answer"
        const val KEY_QUESTION_NUMBER = "Question-Number"
        const val SLIDER_VALUE = "Slider-Value"
        const val KEY_ASSESSMENT_CODE = "Assessment-Code"

    }
}