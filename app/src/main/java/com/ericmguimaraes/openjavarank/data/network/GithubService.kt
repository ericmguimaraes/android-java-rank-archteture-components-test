package com.ericmguimaraes.openjavarank.data.network

import android.arch.lifecycle.LiveData
import com.ericmguimaraes.openjavarank.data.model.PullRequest
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GithubService {

    //q=language:Java&sort=stars&page=1
    @GET("search/repositories")
    fun getRepos(@Query("q") q: String,
                 @Query("sort") sort: String,
                 @Query("page") page: Int): LiveData<ApiResponse<RepoResponse>>

    @GET("repos/{owner}/{repo}/pulls")
    fun getPullRequests(@Path("owner") owner: String,
                        @Path("repo") repo: String): LiveData<ApiResponse<List<PullRequest>>>

}