package com.lysn.clinician.model

import android.annotation.SuppressLint
import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import com.lysn.clinician.utils.AppConstants
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue
import lombok.Generated

@SuppressLint("ParcelCreator")
@Parcelize
@Keep
@lombok.Generated
data class VideoSessionTokenResponse(
    @SerializedName("token") val token: String?,
    @SerializedName("session_id") val sessionId: String?,
    @SerializedName("consultation") val consultation: VideoSessionConsultationDetails?,
    @SerializedName("stream_type") val streamType: String?
) : Parcelable


@SuppressLint("ParcelCreator")
@Parcelize
@Keep
@lombok.Generated
data class VideoSessionConsultationDetails(
    @SerializedName("admin_url") val adminUrl: String? = AppConstants.EMPTY_VALUE,
    @SerializedName("assessmentrequest") val assessmentRequest: @RawValue Any? = null,
    @SerializedName("calculate_client_payment_amount") val calculateClientPaymentAmount: Double = 0.0,
    @SerializedName("can_cancel") val canCancel: Boolean = false,
    @SerializedName("can_join") val canJoin: Boolean = false,
    @SerializedName("can_reschedule") val canReschedule: Boolean = false,
    @SerializedName("case_notes") val caseNotes: List<CaseNote>? = listOf(),
    @SerializedName("client") val client: Client? = null,
    @SerializedName("client_rating") val clientRating: @RawValue Any? = null,
    @SerializedName("coupon_used_name") val couponUsedName: @RawValue Any? = null,
    @SerializedName("date_time") val dateTime: String? = AppConstants.EMPTY_VALUE,
    @SerializedName("display_consultation") val displayConsultation: String? = AppConstants.EMPTY_VALUE,
    @SerializedName("duration") val duration: String? = AppConstants.EMPTY_VALUE,
    @SerializedName("duration_minutes") val durationMinutes: Int = 0,
    @SerializedName("end_date_time") val endDateTime: String? = AppConstants.EMPTY_VALUE,
    @SerializedName("google_calendar_url") val googleCalendarUrl: String? = AppConstants.EMPTY_VALUE,
    @SerializedName("has_archives") val hasArchives: Boolean = false,
    @SerializedName("id") val id: Int = 0,
    @SerializedName("is_client_first_session") val isClientFirstSession: Boolean = false,
    @SerializedName("is_matching") val isMatching: Boolean = false,
    @SerializedName("is_risk_reported") val isRiskReported: @RawValue Any? = null,
    @SerializedName("is_upcoming") val isUpcoming: Boolean = false,
    @SerializedName("outlook_calendar_url") val outlookCalendarUrl: String? = AppConstants.EMPTY_VALUE,
    @SerializedName("payment_type") val paymentType: String? = AppConstants.EMPTY_VALUE,
    @SerializedName("pending_reschedule_request") val pendingRescheduleRequest: @RawValue Any? = null,
    @SerializedName("pending_schedule_request") val pendingScheduleRequest: @RawValue Any? = null,
    @SerializedName("price") val price: Double = 0.0,
    @SerializedName("price_with_coupon") val priceWithCoupon: @RawValue Any? = null,
    @SerializedName("provider") val provider: Int = 0,
    @SerializedName("real_duration_minutes") val realDurationMinutes: Int = 0,
    @SerializedName("referral") val referral: @RawValue Any? = null,
    @SerializedName("session_ended_time") val sessionEndedTime: @RawValue Any? = null,
    @SerializedName("session_started_time") val sessionStartedTime: @RawValue Any? = null,
    @SerializedName("session_url") val sessionUrl: String? = AppConstants.EMPTY_VALUE,
    @SerializedName("should_therapist_process_k10_assessment_during_consultation") val shouldTherapistProcessK10AssessmentDuringConsultation: Boolean = false,
    @SerializedName("should_update_referral") val shouldUpdateReferral: Boolean = false,
    @SerializedName("status") val status: String? = AppConstants.EMPTY_VALUE,
    @SerializedName("status_for_client_display") val statusForClientDisplay: String? = AppConstants.EMPTY_VALUE,
    @SerializedName("therapist") val therapist: Therapist? = null,
    @SerializedName("therapist_rating") val therapistRating: @RawValue Any? = null,
    @SerializedName("type") val type: String? = AppConstants.EMPTY_VALUE
) : Parcelable {

    @SuppressLint("ParcelCreator")
    @Parcelize
    @Keep
    data class CaseNote(
        @SerializedName("client") val client: Int = 0,
        @SerializedName("consultation_date") val consultationDate: String? = null,
        @SerializedName("consultation_id") val consultationId: Int = 0,
        @SerializedName("created") val created: String? = null,
        @SerializedName("data") val data: Data? = null,
        @SerializedName("id") val id: Int = 0,
        @SerializedName("is_editable") val isEditable: Boolean = false,
        @SerializedName("notes") val notes: List<Notes>? = null,
        @SerializedName("status") val status: String? = null,
        @SerializedName("title") val title: String? = null,
        @SerializedName("type") val type: String? = null

    ) : Parcelable {
        @SuppressLint("ParcelCreator")
        @Parcelize
        @Keep
        data class Notes(
            @SerializedName("author") val author: Author? = null,
            @SerializedName("consultationarchive_notes") val consultationArchiveNotes: List<String>? = null,
            @SerializedName("created") val created: String? = null,
            @SerializedName("description") val description: String? = null,
            @SerializedName("id") val id: Int = 0,
            @SerializedName("note_files") val noteFiles: List<String>? = null
        ) : Parcelable {
            @SuppressLint("ParcelCreator")
            @Parcelize
            @Keep
            data class Author(
                @SerializedName("first_name") val firstName: String? = null,
                @SerializedName("get_full_name") val getFullName: String? = null,
                @SerializedName("id") val id: Int = 0,
                @SerializedName("last_name") val lastName: String? = null,
                @SerializedName("slug") val slug: String? = null
            ) : Parcelable
        }

        @SuppressLint("ParcelCreator")
        @Parcelize
        @Keep
        data class Data(
            @SerializedName("consultation_number") val consultationNumber: Int = 0
        ) : Parcelable
    }

    @SuppressLint("ParcelCreator")
    @Parcelize
    @Keep
    data class Client(
        @SerializedName("first_name") val firstName: String? = null,
        @SerializedName("get_full_name") val getFullName: String? = null,
        @SerializedName("id") val id: Int = 0 ,
        @SerializedName("is_complete") val isComplete: Boolean = false,
        @SerializedName("last_name") val lastName: String? = null,
        @SerializedName("medicare_number") val medicareNumber: String? = null,
        @SerializedName("next_of_kin_name") val nextOfKinName: String? = null,
        @SerializedName("next_of_kin_phone") val nextOfKinPhone: String? = null,
        @SerializedName("next_of_kin_relationship") val nextOfKinRelationship: String? = null,
        @SerializedName("payment_token") val paymentToken: String? = null,
        @SerializedName("photo") val photo: String? = null,
        @SerializedName("photo_100x100") val photo100x100: String? = null,
        @SerializedName("photo_40x40") val photo40x40: String? = null,
        @SerializedName("relationship_to_northern_star") val relationshipToNorthernStar: String? = null,
        @SerializedName("user") val user: User? = null
    ) : Parcelable {
        @SuppressLint("ParcelCreator")
        @Parcelize
        @Keep
        data class User(
            @SerializedName("address") val address: String? = null,
            @SerializedName("city") val city: String? = null,
            @SerializedName("date_of_birth") val dateOfBirth: String? = null,
            @SerializedName("email") val email: String? = null,
            @SerializedName("first_name") val firstName: String? = null,
            @SerializedName("get_full_name") val getFullName: String? = null,
            @SerializedName("id") val id: Int = 0,
            @SerializedName("last_name") val lastName: String? = null,
            @SerializedName("phone") val phone: String? = null,
            @SerializedName("postcode") val postcode: String? = null,
            @SerializedName("state") val state: String? = null,
            @SerializedName("user_type") val userType: String? = null
        ) : Parcelable
    }

    @SuppressLint("ParcelCreator")
    @Parcelize
    @Keep
    data class Therapist(
        @SerializedName("accepts_f2f_pricing") val acceptsF2fPricing: Boolean = false,
        @SerializedName("accepts_phone_pricing") val acceptsPhonePricing: Boolean = false,
        @SerializedName("accepts_video_pricing") val acceptsVideoPricing: Boolean = false,
        @SerializedName("consultation_book_ahead_time") val consultationBookAheadTime: Int = 0,
        @SerializedName("description") val description: String? = null,
        @SerializedName("first_name") val firstName: String? = null,
        @SerializedName("get_full_name") val getFullName: String? = null,
        @SerializedName("id") val id: Int = 0,
        @SerializedName("is_f2f_configured") val isF2fConfigured: Boolean = false,
        @SerializedName("is_matching_package_already_used") val isMatchingPackageAlreadyUsed: Boolean = false,
        @SerializedName("is_phone_configured") val isPhoneConfigured: Boolean = false,
        @SerializedName("is_providing_f2f") val isProvidingF2f: Boolean = false,
        @SerializedName("is_providing_phone") val isProvidingPhone: Boolean = false,
        @SerializedName("is_providing_phone_matching") val isProvidingPhoneMatching: Boolean = false,
        @SerializedName("is_providing_phone_quick") val isProvidingPhoneQuick: Boolean = false,
        @SerializedName("is_providing_phone_standard") val isProvidingPhoneStandard: Boolean = false,
        @SerializedName("is_providing_video") val isProvidingVideo: Boolean = false,
        @SerializedName("is_providing_video_matching") val isProvidingVideoMatching: Boolean = false,
        @SerializedName("is_providing_video_quick") val isProvidingVideoQuick: Boolean = false,
        @SerializedName("is_providing_video_standard") val isProvidingVideoStandard: Boolean = false,
        @SerializedName("is_video_configured") val isVideoConfigured: Boolean = false,
        @SerializedName("is_ym_certified") val isYmCertified: Boolean = false,
        @SerializedName("languages") val languages: List<String>? = null,
        @SerializedName("last_name") val lastName: String? = null,
        @SerializedName("next_available_at") val nextAvailableAt: String? = null,
        @SerializedName("phone") val phone: String? = null,
        @SerializedName("phone_quick_pricing") val phoneQuickPricing: Double = 0.0,
        @SerializedName("phone_quick_pricing_medicare_bulk_billing") val phoneQuickPricingMedicareBulkBilling: @RawValue Any? = null,
        @SerializedName("phone_standard_pricing") val phoneStandardPricing: Double = 0.0,
        @SerializedName("phone_standard_pricing_medicare_bulk_billing") val phoneStandardPricingMedicareBulkBilling: @RawValue Any? = null,
        @SerializedName("photo") val photo: String? = null,
        @SerializedName("photo_100x100") val photo100x100: String? = null,
        @SerializedName("photo_40x40") val photo40x40: String? = null,
        @SerializedName("psychologist_type") val psychologistType: String? = null,
        @SerializedName("psychologist_type_display") val psychologistTypeDisplay: String? = null,
        @SerializedName("slug") val slug: String? = null,
        @SerializedName("specialties") val specialties: String? = null,
        @SerializedName("supports_medicare_bulk_billing") val supportsMedicareBulkBilling: Boolean = false,
        @SerializedName("supports_medicare_non_bulk_billing") val supportsMedicareNonBulkBilling: Boolean = false,
        @SerializedName("top_specialties") val topSpecialties: String? = null,
        @SerializedName("video_quick_pricing") val videoQuickPricing: Double = 0.0,
        @SerializedName("video_quick_pricing_medicare_bulk_billing") val videoQuickPricingMedicareBulkBilling: @RawValue Any? = null,
        @SerializedName("video_standard_pricing") val videoStandardPricing: Double = 0.0,
        @SerializedName("video_standard_pricing_medicare_bulk_billing") val videoStandardPricingMedicareBulkBilling: @RawValue Any? = null
    ) : Parcelable {
        @SuppressLint("ParcelCreator")
        @Parcelize
        @Keep
        data class Provider(
            @SerializedName("abn_number") val abnNumber: String? = null,
            @SerializedName("f2f_extended_pricing") val f2fExtendedPricing:  @RawValue Any? = null,
            @SerializedName("f2f_quick_pricing") val f2fQuickPricing: String? = null,
            @SerializedName("f2f_standard_pricing") val f2fStandardPricing: String? = null,
            @SerializedName("is_external") val isExternal: Boolean = false,
            @SerializedName("is_f2f_configured") val isF2fConfigured: Boolean = false,
            @SerializedName("is_providing_f2f_extended") val isProvidingF2fExtended: Boolean = false,
            @SerializedName("is_providing_f2f_matching") val isProvidingF2fMatching: Boolean = false,
            @SerializedName("is_providing_f2f_quick") val isProvidingF2fQuick: Boolean = false,
            @SerializedName("is_providing_f2f_standard") val isProvidingF2fStandard: Boolean = false,
            @SerializedName("medicare_provider_number") val medicareProviderNumber: String? = null,
            @SerializedName("pk") val pk: Int = 0,
            @SerializedName("provider_address") val providerAddress: String? = null,
            @SerializedName("provider_city") val providerCity: String? = null,
            @SerializedName("provider_location") val providerLocation: String? = null,
            @SerializedName("provider_postcode") val providerPostcode: String? = null,
            @SerializedName("provider_practice_name") val providerPracticeName: String? = null,
            @SerializedName("provider_state") val providerState: String? = null
        ) : Parcelable
    }
}