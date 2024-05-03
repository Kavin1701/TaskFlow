package com.example.taskflow

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.taskflow.databinding.ActivityAddHeadBinding
import com.example.taskflow.utils.UserData
import com.example.taskflow.utils.UserListAdapter
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

class AddHeadActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddHeadBinding
    private lateinit var eventNameList: ArrayList<String>
    private lateinit var adapter: UserListAdapter
    private lateinit var userList: MutableList<UserData>
    private lateinit var recyclerView: RecyclerView
    private var db = Firebase.firestore
    private lateinit var searchEt: EditText
    private var selectedUsers: MutableSet<UserData> = HashSet()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAddHeadBinding.inflate(layoutInflater)

        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        eventNameList = intent.getStringArrayListExtra("eventList") ?: ArrayList()
//        Toast.makeText(this, eventNameList.toString(), Toast.LENGTH_LONG).show()
        init()
        getDataFromFirebase()
        registerEvents()
    }

    fun init(){
        binding.recyclerview.setHasFixedSize(true)
        binding.recyclerview.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        userList = mutableListOf()
        adapter = UserListAdapter(userList, selectedUsers)
        recyclerView = binding.recyclerview
        recyclerView.adapter = adapter
        searchEt = binding.searchEt
    }

    private fun getDataFromFirebase() {
        db = FirebaseFirestore.getInstance()
        db.collection("users")
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    Toast.makeText(this, exception.toString(), Toast.LENGTH_SHORT).show()
                }

                if (snapshot != null && !snapshot.isEmpty) {
                    userList.clear() // Clear existing data before adding new data
                    for (data in snapshot.documents) {
                        val user: UserData? = data.toObject(UserData::class.java)
                        if (user != null) {
                            userList.add(user)
                        }
                    }
                    recyclerView.adapter = UserListAdapter(userList, selectedUsers)
                }
            }
    }

    private fun registerEvents(){
        searchEt.addTextChangedListener { text ->
            val searchText = text.toString().trim()
            val filteredList =
                userList.filter { user ->
                    user.name!!.startsWith(searchText, ignoreCase = true)
                }
            recyclerView.adapter = UserListAdapter(filteredList, adapter.getSelectedUsers())
            }

        binding.addheads.setOnClickListener {
            val db = FirebaseFirestore.getInstance()

//            val

            for (event in eventNameList) {
                val querySnapshot = db.collection("events")
                    .whereEqualTo("eventName", event)
                    .get()
                    .addOnSuccessListener { documents ->
                        for (document in documents) {
                            val documentData = document.data
                        }
                    }
                    .addOnFailureListener { exception ->
                        // Handle any errors
                        Log.w(TAG, "Error getting documents: ", exception)
                    }
            }



//
//            query.get().addOnSuccessListener { documents ->
//                if (!documents.isEmpty) {
//                    for (document in documents) {
//                        val documentReference = document.reference
//                        // Fetch the document associated with the reference
//                        documentReference.get().addOnSuccessListener { documentSnapshot ->
//                            val eventName = documentSnapshot.getString("eventName")
//                            if (eventName != null) {
//                                // Print the eventName
//                                println("Event Name: $eventName")
//                            }
//                        }.addOnFailureListener { exception ->
//                            // Handle failure to fetch document
//                            println("Error fetching document: ${exception.message}")
//                        }
//                    }
//
//                } else {
//                    Toast.makeText(this, "No documents found", Toast.LENGTH_LONG).show()
//                }
//            }.addOnFailureListener { exception ->
//                Toast.makeText(this, "Error: ${exception.message}", Toast.LENGTH_LONG).show()
//            }

            finish()
        }

    }

}