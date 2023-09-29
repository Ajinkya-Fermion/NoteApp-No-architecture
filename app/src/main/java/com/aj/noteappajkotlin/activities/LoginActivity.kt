package com.aj.noteappajkotlin.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.aj.noteappajkotlin.Constants
import com.aj.noteappajkotlin.NoteDatabase
import com.aj.noteappajkotlin.R
import com.aj.noteappajkotlin.SharePrefUtils.Companion.saveUserId
import com.aj.noteappajkotlin.Util
import com.aj.noteappajkotlin.Util.Companion.toastIt
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    //We are making the userId Global as we require it's value in another function
    var userId : Long = -1 //Default value setting as '-1' for non registered user
    private lateinit var edtxtPwd : EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val edtxtMobile : EditText = findViewById(R.id.edtxt_mobile_login)
        edtxtPwd = findViewById(R.id.edtxt_pwd)
        val loginBtn : Button = findViewById(R.id.btn_login)
        edtxtPwd.visibility = View.GONE //Setting intially password field invisible for fresh users.

        loginBtn.setOnClickListener{
            //Reading entered value from inputField
//            val mobileNo : Int = getInteger(edtxtMobile.text.toString()) as Int //casting
            val mobileNo : String = edtxtMobile.text.toString()
            val pwd : String = edtxtPwd.text.toString()
            loginUser(mobileNo,pwd)
        }
    }

    private fun loginUser(mobileNo : String, password : String){

//        var userId : Long = -1 //Default value setting as '-1' for non registered user

        //Add validations for inputfields
        if(mobileNo.isEmpty()){
            toastIt("Mobile number cannot be empty",this)
        }
        else if(mobileNo.length < 10){
            toastIt("Mobile number must be 10 digit",this)
        }
        else if(!Util.isValidMobileNumber(mobileNo)){
            toastIt("Invalid Mobile number series",this)
        }
        //This condition will check whether mobileNo exist in DB
        else if(!edtxtPwd.isVisible){
            //Check whether user exist in DB and user has completed registration
            GlobalScope.launch (Dispatchers.Main) {
                userId = checkUserExistAndRegistered(mobileNo.toLong())
                if(userId>=0){
                    edtxtPwd.visibility = View.VISIBLE
                    toastIt("User Exist! Please enter password",this@LoginActivity)
                }
                else{
                    goToRegister(mobileNo)
                }
            }
        }
        else if(edtxtPwd.isVisible){
            if(password.isEmpty()){
                toastIt("Password cannot be empty",this)
            }
            else if(password.length > 8){
                toastIt("Password must be of 8 characters length",this)
            }
            else {
                //We are passing 'userId' here instead of 'mobileNo'
                validateLoginCredentials(userId,password)
            }
        }
    }

    private suspend fun checkUserExistAndRegistered(mobileNo : Long):Long{
        val db = NoteDatabase.getDatabase(this) //This is singleton method calling
        val userDao = db.userDao()
        //Check whether mobile number exist and regStatus is true(1)
        val userId: Long = userDao.getUserByMobileNumber(mobileNo,true) ?: -1L
        return if (userId != -1L) {
            // Mobile number exists
            println("Mobile number exists: $userId")
            userId
        } else {
            // Mobile number does not exist
            println("Mobile number does not exist: $userId")
            -1L //-1 means no user exist
        }
    }

    private fun goToRegister(mobileNo : String){
        val myIntent = Intent(this, RegisterActivity::class.java)
        myIntent.putExtra("mobileNo", mobileNo) //Optional parameters
        this.startActivity(myIntent)
    }

    private fun goToHome(userId: String){
        //Save the "userId" in SharedPreference
        saveUserId(userId,this)

        val myIntent = Intent(this, HomeActivity::class.java)
        this.startActivity(myIntent)
        finish()
//        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }

    private fun validateLoginCredentials(userId : Long, password : String){
        GlobalScope.launch (Dispatchers.Main) {
            if(checkPasswordMatchingForUser(userId,password)){
                Constants.userID = userId
                goToHome(userId.toString())
            }
            else{
                toastIt("Incorrect Password",this@LoginActivity)
            }
        }
    }

    private suspend fun checkPasswordMatchingForUser(userId : Long, password : String):Boolean{
        val db = NoteDatabase.getDatabase(this) //This is singleton method calling
        val userDao = db.userDao()
        //Check whether mobile number exist and regStatus is true(1)
        val returnedUserId : Long = userDao.getUserByPassword(userId,password) ?: -1L
        return returnedUserId > -1L //This returns boolean status
    }
}