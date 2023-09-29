package com.aj.noteappajkotlin.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.aj.noteappajkotlin.Constants
import com.aj.noteappajkotlin.R
import com.aj.noteappajkotlin.SharePrefUtils.Companion.getUserId


class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        //Below code for deprecated handler usage
        //https://stackoverflow.com/questions/61023968/what-do-i-use-now-that-handler-is-deprecated
        Handler(Looper.getMainLooper()).postDelayed({
            checkUserIdExist()
        }, 2000)
    }

    private fun goToHome(){
        val myIntent = Intent(this, HomeActivity::class.java)
        startActivity(myIntent)
        finish()
//        overridePendingTransition(R.anim.slide_in_right,
//            R.anim.slide_out_left)
    }

    private fun goToLogin(){
        val myIntent = Intent(this, LoginActivity::class.java)
        startActivity(myIntent)
        finish()
//        overridePendingTransition(R.anim.slide_in_right,
//            R.anim.slide_out_left)
    }

    //We check whether userId exist ot not.
    private fun checkUserIdExist() {
        val userId : String = getUserId(this)

        if(userId.isEmpty()){
            goToLogin()
        }
        //UserId exist so go to home
        else {
            Constants.userID = userId.toLong()
            goToHome()
        }
    }
}