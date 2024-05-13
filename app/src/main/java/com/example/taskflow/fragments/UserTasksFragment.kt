package com.example.taskflow.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.taskflow.DetailDataCallback
import com.example.taskflow.MainActivity
import com.example.taskflow.R
import com.example.taskflow.UserActivity
import com.example.taskflow.databinding.FragmentUserHomeBinding
import com.example.taskflow.databinding.FragmentUserTasksBinding
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
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
interface DetailDataCallback {
    fun onDataReceived(detailList: List<DetailData>, intent: Intent)
}
class UserTasksFragment(private val event: String?,
                        private val eventNameList:  ArrayList<String>?)
    : Fragment(), AddEventFragment.DialogNextBtnClickListener,
        EventNameAdapter.OnItemClickListener, DetailDataCallback{
    lateinit var binding: FragmentUserTasksBinding
    private var db = Firebase.firestore
    private lateinit var auth: FirebaseAuth
    private lateinit var popUpFragment: AddEventFragment
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: EventNameAdapter
    private lateinit var eventList: MutableList<EventData>
    private var detailList: MutableList<DetailData> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserTasksBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(view)
        getDataFromFirebase()
        registerEvents()
    }

    private fun init(view: View){
        binding.eventName.text = event
        auth = FirebaseAuth.getInstance()
        binding.recyclerview.setHasFixedSize(true)
        binding.recyclerview.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        eventList = mutableListOf()
        adapter = EventNameAdapter(eventList, this)
        recyclerView = binding.recyclerview
        recyclerView.adapter = adapter
    }

    fun registerEvents(){

        binding.addEventBt.setOnClickListener{
            popUpFragment = AddEventFragment()
            popUpFragment.setListener(this)
            popUpFragment.show(childFragmentManager, "AddEventFragment")
        }
    }

    override fun onSaveEvent(eventName: String, eventDesc: String, endDate: String, eventEt: TextInputEditText, eventDescEt: TextInputEditText) {

        GlobalScope.launch(Dispatchers.Main) {
            val docRef = getDocRef()
            val event = hashMapOf(
                "eventName" to eventName,
                "eventDesc" to eventDesc,
                "endDate" to endDate,
                "status" to "live",
            )

            if (docRef != null) {
                docRef.reference.collection("events")
                    .add(event)
                    .addOnSuccessListener { documentReference ->
                        Toast.makeText(requireContext(), "Event added successfully", Toast.LENGTH_SHORT).show()
                        eventEt.text = null
                        eventDescEt.text = null

                        popUpFragment.dismiss()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(requireContext(), "Failed to add event: ${e.message}", Toast.LENGTH_SHORT)
                            .show()
                    }
            }
        }
    }

    private fun getDataFromFirebase() {
        GlobalScope.launch(Dispatchers.Main) {
            db = FirebaseFirestore.getInstance()

            val docRef = getDocRef()
//            val querySnapshot = docRef!!.reference.collection("events").get().await()
            if (docRef != null) {
                docRef.reference.collection("events")
                    .addSnapshotListener { snapshot, exception ->
                        if (exception != null) {
                            Toast.makeText(requireContext(), exception.toString(), Toast.LENGTH_SHORT)
                                .show()
                        }

                        if (snapshot != null && !snapshot.isEmpty) {
                            eventList.clear() // Clear existing data before adding new data
                            for (data in snapshot.documents) {
                                val event: EventData? = data.toObject(EventData::class.java)
                                if (event != null) {
                                    eventList.add(event)
                                }
                            }
                            recyclerView.adapter = EventNameAdapter(eventList, this@UserTasksFragment)
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

    override fun onItemClick(eventName: String, eventDesc: String, endDate: String) {
        val intent = Intent(requireContext(), UserActivity::class.java)
        intent.putExtra("eventName", eventName)
        intent.putExtra("eventDesc", eventDesc)
        intent.putExtra("endDate", endDate)
        val eventListTemp = eventNameList?.let { list ->
            ArrayList<String>().apply {
                list.forEach { element ->
                    add(element)
                }
            }

        }
        if (event != null) {
            eventListTemp?.add(event)
        }

        intent.putStringArrayListExtra("eventNameList", eventListTemp)
//        Log.e("t1", eventName)
//        Log.e("t1", eventDesc)
//        Log.e("t1", endDate)
//        event?.let { Log.e("t1", it) }
//        Log.e("t1", eventListTemp.toString())
        getDataFromFirebase1(EventData2(eventName, eventDesc, endDate, eventListTemp), intent, this)
    }

    private fun getDataFromFirebase1(event: EventData2, intent: Intent, callback: DetailDataCallback) {
        GlobalScope.launch(Dispatchers.Main) {
            db = FirebaseFirestore.getInstance()

            val docRef = getDocRef1(event)
            if (docRef != null) {
                docRef.reference.collection("details")
                    .addSnapshotListener { snapshot, exception ->
                        if (exception != null) {
                            Toast.makeText(requireContext(), exception.toString(), Toast.LENGTH_SHORT)
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
        }
    }

    override fun onDataReceived(detailList: List<DetailData>, intent: Intent) {
        val detailListJson = Gson().toJson(detailList)


        // Put the JSON string into the intent
        intent.putExtra("detailList", detailListJson)
        startActivity(intent)
    }

    suspend fun getDocRef1(event: EventData2): DocumentSnapshot? {
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