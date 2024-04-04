package com.example.taskflow.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.taskflow.R
import com.example.taskflow.databinding.FragmentAdminBinding
import com.example.taskflow.databinding.FragmentLoginBinding
import com.example.taskflow.databinding.FragmentUserBinding
import com.google.firebase.auth.FirebaseAuth


class UserFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var navControl: NavController
    private lateinit var binding: FragmentUserBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init(view)
        registerEvents()
    }

    private fun init(view: View){
        navControl = Navigation.findNavController(view)
        auth = FirebaseAuth.getInstance()
    }

    private fun registerEvents() {

        binding.logoutBt.setOnClickListener {
            auth.signOut()
            navControl.navigate(R.id.action_userFragment_to_splashFragment)
        }
    }
}