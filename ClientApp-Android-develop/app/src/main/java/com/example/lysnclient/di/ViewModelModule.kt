package com.example.lysnclient.di

import com.example.lysnclient.viewmodel.*
import org.koin.android.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

val viewModelModule = module {
    viewModel { UserAuthenticateViewModel(get(), get()) }
    viewModel { TermsConditionViewModel(get()) }
    viewModel { WizardScreenViewModel() }
    viewModel { ListOfAssessmentViewModel(get()) }
    viewModel { AssessmentDetailViewModel(get()) }
    viewModel { AssessmentQuestionViewModel(get()) }
    viewModel { ReviewAssessmentViewModel(get()) }
    viewModel { HomeDashboardViewModel(get()) }
    viewModel { AssessmentSubmittedViewModel() }
    viewModel { WBTIntroViewModel(get()) }
    viewModel { WBTQuestionsViewModel(get()) }
    viewModel { WBTOutputScreenViewModel(get()) }
    viewModel { WBTLearnMoreViewModel(get()) }

}
