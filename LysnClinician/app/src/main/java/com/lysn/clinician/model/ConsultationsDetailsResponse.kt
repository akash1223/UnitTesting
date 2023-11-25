package com.lysn.clinician.model


import android.annotation.SuppressLint
import android.graphics.Color
import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable
import com.lysn.clinician.http.HttpConstants
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.RawValue
import lombok.Generated


@SuppressLint("ParcelCreator")
@Parcelize
@Keep
@lombok.Generated
data class ConsultationsDetailsResponse(
    @SerializedName("by_id") val byId: Map<Int, ConsultationDetails> = mapOf(),
    @SerializedName("can_join_ids") val canJoinIds: List<Int> = listOf(),
    @SerializedName("past_ids") val pastIds: List<Int> = listOf(),
    @SerializedName("upcoming_ids") val upcomingIds: List<Int> = listOf()
) : Parcelable

@SuppressLint("ParcelCreator")
@Parcelize
@Keep
@lombok.Generated
data class ConsultationDetails(
    @SerializedName("calculate_client_payment_amount") val calculateClientPaymentAmount: Double = 0.0,
    @SerializedName("can_cancel") val canCancel: Boolean = false,
    @SerializedName("can_join") val canJoin: Boolean = false,
    @SerializedName("can_reschedule") val canReschedule: Boolean = false,
    @SerializedName("case_notes") val caseNotes: List<CaseNotes>? = listOf(),
    @SerializedName("client") val client: Client? = Client(),
    @SerializedName("client_rating") val clientRating: @RawValue Any? = null,
    @SerializedName("coupon_used_name") val couponUsedName: @RawValue Any? = null,
    @SerializedName("date_time") var dateTime: String? = HttpConstants.EMPTY_VALUE,
    @SerializedName("display_consultation") val displayConsultation: String? = HttpConstants.EMPTY_VALUE,
    @SerializedName("duration") val duration: String? = HttpConstants.EMPTY_VALUE,
    @SerializedName("duration_minutes") val durationMinutes: Int = 0,
    @SerializedName("end_date_time") val endDateTime: String? = HttpConstants.EMPTY_VALUE,
    @SerializedName("google_calendar_url") val googleCalendarUrl: String? = HttpConstants.EMPTY_VALUE,
    @SerializedName("has_archives") val hasArchives: Boolean = false,
    @SerializedName("id") val id: Int = 0,
    @SerializedName("is_claimable") val isClaimable: Boolean = false,
    @SerializedName("is_matching") val isMatching: Boolean = false,
    @SerializedName("is_upcoming") val isUpcoming: Boolean = false,
    @SerializedName("outlook_calendar_url") val outlookCalendarUrl: String? = HttpConstants.EMPTY_VALUE,
    @SerializedName("pending_reschedule_request") val pendingRescheduleRequest: @RawValue Any? = null,
    @SerializedName("pending_schedule_request") val pendingScheduleRequest: @RawValue Any? = null,
    @SerializedName("price") val price: Double = 0.0,
    @SerializedName("price_with_coupon") val priceWithCoupon: @RawValue Any? = null,
    @SerializedName("provider") val provider: Int = 0,
    @SerializedName("real_duration_minutes") val realDurationMinutes: Int = 0,
    @SerializedName("referral") val referral: @RawValue Any? = null,
    @SerializedName("session_url") val sessionUrl: String? = HttpConstants.EMPTY_VALUE,
    @SerializedName("should_update_referral") val shouldUpdateReferral: Boolean = false,
    @SerializedName("status") var status: String? = HttpConstants.EMPTY_VALUE,
    @SerializedName("status_for_client_display") var statusForClientDisplay: String? = HttpConstants.EMPTY_VALUE,
    @SerializedName("therapist") val therapist: Therapist? = null,
    @SerializedName("therapist_rating") val therapistRating: @RawValue Any? = null,
    @SerializedName("type") val type: String? = HttpConstants.EMPTY_VALUE,
    @SerializedName("use_referral") val useReferral: Boolean = false,
    var timerDisplayName:String? = statusForClientDisplay,
    var timerStatus:String? = status

) : Parcelable {

    @SuppressLint("ParcelCreator")
    @Parcelize
    @Keep
    data class Client(
        @SerializedName("first_name") val firstName: String? = HttpConstants.EMPTY_VALUE,
        @SerializedName("get_full_name") val getFullName: String? = HttpConstants.EMPTY_VALUE,
        @SerializedName("id") val id: Int = 0,
        @SerializedName("is_complete") val isComplete: Boolean = false,
        @SerializedName("is_medipass_registered") val isMedipassRegistered: Boolean = false,
        @SerializedName("last_name") val lastName: String? = HttpConstants.EMPTY_VALUE,
        @SerializedName("payment_token") val paymentToken: String? = HttpConstants.EMPTY_VALUE,
        @SerializedName("photo") val photo: String? = HttpConstants.EMPTY_VALUE,
        @SerializedName("photo_100x100") val photo100x100: String? = HttpConstants.EMPTY_VALUE,
        @SerializedName("photo_40x40") val photo40x40: String? = HttpConstants.EMPTY_VALUE
    ) : Parcelable

    @SuppressLint("ParcelCreator")
    @Parcelize
    @Keep
    data class CaseNotes(
        @SerializedName("client") val client: Int = 0,
        @SerializedName("consultation_date") val consultationDate: String? = HttpConstants.EMPTY_VALUE,
        @SerializedName("consultation_id") val consultationId: Int = 0,
        @SerializedName("created") val created: String? = HttpConstants.EMPTY_VALUE,
        @SerializedName("data") val data: Data? = Data(),
        @SerializedName("id") val id: Int = 0,
        @SerializedName("is_editable") val isEditable: Boolean = false,
        @SerializedName("notes") val notes: List<Notes>? = listOf(),
        @SerializedName("status") val status: String? = HttpConstants.EMPTY_VALUE,
        @SerializedName("title") val title: String? = HttpConstants.EMPTY_VALUE,
        @SerializedName("type") val type: String? = HttpConstants.EMPTY_VALUE
    ) : Parcelable {
        @SuppressLint("ParcelCreator")
        @Parcelize
        @Keep
        data class Notes(
            @SerializedName("author") val author: Author? = Author(),
            @SerializedName("consultationarchive_notes") val consultationarchiveNotes: List<String>? = listOf(),
            @SerializedName("created") val created: String? = HttpConstants.EMPTY_VALUE,
            @SerializedName("description") val description: String? = HttpConstants.EMPTY_VALUE,
            @SerializedName("id") val id: Int = 0,
            @SerializedName("note_files") val noteFiles: @RawValue  Any
        ) : Parcelable {
            @SuppressLint("ParcelCreator")
            @Parcelize
            @Keep
            data class Author(
                @SerializedName("first_name") val firstName: String? = HttpConstants.EMPTY_VALUE,
                @SerializedName("get_full_name") val getFullName: String? = HttpConstants.EMPTY_VALUE,
                @SerializedName("id") val id: Int = 0,
                @SerializedName("last_name") val lastName: String? = HttpConstants.EMPTY_VALUE,
                @SerializedName("slug") val slug: String? = HttpConstants.EMPTY_VALUE
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
    data class Therapist(
        @SerializedName("accepts_f2f_pricing") val acceptsF2fPricing: Boolean = false,
        @SerializedName("accepts_phone_pricing") val acceptsPhonePricing: Boolean = false,
        @SerializedName("accepts_video_pricing") val acceptsVideoPricing: Boolean = false,
        @SerializedName("consultation_book_ahead_time") val consultationBookAheadTime: Int = 0,
        @SerializedName("description") val description: String? = HttpConstants.EMPTY_VALUE,
        @SerializedName("first_name") val firstName: String? = HttpConstants.EMPTY_VALUE,
        @SerializedName("get_full_name") val getFullName: String? = HttpConstants.EMPTY_VALUE,
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
        @SerializedName("languages") val languages: List<String>? = listOf(),
        @SerializedName("last_name") val lastName: String? = HttpConstants.EMPTY_VALUE,
        @SerializedName("next_available_at") val nextAvailableAt: String? = HttpConstants.EMPTY_VALUE,
        @SerializedName("phone_quick_pricing") val phoneQuickPricing: Double? = 0.0,
        @SerializedName("phone_standard_pricing") val phoneStandardPricing: Double? = 0.0,
        @SerializedName("photo") val photo: String? = HttpConstants.EMPTY_VALUE,
        @SerializedName("photo_100x100") val photo100x100: String? = HttpConstants.EMPTY_VALUE,
        @SerializedName("photo_40x40") val photo40x40: String? = HttpConstants.EMPTY_VALUE,
        @SerializedName("providers") val providers: List<Provider>? = listOf(),
        @SerializedName("psychologist_type") val psychologistType: String? = HttpConstants.EMPTY_VALUE,
        @SerializedName("psychologist_type_display") val psychologistTypeDisplay: String? = HttpConstants.EMPTY_VALUE,
        @SerializedName("slug") val slug: String? = HttpConstants.EMPTY_VALUE,
        @SerializedName("specialties") val specialties: String? = HttpConstants.EMPTY_VALUE,
        @SerializedName("supports_medicare_bulk_billing") val supportsMedicareBulkBilling: Boolean = false,
        @SerializedName("supports_online_claims") val supportsOnlineClaims: Boolean = false,
        @SerializedName("video_quick_pricing") val videoQuickPricing: Double? = 0.0,
        @SerializedName("video_standard_pricing") val videoStandardPricing: Double? = 0.0
    ) : Parcelable {
        @SuppressLint("ParcelCreator")
        @Parcelize
        @Keep
        data class Provider(
            @SerializedName("abn_number") val abnNumber: String? = HttpConstants.EMPTY_VALUE,
            @SerializedName("f2f_extended_pricing") val f2fExtendedPricing: String? = HttpConstants.EMPTY_VALUE,
            @SerializedName("f2f_quick_pricing") val f2fQuickPricing: String? = HttpConstants.EMPTY_VALUE,
            @SerializedName("f2f_standard_pricing") val f2fStandardPricing: String? = HttpConstants.EMPTY_VALUE,
            @SerializedName("is_external") val isExternal: Boolean = false,
            @SerializedName("is_f2f_configured") val isF2fConfigured: Boolean = false,
            @SerializedName("is_providing_f2f_extended") val isProvidingF2fExtended: Boolean = false,
            @SerializedName("is_providing_f2f_matching") val isProvidingF2fMatching: Boolean = false,
            @SerializedName("is_providing_f2f_quick") val isProvidingF2fQuick: Boolean = false,
            @SerializedName("is_providing_f2f_standard") val isProvidingF2fStandard: Boolean = false,
            @SerializedName("medicare_provider_number") val medicareProviderNumber: String? = HttpConstants.EMPTY_VALUE,
            @SerializedName("pk") val pk: Int = 0,
            @SerializedName("provider_address") val providerAddress: String? = HttpConstants.EMPTY_VALUE,
            @SerializedName("provider_city") val providerCity: String? = HttpConstants.EMPTY_VALUE,
            @SerializedName("provider_location") val providerLocation: String ?= HttpConstants.EMPTY_VALUE,
            @SerializedName("provider_postcode") val providerPostcode: String? = HttpConstants.EMPTY_VALUE,
            @SerializedName("provider_practice_name") val providerPracticeName: String? = HttpConstants.EMPTY_VALUE,
            @SerializedName("provider_state") val providerState: String? = HttpConstants.EMPTY_VALUE
        ) : Parcelable
    }
}

