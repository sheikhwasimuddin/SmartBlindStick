package com.example.smartblindstick

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavigation: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNavigation = findViewById(R.id.bottomNavigation)

        // Default fragment
        if (savedInstanceState == null) {
            loadFragment(DashboardFragment())
        }

        bottomNavigation.setOnItemSelectedListener {

            when (it.itemId) {

                R.id.nav_dashboard -> loadFragment(DashboardFragment())

                R.id.nav_map -> loadFragment(MapFragment())

                R.id.nav_profile -> loadFragment(ProfileFragment())

                R.id.nav_settings -> loadFragment(SettingsFragment())
            }

            true
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}