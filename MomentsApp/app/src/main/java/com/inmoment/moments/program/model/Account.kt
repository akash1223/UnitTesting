package com.inmoment.moments.home.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "account", indices = [Index(value = ["accountId"], unique = true)])
data class Account(
    @PrimaryKey val accountId: String,
    val name: String,
    var isDefaultAccount: Boolean = false
)