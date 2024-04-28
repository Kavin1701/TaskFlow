package com.example.taskflow.fragments

import EventPage
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.taskflow.AdminActivity
import com.example.taskflow.EventsActivity

import com.example.taskflow.databinding.FragmentLoginBinding
import com.google.firebase.auth.FirebaseAuth


class LoginFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var navControl: NavController
    private lateinit var binding: FragmentLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
        Log.e("route", "LoginActivity")
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

    private fun registerEvents(){

        binding.loginBt.setOnClickListener{
            val email = binding.emailEt.text.toString().trim()
            val pass = binding.passEt.text.toString().trim()

            if(email.isNotEmpty() && pass.isNotEmpty()){

                binding.progressBar.visibility = View.VISIBLE
                auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener {
                    if (it.isSuccessful) {
                        Toast.makeText(context, "Login Successfully", Toast.LENGTH_SHORT).show()
                        if (email != null && email.equals("admin@gmail.com")) {
                            val intent = Intent(requireContext(), AdminActivity::class.java)
                            startActivity(intent)
                        } else {
                            val intent = Intent(requireContext(), EventsActivity::class.java)
                            startActivity(intent)
                        }
                    } else {
                        Toast.makeText(context, it.exception?.message, Toast.LENGTH_SHORT).show()

                    }
                    binding.progressBar.visibility = View.GONE
                }
            }
            else{
                Toast.makeText(context, "Empty fields are not allowed", Toast.LENGTH_SHORT).show()
            }
        }
    }
}