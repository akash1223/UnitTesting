package com.lysn.clinician.model

 class AllowNotificationRequestData {
     var user = User()
 }

 class User{
    var send_email_reminders =false
    var send_sms_reminders =  false
}