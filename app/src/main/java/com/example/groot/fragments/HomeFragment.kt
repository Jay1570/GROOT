    package com.example.groot.fragments

    import android.app.SearchManager
    import android.content.Intent
    import android.database.MatrixCursor
    import android.os.Bundle
    import android.provider.BaseColumns
    import androidx.fragment.app.Fragment
    import android.view.LayoutInflater
    import android.view.View
    import android.view.ViewGroup
    import androidx.appcompat.widget.SearchView
    import androidx.lifecycle.ViewModelProvider
    import com.example.groot.R
    import com.example.groot.RepoActivity
    import com.example.groot.RepositorySearchActivity
    import com.example.groot.UserSearchActivity
    import com.example.groot.StarredActivity
    import com.example.groot.adapter.SearchOptionAdapter
    import com.example.groot.viewmodel.ProfileViewModel
    import com.google.android.material.appbar.MaterialToolbar
    import com.google.android.material.button.MaterialButton
    import com.google.android.material.snackbar.Snackbar

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
            viewModel.profile.observe(viewLifecycleOwner) { username = it.userName + " " }

            layRepository.setOnClickListener {
                val intent = Intent(activity, RepoActivity::class.java)
                intent.putExtra("username", username)
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
            val searchView = appBar.menu.findItem(R.id.search).actionView as SearchView
            searchView.queryHint = "Search"

            val cursor = MatrixCursor(arrayOf(BaseColumns._ID, SearchManager.SUGGEST_COLUMN_TEXT_1))
            val adapter = SearchOptionAdapter(requireContext(), cursor, false)
            searchView.suggestionsAdapter = adapter

            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    if(query!!.length < 2) {
                        Snackbar.make(view.findViewById(R.id.main), "Atleast enter 2 characters",Snackbar.LENGTH_SHORT).show()
                        return true
                    }
                    Snackbar.make(view.findViewById(R.id.main), "Please select one of the option",Snackbar.LENGTH_SHORT).show()
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    val updatedCursor = MatrixCursor(arrayOf(BaseColumns._ID, SearchManager.SUGGEST_COLUMN_TEXT_1))
                    val query = newText ?: ""

                    updatedCursor.addRow(arrayOf(0, "Search Users with \"$query\""))
                    updatedCursor.addRow(arrayOf(1, "Search Repositories with \"$query\""))

                    adapter.changeCursor(updatedCursor)
                    adapter.notifyDataSetChanged()

                    return true
                }
            })

            searchView.setOnSuggestionListener(object : SearchView.OnSuggestionListener{
                override fun onSuggestionSelect(position: Int): Boolean {
                    return true
                }

                override fun onSuggestionClick(position: Int): Boolean {
                    val cur = searchView.suggestionsAdapter.cursor
                    cur.moveToPosition(position)
                    val suggestion = cur.getString(cur.getColumnIndexOrThrow(SearchManager.SUGGEST_COLUMN_TEXT_1))
                    val query: String = searchView.query.toString()
                    if(suggestion.contains("Users")) {
                        val intent = Intent(requireContext(), UserSearchActivity::class.java).apply {
                            putExtra("QUERY", query)
                        }
                        startActivity(intent)
                    } else {
                        val intent = Intent(requireContext(), RepositorySearchActivity::class.java).apply {
                            putExtra("QUERY", query)
                        }
                        startActivity(intent)
                    }
                    return true
                }
            })

            appBar.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.search -> {
                        true
                    }
                    else -> false
                }
            }
        }
    }