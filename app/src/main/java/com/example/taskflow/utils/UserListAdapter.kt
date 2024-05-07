package com.example.taskflow.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.taskflow.R

class UserListAdapter(private val userList: List<UserData>, private val selectedUsers: MutableSet<UserData>) : RecyclerView.Adapter<UserListAdapter.MyViewHolder>() {

    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val Name : TextView = itemView.findViewById(R.id.name)
        val Email : TextView = itemView.findViewById(R.id.email)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.user_list_item, parent, false)
        return UserListAdapter.MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val user = userList[position]

        holder.Name.text = user.name
        holder.Email.text = user.email

        holder.itemView.setOnClickListener {
            if (selectedUsers.contains(user)) {
                selectedUsers.remove(user)
            } else {
                selectedUsers.add(user)
            }
            notifyDataSetChanged()
        }


        val isSelected = selectedUsers.contains(user)
        holder.itemView.isActivated = isSelected
        val backgroundColor = if (isSelected) R.color.blue else android.R.color.transparent
        holder.itemView.findViewById<LinearLayout>(R.id.llt).setBackgroundColor(
            ContextCompat.getColor(holder.itemView.context, backgroundColor)
        )
    }


    fun getSelectedUsers(): MutableSet<UserData> {
        return selectedUsers
    }


}