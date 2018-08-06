package com.ericmguimaraes.openjavarank

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingComponent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.ericmguimaraes.openjavarank.ui.adapters.RepoAdapter
import com.ericmguimaraes.openjavarank.ui.binding.FragmentDataBindingComponent
import com.ericmguimaraes.openjavarank.databinding.FragmentRepoListBinding
import com.ericmguimaraes.openjavarank.utilities.AppExecutors
import com.ericmguimaraes.openjavarank.utilities.InjectorUtils
import com.ericmguimaraes.openjavarank.utilities.autoCleared
import com.ericmguimaraes.openjavarank.viewmodels.RepoListViewModel

class RepoFragment : Fragment() {

    lateinit var viewModelFactory: ViewModelProvider.Factory

    lateinit var repoViewModel: RepoListViewModel

    lateinit var appExecutors: AppExecutors

    // mutable for testing
    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)
    var binding by autoCleared<FragmentRepoListBinding>()

    private var adapter by autoCleared<RepoAdapter>()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModelFactory = InjectorUtils.provideRepoListViewModelFactory(requireContext())!!
        appExecutors = InjectorUtils.provideAppExecutors()
        repoViewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(RepoListViewModel::class.java)

        val repositories = repoViewModel.repositories
        repositories.observe(this, Observer { resource ->
            binding.repoResource = resource
            binding.contributorsStatus = resource?.status
        })

        val adapter = RepoAdapter(dataBindingComponent, appExecutors) { repo ->
            navController().navigate(
                    RepoFragmentDirections.showPrs(repo.owner.login, repo.name)
            )
        }
        this.adapter = adapter
        binding.repoList.adapter = adapter
        initRepoList(repoViewModel)
    }

    private fun initRepoList(viewModel: RepoListViewModel) {
        viewModel.repositories.observe(this, Observer { listResource ->
            if (listResource?.data != null) {
                listResource.data.observe(this, Observer {
                    adapter.submitList(it)
                })
            }
        })
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val dataBinding = DataBindingUtil.inflate<FragmentRepoListBinding>(
                inflater,
                R.layout.fragment_repo_list,
                container,
                false
        )
        binding = dataBinding
        return dataBinding.root
    }

    /**
     * Created to be able to override in tests
     */
    fun navController() = findNavController()

}
