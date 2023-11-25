package com.example.lysnclient.di

import android.content.Context
import com.example.lysnclient.BuildConfig
import com.example.lysnclient.http.AuthTokenInterceptor
import com.example.lysnclient.http.IHttpService
import com.example.lysnclient.http.NetworkConnectionInterceptor
import com.example.lysnclient.http.RefreshTokenAuthenticator
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber

val httpModule = module {
    val tag = "Retrofit: "
    fun initRetrofit(httpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BuildConfig.BASE_URL)
            .client(httpClient)
            .build()
    }

    fun initHttpClient(
        authTokenInterceptor: AuthTokenInterceptor,
        netConInterceptor: NetworkConnectionInterceptor,
        refreshTokenAuthenticator: RefreshTokenAuthenticator
    ): OkHttpClient {
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

        return okHttpClient.build()
    }

    fun initRetrofitHttpService(retrofit: Retrofit): IHttpService {
        return retrofit.create(IHttpService::class.java)
    }

    fun initAuthTokenInterceptor(context: Context): AuthTokenInterceptor {
        return AuthTokenInterceptor(context)
    }

    fun initTokenAuthenticator(context: Context): RefreshTokenAuthenticator {
        return RefreshTokenAuthenticator(context)
    }

    single { initRetrofit(get()) }
    single { NetworkConnectionInterceptor(get()) }
    single { initAuthTokenInterceptor(get()) }
    single { initHttpClient(get(), get(), get()) }
    single { initRetrofitHttpService(get()) }
    single { initTokenAuthenticator(get()) }
}
