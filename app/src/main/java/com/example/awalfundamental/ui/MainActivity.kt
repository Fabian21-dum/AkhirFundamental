package com.example.awalfundamental.ui

import android.os.Bundle
import android.os.StrictMode
import androidx.activity.viewModels
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.awalfundamental.R
import com.example.awalfundamental.databinding.ActivityMainBinding
import com.example.awalfundamental.ui.fragments.FinishedFragment
import com.example.awalfundamental.ui.fragments.HomeFragment
import com.example.awalfundamental.ui.viewmodels.MainViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val mainViewModel: MainViewModel by viewModels()
    private fun loadFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.rvHome, fragment)
        transaction.commit()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bottomNavigation: BottomNavigationView = findViewById(R.id.nav_view)
        bottomNavigation.setOnItemSelectedListener { item ->
            val fragment = when (item.itemId) {
                R.id.navigation_finished -> FinishedFragment()
                R.id.navigation_home -> HomeFragment()
                else -> null
            }

            fragment?.let {
                loadFragment(it)
                true
            } ?: false
        }

        if (savedInstanceState == null) {
            loadFragment(HomeFragment())
        }

        StrictMode.setThreadPolicy(
            StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .build()
        )

    }
}