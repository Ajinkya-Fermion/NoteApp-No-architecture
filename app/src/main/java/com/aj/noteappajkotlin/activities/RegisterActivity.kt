package com.aj.noteappajkotlin.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.aj.noteappajkotlin.Constants
import com.aj.noteappajkotlin.NoteDatabase
import com.aj.noteappajkotlin.R
import com.aj.noteappajkotlin.SharePrefUtils.Companion.saveUserId
import com.aj.noteappajkotlin.User
import com.aj.noteappajkotlin.Util
import com.aj.noteappajkotlin.Util.Companion.toastIt
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val edtxtName : EditText = findViewById(R.id.edtxt_name)
        val edtxtEmail : EditText = findViewById(R.id.edtxt_emailId)
        val edtxtMobile : EditText = findViewById(R.id.edtxt_mobile)
        val edtxtPwd : EditText = findViewById(R.id.edtxt_pwd)
        val edtxtConfPwd : EditText = findViewById(R.id.edtxt_confirmPwd)
        val registerBtn : Button = findViewById(R.id.btn_register)

        //Reading data
        val bundle: Bundle? = intent.extras
        val mobileNoData: String? = bundle?.getString("mobileNo")
//        if (mobileNoData != null) {
//            toastIt(mobileNoData,this)
//        }

        edtxtMobile.isEnabled = false //Disabling editing of mobile number
        edtxtMobile.setText(mobileNoData) //Setting up the entered mobile No

        registerBtn.setOnClickListener{
            val name : String = edtxtName.text.toString()
            val email : String = edtxtEmail.text.toString()
            val mobileNo : String = edtxtMobile.text.toString()
            val pwd : String = edtxtPwd.text.toString()
            val confPwd : String = edtxtConfPwd.text.toString()

            registerUser(name, email, mobileNo, pwd, confPwd)
        }
    }

    private fun registerUser(name : String, email : String, mobileNo : String, pwd : String, confPwd : String){

        var userId : Long

        if(name.isEmpty()){
            toastIt("Name cannot be empty",this)
        }
        else if(!Util.isValidName(name)){
            toastIt("Name should contain alphabets only",this)
        }
        else if(email.isEmpty()){
            toastIt("Email cannot be empty",this)
        }
        else if(!Util.isValidEmail(email)){
            toastIt("Invalid email",this)
        }
        else if(pwd.isEmpty()){
            toastIt("Password cannot be empty",this)
        }
        else if(confPwd.isEmpty()){
            toastIt("Confirm Password cannot be empty",this)
        }
        else if(pwd != confPwd){
            toastIt("Confirm password mismatch!",this)
        }
        else{
            GlobalScope.launch (Dispatchers.Main) {
                //Here we will do pure Insert DB operation.
                userId = insertUserData(name,email,mobileNo,pwd)

                //Post Registration
                //Save the "userId" in SharedPreference
                Constants.userID = userId
                this@RegisterActivity.goToHome(userId.toString())
            }
        }

    }

    private fun goToHome(userId: String){
        saveUserId(userId, this)
        val myIntent = Intent(this, HomeActivity::class.java)
        this.startActivity(myIntent)
        finish() //Kill the Activity
    }

    private suspend fun insertUserData(name: String, email: String, mobileNo: String, pwd: String):Long{
        val db = NoteDatabase.getDatabase(this) //This is singleton method calling
        val userDao = db.userDao()
        //Rest all params mobileNo, name, pwd, email are entered as it is.
        val user = User(mobileNo.toLong(),name,pwd,email,true)
        val userId: Long = userDao.addUser(user) as Long
        return if (userId > 0) {
            // Mobile number exists
            println("UserId exists: $userId")
            userId
        } else {
            // Mobile number does not exist
            println("UserId does not exist: $userId")
            0
        }
    }


}