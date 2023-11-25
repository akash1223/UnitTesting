package com.inmoment.moments.program.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey
import com.inmoment.moments.home.model.Account
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(
    foreignKeys = [ForeignKey(
        entity = Account::class, parentColumns = ["accountId"],
        childColumns = ["accountId"], onDelete = CASCADE
    )]
)
data class Program(
    @PrimaryKey
    val id: String,
    val accountId: String,
    val programName: String,
    val cloudType: String,
    val userProgramId: String,
    val cloudShortText: String,
    val domainColor: Int,
    var isDefaultProgram: Boolean = false
) : Parcelable