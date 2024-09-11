package com.example.groot

import android.app.SearchManager
import android.content.Intent
import android.content.res.Configuration
import android.database.MatrixCursor
import android.os.Bundle
import android.provider.BaseColumns
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.viewpager2.widget.ViewPager2
import com.example.groot.adapter.HomeViewPagerAdapter
import com.example.groot.adapter.SearchOptionAdapter
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationBarView
import com.google.android.material.snackbar.Snackbar

class HomeActivity : AppCompatActivity() {

    private lateinit var navView: NavigationBarView
    private lateinit var viewPager: ViewPager2
    private lateinit var toolbar: MaterialToolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)

        navView=findViewById(R.id.navigation)
        viewPager=findViewById(R.id.viewPager)

        toolbar=findViewById(R.id.topAppBar)
        applyInsets(findViewById(R.id.main))

        viewPager.adapter=HomeViewPagerAdapter(this)
        navView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.home->viewPager.currentItem=0
                R.id.explore->viewPager.currentItem=1
                R.id.profile->viewPager.currentItem=2
            }
            true
        }

        viewPager.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                when (position) {
                    0 -> {
                        navView.selectedItemId = R.id.home
                        toolbar.title = getString(R.string.home)
                        toolbar.menu.clear()
                        toolbar.inflateMenu(R.menu.top_app_bar)
                    }
                    1 -> {
                        navView.selectedItemId = R.id.explore
                        toolbar.title = getString(R.string.explore)
                        toolbar.menu.clear()
                        toolbar.inflateMenu(R.menu.top_app_bar)
                    }
                    2 -> {
                        navView.selectedItemId = R.id.profile
                        toolbar.title = getString(R.string.profile)
                        toolbar.menu.clear()
                        toolbar.inflateMenu(R.menu.top_app_bar_profile)
                    }
                }
            }
        })

        toolbar.setOnMenuItemClickListener {
            when(it.itemId) {
                R.id.search -> {
                    val searchView = toolbar.menu.findItem(R.id.search).actionView as SearchView
                    searchView.queryHint = "Search"

                    val cursor = MatrixCursor(arrayOf(BaseColumns._ID, SearchManager.SUGGEST_COLUMN_TEXT_1))
                    val adapter = SearchOptionAdapter(this, cursor, false)
                    searchView.suggestionsAdapter = adapter

                    searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                        override fun onQueryTextSubmit(query: String?): Boolean {
                            if(query!!.length < 2) {
                                Snackbar.make(findViewById(R.id.main), "Atleast enter 2 characters",
                                    Snackbar.LENGTH_SHORT).show()
                                return true
                            }
                            Snackbar.make(findViewById(R.id.main), "Please select one of the option",
                                Snackbar.LENGTH_SHORT).show()
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
                                val intent = Intent(this@HomeActivity, UserSearchActivity::class.java).apply {
                                    putExtra("QUERY", query)
                                }
                                startActivity(intent)
                            } else {
                                val intent = Intent(this@HomeActivity, RepositorySearchActivity::class.java).apply {
                                    putExtra("QUERY", query)
                                }
                                startActivity(intent)
                            }
                            return true
                        }
                    })

                    true
                }
                R.id.Settings -> {
                    startActivity(Intent(this, SettingsActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }
    private fun applyInsets(viewGroup: ViewGroup) {
        ViewCompat.setOnApplyWindowInsetsListener(viewGroup) { v, insets ->
            val orientation = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
            insets.getInsets(WindowInsetsCompat.Type.ime())
            val systemBarsInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout())
            v.findViewById<MaterialToolbar>(R.id.topAppBar).updatePadding(
                systemBarsInsets.left,
                systemBarsInsets.top,
                systemBarsInsets.right,
                if (orientation) systemBarsInsets.bottom/2 else toolbar.paddingBottom
            )
            v.findViewById<NavigationBarView>(R.id.navigation).updatePadding(
                systemBarsInsets.left,
                if(orientation) systemBarsInsets.top else navView.paddingTop,
                systemBarsInsets.right,
                systemBarsInsets.bottom
            )
            WindowInsetsCompat.CONSUMED
        }
    }
}