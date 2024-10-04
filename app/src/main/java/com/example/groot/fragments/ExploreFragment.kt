package com.example.groot.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.groot.R
import com.example.groot.RepoDetailsActivity
import com.example.groot.adapter.RepositoryListAdapter
import com.example.groot.viewmodel.ExploreViewModel


class ExploreFragment : Fragment() {

    private lateinit var viewModel: ExploreViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this)[ExploreViewModel::class.java]
        val view = inflater.inflate(R.layout.fragment_explore, container, false)
        val recyclerView = view?.findViewById<RecyclerView>(R.id.recyclerView)
        val adapter = RepositoryListAdapter(repo = emptyList()) { onItemClick(it) }
        recyclerView?.layoutManager = LinearLayoutManager(requireContext())
        recyclerView?.adapter = adapter
        viewModel.fetchRepo()
        viewModel.exploreRepository.observe(viewLifecycleOwner) { repos ->
            Log.d("ExploreFragment", repos.size.toString())
            adapter.updateList(repos)
            adapter.notifyDataSetChanged()
        }
        return view
    }

    private fun onItemClick(path: String) {
        val intent = Intent(requireContext(), RepoDetailsActivity::class.java)
        intent.putExtra("path", path)
        startActivity(intent)
    }
}