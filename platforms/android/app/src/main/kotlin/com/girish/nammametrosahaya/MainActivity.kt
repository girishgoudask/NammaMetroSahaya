package com.girish.nammametrosahaya

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // 1. Initialize Splash Screen
        installSplashScreen()
        
        super.onCreate(savedInstanceState)
        
        // 2. Set the content view
        setContentView(R.layout.activity_main)

        // 3. Setup Navigation with error safety
        try {
            val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
            val navController = navHostFragment.navController
            val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
            bottomNav.setupWithNavController(navController)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
