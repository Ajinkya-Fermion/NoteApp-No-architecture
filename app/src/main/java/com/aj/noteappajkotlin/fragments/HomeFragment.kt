package com.aj.noteappajkotlin.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aj.noteappajkotlin.Constants
import com.aj.noteappajkotlin.Note
import com.aj.noteappajkotlin.NoteDatabase
import com.aj.noteappajkotlin.R
import com.aj.noteappajkotlin.Util.Companion.toastIt
import com.aj.noteappajkotlin.activities.NoteActivity
import com.aj.noteappajkotlin.adapters.NoteAdapter
import com.aj.noteappajkotlin.models.NotesModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class HomeFragment : Fragment() {

    //Interfaces used in old style for Fragment to HostActivity communication
    //https://stuff.mit.edu/afs/sipb/project/android/docs/training/basics/fragments/communicating.html
    private var mCallback: OnLogOutBtnListener? = null

    private var notes : List<Note> = arrayListOf() //Here empty array annoted by "arrayListOf()"
    private lateinit var rvNoteList : RecyclerView

    //Receiver
    private var activityResultLaunch: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        ActivityResultCallback<ActivityResult> { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val noteResult = data?.getStringExtra(Constants.NOTE_RESULT)
                if(noteResult.equals(Constants.REFRESH_NOTES)){
                    //Refresh the NotesList
//                    context?.let { it1 -> toastIt("New note added", it1) }
                    GlobalScope.launch (Dispatchers.Main) {
                        notes = getNotes(Constants.userID,activity as Context)

                        val arraylist = arrayListOf<NotesModel>()

                        //for loop to get data in List<Note> to ArrayList<NotesModel>
                        for (item in notes) {
                            arraylist.add(
                                NotesModel(
                                    item.noteId,
                                    item.noteTitle,
                                    item.noteContent,
                                    item.userId
                                )
                            )
                        }

                        // 3 - Adapter
                        val adapter = NoteAdapter(arraylist, View.OnClickListener {
                            //"rvNoteList.getChildAdapterPosition(it)" provides position in adapter
                            updateNote(arraylist[rvNoteList.getChildAdapterPosition(it)])
                        })

                        rvNoteList.adapter = adapter
                    }
                }
                else
                {
                    context?.let { it1 -> toastIt("No change", it1) }
                }

            }
        })

    override fun onAttach(context: Context) {
        super.onAttach(context)

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        mCallback = try {
            context as OnLogOutBtnListener
        } catch (e: ClassCastException) {
            throw ClassCastException(
                context.toString()
                        + " must implement OnHeadlineSelectedListener"
            )
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.top_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view : View = inflater.inflate(R.layout.fragment_home, container, false)
        val activity = activity as Context
        val fab = view.findViewById<FloatingActionButton>(R.id.fab_addNote)
        val toolbar = view.findViewById(R.id.tb_home) as Toolbar?
        toolbar?.setNavigationIcon(R.drawable.ic_launcher_foreground)
        if (toolbar != null) {
            toolbar.inflateMenu(R.menu.top_menu)
            toolbar.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.logout -> mCallback?.onLogOutClicked();
                }
                true
            }
        }
        rvNoteList = view.findViewById(R.id.rv_noteList)
        rvNoteList.layoutManager = GridLayoutManager(activity,2)

        fab.setOnClickListener(){
            addNote(view)
        }

        GlobalScope.launch (Dispatchers.Main) {
            notes = getNotes(Constants.userID,activity)
//            toastIt("Notes length:"+notes.size,activity)
            val arraylist = arrayListOf<NotesModel>()

            //for loop to get data in List<Note> to ArrayList<NotesModel>
            for (item in notes) {
                arraylist.add(
                    NotesModel(
                        item.noteId,
                        item.noteTitle,
                        item.noteContent,
                        item.userId
                    )
                )
            }

            // 3 - Adapter
            val adapter = NoteAdapter(arraylist, View.OnClickListener {
                //"rvNoteList.getChildAdapterPosition(it)" provides position in adapter
                updateNote(arraylist[rvNoteList.getChildAdapterPosition(it)])
            })

            rvNoteList.adapter = adapter
        }

        // Inflate the layout for this fragment
        return view
    }

    private fun addNote(view : View){
        val intent = Intent(activity, NoteActivity::class.java)
        intent.putExtra(Constants.NOTE_ACTION, Constants.ADD_NOTE)
        activityResultLaunch.launch(intent)
    }

    private fun updateNote(notesModel: NotesModel){
        val myIntent = Intent(activity, NoteActivity::class.java)
        myIntent.putExtra(Constants.NOTE_ACTION, Constants.UPDATE_NOTE)

        //Below we are passing data as custom object through serialization
        val passingObject = NotesModel()
        passingObject.noteId = notesModel.noteId
        passingObject.userId = notesModel.userId
        passingObject.noteTitle = notesModel.noteTitle
        passingObject.noteContent = notesModel.noteContent
        myIntent.putExtra("noteModelObj", passingObject)
        activityResultLaunch.launch(myIntent)
    }

    private suspend fun getNotes(userId: Long, context: Context):List<Note>{
        val db = NoteDatabase.getDatabase(context) //This is singleton method calling
        val noteDao = db.noteDao()
        val notes: List<Note> = noteDao.getNotes(userId)
        if (notes.isNotEmpty()) {
            return notes
        } else {
            return arrayListOf() //Empty array
        }
    }

    // Container Activity must implement this interface
    interface OnLogOutBtnListener {
        fun onLogOutClicked()
    }
}