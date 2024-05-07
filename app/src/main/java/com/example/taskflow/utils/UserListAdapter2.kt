package com.example.taskflow.utils

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.taskflow.R

class UserListAdapter2(private val type: String, private var userList: MutableList<UserData2>, private val listener: OnUserItemClickListener) : RecyclerView.Adapter<UserListAdapter2.MyViewHolder>() {

    interface OnUserItemClickListener {
        fun onDelete(docId: String, type:String)
    }

    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val Name : TextView = itemView.findViewById(R.id.name)
        val Email : TextView = itemView.findViewById(R.id.email)
        val DeleteButton: ImageView = itemView.findViewById(R.id.delete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.user_list_item2, parent, false)
        return UserListAdapter2.MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val user = userList[position]

        holder.Name.text = user.name
        holder.Email.text = user.email
        holder.DeleteButton.setOnClickListener{
            listener.onDelete(userList[position].docId!!, type)
        }
    }

    fun updateList(newList: MutableList<UserData2>) {
        userList = newList
        notifyDataSetChanged()
    }

}