package com.example.taskflow

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.taskflow.fragments.UserChatFragment
import com.example.taskflow.fragments.UserHomeFragment
import com.example.taskflow.fragments.UserProfileFragment
import com.example.taskflow.fragments.UserTasksFragment
import com.example.taskflow.utils.DetailData
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.common.reflect.TypeToken
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson

class UserActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    private lateinit var bottomUserNavigationView: BottomNavigationView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()

        setContentView(R.layout.activity_user)

        val eventName = intent.getStringExtra("eventName")
        if (eventName == null){
            val intent = Intent(this, EventsActivity::class.java)
            startActivity(intent)
        }
        val eventDesc = intent.getStringExtra("eventDesc")
        val endDate = intent.getStringExtra("endDate")
        val eventNameList = intent.getStringArrayListExtra("eventNameList")
        val detailListJson = intent.getStringExtra("detailList")
        val detailList: List<DetailData> = Gson().fromJson(detailListJson, object : TypeToken<List<DetailData>>() {}.type)



        bottomUserNavigationView = findViewById(R.id.bottom_navigation_user)

        bottomUserNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.bottom_menu_info -> {
                    replaceFragment(UserHomeFragment(eventName, eventDesc, endDate, eventNameList, detailList))
                    true
                }
                R.id.bottom_menu_tasks -> {
                    replaceFragment(UserTasksFragment(eventName, eventNameList))
                    true
                }
                R.id.bottom_menu_chat -> {
                    replaceFragment(UserChatFragment(eventName, eventNameList))
                    true
                }
                R.id.bottom_menu_profile -> {
                    replaceFragment(UserProfileFragment(eventName, eventNameList))
                    true
                }
                else -> false
            }
        }
        replaceFragment(UserHomeFragment(eventName, eventDesc, endDate, eventNameList, detailList))
registerEvent()
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.user_frame_cotainer, fragment)
            .commit()
    }

    private fun registerEvent(){
        val logoutButton = findViewById<Button>(R.id.logoutBt)
        logoutButton.setOnClickListener{
            auth.signOut()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}