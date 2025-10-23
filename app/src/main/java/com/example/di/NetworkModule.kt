package com.example.di

import com.example.Data.Local.TokenStore
import com.example.Data.Remote.BranchApi
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val BASE_URL = "https://android-messaging.branch.co/"

    @Provides @Singleton
    fun json(): Json = Json { ignoreUnknownKeys = true }

    @Provides @Singleton
    fun okHttp(tokenStore: TokenStore): OkHttpClient {
        val authInterceptor = Interceptor { chain ->
            val req = chain.request()
            val token = runBlocking { tokenStore.token.first() }
            val newReq = if (token.isNullOrBlank()) req
            else req.newBuilder()
                .addHeader("X-Branch-Auth-Token", token)
                .build()
            chain.proceed(newReq)
        }

        return OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .addInterceptor(authInterceptor)
            .build()
    }

    @Provides @Singleton
    fun retrofit(okHttp: OkHttpClient, json: Json): Retrofit {
        val contentType = "application/json".toMediaType()
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttp)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
    }

    @Provides @Singleton
    fun api(retrofit: Retrofit): BranchApi = retrofit.create(BranchApi::class.java)
}