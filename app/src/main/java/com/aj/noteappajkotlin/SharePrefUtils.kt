package com.aj.noteappajkotlin

import android.content.Context
import android.content.SharedPreferences

class SharePrefUtils {

    companion object{

        @Volatile
        private var SHARE_PREF_INSTANCE: SharedPreferences? = null

        //Singleton class created for Datastore access in different screens
        private fun getPreference(context: Context) : SharedPreferences {
            if (SHARE_PREF_INSTANCE == null) {
                synchronized(this) {
                    SHARE_PREF_INSTANCE = context.getSharedPreferences(Constants.USER_STORE
                        , Context.MODE_PRIVATE)
                }
            }
            return SHARE_PREF_INSTANCE!!
        }

        fun saveUserId(userId : String,context: Context){
            getPreference(context).edit().putString(Constants.USER_ID_KEY,userId).apply()
        }

        fun getUserId(context: Context) : String{
            return getPreference(context).getString(Constants.USER_ID_KEY,"").toString()
        }

    }
}