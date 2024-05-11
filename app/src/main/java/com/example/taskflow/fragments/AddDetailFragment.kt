package com.example.taskflow.fragments

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.taskflow.R
import com.example.taskflow.databinding.FragmentAddDetailBinding
import com.example.taskflow.databinding.FragmentAddEventBinding
import com.google.android.material.textfield.TextInputEditText
import java.util.Calendar

class AddDetailFragment : DialogFragment() {
    private lateinit var binding: FragmentAddDetailBinding
    private lateinit var listener: AddDetailFragment.DialogNextBtnClickListener

    fun setListener(listener: AddDetailFragment.DialogNextBtnClickListener){
        this.listener = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        registerEvents()
    }

    private fun registerEvents(){
        binding.eventNextBtn.setOnClickListener{
            val title = binding.eventEt.text.toString()
            val desc = binding.eventDescEt.text.toString()

            if (title.isNotEmpty() and desc.isNotEmpty()){
                listener.onSaveEvent(title, desc, binding.eventEt, binding.eventDescEt)
            }
            else{
                Toast.makeText(context, "Please type some title and description", Toast.LENGTH_SHORT).show()
            }
        }

        binding.eventClose.setOnClickListener{
            dismiss()
        }
    }

    interface DialogNextBtnClickListener {
        fun onSaveEvent(title: String, desc: String, eventEt: TextInputEditText, eventDescEt: TextInputEditText)
    }
}