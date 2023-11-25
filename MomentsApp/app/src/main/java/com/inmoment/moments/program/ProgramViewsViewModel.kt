package com.inmoment.moments.program


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inmoment.moments.home.model.AccountProgram
import com.inmoment.moments.program.model.Program
import com.lysn.clinician.utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProgramViewsViewModel @Inject constructor(private val programDataManager: ProgramDataManager) :
    ViewModel() {


    private var _programList = MutableLiveData<List<AccountProgram>>()
    val programList: LiveData<List<AccountProgram>>
        get() = _programList
    val onDatabaseUpdate = SingleLiveEvent<Boolean>()



    init {
        getAccountProgram()
    }

    fun getAccountProgram() {
        viewModelScope.launch {

                _programList.postValue(programDataManager.getAccountProgramData())

        }
    }

    fun updateSelectedProgramData(program: Program) {
        viewModelScope.launch {
            programDataManager.updateSelectedProgramData(program)
            onDatabaseUpdate.value = true
        }
    }


}
