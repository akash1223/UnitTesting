package com.inmoment.moments.framework.di

import android.content.Context
import androidx.room.Room
import com.inmoment.moments.framework.common.Logger
import com.inmoment.moments.framework.manager.database.MomentDB
import com.inmoment.moments.framework.manager.database.ProgramDao
import com.inmoment.moments.framework.manager.database.SavedViewsDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module
class DataBaseModel {

    val TAG = "DataBaseModel"

    @Provides
    @Singleton
    fun providesMomentDB(@ApplicationContext context: Context): MomentDB {
        var momentDB: MomentDB? = null
        try {
            momentDB = Room.databaseBuilder(context, MomentDB::class.java, "MomentDB")
                .fallbackToDestructiveMigration().build()
        } catch (ex: Exception) {
            Logger.i(TAG, ex.message.toString())
        }
        return momentDB!!;
    }

    @Provides
    fun providesSavedViewDao(momentDB: MomentDB): SavedViewsDao {
        return momentDB.savedViewDao()
    }

    @Provides
    fun providesProgramDao(momentDB: MomentDB): ProgramDao {
        return momentDB.programDao()
    }

}