package com.ericmguimaraes.openjavarank.data.repositories

import android.arch.paging.PagedList
import android.util.Log
import com.ericmguimaraes.openjavarank.data.daos.RepoDao
import com.ericmguimaraes.openjavarank.data.model.Repo
import com.ericmguimaraes.openjavarank.data.network.GithubService
import com.ericmguimaraes.openjavarank.data.network.RepoResponse
import com.ericmguimaraes.openjavarank.utilities.AppExecutors
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RepoBoundaryCallback(
        private val appExecutors: AppExecutors,
        private val service: GithubService,
        private val repoDao: RepoDao,
        private val onLoading: (isLoading: Boolean) -> Unit,
        private val onError: () -> Unit
) : PagedList.BoundaryCallback<Repo>() {

    // keep the last requested page.
    // When the request is successful, increment the page number.
    private var lastRequestedPage: Int = 1

    // avoid triggering multiple requests in the same time
    private var isRequestInProgress = false


    override fun onZeroItemsLoaded() {
        requestAndSaveData()
    }

    override fun onItemAtEndLoaded(itemAtEnd: Repo) {
        requestAndSaveData()
    }

    private fun requestAndSaveData() {
        if (isRequestInProgress) return

        isRequestInProgress = true
        onLoading(isRequestInProgress)
        appExecutors.diskIO().execute {
            if (lastRequestedPage == 1) {
                val count = repoDao.getNumberOfRows()
                if (count != 0) {
                    lastRequestedPage = count / NETWORK_PAGE_SIZE
                }
            }
            appExecutors.mainThread().execute {
                makeRequest()
            }
        }
    }

    private fun makeRequest() {
        service.getRepos("language:Java", "stars", lastRequestedPage, NETWORK_PAGE_SIZE).enqueue(
                object : Callback<RepoResponse> {
                    override fun onFailure(call: Call<RepoResponse>?, t: Throwable) {
                        Log.d("REPOBOUND", "fail to get data", t)
                        onError()
                    }

                    override fun onResponse(
                            call: Call<RepoResponse>?,
                            response: Response<RepoResponse>
                    ) {
                        if (response.isSuccessful) {
                            val repos = response.body()?.items ?: emptyList()
                            appExecutors.diskIO().execute {
                                repoDao.insertAll(repos)
                            }
                            lastRequestedPage++
                            isRequestInProgress = false
                            onLoading(isRequestInProgress)
                        } else {
                            onError()
                            isRequestInProgress = false
                            onLoading(isRequestInProgress)
                        }
                    }
                }
        )
    }

    companion object {
        private const val NETWORK_PAGE_SIZE = 50
    }
}
