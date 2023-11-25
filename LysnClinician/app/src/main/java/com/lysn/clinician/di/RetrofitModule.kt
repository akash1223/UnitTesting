package com.lysn.clinician.di

import android.content.Context
import com.lysn.clinician.BuildConfig
import com.lysn.clinician.http.AuthTokenInterceptor
import com.lysn.clinician.http.IHTTPService
import com.lysn.clinician.http.NetworkConnectionInterceptor
import com.lysn.clinician.http.RefreshTokenAuthenticator
import com.lysn.clinician.utils.PreferenceUtil
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import java.util.concurrent.TimeUnit

val httpModule = module {
    val tag = "Retrofit: "

    fun initRetrofit(httpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BuildConfig.BASE_URL)
            .client(httpClient)
            .build()
    }

    fun initHttpClient(authTokenInterceptor: AuthTokenInterceptor,
                       netConInterceptor: NetworkConnectionInterceptor,
                       refreshTokenAuthenticator: RefreshTokenAuthenticator): OkHttpClient {
        val okHttpClient = OkHttpClient.Builder()
        if (BuildConfig.DEBUG) {
            val logging = HttpLoggingInterceptor {
                Timber.i("$tag  $it")
            }
            logging.level = HttpLoggingInterceptor.Level.BODY
            okHttpClient.addInterceptor(logging)
        }
        okHttpClient.addInterceptor(netConInterceptor)
        okHttpClient.addInterceptor(authTokenInterceptor)
        okHttpClient.authenticator(refreshTokenAuthenticator)
        okHttpClient.connectTimeout(50, TimeUnit.SECONDS)
        okHttpClient.readTimeout(50, TimeUnit.SECONDS)
        return okHttpClient.build()
    }

    fun initRetrofitHttpService(retrofit: Retrofit): IHTTPService {
        return retrofit.create(IHTTPService::class.java)
    }

    fun initAuthTokenInterceptor(preferenceUtil: PreferenceUtil): AuthTokenInterceptor {
        return AuthTokenInterceptor(preferenceUtil)
    }

    fun initTokenAuthenticator(preferenceUtil: PreferenceUtil,context: Context): RefreshTokenAuthenticator {
        return RefreshTokenAuthenticator(preferenceUtil,context)
    }

    single { initAuthTokenInterceptor(get()) }
    single { initTokenAuthenticator(get(),get()) }
    single { NetworkConnectionInterceptor(get()) }
    single { initHttpClient(get(),get(),get()) }
    single { initRetrofit(get()) }
    single { initRetrofitHttpService(get()) }
}
