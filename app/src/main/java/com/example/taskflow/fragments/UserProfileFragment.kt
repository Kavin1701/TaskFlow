package com.example.taskflow.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.example.taskflow.MainActivity
import com.example.taskflow.R
import com.example.taskflow.databinding.FragmentLoginBinding
import com.example.taskflow.databinding.FragmentUserProfileBinding
import com.google.firebase.auth.FirebaseAuth


class UserProfileFragment : Fragment() {
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
//        registerEvents()
    }

    private fun init(view: View){
        auth = FirebaseAuth.getInstance()
    }

//    private fun registerEvents() {
//        binding.logoutBt.setOnClickListener{
//            auth.signOut()
//
//            val intent = Intent(requireContext(), MainActivity::class.java)
//            startActivity(intent)
//        }
//    }
}