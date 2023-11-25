package com.lysn.clinician.di

import com.lysn.clinician.ui.consultation_list.ConsultationListViewModel
import com.lysn.clinician.ui.consultation_details.ConsultationDetailsViewModel
import com.lysn.clinician.ui.join_consultation.JoinConsultationViewModel
import com.lysn.clinician.ui.profile.ProfileViewModel
import com.lysn.clinician.ui.profile.SettingsViewModel
import com.lysn.clinician.ui.signin.SignInViewModel
import com.lysn.clinician.ui.terms_condition.TermsAndConditionViewModel
import com.lysn.clinician.ui.video_session.VideoSessionViewModel
import com.lysn.clinician.ui.video_session.chat.ChatViewModel
import org.koin.dsl.module
import org.koin.androidx.viewmodel.dsl.viewModel

val viewModelModule = module {
    viewModel { SignInViewModel(get(),get(),get()) }
    viewModel { ConsultationListViewModel(get(),get()) }
    viewModel { TermsAndConditionViewModel(get(),get()) }
    viewModel { ConsultationDetailsViewModel(get(),get(),get()) }
    viewModel { JoinConsultationViewModel(get(),get(),get()) }
    viewModel { VideoSessionViewModel(get()) }
    viewModel { ChatViewModel() }
    viewModel { ProfileViewModel(get(),get(),get()) }
    viewModel { SettingsViewModel(get()) }


}