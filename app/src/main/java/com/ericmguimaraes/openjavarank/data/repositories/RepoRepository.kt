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
import com.ericmguimaraes.openjavarank.data.daos.RepoDao
import com.ericmguimaraes.openjavarank.data.model.Repo
import com.ericmguimaraes.openjavarank.data.network.ApiResponse
import com.ericmguimaraes.openjavarank.data.network.GithubService
import com.ericmguimaraes.openjavarank.data.network.RepoResponse
import com.ericmguimaraes.openjavarank.utilities.AppExecutors

class RepoRepository private constructor(
        private val service: GithubService,
        private val repoDao: RepoDao,
        private val appExecutors: AppExecutors
) {

    // implement getRepos repository
    fun getRepos(): LiveData<Resource<List<Repo>>> {
        return object : NetworkBoundResource<List<Repo>, RepoResponse>(appExecutors) {
            override fun saveCallResult(item: RepoResponse) {
                repoDao.insertAll(item.items)
                //todo save page
            }

            override fun shouldFetch(data: List<Repo>?): Boolean {
                return true //todo melhorar tatica de shouldFetch
            }

            override fun loadFromDb(): LiveData<List<Repo>> {
                return repoDao.getRepos()
            }

            override fun createCall(): LiveData<ApiResponse<RepoResponse>> {
                //q=language:Java&sort=stars&page=1
                return service.getRepos("language:Java", "stars", 1)
            }
        }.asLiveData()
    }

    companion object {

        // For Singleton instantiation
        @Volatile
        private var instance: RepoRepository? = null

        fun getInstance(service: GithubService, repoDao: RepoDao, appExecutors: AppExecutors) =
                instance ?: synchronized(this) {
                    instance
                            ?: RepoRepository(service, repoDao, appExecutors).also { instance = it }
                }
    }
}