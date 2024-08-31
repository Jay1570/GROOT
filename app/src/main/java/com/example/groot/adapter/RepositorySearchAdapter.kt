package com.example.groot.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.groot.R
import com.example.groot.model.Repository

class RepositorySearchAdapter(private var repo: List<Repository>, val onItemClick : (String) -> Unit):
    RecyclerView.Adapter<RepositorySearchAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val userIcon: ImageView = itemView.findViewById(R.id.user_icon)
        val username: TextView = itemView.findViewById(R.id.username)
        val repoName: TextView = itemView.findViewById(R.id.repo_name)

        init {
            itemView.setOnClickListener { onItemClick(repo[adapterPosition].owner + " / " + repo[adapterPosition].name) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_repository, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val repository = repo[position]
        holder.userIcon.setImageResource(R.drawable.repo_git_svgrepo_com)
        holder.username.text = repository.owner
        holder.repoName.text = repository.name
    }

    override fun getItemCount(): Int {
        return repo.size
    }

    fun updateList(newList: List<Repository>) {
        repo = newList
    }
}