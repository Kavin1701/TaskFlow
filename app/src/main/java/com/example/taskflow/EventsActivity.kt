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
import com.example.taskflow.utils.DetailData
import com.example.taskflow.utils.DetailDataAdapter
import com.example.taskflow.utils.EventData
import com.example.taskflow.utils.EventData2
import com.example.taskflow.utils.EventNameAdapter
import com.example.taskflow.utils.EventNameAdapter2
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


interface DetailDataCallback {
    fun onDataReceived(detailList: List<DetailData>, intent: Intent)
}

class EventsActivity : AppCompatActivity(), EventNameAdapter2.OnItemClickListener, DetailDataCallback {

    private lateinit var binding: ActivityEventPageBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: EventNameAdapter2
    private lateinit var liveEventList: MutableList<EventData2>
    private lateinit var pastEventList: MutableList<EventData2>
    private var db = Firebase.firestore
    private lateinit var eventDocNameList: MutableList<String>
    private var eventNameList: MutableList<String> = mutableListOf()
    private var e: String = ""
    val eventMap: MutableMap<String, MutableList<String>> = mutableMapOf()
    private var detailList: MutableList<DetailData> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_events)


        binding = ActivityEventPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
        getDataFromFirebase()
        Log.e("map", "something")

        registerEvents()
        Log.e("route", "EventActivity")
    }

    private fun init(){
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        eventDocNameList = mutableListOf()
        liveEventList = mutableListOf()
        pastEventList = mutableListOf()


        binding.recyclerview.setHasFixedSize(true)
        binding.recyclerview.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        adapter = EventNameAdapter2(liveEventList, this)
        recyclerView = binding.recyclerview
        recyclerView.adapter = adapter

    }

    private fun getDataFromFirebase() {
        GlobalScope.launch(Dispatchers.Main) {
            try {
                val snapshot = db.collection("events").get().await()

                if (snapshot != null && !snapshot.isEmpty) {
//                    liveEventList.clear()
                    traverseEvents(db.collection("events"))
//                    recyclerView.adapter = EventNameAdapter2(liveEventList, this@EventsActivity)
                    Log.e("map",eventMap.toString())
                    Log.e("map",liveEventList.toString())
                    Log.e("map",pastEventList.toString())
                }
            } catch (e: Exception) {
                Toast.makeText(this@EventsActivity, "Error fetching data: ${e.message}", Toast.LENGTH_SHORT).show()
                Log.e("EventPage", "Error fetching data", e)
            }
        }
    }

    override fun onItemClick(event: EventData2) {
        val intent = Intent(this, UserActivity::class.java)
        intent.putExtra("eventName", event.eventName)
        intent.putExtra("eventDesc", event.eventDesc)
        intent.putExtra("endDate", event.endDate)
        intent.putStringArrayListExtra("eventNameList", ArrayList(event.eventNameList))
        getDataFromFirebase1(event, intent, this@EventsActivity)
//        startActivity(intent)
    }




    private fun getDataFromFirebase1(event: EventData2, intent: Intent, callback: DetailDataCallback) {
        GlobalScope.launch(Dispatchers.Main) {
            db = FirebaseFirestore.getInstance()

            val docRef = getDocRef(event)
            if (docRef != null) {
                docRef.reference.collection("details")
                    .addSnapshotListener { snapshot, exception ->
                        if (exception != null) {
                            Toast.makeText(this@EventsActivity, exception.toString(), Toast.LENGTH_SHORT)
                                .show()
                        }

                        if (snapshot != null && !snapshot.isEmpty) {
                            detailList.clear()
                            for (data in snapshot.documents) {
                                val detail: DetailData? = data.toObject(DetailData::class.java)
                                if (detail != null) {
                                    detailList.add(detail)
                                }
                            }
                            callback.onDataReceived(detailList, intent)
//                            Log.e("check3", detailList.toString())
                        }
                    }
            }
        }
    }

    override fun onDataReceived(detailList: List<DetailData>, intent: Intent) {
        val detailListJson = Gson().toJson(detailList)

        // Put the JSON string into the intent
        intent.putExtra("detailList", detailListJson)
        startActivity(intent)
    }

    suspend fun getDocRef(event: EventData2): DocumentSnapshot? {
        val eventListTemp = event.eventNameList?.let { list ->
            ArrayList<String>().apply {
                list.forEach { element ->
                    add(element)
                }
            }
        }
        eventListTemp?.add(event.eventName.toString())
        Log.e("check1", eventListTemp.toString())

        var eventsCollectionRef = db.collection("events").get().await()
//
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
                    val eventDesc = doc.getString("eventDesc") ?: "no description"
                    val endDate = doc.getString("endDate") ?: "no end data"

                    // Create an EventData2 object
                    val eventData = EventData2(eventName = eventName, eventDesc = eventDesc, endDate = endDate, eventNameList = eventNameList.toMutableList())

                    val islive = doc.getString("status") ?: ""
                    // Add the eventData to the eventList
//                    if ()
//                        liveEventList.add(eventData)
                    if (islive == "live" && (!liveEventList.any { it.eventName == eventName })) {
                        liveEventList.add(eventData)
                        recyclerView.adapter = EventNameAdapter2(liveEventList, this)
                    }
                    else
                        pastEventList.add(eventData)
                }
                else {
                    eventNameList.add(eventName)
                    traverseEvents(doc.reference.collection("events"))
                    eventNameList.removeAt(eventNameList.size - 1)
                }

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