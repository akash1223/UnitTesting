package com.inmoment.moments.framework.manager.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.inmoment.moments.framework.datamodel.SavedViewsListResponseData
import com.inmoment.moments.home.model.Account
import com.inmoment.moments.program.model.Program

const val DB_VERSION = 5

@Database(
    entities = [SavedViewsListResponseData::class, Account::class, Program::class],
    version = DB_VERSION,
    exportSchema = false
)
abstract class MomentDB : RoomDatabase() {
    abstract fun savedViewDao(): SavedViewsDao
    abstract fun programDao(): ProgramDao
}