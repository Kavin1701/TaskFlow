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
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
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
    private lateinit var type: String

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
        type = intent.getStringExtra("type").toString()

        Toast.makeText(this, type, Toast.LENGTH_LONG).show()
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

            GlobalScope.launch(Dispatchers.Main) {
                val docRef = getDocRef(eventNameList)
                if (docRef != null){
                    for (user in adapter.getSelectedUsers()){
                        val data = hashMapOf("email" to user.email)
                        docRef.reference.collection(type).add(data)
                    }

                    Toast.makeText(this@AddHeadActivity,"added", Toast.LENGTH_SHORT).show()

                }
                finish()
            }

        }

    }

    suspend fun getDocRef(eventNameList: List<String>): DocumentSnapshot? {
        val firestore = FirebaseFirestore.getInstance()
        var eventsCollectionRef = firestore.collection("events").get().await()

        var docRef: DocumentSnapshot? = null
        for (event in eventNameList) {
            docRef = eventsCollectionRef.documents.find { it.getString("eventName") == event }
            if (docRef != null) {
                val eventName = docRef.getString("eventName")
                Log.e("helo", "Document found for event: $eventName")
                 eventsCollectionRef = docRef.reference.collection("events").get().await()
            } else {
                Log.e("helo", "No document found for event: $event")
            }
        }

        return docRef
    }


}