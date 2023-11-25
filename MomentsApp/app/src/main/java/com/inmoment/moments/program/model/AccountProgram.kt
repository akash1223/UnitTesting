package com.inmoment.moments.home.model

import androidx.room.Embedded
import androidx.room.Relation
import com.inmoment.moments.program.model.Program

data class AccountProgram(
    @Embedded val account: Account,
    @Relation(parentColumn = "accountId", entityColumn = "accountId")
    val programList: MutableList<Program> = mutableListOf()
)