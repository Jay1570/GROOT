package com.example.groot.adapter

import android.app.SearchManager
import android.content.Context
import android.database.Cursor
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cursoradapter.widget.CursorAdapter

class SearchOptionAdapter(context: Context?, c: Cursor?, autoRequery: Boolean) :
    CursorAdapter(context, c, autoRequery) {

    override fun newView(context: Context?, cursor: Cursor?, parent: ViewGroup?): View {
        val inflater = LayoutInflater.from(context)
        return inflater.inflate(android.R.layout.simple_list_item_1, parent, false)
    }

    override fun bindView(view: View?, context: Context?, cursor: Cursor?) {
        val textView = view?.findViewById<TextView>(android.R.id.text1)
        textView?.text = cursor?.getString(cursor.getColumnIndexOrThrow(SearchManager.SUGGEST_COLUMN_TEXT_1))
    }
}