package com.example.taskflow

import android.os.Bundle
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
        val event = intent.getStringExtra("event")

        bottomUserNavigationView = findViewById(R.id.bottom_navigation_user)

        bottomUserNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.bottom_menu_info -> {
                    replaceFragment(UserHomeFragment(event))
                    true
                }
                R.id.bottom_menu_tasks -> {
                    replaceFragment(UserTasksFragment(event))
                    true
                }
                R.id.bottom_menu_chat -> {
                    replaceFragment(UserChatFragment(event))
                    true
                }
                R.id.bottom_menu_profile -> {
                    replaceFragment(UserProfileFragment())
                    true
                }
                else -> false
            }
        }
        replaceFragment(UserHomeFragment(event))

    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.user_frame_cotainer, fragment)
            .commit()
    }
}