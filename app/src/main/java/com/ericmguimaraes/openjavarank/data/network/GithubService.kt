package com.ericmguimaraes.openjavarank.data.network

import android.arch.lifecycle.LiveData
import retrofit2.http.GET
import retrofit2.http.Query

interface GithubService {

    //q=language:Java&sort=stars&page=1
    @GET("search/repositories")
    fun getRepos(@Query("q") q: String,
                 @Query("sort") sort: String,
                 @Query("page") page: Int): LiveData<ApiResponse<RepoResponse>>

}