package com.example.taskflow.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.taskflow.R
import com.google.firebase.firestore.FirebaseFirestore

class MessageAdapter(private val currentUserEmail: String) : RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    private val messages = mutableListOf<Message>()
    private val userMap = mutableMapOf<String, String>() // Map email addresses to usernames

    // Initialize Firestore
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_message, parent, false)
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messages[position]
        holder.bind(message)
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    fun addMessage(message: Message) {
        messages.add(message)
        notifyDataSetChanged()
    }

    fun setUserMap(userMap: Map<String, String>) {
        this.userMap.clear()
        this.userMap.putAll(userMap)
        notifyDataSetChanged()
    }

    inner class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val senderTextView: TextView = itemView.findViewById(R.id.sender_text_view)
        private val messageTextView: TextView = itemView.findViewById(R.id.message_text_view)
        private val timestampTextView: TextView = itemView.findViewById(R.id.timestamp_text_view)

        fun bind(message: Message) {
            // Use the userMap to get the username associated with the sender's email
            val senderUsername = userMap[message.sender] ?: message.sender

            senderTextView.text = senderUsername
            messageTextView.text = message.text
            timestampTextView.text = message.getFormattedTimestamp()

            // Set alignment based on the sender's email
            val layoutParams = itemView.layoutParams as ViewGroup.MarginLayoutParams
            if (message.sender == currentUserEmail) {
                layoutParams.marginStart = 100 // Right-align message
                layoutParams.marginEnd = 0
            } else {
                layoutParams.marginStart = 0
                layoutParams.marginEnd = 100 // Left-align message
            }
            itemView.layoutParams = layoutParams
        }
    }
}
