/*
 * Copyright 2018 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ericmguimaraes.openjavarank.data.repositories

import android.arch.lifecycle.LiveData
import com.ericmguimaraes.openjavarank.data.NetworkBoundResource
import com.ericmguimaraes.openjavarank.data.Resource
import com.ericmguimaraes.openjavarank.data.daos.PullRequestDao
import com.ericmguimaraes.openjavarank.data.model.PullRequest
import com.ericmguimaraes.openjavarank.data.network.ApiResponse
import com.ericmguimaraes.openjavarank.data.network.GithubService
import com.ericmguimaraes.openjavarank.utilities.AppExecutors
import com.ericmguimaraes.openjavarank.utilities.RateLimiter
import java.util.concurrent.TimeUnit

class PullRequestRepository private constructor(
        private val service: GithubService,
        private val pullRequestDao: PullRequestDao,
        private val appExecutors: AppExecutors
) {

    private val prsListRateLimit = RateLimiter<String>(5, TimeUnit.MINUTES)

    fun getPulls(owner: String, repoName: String): LiveData<Resource<List<PullRequest>>> {
        return object : NetworkBoundResource<List<PullRequest>, List<PullRequest>>(appExecutors) {
            override fun saveCallResult(item: List<PullRequest>) {
                item.forEach {
                    it.repo = repoName
                }
                pullRequestDao.insertAll(item)
                prsListRateLimit.reset(repoName + "prs")
            }

            override fun shouldFetch(data: List<PullRequest>?): Boolean {
                return prsListRateLimit.shouldFetch(repoName + "prs")
            }

            override fun loadFromDb(): LiveData<List<PullRequest>> {
                return pullRequestDao.getPullRequests(repoName)
            }

            override fun createCall(): LiveData<ApiResponse<List<PullRequest>>> {
                return service.getPullRequests(owner, repoName)
            }
        }.asLiveData()
    }

    companion object {

        // For Singleton instantiation
        @Volatile
        private var instance: PullRequestRepository? = null

        fun getInstance(service: GithubService, pullRequestDao: PullRequestDao, appExecutors: AppExecutors) =
                instance ?: synchronized(this) {
                    instance
                            ?: PullRequestRepository(service, pullRequestDao, appExecutors).also { instance = it }
                }
    }
}