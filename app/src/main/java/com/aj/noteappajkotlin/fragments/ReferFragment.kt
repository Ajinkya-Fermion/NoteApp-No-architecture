package com.aj.noteappajkotlin.fragments

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.aj.noteappajkotlin.R
import com.aj.noteappajkotlin.Util.Companion.toastIt


class ReferFragment : Fragment() ,View.OnClickListener{

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.fragment_refer, container, false)
        val whatsappBtn = view.findViewById<Button>(R.id.btn_whatsapp)
        val msgBtn = view.findViewById<Button>(R.id.btn_msg)
        val emailBtn = view.findViewById<Button>(R.id.btn_email)
        whatsappBtn.setOnClickListener(this)
        msgBtn.setOnClickListener(this)
        emailBtn.setOnClickListener(this)

        return view
    }

    private fun onWhatsAppShareClicked(context: Context) {
        val mobileNumber = "9665494024"
        val url =
            "https://api.whatsapp.com/send?phone=${mobileNumber}&text=You%20can%20now%20send%20me%20audio%20and%20video%20messages%20on%20the%20app%20-%20Chirp.%20%0A%0Ahttps%3A//bit.ly/chirp_android"

        val intent = Intent(Intent.ACTION_VIEW).apply {
            this.data = Uri.parse(url)
            this.`package` = "com.whatsapp"
        }

        try {
            context.startActivity(intent)
        } catch (ex : ActivityNotFoundException){
            toastIt("No Whatapp App found",context)
            //whatsapp not installled
        }
    }

//    fun isPackageInstalled(context: Context, packageName: String): Boolean {
//        return try {
//            context.packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
//            true
//        } catch (ex: Exception) {
//            false
//        }
//    }

    private fun shareTextMessageOnly(context: Context) {

        val sendIntent = Intent(Intent.ACTION_VIEW)
        sendIntent.putExtra("sms_body", "Hey this is Aj")
        sendIntent.type = "vnd.android-dir/mms-sms"

        try {
            startActivity(sendIntent)
        }
        catch (ex : ActivityNotFoundException){
            //whatsapp not installled
            toastIt("No Message App found",context)
        }
    }


    private fun sendEmail(context: Context) {
        Log.i("Send email", "")
        val to = arrayOf("ajinkyam@fermion.in","ajinkyamandavkar92@gmail.com")
        val cc = arrayOf("")
        val emailIntent = Intent(Intent.ACTION_SEND)
        emailIntent.setDataAndType(Uri.parse("mailto:"),"text/plain")
        emailIntent.putExtra(Intent.EXTRA_EMAIL, to)
        emailIntent.putExtra(Intent.EXTRA_CC, cc)
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Note App Demo")
        emailIntent.putExtra(Intent.EXTRA_TEXT, "This is the test email from NoteApp")
        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."))
            Log.i("Finished sending email...", "")
        } catch (ex: ActivityNotFoundException) {
            Toast.makeText(
                context,
                "There is no email client installed.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    //If we implement interface approach as we did in legacy android java
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_whatsapp -> {
                onWhatsAppShareClicked(v.context)
            }
            R.id.btn_msg -> {
                shareTextMessageOnly(v.context)
            }
            R.id.btn_email -> {
                sendEmail(v.context)
            }
        }
    }
}