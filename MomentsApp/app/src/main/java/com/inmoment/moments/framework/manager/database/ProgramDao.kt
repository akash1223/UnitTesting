package com.inmoment.moments.framework.manager.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.inmoment.moments.framework.common.Logger
import com.inmoment.moments.home.model.Account
import com.inmoment.moments.home.model.AccountProgram
import com.inmoment.moments.program.model.Program

@Dao
interface ProgramDao {


    suspend fun insertAccountAndProgram(accountProgram: List<AccountProgram>) {

        try {
            accountProgram.sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER, { it.account.name }))
            accountProgram.forEach { it1 ->

                //remove space
                it1.account.name.trim()
                it1.programList.forEach { it.programName.trim() }

                // sort program
                val programList = it1.programList.sortedWith(
                    compareBy(String.CASE_INSENSITIVE_ORDER,
                        { it.programName })
                )
                // insert account and program to database
                insertAccount(it1.account)
                insertProgramList(programList)
            }
        } catch (ex: Exception) {
            Logger.i("ProgramDao", ex.stackTraceToString())
        }

    }

    suspend fun updateAccountAndProgram(program: Program) {
        updateProgram(program.id)
        updateAccount(program.accountId)
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccount(account: Account)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProgramList(program: List<Program>)

    @Query("SELECT * FROM account order by name collate nocase asc")
    suspend fun getAccountProgramList(): List<AccountProgram>

    @Query("SELECT * FROM program WHERE isDefaultProgram = 1")
    fun getDefaultProgramLiveData(): LiveData<Program>

    @Query("SELECT COUNT(*) FROM program")
    suspend fun checkProgramTableExist(): Int

    @Query("UPDATE program set isDefaultProgram = case when id=:programId then 1 else 0 end")
    suspend fun updateProgram(programId: String)

    @Query("UPDATE account set isDefaultAccount = case when accountId=:accountId then 1 else 0 end")
    suspend fun updateAccount(accountId: String)

}