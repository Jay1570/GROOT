package com.example.groot.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.groot.R
import com.example.groot.model.Repository

class RepositoryListAdapter(private var repo: List<Repository> = emptyList(), private var repoPath: List<String> = emptyList(), val onItemClick : (String) -> Unit):
    RecyclerView.Adapter<RepositoryListAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val userIcon: ImageView = itemView.findViewById(R.id.user_icon)
        val username: TextView = itemView.findViewById(R.id.username)
        val repoName: TextView = itemView.findViewById(R.id.repo_name)

        init {
            itemView.setOnClickListener {
                if (repo.isNotEmpty()) onItemClick(repo[adapterPosition].owner + " / " + repo[adapterPosition].name)
                else onItemClick(repoPath[adapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_repository, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.userIcon.setImageResource(R.drawable.repo_git_svgrepo_com)
        if (repo.isNotEmpty()){
            val repository = repo[position]
            holder.username.text = repository.owner
            holder.repoName.text = repository.name
        } else {
            val path = repoPath[position]
            holder.username.text = path.substringBefore("/").trim()
            holder.repoName.text = path.substringAfter("/").trim()
        }
    }

    override fun getItemCount(): Int {
        return if (repo.isNotEmpty()) repo.size else repoPath.size
    }

    fun updateList(newList: List<Repository>) {
        repo = newList
        notifyDataSetChanged()
    }

    fun updatePath(newList: List<String>) {
        repoPath = newList
        notifyDataSetChanged()
    }
}