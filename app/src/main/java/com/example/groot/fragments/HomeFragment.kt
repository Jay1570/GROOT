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
    import com.example.groot.R
    import com.example.groot.RepoActivity
    import com.example.groot.RepositorySearchActivity
    import com.example.groot.UserSearchActivity
    import com.example.groot.StarredActivity
    import com.example.groot.adapter.SearchOptionAdapter
    import com.google.android.material.appbar.MaterialToolbar
    import com.google.android.material.button.MaterialButton

    class HomeFragment : Fragment() {

        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            val view = inflater.inflate(R.layout.fragment_home, container, false)
            val layRepository: MaterialButton = view.findViewById(R.id.btnRepository)
            val layStarred: MaterialButton = view.findViewById(R.id.btnStarred)

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
            val searchView = appBar.menu.findItem(R.id.search).actionView as SearchView

            val cursor = MatrixCursor(arrayOf(BaseColumns._ID, SearchManager.SUGGEST_COLUMN_TEXT_1))
            val adapter = SearchOptionAdapter(requireContext(), cursor, false)
            searchView.suggestionsAdapter = adapter

            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    query?.let {
                        val intent = Intent(requireContext(), RepositorySearchActivity::class.java).apply {
                            putExtra("QUERY", query)
                        }
                        startActivity(intent)
                    }
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    val updatedCursor = MatrixCursor(arrayOf(BaseColumns._ID, SearchManager.SUGGEST_COLUMN_TEXT_1))
                    if(!newText.isNullOrEmpty()){
                        updatedCursor.addRow(arrayOf(0, "Search Users with \"$newText\""))
                        updatedCursor.addRow(arrayOf(1, "Search Repositories with \"$newText\""))
                    }

                    adapter.changeCursor(updatedCursor)
                    adapter.notifyDataSetChanged()

                    searchView.showContextMenu()
                    return true
                }
            })

            searchView.setOnSuggestionListener(object : SearchView.OnSuggestionListener{
                override fun onSuggestionSelect(position: Int): Boolean {
                    return true
                }

                override fun onSuggestionClick(position: Int): Boolean {
                    val cursor = searchView.suggestionsAdapter.cursor
                    cursor.moveToPosition(position)
                    val suggestion = cursor.getString(cursor.getColumnIndexOrThrow(SearchManager.SUGGEST_COLUMN_TEXT_1))
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