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

package com.ericmguimaraes.openjavarank.viewmodels

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import com.ericmguimaraes.openjavarank.data.Resource
import com.ericmguimaraes.openjavarank.data.model.PullRequest
import com.ericmguimaraes.openjavarank.data.repositories.PullRequestRepository
import com.ericmguimaraes.openjavarank.utilities.AbsentLiveData

class PullRequestListViewModel internal constructor(
        repository: PullRequestRepository
) : ViewModel() {
    private val _prId: MutableLiveData<PRId> = MutableLiveData()

    val pulls: LiveData<Resource<List<PullRequest>>> = Transformations
            .switchMap(_prId) { input ->
                input.ifExists { owner, name ->
                    repository.getPulls(owner, name)
                }
            }

    fun setId(owner: String, name: String) {
        val update = PRId(owner, name)
        if (_prId.value == update) {
            return
        }
        _prId.value = update
    }

    data class PRId(val owner: String, val name: String) {
        fun <T> ifExists(f: (String, String) -> LiveData<T>): LiveData<T> {
            return if (owner.isBlank() || name.isBlank()) {
                AbsentLiveData.create()
            } else {
                f(owner, name)
            }
        }
    }

}