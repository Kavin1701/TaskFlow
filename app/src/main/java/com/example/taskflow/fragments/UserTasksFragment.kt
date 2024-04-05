package com.example.taskflow.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.taskflow.R
import com.example.taskflow.databinding.FragmentUserHomeBinding
import com.example.taskflow.databinding.FragmentUserTasksBinding

class UserTasksFragment(private val event: String?) : Fragment() {
    lateinit var binding: FragmentUserTasksBinding

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
    }

    private fun init(view: View){
        binding.eventName.text = event
    }

}