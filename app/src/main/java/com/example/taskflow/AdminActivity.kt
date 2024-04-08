package com.example.taskflow

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.taskflow.databinding.ActivityAdminBinding
import com.example.taskflow.databinding.FragmentAddEventBinding
import com.example.taskflow.fragments.AddEventFragment
import com.example.taskflow.utils.EventData
import com.example.taskflow.utils.EventNameAdapter
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject

class AdminActivity : AppCompatActivity(), AddEventFragment.DialogNextBtnClickListener {

    private lateinit var binding: ActivityAdminBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var popUpFragment: AddEventFragment
    private lateinit var recyclerView: RecyclerView
    private lateinit var eventList: ArrayList<EventData>
    private var db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
        registerEvents()

        recyclerView = binding.recyclerview
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)


        eventList = arrayListOf()

        db = FirebaseFirestore.getInstance()
        db.collection("events").get().addOnSuccessListener {
            if (!it.isEmpty){
                for (data in it.documents){
                    val event: EventData? = data.toObject(EventData::class.java)
                    if (event != null) {
                        eventList.add(event)
                    }
                    recyclerView.adapter = EventNameAdapter(eventList)
                }
            }
        }
            .addOnFailureListener{
                Toast.makeText(this, it.toString(), Toast.LENGTH_SHORT).show()
            }
    }
    fun init(){
        auth = FirebaseAuth.getInstance()
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

    override fun onSaveEvent(eventName: String, eventEt: TextInputEditText) {
        val db = FirebaseFirestore.getInstance()

        // Query to check if an event with the same name exists
        db.collection("events")
            .whereEqualTo("eventName", eventName)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    // If no event with the same name exists, add the event
                    val event = hashMapOf(
                        "eventName" to eventName
                        // Add other fields if needed
                    )

                    db.collection("events")
                        .add(event)
                        .addOnSuccessListener { documentReference ->
                            Toast.makeText(this, "Event added successfully", Toast.LENGTH_SHORT).show()
                            eventEt.text = null
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

}