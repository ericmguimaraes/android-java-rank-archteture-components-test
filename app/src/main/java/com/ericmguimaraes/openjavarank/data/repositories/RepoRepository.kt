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
import android.arch.lifecycle.MutableLiveData
import android.arch.paging.LivePagedListBuilder
import android.arch.paging.PagedList
import com.ericmguimaraes.openjavarank.data.Resource
import com.ericmguimaraes.openjavarank.data.daos.RepoDao
import com.ericmguimaraes.openjavarank.data.model.Repo
import com.ericmguimaraes.openjavarank.data.network.GithubService
import com.ericmguimaraes.openjavarank.utilities.AppExecutors

class RepoRepository private constructor(
        private val service: GithubService,
        private val repoDao: RepoDao,
        private val appExecutors: AppExecutors
) {

    private val repoResult = MutableLiveData<Resource<LiveData<PagedList<Repo>>>>()
    private var data: LiveData<PagedList<Repo>>? = null

    // implement getRepos repository
    fun getRepos(): LiveData<Resource<LiveData<PagedList<Repo>>>> {

        // Get data source factory from the local cache
        val dataSourceFactory = repoDao.getRepos()

        // Construct the boundary callback
        val boundaryCallback = RepoBoundaryCallback(appExecutors, service, repoDao, {
            if (it) {
                repoResult.value = Resource.loading(data)
            }
        }, {
            repoResult.value = Resource.error("", data)
        })

        // Get the paged list
        data = LivePagedListBuilder(dataSourceFactory, DATABASE_PAGE_SIZE)
                .setBoundaryCallback(boundaryCallback)
                .build()

        repoResult.value = Resource.success(data)

        return repoResult
    }

    companion object {

        // For Singleton instantiation
        @Volatile
        private var instance: RepoRepository? = null

        const val DATABASE_PAGE_SIZE = 50

        fun getInstance(service: GithubService, repoDao: RepoDao, appExecutors: AppExecutors) =
                instance ?: synchronized(this) {
                    instance
                            ?: RepoRepository(service, repoDao, appExecutors).also { instance = it }
                }
    }
}