package com.example.taskflow.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.taskflow.AddHeadActivity
import com.example.taskflow.databinding.FragmentUserProfileBinding
import com.example.taskflow.utils.UserData2
import com.example.taskflow.utils.UserListAdapter2
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class UserProfileFragment(private val eventName: String?, private val eventList:  ArrayList<String>?) : Fragment(), UserListAdapter2.OnUserItemClickListener {
    lateinit var binding: FragmentUserProfileBinding
    lateinit var auth: FirebaseAuth
    private lateinit var adapter: UserListAdapter2
    private lateinit var adapter2: UserListAdapter2
    private lateinit var userList: MutableList<UserData2>
    private lateinit var userList2: MutableList<UserData2>
    lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        getUserListFromDatabase()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init(view)
        registerEvents()
    }

    private fun init(view: View){

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        userList = mutableListOf()
        userList2 = mutableListOf()

        adapter = UserListAdapter2("heads", userList, this)
        adapter2 = UserListAdapter2("members", userList2, this)

        binding.recyclerview.setHasFixedSize(true)
        binding.recyclerview.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerview.adapter = adapter

        binding.recyclerview2.setHasFixedSize(true)
        binding.recyclerview2.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerview2.adapter = adapter2
    }

    private  fun registerEvents(){
        binding.addHeads.setOnClickListener{
            val eventListTemp = eventList?.let { list ->
                ArrayList<String>().apply {
                    list.forEach { element ->
                        add(element)
                    }
                }
            }
            eventListTemp?.add(eventName.toString())
            val intent = Intent(requireContext(), AddHeadActivity::class.java)
            intent.putStringArrayListExtra("eventList", eventListTemp)
            intent.putExtra("type", "heads")
            startActivity(intent)
        }

        binding.addMembers.setOnClickListener{
            val eventListTemp = eventList?.let { list ->
                ArrayList<String>().apply {
                    list.forEach { element ->
                        add(element)
                    }
                }
            }
            eventListTemp?.add(eventName.toString())
            val intent = Intent(requireContext(), AddHeadActivity::class.java)
            intent.putStringArrayListExtra("eventList", eventListTemp)
            intent.putExtra("type", "members")
            startActivity(intent)
        }
    }

    private fun getUserListFromDatabase(){
        try {
            GlobalScope.launch(Dispatchers.Main) {
                userList = mutableListOf()
                userList2 = mutableListOf()
                adapter.updateList(userList)
                adapter2.updateList(userList2)
                val docRef = getDocRef()
                if(docRef != null) {
                    val usersCollection = db.collection("users")
                    val querySnapshot = docRef!!.reference.collection("heads").get().await()
                    for (document in querySnapshot.documents) {
                        var name: String? = "h"
                        val email = document.getString("email") ?: ""
                        val query = usersCollection.whereEqualTo("email", email)
                        query.get()
                            .addOnSuccessListener { documents ->
                                for (document2 in documents) {
                                    name = document2.getString("name")
                                    userList.add(UserData2(name, email, document.id))
                                    adapter.updateList(userList)
                                }
                            }
                    }
                    val querySnapshot2 = docRef!!.reference.collection("members").get().await()
                    for (document in querySnapshot2.documents) {
                        var name: String? = "h"
                        val email = document.getString("email") ?: ""
                        val query = usersCollection.whereEqualTo("email", email)
                        query.get()
                            .addOnSuccessListener { documents ->
                                for (document2 in documents) {
                                    name = document2.getString("name")
                                    userList2.add(UserData2(name, email, document.id))
                                    adapter2.updateList(userList2)
                                }
                            }
                    }
                }

            }
        } catch (e: Exception) {
        }
    }

    suspend fun getDocRef(): DocumentSnapshot? {
        val eventListTemp = eventList?.let { list ->
            ArrayList<String>().apply {
                list.forEach { element ->
                    add(element)
                }
            }
        }
        eventListTemp?.add(eventName.toString())

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

    override fun onDelete(docId: String, type: String) {
        lifecycleScope.launch {
            if (deleteUser(docId, type)) {
                getUserListFromDatabase()
                Toast.makeText(requireContext(), "deleted", Toast.LENGTH_SHORT).show()
            }
            else{
                Toast.makeText(requireContext(), "can't be deleted", Toast.LENGTH_SHORT).show()
            }
        }

    }

    suspend fun deleteUser(docId: String, type: String): Boolean{
        return try {
            val docRef = getDocRef()
            docRef!!.reference.collection(type)
                .document(docId)
                .delete()
                .await()
            true
        } catch (e: Exception) {
            false
        }
    }
}