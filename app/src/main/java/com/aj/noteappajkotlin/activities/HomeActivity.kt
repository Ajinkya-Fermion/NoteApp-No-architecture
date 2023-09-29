package com.aj.noteappajkotlin.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.aj.noteappajkotlin.Constants
import com.aj.noteappajkotlin.R
import com.aj.noteappajkotlin.SharePrefUtils.Companion.saveUserId
import com.aj.noteappajkotlin.fragments.AboutUsFragment
import com.aj.noteappajkotlin.fragments.HomeFragment
import com.aj.noteappajkotlin.fragments.ReferFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeActivity : AppCompatActivity(), HomeFragment.OnLogOutBtnListener {

    private lateinit var bottomNavigation : BottomNavigationView
    private var doubleBackToExitPressedOnce = false

        override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        bottomNavigation = findViewById(R.id.bnv_view)

        val toolbar = supportActionBar

        loadFragment(HomeFragment())

        bottomNavigation.setOnItemSelectedListener { item ->
            val fragment: Fragment
            when (item.itemId) {
                R.id.nav_home -> {
                    toolbar?.title = "Home"
                    fragment = HomeFragment()
                    loadFragment(fragment)
                    true
                }
                R.id.nav_refer -> {
                    toolbar?.title = "Refer"
                    fragment = ReferFragment()
                    loadFragment(fragment)
                    true

                }
                R.id.nav_aboutUs -> {
                    toolbar?.title = "AboutUs"
                    fragment = AboutUsFragment()
                    loadFragment(fragment)
                    true

                }
                else -> false
            }
        }

        onBackPressedDispatcher.addCallback(this, object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                onBackPress() //We need to terminate Activity manually
            }
        })
    }

    private fun loadFragment(fragment: Fragment) {
        // load fragment
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    private fun logOutUser(){
        //Delete the "userId" in SharedPreference
        Constants.userID = 0L //Resetting value.
        saveUserId("",this)
        finish()
        goToLogin()
    }

    private fun goToLogin(){
        val myIntent = Intent(this, LoginActivity::class.java)
        this.startActivity(myIntent)
    }

    override fun onLogOutClicked() {
        logOutUser()
    }

    fun onBackPress() {
        if (doubleBackToExitPressedOnce) {
            finish()
            // on below line we are exiting our activity
//            System.exit(2) //Java code....This restarts the activity from SplashActivity
//            exitProcess(1) //Kotlin code....This restarts the activity from SplashActivity
            //https://proandroiddev.com/a-cautionary-tale-on-android-do-not-call-system-exit-5279e0d5dbe0
            finishAffinity() //Removes all activities in backstack
        }

        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show()

        Handler(Looper.getMainLooper()).postDelayed({
            doubleBackToExitPressedOnce = false
        }, 2000)// Delay of 2 seconds to allow for the second back press

    }
}