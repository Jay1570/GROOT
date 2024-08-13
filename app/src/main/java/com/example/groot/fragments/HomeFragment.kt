package com.example.groot.fragments

import android.app.SearchManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.SearchView
import com.example.groot.R
import com.example.groot.RepoActivity
import com.example.groot.SearchResultsActivity
import com.example.groot.StarredActivity
import com.google.android.material.appbar.MaterialToolbar

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        val layRepository: LinearLayout = view.findViewById(R.id.layRepository)
        val layStarred: LinearLayout = view.findViewById(R.id.layStarred)
        layRepository.setOnClickListener {

            val intent = Intent(activity, RepoActivity::class.java)
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

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeFragment.
         */
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}