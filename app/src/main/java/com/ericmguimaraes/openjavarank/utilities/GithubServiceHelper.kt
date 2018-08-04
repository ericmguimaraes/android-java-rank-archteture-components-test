package com.ericmguimaraes.openjavarank.utilities

import com.ericmguimaraes.openjavarank.data.network.GithubService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object GithubServiceHelper {

    private var ourInstance: GithubService? = null

    val instance: GithubService
        get() {
            if (ourInstance == null) {

                val loggin = HttpLoggingInterceptor()
                loggin.level = HttpLoggingInterceptor.Level.BODY
                val client = OkHttpClient.Builder().addInterceptor(loggin).build()

                ourInstance = Retrofit.Builder()
                        .baseUrl(BASE_GITHUB_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .addCallAdapterFactory(LiveDataCallAdapterFactory())
                        .client(client)
                        .build()
                        .create(GithubService::class.java)
            }
            return ourInstance!!
        }

}
