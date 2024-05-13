package com.example.taskflow.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.taskflow.R
import com.example.taskflow.databinding.FragmentUserChatBinding
import com.example.taskflow.databinding.FragmentUserHomeBinding
import com.example.taskflow.utils.ChatData
import com.example.taskflow.utils.DetailData
import com.example.taskflow.utils.EventData
import com.example.taskflow.utils.EventNameAdapter
import com.example.taskflow.utils.MessageAdapter
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class UserChatFragment(private val event: String?,
                       private val eventNameList:  ArrayList<String>?) : Fragment() {

    lateinit var binding: FragmentUserChatBinding
    private var db = Firebase.firestore
    private lateinit var adapter: MessageAdapter
    private lateinit var currentUserEmail: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentUserChatBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init(view)
        load()
        binding.sendMessageButton.setOnClickListener{
            sendMessage()
        }
        scrollToBottom(binding.recyclerViewMessages)
    }

    private fun init(view: View){
        currentUserEmail = FirebaseAuth.getInstance().currentUser?.email ?: ""
        binding.eventName.text = event
        binding.recyclerViewMessages.layoutManager = LinearLayoutManager(requireContext())
        adapter = MessageAdapter(currentUserEmail)
        binding.recyclerViewMessages.adapter = adapter
    }

    private fun sendMessage() {
        GlobalScope.launch(Dispatchers.Main) {
            val docRef = getDocRef()
            val messageText = binding.messageEditText.text.toString()
            if (messageText.isNotEmpty()) {
                val timestamp = System.currentTimeMillis()

                val message = ChatData(messageText, currentUserEmail, timestamp)


                if (docRef != null) {
                    docRef.reference.collection("chats")
                        .add(message)
                        .addOnSuccessListener { documentReference ->
                            binding.messageEditText.text.clear()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(requireContext(), "Failed to send message: ${e.message}", Toast.LENGTH_SHORT)
                                .show()
                        }
                }
            } else {
                // Show message to the user to enter a message
                Toast.makeText(requireContext(), "Please enter a message", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun scrollToBottom(recyclerView: RecyclerView) {
        val layoutManager = recyclerView.layoutManager as LinearLayoutManager
        layoutManager.stackFromEnd = true // Stack items from the bottom
        layoutManager.scrollToPositionWithOffset(adapter.itemCount - 1, 0) // Scroll to the last item
    }
    private fun load(){
        GlobalScope.launch(Dispatchers.Main) {
            db = FirebaseFirestore.getInstance()

            val docRef = getDocRef()
            if (docRef != null) {
                docRef.reference.collection("chats")
                    .addSnapshotListener { snapshot, exception ->
                        if (exception != null) {
                            Toast.makeText(requireContext(), exception.toString(), Toast.LENGTH_SHORT)
                                .show()
                        }

                        if (snapshot != null && !snapshot.isEmpty) {
                            for (dc in snapshot.documentChanges){
                                when (dc.type) {
                                    DocumentChange.Type.ADDED -> {
                                        val message = dc.document.toObject(ChatData::class.java)
                                        adapter.addMessage(message)
                                    }

                                    DocumentChange.Type.MODIFIED -> TODO()
                                    DocumentChange.Type.REMOVED -> TODO()
                                }
                            }
//                            chatList.clear() // Clear existing data before adding new data
//                            for (data in snapshot.documents) {
//                                val chat: ChatData? = data.toObject(ChatData::class.java)
//                                if (chat != null) {
//                                    chatList.add(chat)
//                                }
//                            }
//                            recyclerView.adapter = ChatAdapter(chatList, this@UserChatFragment)
                        }
                    }
            }
        }

    }

    suspend fun getDocRef(): DocumentSnapshot? {
        val eventListTemp = eventNameList?.let { list ->
            ArrayList<String>().apply {
                list.forEach { element ->
                    add(element)
                }
            }
        }
        eventListTemp?.add(event.toString())

        var eventsCollectionRef = db.collection("events").get().await()

        var docRef: DocumentSnapshot? = null
        for (event in eventListTemp!!) {
            docRef = eventsCollectionRef.documents.find { it.getString("eventName") == event }
            if (docRef != null) {
                val eventName = docRef.getString("eventName")
//                Log.e("helo", "Document found for event: $eventName")
                eventsCollectionRef = docRef.reference.collection("events").get().await()
            } else {
//                Log.e("helo", "No document found for event: $event")
            }
        }

        return docRef
    }

}