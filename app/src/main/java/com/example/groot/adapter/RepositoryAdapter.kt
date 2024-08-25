package com.example.groot.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.groot.R

class RepositoryAdapter(
    private val context: Context,
    private val repositories: List<Repository>,
    private val onRepoClick: (Repository) -> Unit
) : RecyclerView.Adapter<RepositoryAdapter.RepositoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepositoryViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_repository, parent, false)
        return RepositoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: RepositoryViewHolder, position: Int) {
        val repository = repositories[position]
        holder.bind(repository)
    }

    override fun getItemCount(): Int {
        return repositories.size
    }

    inner class RepositoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val userIcon: ImageView = itemView.findViewById(R.id.user_icon)
        private val username: TextView = itemView.findViewById(R.id.username)
        private val repoName: TextView = itemView.findViewById(R.id.repo_name)

        fun bind(repository: Repository) {
            // Assuming you have a user icon drawable resource
            userIcon.setImageResource(R.drawable.repo_git_svgrepo_com)
            username.text = repository.username
            repoName.text = repository.name
            itemView.setOnClickListener { onRepoClick(repository) }
        }
    }
}

data class Repository(val name: String, val username: String)
