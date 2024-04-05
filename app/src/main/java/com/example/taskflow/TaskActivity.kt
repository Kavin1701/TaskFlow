package com.example.taskflow
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.taskflow.databinding.ActivityTaskBinding

class TaskActivity : AppCompatActivity() {
    lateinit var binding: ActivityTaskBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.thiranBt.setOnClickListener {
            val intent = Intent(this, UserActivity::class.java)
            intent.putExtra("event", binding.thiranBt.text.toString())
            startActivity(intent)
        }
        binding.womenBt.setOnClickListener {
            val intent = Intent(this, UserActivity::class.java)
            intent.putExtra("event", binding.womenBt.text.toString())
            startActivity(intent)
        }
        binding.placeBt.setOnClickListener {
            val intent = Intent(this, UserActivity::class.java)
            intent.putExtra("event", binding.placeBt.text.toString())
            startActivity(intent)
        }
    }
}