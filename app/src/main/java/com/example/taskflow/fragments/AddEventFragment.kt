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
import com.example.taskflow.databinding.FragmentAddEventBinding
import com.google.android.material.textfield.TextInputEditText
import java.util.Calendar


class AddEventFragment : DialogFragment() {
    private lateinit var binding: FragmentAddEventBinding
    private lateinit var listener: DialogNextBtnClickListener
    private lateinit var calendar: Calendar

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

        calendar = Calendar.getInstance()
        registerEvents()
    }

    private fun registerEvents(){
        binding.eventNextBtn.setOnClickListener{
            val eventName = binding.eventEt.text.toString()
            val eventDesc = binding.eventDescEt.text.toString()
            val selectedDate = binding.endDate.text.toString()
            if (eventName.isNotEmpty() and eventDesc.isNotEmpty()){
                listener.onSaveEvent(eventName, eventDesc, selectedDate, binding.eventEt, binding.eventDescEt)
            }
            else{
                Toast.makeText(context, "Please type some event and description", Toast.LENGTH_SHORT).show()
            }
        }

        binding.eventClose.setOnClickListener{
            dismiss()
        }

        binding.pickEndDate.setOnClickListener{
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = context?.let { it1 ->
                DatePickerDialog(
                    it1,
                    { _: DatePicker, selectedYear: Int, selectedMonth: Int, dayOfMonth: Int ->
                        val selectedDate = "$selectedYear-${selectedMonth + 1}-$dayOfMonth"
                        binding.endDate.text = selectedDate
                    },
                    year,
                    month,
                    dayOfMonth
                )
            }

            if (datePickerDialog != null) {
                datePickerDialog.show()
            }
        }
    }

    interface DialogNextBtnClickListener {
        fun onSaveEvent(eventName: String, eventDesc: String, endDate: String, eventEt: TextInputEditText, eventDescEt: TextInputEditText)
    }


}