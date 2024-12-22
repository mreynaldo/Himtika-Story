package com.capstone.storyappsubmission.data.remote.retrofit

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiConfig {
    private const val BASE_URL = "https://story-api.dicoding.dev/v1/"

    private val loggingInterceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

    private val clientWithoutAuth = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    private val clientWithAuth: (String) -> OkHttpClient = { token ->
        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(Interceptor { chain ->
                val request = chain.request()
                val requestWithAuth = request.newBuilder()
                    .build()
                chain.proceed(requestWithAuth)
            })
            .build()
    }

    private val retrofitWithoutAuth by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(clientWithoutAuth)
            .build()
    }

    private val retrofitWithAuth: (String) -> Retrofit = { token ->
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(clientWithAuth(token))
            .build()
    }

    val apiService: ApiService by lazy {
        retrofitWithoutAuth.create(ApiService::class.java)
    }

    fun getApiServiceWithAuth(token: String): ApiService {
        return retrofitWithAuth(token).create(ApiService::class.java)
    }
}
