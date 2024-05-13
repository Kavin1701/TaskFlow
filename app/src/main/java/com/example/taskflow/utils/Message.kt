package com.example.taskflow.utils

import java.text.SimpleDateFormat
import java.util.*

data class Message(
    var text: String = "",
    var sender: String = "",
    var timestamp: Long = 0
) {
    constructor() : this("", "", 0) // No-argument constructor

    fun getFormattedTimestamp(): String {
        if (timestamp > 0) {
            val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            return dateFormat.format(Date(timestamp))
        }
        return "" // or return some default value
    }

}


