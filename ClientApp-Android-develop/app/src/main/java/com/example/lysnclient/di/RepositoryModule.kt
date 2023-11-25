package com.example.lysnclient.di

import com.example.lysnclient.repository.AppRepository
import org.koin.dsl.module.module

val repositoryModule = module {
    single { AppRepository(get(), get()) }
}