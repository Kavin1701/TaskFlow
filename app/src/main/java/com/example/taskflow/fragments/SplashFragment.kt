package com.example.taskflow.fragments

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.taskflow.AdminActivity
import com.example.taskflow.R
import com.example.taskflow.UserActivity
import com.google.firebase.auth.FirebaseAuth

class SplashFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        navController = Navigation.findNavController(view)

        Handler(Looper.myLooper()!!).postDelayed(Runnable {
            if(auth.currentUser != null){
                var email = auth.currentUser!!.email
                if (email != null && email.equals("admin@gmail.com")) {
                    val intent = Intent(requireContext(), AdminActivity::class.java)
                    startActivity(intent)
                }
                else{
                    val intent = Intent(requireContext(), UserActivity::class.java)
                    startActivity(intent)
                }
            }
            else{
                navController.navigate(R.id.action_splashFragment_to_loginFragment)
            }

        }, 2000)
    }

}