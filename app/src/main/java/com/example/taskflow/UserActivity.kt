package com.example.taskflow

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.taskflow.fragments.UserChatFragment
import com.example.taskflow.fragments.UserHomeFragment
import com.example.taskflow.fragments.UserProfileFragment
import com.example.taskflow.fragments.UserTasksFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class UserActivity : AppCompatActivity() {

    private lateinit var bottomUserNavigationView: BottomNavigationView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_user)
        Log.e("route", "UserActivity")
        val eventName = intent.getStringExtra("eventName")
        if (eventName == null){
            val intent = Intent(this, EventsActivity::class.java)
            startActivity(intent)
        }
        val eventDesc = intent.getStringExtra("eventDesc")
        val endDate = intent.getStringExtra("endDate")
        val eventNameList = intent.getStringArrayListExtra("eventNameList")


        bottomUserNavigationView = findViewById(R.id.bottom_navigation_user)

        bottomUserNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.bottom_menu_info -> {
                    replaceFragment(UserHomeFragment(eventName))
                    true
                }
                R.id.bottom_menu_tasks -> {
                    replaceFragment(UserTasksFragment(eventName, eventNameList))
                    true
                }
                R.id.bottom_menu_chat -> {
                    replaceFragment(UserChatFragment(eventName))
                    true
                }
                R.id.bottom_menu_profile -> {
                    replaceFragment(UserProfileFragment(eventName, eventNameList))
                    true
                }
                else -> false
            }
        }
        replaceFragment(UserHomeFragment(eventName))

    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.user_frame_cotainer, fragment)
            .commit()
    }
}