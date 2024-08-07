package com.example.groot.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import com.example.groot.R
import com.example.groot.model.User
import de.hdodenhof.circleimageview.CircleImageView

class UserListRecyclerViewAdapter(private var users: List<User>):
    RecyclerView.Adapter<UserListRecyclerViewAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val imageView: CircleImageView = itemView.findViewById(R.id.userImage)
        val username: TextView = itemView.findViewById(R.id.username)
        val email: TextView = itemView.findViewById(R.id.email)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.user_item,parent,false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentUser = users[position]
        holder.imageView.load(currentUser.imgUrl) {
            transformations(CircleCropTransformation())
            placeholder(R.drawable.user)
            error(R.drawable.user)
        }
        holder.username.text = currentUser.userName
        holder.email.text = currentUser.email
    }

    fun updateUsers(newUsers: List<User>) {
        users = newUsers
    }

    override fun getItemCount() = users.size
}