package com.lysn.clinician.di

import com.lysn.clinician.repository.ConsultationRepository
import com.lysn.clinician.repository.ProfileRepository
import com.lysn.clinician.repository.SignInRepository
import org.koin.dsl.module

val repositoryModule = module {
    single { SignInRepository(get(),get()) }
    single { ConsultationRepository(get(),get()) }
    single { ProfileRepository(get(),get(),get()) }

}