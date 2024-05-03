package com.example.taskflow.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.taskflow.AddHeadActivity
import com.example.taskflow.databinding.FragmentUserProfileBinding
import com.google.firebase.auth.FirebaseAuth

class UserProfileFragment(private val eventName: String?, private val eventList:  ArrayList<String>?) : Fragment() {
    lateinit var binding: FragmentUserProfileBinding
    lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init(view)

        registerEvents()
    }

    private fun init(view: View){
        auth = FirebaseAuth.getInstance()
    }

    private  fun registerEvents(){
        binding.addHeads.setOnClickListener{
            eventList?.add(eventName.toString())
            val intent = Intent(requireContext(), AddHeadActivity::class.java)
            intent.putStringArrayListExtra("eventList", eventList)
            startActivity(intent)
        }
    }

}