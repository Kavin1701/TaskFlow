package com.example.taskflow.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.taskflow.EventsActivity
import com.example.taskflow.R
import com.example.taskflow.databinding.FragmentLoginBinding
import com.example.taskflow.databinding.FragmentUserHomeBinding
import com.example.taskflow.utils.DetailData
import com.example.taskflow.utils.DetailDataAdapter
import com.example.taskflow.utils.EventData
import com.example.taskflow.utils.EventNameAdapter
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class UserHomeFragment(private val event: String?,
                       private val eventDesc: String?,
                       private val endDate: String?,
                       private val eventNameList:  ArrayList<String>?,
                       private var detailList1: List<DetailData>) : Fragment(), AddDetailFragment.DialogNextBtnClickListener {
    lateinit var binding: FragmentUserHomeBinding
    private lateinit var popUpFragment: AddDetailFragment
    private var db = Firebase.firestore
    private lateinit var detailList: MutableList<DetailData>
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: DetailDataAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserHomeBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        init(view)
        registerEvents()
//
    }

    private fun init(view: View){
        binding.eventName.text = event
        binding.eventDesc.text = eventDesc
        binding.endDate.text = endDate

        binding.recyclerview.setHasFixedSize(true)
        binding.recyclerview.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        detailList = detailList1.toMutableList()
        adapter = DetailDataAdapter(detailList)
        recyclerView = binding.recyclerview
        recyclerView.adapter = adapter
    }

    fun registerEvents(){

        binding.addEventBt.setOnClickListener{
            popUpFragment = AddDetailFragment()
            popUpFragment.setListener(this)
            popUpFragment.show(childFragmentManager, "AddDetailFragment")
        }
    }

    override fun onSaveEvent(title: String, desc: String, eventEt: TextInputEditText, eventDescEt: TextInputEditText) {

        GlobalScope.launch(Dispatchers.Main) {
            val docRef = getDocRef()
            val detail = hashMapOf(
                "title" to title,
                "desc" to desc,
            )

            if (docRef != null) {
                docRef.reference.collection("details")
                    .add(detail)
                    .addOnSuccessListener { documentReference ->
                        Toast.makeText(requireContext(), "Detail added successfully", Toast.LENGTH_SHORT).show()
                        eventEt.text = null
                        eventDescEt.text = null

                        popUpFragment.dismiss()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(requireContext(), "Failed to add Detail: ${e.message}", Toast.LENGTH_SHORT)
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
//Log.e("t", docRef.toString())
                docRef.reference.collection("details")
                    .addSnapshotListener { snapshot, exception ->
                        if (exception != null) {
                            Toast.makeText(requireContext(), exception.toString(), Toast.LENGTH_SHORT)
                                .show()
                        }

                        if (snapshot != null && !snapshot.isEmpty) {
                            detailList.clear() // Clear existing data before adding new data
                            for (data in snapshot.documents) {
                                val detail: DetailData? = data.toObject(DetailData::class.java)
                                if (detail != null) {
                                    detailList.add(detail)
                                }
                            }
                            recyclerView.adapter = DetailDataAdapter(detailList)
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