package com.lysn.clinician.model


import android.annotation.SuppressLint
import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable
import com.lysn.clinician.http.HttpConstants
import kotlinx.android.parcel.RawValue

@SuppressLint("ParcelCreator")
@Parcelize
@Keep
data class UserProfileResponse(
    @SerializedName("approved_booking_terms") val approvedBookingTerms: Boolean = false,
    @SerializedName("credit_cards") val creditCards: List<CreditCard> = listOf(),
    @SerializedName("id") val id: Int = 0,
    @SerializedName("invited_by") val invitedBy:  @RawValue Any? = null,
    @SerializedName("is_allowed_for_medicare_bulk_billing") val isAllowedForMedicareBulkBilling: Boolean = false,
    @SerializedName("is_complete") val isComplete: Boolean = false,
    @SerializedName("is_complete_for_booking") val isCompleteForBooking: Boolean = false,
    @SerializedName("is_first_login_and_has_signup_coupon") val isFirstLoginAndHasSignupCoupon: Boolean = false,
    @SerializedName("is_first_login_and_has_wavelength_coupon") val isFirstLoginAndHasWavelengthCoupon: Boolean = false,
    @SerializedName("matching_sessions_left") val matchingSessionsLeft: Int = 0,
    @SerializedName("medipass_member_id") val medipassMemberId: String = HttpConstants.EMPTY_VALUE,
    @SerializedName("medipass_patient_id") val medipassPatientId: String = HttpConstants.EMPTY_VALUE,
    @SerializedName("next_of_kin_name") val nextOfKinName: String = HttpConstants.EMPTY_VALUE,
    @SerializedName("next_of_kin_phone") val nextOfKinPhone: String = HttpConstants.EMPTY_VALUE,
    @SerializedName("next_of_kin_relationship") val nextOfKinRelationship: String = HttpConstants.EMPTY_VALUE,
    @SerializedName("photo") val photo: String = HttpConstants.EMPTY_VALUE,
    @SerializedName("photo_100x100") val photo100x100: String = HttpConstants.EMPTY_VALUE,
    @SerializedName("photo_40x40") val photo40x40: String = HttpConstants.EMPTY_VALUE,
    @SerializedName("should_display_medicare_bulk_billing_popup") val shouldDisplayMedicareBulkBillingPopup: Boolean = false,
    @SerializedName("transactions") val transactions: List<Transaction> = listOf(),
    @SerializedName("user") val user: User = User(),
    @SerializedName("user_coupons") val userCoupons:  @RawValue List<Any> = listOf()
) : Parcelable {
    @SuppressLint("ParcelCreator")
    @Parcelize
    @Keep
    data class CreditCard(
        @SerializedName("card_expiry") val cardExpiry: String = HttpConstants.EMPTY_VALUE,
        @SerializedName("card_expiry_month_year") val cardExpiryMonthYear: String = HttpConstants.EMPTY_VALUE,
        @SerializedName("card_type") val cardType: String = HttpConstants.EMPTY_VALUE,
        @SerializedName("cardholder_name") val cardholderName: String = HttpConstants.EMPTY_VALUE,
        @SerializedName("default") val default: Boolean = false,
        @SerializedName("id") val id: Int = 0,
        @SerializedName("image_url") val imageUrl: String = HttpConstants.EMPTY_VALUE,
        @SerializedName("masked_number") val maskedNumber: String = HttpConstants.EMPTY_VALUE
    ) : Parcelable

    @SuppressLint("ParcelCreator")
    @Parcelize
    @Keep
    data class Transaction(
        @SerializedName("amount") val amount: String = HttpConstants.EMPTY_VALUE,
        @SerializedName("consultation_description") val consultationDescription: String = HttpConstants.EMPTY_VALUE,
        @SerializedName("consultation_therapist_paid_amount") val consultationTherapistPaidAmount:  @RawValue Any? = null,
        @SerializedName("created") val created: String = HttpConstants.EMPTY_VALUE,
        @SerializedName("file_pdf") val filePdf: String = HttpConstants.EMPTY_VALUE,
        @SerializedName("id") val id: Int = 0,
        @SerializedName("successful") val successful: Boolean = false,
        @SerializedName("type") val type: String = HttpConstants.EMPTY_VALUE
    ) : Parcelable

    @SuppressLint("ParcelCreator")
    @Parcelize
    @Keep
    data class User(
        @SerializedName("active_consultations") val activeConsultations:  @RawValue List<Any> = listOf(),
        @SerializedName("address") val address: String = HttpConstants.EMPTY_VALUE,
        @SerializedName("age") val age: Int = 0,
        @SerializedName("city") val city: String = HttpConstants.EMPTY_VALUE,
        @SerializedName("code") val code: String = HttpConstants.EMPTY_VALUE,
        @SerializedName("date_of_birth") val dateOfBirth: String = HttpConstants.EMPTY_VALUE,
        @SerializedName("email") val email: String = HttpConstants.EMPTY_VALUE,
        @SerializedName("first_name") val firstName: String = HttpConstants.EMPTY_VALUE,
        @SerializedName("gender") val gender: String = HttpConstants.EMPTY_VALUE,
        @SerializedName("get_full_name") val getFullName: String = HttpConstants.EMPTY_VALUE,
        @SerializedName("has_wellbeing_tracker_access") val hasWellbeingTrackerAccess: Boolean = false,
        @SerializedName("id") val id: Int = 0,
        @SerializedName("initials") val initials: String = HttpConstants.EMPTY_VALUE,
        @SerializedName("is_latest_technical_check_failed") val isLatestTechnicalCheckFailed: Boolean = false,
        @SerializedName("is_phone_verified") val isPhoneVerified: Boolean = false,
        @SerializedName("languages") val languages: List<Int> = listOf(),
        @SerializedName("last_name") val lastName: String = HttpConstants.EMPTY_VALUE,
        @SerializedName("latest_technical_check") val latestTechnicalCheck:  @RawValue Any? = null,
        @SerializedName("notifications_unread_count") val notificationsUnreadCount: Int = 0,
        @SerializedName("phone") val phone: String = HttpConstants.EMPTY_VALUE,
        @SerializedName("postcode") val postcode: String = HttpConstants.EMPTY_VALUE,
        @SerializedName("relationship_to_northern_star") val relationshipToNorthernStar: String = HttpConstants.EMPTY_VALUE,
        @SerializedName("send_email_reminders") val sendEmailReminders: Boolean = false,
        @SerializedName("send_push_reminders") val sendPushReminders: Boolean = false,
        @SerializedName("send_sms_reminders") val sendSmsReminders: Boolean = false,
        @SerializedName("should_run_technical_check") val shouldRunTechnicalCheck: Boolean = false,
        @SerializedName("state") val state: String = HttpConstants.EMPTY_VALUE,
        @SerializedName("surveys") val surveys: Surveys = Surveys(),
        @SerializedName("timezone") val timezone: String = HttpConstants.EMPTY_VALUE,
        @SerializedName("user_type") val userType: String = HttpConstants.EMPTY_VALUE
    ) : Parcelable {
        @SuppressLint("ParcelCreator")
        @Parcelize
        @Keep
        data class Surveys(
            @SerializedName("client-survey") val clientSurvey: ClientSurvey = ClientSurvey()
        ) : Parcelable {
            @SuppressLint("ParcelCreator")
            @Parcelize
            @Keep
            data class ClientSurvey(
                @SerializedName("form") val form:  @RawValue Any? = null,
                @SerializedName("value") val value:  @RawValue Any? = null
            ) : Parcelable
        }
        
    }
    
}