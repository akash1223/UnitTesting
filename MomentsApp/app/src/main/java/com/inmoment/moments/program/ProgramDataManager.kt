package com.inmoment.moments.program

import android.content.Context
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import com.inmoment.moments.R
import com.inmoment.moments.framework.common.Logger
import com.inmoment.moments.framework.datamodel.UserProgramsResponseData
import com.inmoment.moments.framework.dto.OperationResult
import com.inmoment.moments.framework.manager.TaskManager
import com.inmoment.moments.framework.manager.database.ProgramDao
import com.inmoment.moments.framework.manager.network.RestApiHelper
import com.inmoment.moments.framework.persist.SharedPrefsInf
import com.inmoment.moments.home.model.Account
import com.inmoment.moments.home.model.AccountProgram
import com.inmoment.moments.program.model.Program
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


@Suppress("RedundantSuspendModifier")
class ProgramDataManager @Inject constructor(
    private val context: Context,
    private val apiHelper: RestApiHelper,
    private val sharedPrefsInf: SharedPrefsInf,
    private val programDao: ProgramDao
) : TaskManager() {

    fun getAccountPrograms(coroutineScope: CoroutineScope) =
        execute(::getAccountProgramsInfo, coroutineScope)

    @VisibleForTesting
    suspend fun getAccountProgramsInfo(): OperationResult<UserProgramsResponseData> {
        return apiHelper.getUserPrograms()
        // return setProgramDada(programData)
    }

    /* private fun setProgramDada(programData: UserProgramsResponseData?): OperationResult<List<AccountProgram>> {
         val operationResult = OperationResult<List<AccountProgram>>()
         val mutableList = mutableMapOf<String, AccountProgram>()

         var userProgramId: String? = null

         programData?.programs?.forEach {
             if (programData.defaultAccountId == it.account.id && programData.defaultProgramId == it.id) {
                 userProgramId = it.userProgramId

             }
             var program: Program? = null
             try {
                 program = Program(
                     it.id,
                     it.account.id,
                     it.name,
                     it.cloudType,
                     it.userProgramId,
                     getCloudShortName(it.cloudType),
                     getDomainColor(it.cloudType)
                 )
             } catch (ex: Exception) {
                 Logger.d(TAG, "Error in program object creation>" + ex)

             }

             if (mutableList.containsKey(it.account.id)) {
                 mutableList[it.account.id]?.programList?.add(program!!)
             } else {
                 mutableList[it.account.id] = AccountProgram(
                     it.account.id,
                     it.account.name
                 )
                 mutableList[it.account.id]?.programList?.add(program!!)
             }
         }

         sharedPrefsInf.setDefaultAccountAndProgramId(
             programData!!.defaultAccountId,
             programData.defaultProgramId,
             userProgramId ?: ""
         )
         operationResult.result = mutableList.values.toList()
         return operationResult
     }*/

    private fun getDomainColor(cloudType: String): Int {
        return when (cloudType.toLowerCase()) {
            "employee" -> {
                context.resources.getColor(R.color.program_color_violet)

            }
            "marketing" -> {
                context.resources.getColor(R.color.program_color_green)
            }
            else -> {
                context.resources.getColor(R.color.program_color_blue)
            }
        }
    }

    private fun getCloudShortName(cloudType: String): String {
        return when (cloudType.toLowerCase()) {
            "employee" -> {
                "EX"
            }
            "marketing" -> {
                "MX"
            }
            else -> {
                "CX"
            }
        }
    }

    fun storeProgramAndAccount(
        programData: UserProgramsResponseData,
        coroutineScope: CoroutineScope
    ) {

        val mutableList = mutableMapOf<String, AccountProgram>()

        var userProgramId: String? = null

        programData?.programs?.forEach {
            var isDefaultProgram = false
            if (programData.defaultAccountId == it.account.id && programData.defaultProgramId == it.id) {
                userProgramId = it.userProgramId
                isDefaultProgram = true
            }
            var program: Program? = null
            try {
                program = Program(
                    it.id,
                    it.account.id,
                    it.name.trim(),
                    it.cloudType,
                    it.userProgramId,
                    getCloudShortName(it.cloudType),
                    getDomainColor(it.cloudType),
                    isDefaultProgram
                )
            } catch (ex: Exception) {
                Logger.d(TAG, "Error in program object creation>" + ex)

            }

            if (mutableList.containsKey(it.account.id)) {
                mutableList[it.account.id]?.programList?.add(program!!)
            } else {

                mutableList[it.account.id] = AccountProgram(
                    Account(
                        it.account.id,
                        it.account.name.trim(), (programData.defaultAccountId == it.account.id)
                    )
                )
                mutableList[it.account.id]?.programList?.add(program!!)
            }
        }
        coroutineScope.launch(Dispatchers.IO)
        {
            programDao.insertAccountAndProgram(mutableList.values.toList())
            val getList = programDao.getAccountProgramList()
            val test = ""
        }


        sharedPrefsInf.setDefaultAccountAndProgramId(
            programData!!.defaultAccountId,
            programData.defaultProgramId,
            userProgramId ?: ""
        )
    }

    fun getSelectedProgram(): LiveData<Program> {
        return programDao.getDefaultProgramLiveData()
    }

    suspend fun checkProgramTableExist(): Boolean {
        return programDao.checkProgramTableExist() > 0
    }

    suspend fun getAccountProgramData(): List<AccountProgram> {
        return programDao.getAccountProgramList()
    }

    suspend fun updateSelectedProgramData(program: Program) {
        programDao.updateAccountAndProgram(program)
    }


}