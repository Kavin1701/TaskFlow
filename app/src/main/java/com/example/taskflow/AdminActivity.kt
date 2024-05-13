package com.example.taskflow

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.taskflow.databinding.ActivityAdminBinding
import com.example.taskflow.fragments.AddEventFragment
import com.example.taskflow.utils.DetailData
import com.example.taskflow.utils.EventData
import com.example.taskflow.utils.EventData2
import com.example.taskflow.utils.EventNameAdapter
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


class AdminActivity : AppCompatActivity(), AddEventFragment.DialogNextBtnClickListener, EventNameAdapter.OnItemClickListener, DetailDataCallback {

    private lateinit var binding: ActivityAdminBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var popUpFragment: AddEventFragment
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: EventNameAdapter
    private lateinit var eventList: MutableList<EventData>
    private var db = Firebase.firestore
    private var detailList: MutableList<DetailData> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
        getDataFromFirebase()
        registerEvents()
    }

    fun init(){
        auth = FirebaseAuth.getInstance()
        binding.recyclerview.setHasFixedSize(true)
        binding.recyclerview.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        eventList = mutableListOf()
        adapter = EventNameAdapter(eventList, this)
        recyclerView = binding.recyclerview
        recyclerView.adapter = adapter
    }

    private fun getDataFromFirebase() {
        db = FirebaseFirestore.getInstance()
        db.collection("events")
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    Toast.makeText(this, exception.toString(), Toast.LENGTH_SHORT).show()
                }

                if (snapshot != null && !snapshot.isEmpty) {
                    eventList.clear() // Clear existing data before adding new data
                    for (data in snapshot.documents) {
                        val event: EventData? = data.toObject(EventData::class.java)
                        if (event != null) {
                            eventList.add(event)
                        }
                    }
                    recyclerView.adapter = EventNameAdapter(eventList, this)
                }
            }
    }


    fun registerEvents(){
        binding.logoutBt.setOnClickListener{
            auth.signOut()

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        binding.addEventBt.setOnClickListener{
            popUpFragment = AddEventFragment()
            popUpFragment.setListener(this)
            popUpFragment.show(supportFragmentManager, "AddEventFragment")
        }
    }

    override fun onSaveEvent(eventName: String, eventDesc: String, endDate: String, eventEt: TextInputEditText, eventDescEt: TextInputEditText) {

        val db = FirebaseFirestore.getInstance()

        // Query to check if an event with the same name exists
        db.collection("events")
            .whereEqualTo("eventName", eventName)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    // If no event with the same name exists, add the event
                    val event = hashMapOf(
                        "eventName" to eventName,
                        "eventDesc" to eventDesc,
                        "endDate" to endDate,
                        "status" to "live",
                        // Add other fields if needed
                    )

                    db.collection("events")
                        .add(event)
                        .addOnSuccessListener { documentReference ->
                            Toast.makeText(this, "Event added successfully", Toast.LENGTH_SHORT).show()
                            eventEt.text = null
                            eventDescEt.text = null

                            popUpFragment.dismiss()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Failed to add event: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    // If an event with the same name exists, show a message to the user
                    Toast.makeText(this, "Event with this name already exists", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to check event existence: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onItemClick(eventName: String, eventDesc: String, endDate: String) {
//        Toast.makeText(this, "have not access", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, UserActivity::class.java)
        intent.putExtra("eventName", eventName)
        intent.putExtra("eventDesc", eventDesc)
        intent.putExtra("endDate", endDate)
        intent.putStringArrayListExtra("eventNameList", ArrayList())
        getDataFromFirebase1(EventData2(eventName, eventDesc, endDate, mutableListOf()), intent, this)
    }

    private fun getDataFromFirebase1(event: EventData2, intent: Intent, callback: DetailDataCallback) {
        GlobalScope.launch(Dispatchers.Main) {
            db = FirebaseFirestore.getInstance()

            val docRef = getDocRef(event)
            if (docRef != null) {
                docRef.reference.collection("details")
                    .addSnapshotListener { snapshot, exception ->
                        if (exception != null) {
                            Toast.makeText(this@AdminActivity, exception.toString(), Toast.LENGTH_SHORT)
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
                        else{
                            detailList.clear()
                            callback.onDataReceived(detailList, intent)
                        }
                    }
            }
            else{

            }
        }
    }

    override fun onDataReceived(detailList: List<DetailData>, intent: Intent) {
        val detailListJson = Gson().toJson(detailList)
        Log.e("hai","hai")

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

}