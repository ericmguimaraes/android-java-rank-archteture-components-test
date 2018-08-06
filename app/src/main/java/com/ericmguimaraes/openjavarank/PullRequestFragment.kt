package com.ericmguimaraes.openjavarank

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.databinding.DataBindingComponent
import android.databinding.DataBindingUtil
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ericmguimaraes.openjavarank.ui.adapters.PullRequestsAdapter
import com.ericmguimaraes.openjavarank.ui.binding.FragmentDataBindingComponent
import com.ericmguimaraes.openjavarank.databinding.FragmentPrListBinding
import com.ericmguimaraes.openjavarank.utilities.AppExecutors
import com.ericmguimaraes.openjavarank.utilities.InjectorUtils
import com.ericmguimaraes.openjavarank.utilities.autoCleared
import com.ericmguimaraes.openjavarank.viewmodels.PullRequestListViewModel

class PullRequestFragment : Fragment() {

    lateinit var viewModelFactory: ViewModelProvider.Factory

    lateinit var prsViewModel: PullRequestListViewModel

    lateinit var appExecutors: AppExecutors

    // mutable for testing
    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)
    var binding by autoCleared<FragmentPrListBinding>()

    private var adapter by autoCleared<PullRequestsAdapter>()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModelFactory = InjectorUtils.providePullRequestListViewModelFactory(requireContext())!!
        appExecutors = InjectorUtils.provideAppExecutors()
        prsViewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(PullRequestListViewModel::class.java)
        val params = PullRequestFragmentArgs.fromBundle(arguments)
        prsViewModel.setId(params.owner, params.name)

        val repositories = prsViewModel.pulls
        repositories.observe(this, Observer { resource ->
            binding.repoResource = resource
            binding.contributorsStatus = resource?.status
        })

        val adapter = PullRequestsAdapter(dataBindingComponent, appExecutors) { pull ->
            val url = pull.htmlUrl
            val intent = Intent()
            intent.action = Intent.ACTION_VIEW
            intent.data = Uri.parse(url)
            startActivity(intent)
        }
        this.adapter = adapter
        binding.repoList.adapter = adapter
        initPullsList(prsViewModel)
    }

    private fun initPullsList(viewModel: PullRequestListViewModel) {
        viewModel.pulls.observe(this, Observer { listResource ->
            if (listResource?.data != null) {
                adapter.submitList(listResource.data)
            } else {
                adapter.submitList(emptyList())
            }
        })
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val dataBinding = DataBindingUtil.inflate<FragmentPrListBinding>(
                inflater,
                R.layout.fragment_pr_list,
                container,
                false
        )
        binding = dataBinding
        return dataBinding.root
    }

}
