package com.aj.noteappajkotlin.activities

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.aj.noteappajkotlin.Constants
import com.aj.noteappajkotlin.Note
import com.aj.noteappajkotlin.NoteDatabase
import com.aj.noteappajkotlin.R
import com.aj.noteappajkotlin.Util.Companion.toastIt
import com.aj.noteappajkotlin.models.NotesModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.Serializable


class NoteActivity : AppCompatActivity() {

    private lateinit var edtxtTitle : EditText
    private lateinit var edtxtNoteContent : EditText
    private lateinit var txvNoteTitle : TextView
    private var noteId: Long = 0L //This is used for storing 'noteId' for case of 'update'

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note)
        edtxtTitle = findViewById(R.id.edtxt_title)
        edtxtNoteContent = findViewById(R.id.edtxt_noteContent)
        txvNoteTitle = findViewById(R.id.txv_noteTitle)
        val btnSaveNote : Button = findViewById(R.id.btn_saveNote)

        //Reading noteAction to perform
        val bundle: Bundle? = intent.extras
        val noteAction: String? = bundle?.getString(Constants.NOTE_ACTION)

        if (noteAction != null) {
            autoFillNoteDataIfExist(noteAction)
        }

        btnSaveNote.setOnClickListener {
            val noteTitle : String = edtxtTitle.text.toString()
            val noteContent : String = edtxtNoteContent.text.toString()

            if(noteTitle.isEmpty()){
                toastIt("Title cannot be empty",this)
            }
            else if(noteContent.isEmpty()){
                toastIt("Note Content cannot be empty",this)
            }
            //action to add note
            else if(noteAction.equals(Constants.ADD_NOTE)){
                GlobalScope.launch (Dispatchers.Main) {
                    val noteId : Long = insertNote(noteTitle, noteContent, Constants.userID)

                    if(noteId > 0) {
                        //Intent to go back
                        val intent = Intent()
                        intent.putExtra(Constants.NOTE_RESULT, Constants.REFRESH_NOTES)
                        setResult(Activity.RESULT_OK, intent)
                        finish()
                    }
                }
            }
            //action to update note
            else if(noteAction.equals(Constants.UPDATE_NOTE)){
                GlobalScope.launch (Dispatchers.Main) {

                    val noOfRows : Int = updateNote(edtxtTitle.text.toString(),
                        edtxtNoteContent.text.toString(),noteId)

                    if(noOfRows > 0) {
                        //Intent to go back
                        val intent = Intent()
                        intent.putExtra(Constants.NOTE_RESULT, Constants.REFRESH_NOTES)
                        setResult(Activity.RESULT_OK, intent)
                        finish()
                    }
                }
            }
        }

        //Below method written here for hardware backpress handling
        //If OnBackPressedCallback set to 'false' then -> handleOnBackPressed() overriding don't work
        //If OnBackPressedCallback set to 'true' then -> handleOnBackPressed() overrides
        onBackPressedDispatcher.addCallback(this, object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val intent = Intent()
                intent.putExtra(Constants.NOTE_RESULT, "")
                setResult(Activity.RESULT_OK,intent)
                finish() //We need to terminate Activity manually
            }
        })
    }

    private fun autoFillNoteDataIfExist(noteAction:String){
        if(noteAction == Constants.UPDATE_NOTE) {
            txvNoteTitle.setText(R.string.update_note_title)
            val myIntent = intent
            val receivedNotesModelObj =
                myIntent.getSerializable("noteModelObj", NotesModel::class.java)
            noteId = receivedNotesModelObj.noteId
            edtxtTitle.setText(receivedNotesModelObj.noteTitle)
            edtxtNoteContent.setText(receivedNotesModelObj.noteContent)
        }
        else{
            txvNoteTitle.setText(R.string.add_note_title)
        }
    }

    private suspend fun insertNote(noteTitle: String, noteContent: String, userId: Long):Long{
        val db = NoteDatabase.getDatabase(this) //This is singleton method calling
        val noteDao = db.noteDao()
        //Rest all params mobileNo, name, pwd, email are entered as it is.
        val note = Note(noteTitle,noteContent,userId)
        val noteId: Long = noteDao.addNote(note) as Long
        return if (noteId > 0) {
            // Mobile number exists
            println("NoteId exists: $userId")
            noteId
        } else {
            // Mobile number does not exist
            println("NoteId does not exist: $userId")
            0
        }
    }

    private suspend fun updateNote(noteTitle: String, noteContent: String, noteId: Long):Int{
        val db = NoteDatabase.getDatabase(this) //This is singleton method calling
        val noteDao = db.noteDao()

        val noOfRows : Int = noteDao.updateNote(noteTitle,noteContent,noteId)
        return if (noOfRows > 0) {
            toastIt("Update successful:$noOfRows",this)
            noOfRows
        } else {
            toastIt("Update failed",this)
            0
        }
    }

    //Below is the new approach for implementation to read parceble/serializable objects
    //https://stackoverflow.com/questions/72571804/getserializableextra-and-getparcelableextra-deprecated-what-is-the-alternative
    private fun <T : Serializable?> Intent.getSerializable(key: String, mClass: Class<T>): T {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            this.getSerializableExtra(key, mClass)!!
        else
            this.getSerializableExtra(key) as T
    }
}