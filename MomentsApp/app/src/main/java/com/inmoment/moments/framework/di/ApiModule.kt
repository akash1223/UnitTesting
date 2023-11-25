package com.inmoment.moments.framework.di

import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.inmoment.moments.BuildConfig
import com.inmoment.moments.framework.common.NETWORK_TIMEOUT
import com.inmoment.moments.framework.common.NetworkHelper
import com.inmoment.moments.framework.datamodel.UserProfileRequestData
import com.inmoment.moments.framework.manager.network.*
import com.inmoment.moments.framework.persist.SharedPrefsInf
import com.inmoment.moments.framework.persist.SharedPrefsWrapper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * @author Cybage
 * @version 1.0
 * @since 09/10/20
 */

const val apiHelperFromNetwork = true

@Module
@InstallIn(SingletonComponent::class)
class ApiModule {

    val TAG = "ApiModule"

    @Provides
    fun provideBaseUrl() = BuildConfig.ServiceEndPointBaseURL

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder()
            .setLenient()
            .create()
    }

    @Provides
    @Singleton
    fun getHttpLoggingInterceptor(): HttpLoggingInterceptor {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        return httpLoggingInterceptor
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        httpLoggingInterceptor: HttpLoggingInterceptor,
        context: Context,
        refreshTokenAuthenticator: RefreshTokenAuthenticator,
        authTokenInterceptor: AuthTokenInterceptor,
        networkHelper: NetworkHelper
    ) =
        OkHttpClient.Builder()
            .readTimeout(NETWORK_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(NETWORK_TIMEOUT, TimeUnit.SECONDS)
            .connectTimeout(NETWORK_TIMEOUT, TimeUnit.SECONDS)
            .addInterceptor(NetworkConnectionInterceptor(context, networkHelper))
            .addInterceptor(httpLoggingInterceptor)
            .addInterceptor(authTokenInterceptor)
            .addInterceptor(refreshTokenAuthenticator)
            .build()


    @Provides
    fun provideRetrofit(gson: Gson, okHttpClient: OkHttpClient, baseUrl: String): Retrofit {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create(gson))
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .build()
    }


    @Provides
    @Singleton
    fun provideRestApiService(retrofit: Retrofit): RestApiInterfaceDao =
        retrofit.create(RestApiInterfaceDao::class.java)

    @Provides
    @Singleton
    fun provideApiHelper(
        context: Context,
        restApiInterfaceDao: RestApiInterfaceDao
    ): RestApiHelper {
        return if (apiHelperFromNetwork) {
            RestApiHelperImpl(restApiInterfaceDao)
        } else {
            FakeRestApiHelperImpl(context, restApiInterfaceDao)
        }
    }

    @Provides
    @Singleton
    fun provideSharedPref(sharedPrefsInf: SharedPrefsWrapper): SharedPrefsInf = sharedPrefsInf

    @Provides
    @Singleton
    fun providesUserProfileRequestData(query: String): UserProfileRequestData {
        return UserProfileRequestData(query)
    }
}