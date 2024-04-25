package com.example.taskflow

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.taskflow.databinding.ActivityEventPageBinding
import com.example.taskflow.utils.EventData
import com.example.taskflow.utils.EventData2
import com.example.taskflow.utils.EventNameAdapter
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class EventsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEventPageBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: EventNameAdapter
    private lateinit var eventList: MutableList<EventData2>
    private var db = Firebase.firestore
    private lateinit var eventDocNameList: MutableList<String>
    private var eventNameList: MutableList<String> = mutableListOf()
    private var e: String = ""
    val eventMap: MutableMap<String, MutableList<String>> = mutableMapOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_events)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding = ActivityEventPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
        getDataFromFirebase()
        Log.e("map", "something")

        registerEvents()
    }

    private fun init(){
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        eventDocNameList = mutableListOf()
        eventList = mutableListOf()



//        binding.recyclerview.setHasFixedSize(true)
//        binding.recyclerview.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
//        adapter = EventNameAdapter(eventList)
//        recyclerView = binding.recyclerview
//        recyclerView.adapter = adapter

    }

    private fun getDataFromFirebase() {
        GlobalScope.launch(Dispatchers.Main) {
            try {
                val snapshot = db.collection("events").get().await()

                if (snapshot != null && !snapshot.isEmpty) {
                    traverseEvents(db.collection("events"))
                    Log.e("map",eventMap.toString())
                }
            } catch (e: Exception) {
                Toast.makeText(this@EventsActivity, "Error fetching data: ${e.message}", Toast.LENGTH_SHORT).show()
                Log.e("EventPage", "Error fetching data", e)
            }
        }
    }

    private suspend fun traverseEvents(eventsCollectionRef: CollectionReference) {
        try {
            val eventsSnapshot = eventsCollectionRef.get().await()

            for (doc in eventsSnapshot.documents) {
                val eventDocName = doc.id
                val eventName = doc.getString("eventName") ?: "no name"

                // Check if the current user's email is in the heads or members collection
                val hasAccess = checkAccess(doc)

                if (hasAccess) {
                    eventDocNameList.add(eventDocName)
                    eventMap[eventName] = eventNameList.toMutableList()
                    eventNameList.add(eventName)
                }

                // Traverse subcollections recursively
                traverseEvents(doc.reference.collection("events"))
                eventNameList.removeAt(eventNameList.size - 1)
            }
        } catch (e: Exception) {
            Log.e("TraverseEvents", "Error traversing events", e)
        }
    }

    private suspend fun checkAccess(doc: DocumentSnapshot): Boolean {
        try {
            val headsSnapshot = doc.reference.collection("heads").get().await()
            val membersSnapshot = doc.reference.collection("members").get().await()

            // Check if the current user's email exists in either heads or members collection
            val currentUserEmail = auth.currentUser?.email
            if (currentUserEmail != null) {
                if (headsSnapshot.documents.any { it.getString("email") == currentUserEmail } ||
                    membersSnapshot.documents.any { it.getString("email") == currentUserEmail }) {
                    return true
                }
            }
        } catch (e: Exception) {
            Log.e("CheckAccess", "Error checking access", e)
        }
        return false
    }

    private fun registerEvents(){
        binding.logoutBt.setOnClickListener{
            auth.signOut()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

}