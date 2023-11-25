package com.example.lysnclient.model

import com.google.gson.annotations.SerializedName
import java.util.ArrayList

data class UserProfile(
    val id: Int,
    @SerializedName("should_display_medicare_bulk_billing_popup") val isallowMedicareBillingPopup: Boolean,
    @SerializedName("is_first_login_and_has_signup_coupon") val hasSignUpCoupon: Boolean,
    @SerializedName("approved_booking_terms") val isApprovedBookingTerms: Boolean,
    @SerializedName("matching_sessions_left") val matchingSessionLeft: Int,
    @SerializedName("transactions") val listOfTransaction: ArrayList<UserTransactions>,
    @SerializedName("user_coupons") val listOfCoupons: ArrayList<UserCoupons>,
    @SerializedName("user") val userData: UserData
)

data class UserData(
    val id: Int,
    @SerializedName("user_type") val userType: String,
    @SerializedName("first_name") val firstName: String,
    @SerializedName("last_name") val lastName: String,
    @SerializedName("get_full_name") val fullName: String,
    @SerializedName("email") val email: String,
    @SerializedName("phone") val phone: String,
    @SerializedName("active_consultations") val listOfActiveConsultations: ArrayList<ActiveConsultations>
)

class ActiveConsultations {}
