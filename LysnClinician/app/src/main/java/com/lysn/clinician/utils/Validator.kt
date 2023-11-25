package com.lysn.clinician.utils

object Validator {

    // Check validation of email address
    fun validateEmailAddress(emailField:String): Boolean {
        return emailField.matches(AppConstants.EMAIL_ADDRESS_PATTERN.toRegex())
    }
}