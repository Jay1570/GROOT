package com.example.groot.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.groot.R
import com.example.groot.model.StorageItem

class StorageAdapter(
    private var storageItems: List<StorageItem>,
    private val onItemClickListener: (StorageItem) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var filteredItems = storageItems

    companion object {
        private const val VIEW_TYPE_FOLDER = 0
        private const val VIEW_TYPE_FILE = 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (filteredItems[position].isFolder) VIEW_TYPE_FOLDER else VIEW_TYPE_FILE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == VIEW_TYPE_FOLDER) {
            val view = inflater.inflate(R.layout.item_folder, parent, false)
            FolderViewHolder(view)
        } else {
            val view = inflater.inflate(R.layout.item_files, parent, false)
            FileViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = filteredItems[position]
        if (getItemViewType(position) == VIEW_TYPE_FOLDER) {
            (holder as FolderViewHolder).bind(item)
        } else {
            (holder as FileViewHolder).bind(item)
        }
        holder.itemView.setOnClickListener { onItemClickListener(item) }
    }

    fun filter(query: String) {
        filteredItems = if(query.isEmpty()) storageItems else storageItems.filter { it.name.startsWith(query, ignoreCase = true) }
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return filteredItems.size
    }

    class FolderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val name: TextView = itemView.findViewById(R.id.name)
        private val icon: ImageView = itemView.findViewById(R.id.icon)

        fun bind(item: StorageItem) {
            name.text = item.name
            icon.setImageResource(R.drawable.folder_svgrepo_com)
        }
    }

    class FileViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val name: TextView = itemView.findViewById(R.id.name)
        private val icon: ImageView = itemView.findViewById(R.id.icon)

        fun bind(item: StorageItem) {
            name.text = item.name
            icon.setImageResource(R.drawable.files_interface_svgrepo_com)
        }
    }
}