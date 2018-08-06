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

package com.ericmguimaraes.openjavarank.adapters

import android.databinding.DataBindingComponent
import android.databinding.DataBindingUtil
import android.support.v7.util.DiffUtil
import android.view.LayoutInflater
import android.view.ViewGroup
import com.ericmguimaraes.openjavarank.R
import com.ericmguimaraes.openjavarank.data.model.Repo
import com.ericmguimaraes.openjavarank.databinding.FragmentRepoListItemBinding
import com.ericmguimaraes.openjavarank.ui.common.DataBoundPagedListAdapter
import com.ericmguimaraes.openjavarank.utilities.AppExecutors

class RepoAdapter(
        private val dataBindingComponent: DataBindingComponent,
        appExecutors: AppExecutors,
        private val callback: ((Repo) -> Unit)?
) : DataBoundPagedListAdapter<Repo, FragmentRepoListItemBinding>(
        appExecutors = appExecutors,
        diffCallback = object : DiffUtil.ItemCallback<Repo>() {
            override fun areItemsTheSame(oldItem: Repo, newItem: Repo): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Repo, newItem: Repo): Boolean {
                return oldItem.stars == newItem.stars
                        && oldItem.description.equals(newItem.description)
            }
        }
) {

    override fun createBinding(parent: ViewGroup): FragmentRepoListItemBinding {
        val binding = DataBindingUtil
                .inflate<FragmentRepoListItemBinding>(
                        LayoutInflater.from(parent.context),
                        R.layout.fragment_repo_list_item,
                        parent,
                        false,
                        dataBindingComponent
                )
        binding.root.setOnClickListener {
            binding.repo?.let {
                callback?.invoke(it)
            }
        }
        return binding
    }

    override fun bind(binding: FragmentRepoListItemBinding, item: Repo) {
        binding.repo = item
    }
}