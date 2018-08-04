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
import com.ericmguimaraes.openjavarank.data.repositories.RepoRepository
import com.ericmguimaraes.openjavarank.viewmodels.RepoListViewModelFactory

/**
 * Static methods used to inject classes needed for various Activities and Fragments.
 */
object InjectorUtils {

    fun provideRepoListViewModelFactory(context: Context): ViewModelProvider.Factory? {
        val repository = getRepoRepository(context)
        return RepoListViewModelFactory(repository)
    }

    private fun getRepoRepository(context: Context): RepoRepository {
        return RepoRepository.getInstance(getGithubServicesInstance(),
                AppDatabase.getInstance(context).repoDao(),
                provideAppExecutors()
        )
    }

    fun getGithubServicesInstance(): GithubService {
        return GithubServiceHelper.instance
    }

    fun provideAppExecutors(): AppExecutors {
        return AppExecutors.instance
    }

    /*
    private fun getGardenPlantingRepository(context: Context): GardenPlantingRepository {
        return GardenPlantingRepository.getInstance(
                AppDatabase.getInstance(context).gardenPlantingDao())
    }

    fun provideGardenPlantingListViewModelFactory(
        context: Context
    ): GardenPlantingListViewModelFactory {
        val repository = getGardenPlantingRepository(context)
        return GardenPlantingListViewModelFactory(repository)
    }

    fun providePlantListViewModelFactory(context: Context): PlantListViewModelFactory {
        val repository = getPlantRepository(context)
        return PlantListViewModelFactory(repository)
    }

    fun providePlantDetailViewModelFactory(
        context: Context,
        plantId: String
    ): PlantDetailViewModelFactory {
        return PlantDetailViewModelFactory(getPlantRepository(context),
                getGardenPlantingRepository(context), plantId)
    }*/
}
