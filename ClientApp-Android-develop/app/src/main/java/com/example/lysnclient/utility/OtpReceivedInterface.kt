package com.example.lysnclient.utility

interface OtpReceivedInterface {
    fun onOtpReceived(otp: String?)
    fun onOtpTimeout()
}