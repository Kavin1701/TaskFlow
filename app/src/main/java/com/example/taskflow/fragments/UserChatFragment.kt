package com.example.taskflow.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.taskflow.R
import com.example.taskflow.databinding.FragmentUserChatBinding
import com.example.taskflow.databinding.FragmentUserHomeBinding

class UserChatFragment(private val event: String?) : Fragment() {

    lateinit var binding: FragmentUserChatBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentUserChatBinding.inflate(inflater, container, false)
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