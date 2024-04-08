package com.example.taskflow.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.taskflow.R
import com.example.taskflow.databinding.FragmentAddEventBinding
import com.google.android.material.textfield.TextInputEditText


class AddEventFragment : DialogFragment() {
    private lateinit var binding: FragmentAddEventBinding
    private lateinit var listener: DialogNextBtnClickListener

    fun setListener(listener: DialogNextBtnClickListener){
        this.listener = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddEventBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        registerEvents()
    }

    private fun registerEvents(){
        binding.eventNextBtn.setOnClickListener{
            val eventName = binding.eventEt.text.toString()
            if (eventName.isNotEmpty()){
                listener.onSaveEvent(eventName, binding.eventEt)
            }
            else{
                Toast.makeText(context, "Please type some event", Toast.LENGTH_SHORT).show()
            }
        }

        binding.eventClose.setOnClickListener{
            dismiss()
        }
    }

    interface DialogNextBtnClickListener{
        fun onSaveEvent(eventName: String, eventEt: TextInputEditText)
    }

}