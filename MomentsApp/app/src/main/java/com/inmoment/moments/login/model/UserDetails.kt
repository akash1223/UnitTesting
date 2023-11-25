package com.inmoment.moments.login.model

import com.inmoment.moments.framework.datamodel.RequestParam

data class UserDetails(val firstParam: String, val secondParam: String) : RequestParam()
data class AccessToken(val token: String, val accessTokenExpirationTime: Long) : RequestParam()
