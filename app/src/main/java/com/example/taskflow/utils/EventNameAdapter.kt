package com.example.taskflow.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.taskflow.R

class EventNameAdapter(private val eventList: MutableList<EventData>,
                       private val listener: EventNameAdapter.OnItemClickListener) : RecyclerView.Adapter<EventNameAdapter.MyViewHolder>() {
    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val Name : TextView = itemView.findViewById(R.id.eventName)
        val Desc : TextView = itemView.findViewById(R.id.eventDesc)
        val eventDate : TextView = itemView.findViewById(R.id.eventDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.event_list_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return eventList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.Name.text = eventList[position].eventName
        holder.Desc.text = eventList[position].eventDesc
        holder.eventDate.text = eventList[position].endDate
        holder.itemView.setOnClickListener {
            listener.onItemClick(holder.Name.text.toString(),
                holder.Desc.text.toString(),
                holder.eventDate.text.toString(),
                )
        }
    }

    interface OnItemClickListener {
        fun onItemClick(eventName: String, eventDesc: String, endDate: String)
    }
}