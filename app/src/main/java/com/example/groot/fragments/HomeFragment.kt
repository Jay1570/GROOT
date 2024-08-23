package com.example.groot.fragments

import android.app.SearchManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import com.example.groot.R
import com.example.groot.RepoActivity
import com.example.groot.SearchResultsActivity
import com.example.groot.StarredActivity
import com.example.groot.viewmodel.ProfileViewModel
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton

class HomeFragment : Fragment() {

    private lateinit var viewModel: ProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this)[ProfileViewModel::class.java]
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        val layRepository: MaterialButton = view.findViewById(R.id.btnRepository)
        val layStarred: MaterialButton = view.findViewById(R.id.btnStarred)
        var username = ""
        viewModel.profile.observe(viewLifecycleOwner) { username = it.userName+" " }
        layRepository.setOnClickListener {
            val intent = Intent(activity, RepoActivity::class.java)
            intent.putExtra("path", username)
            Log.i("Home", username)
            startActivity(intent)
        }

        layStarred.setOnClickListener {
            val intent = Intent(activity, StarredActivity::class.java)
            startActivity(intent)
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val appBar = view.findViewById<MaterialToolbar>(R.id.topAppBar)
        appBar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.search -> {
                    true
                }
                else -> false
            }
        }
        val searchManager: SearchManager = activity?.getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView = appBar.menu.findItem(R.id.search).actionView as SearchView
        val component = ComponentName(requireContext(), SearchResultsActivity::class.java)
        val searchableInfo = searchManager.getSearchableInfo(component)
        searchView.setSearchableInfo(searchableInfo)
    }
}