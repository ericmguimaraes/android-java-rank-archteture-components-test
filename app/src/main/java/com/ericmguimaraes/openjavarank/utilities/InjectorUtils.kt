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

package com.ericmguimaraes.openjavarank.utilities

import android.arch.lifecycle.ViewModelProvider
import android.content.Context
import com.ericmguimaraes.openjavarank.data.database.AppDatabase
import com.ericmguimaraes.openjavarank.data.network.GithubService
import com.ericmguimaraes.openjavarank.data.repositories.PullRequestRepository
import com.ericmguimaraes.openjavarank.data.repositories.RepoRepository
import com.ericmguimaraes.openjavarank.viewmodels.PullRequestListViewModelFactory
import com.ericmguimaraes.openjavarank.viewmodels.RepoListViewModelFactory

/**
 * Static methods used to inject classes needed for various Activities and Fragments.
 */
object InjectorUtils {

    fun provideRepoListViewModelFactory(context: Context): ViewModelProvider.Factory? {
        val repository = getRepoRepository(context)
        return RepoListViewModelFactory(repository)
    }

    fun providePullRequestListViewModelFactory(context: Context): ViewModelProvider.Factory? {
        val repository = getPullRequestRepository(context)
        return PullRequestListViewModelFactory(repository)
    }

    private fun getRepoRepository(context: Context): RepoRepository {
        return RepoRepository.getInstance(getGithubServicesInstance(),
                AppDatabase.getInstance(context).repoDao(),
                provideAppExecutors()
        )
    }

    private fun getPullRequestRepository(context: Context): PullRequestRepository {
        return PullRequestRepository.getInstance(getGithubServicesInstance(),
                AppDatabase.getInstance(context).pullRequestDao(),
                provideAppExecutors()
        )
    }

    fun getGithubServicesInstance(): GithubService {
        return GithubServiceHelper.instance
    }

    fun provideAppExecutors(): AppExecutors {
        return AppExecutors.instance
    }

}
