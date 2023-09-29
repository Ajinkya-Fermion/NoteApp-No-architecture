package com.aj.noteappajkotlin.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.aj.noteappajkotlin.R
import com.aj.noteappajkotlin.models.NotesModel

//We have used 'View.OnClickListener' as callbackmethod as param to get touch control
// in HomeFragment where we call update method.
class NoteAdapter(private val notesList : ArrayList<NotesModel>,
                  private val onClickListener: View.OnClickListener)
    : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

    inner class NoteViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        var noteTitle : TextView
        var noteId : TextView

        init {
            noteTitle = itemView.findViewById(R.id.txv_noteTitle)
            noteId = itemView.findViewById(R.id.txv_noteId)

            //We are taking control from adapter to HomeFragment
            itemView.setOnClickListener(onClickListener)

            //Ideal approach if we want to move to another screen with data
//            itemView.setOnClickListener(){
//
//                val myIntent = Intent(itemView.context, NoteActivity::class.java)
//                myIntent.putExtra(Constants.NOTE_ACTION, Constants.UPDATE_NOTE)
////                activityResultLaunch.launch(myIntent)
//
//                Toast.makeText(itemView.context,"You Choose : ${notesList.get(adapterPosition).noteId}",Toast.LENGTH_SHORT).show()
////                Toast.makeText(itemView.context,"You Choose : ${vaccineList[adapterPosition].name}",
////                    Toast.LENGTH_SHORT).show()
//            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        //Here we inflate layout and we create the view
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.note_item_layout,parent,false)

        return NoteViewHolder(v)
    }

    override fun getItemCount(): Int {
        return notesList.size
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.noteTitle.text = notesList[position].noteTitle
        holder.noteId.text = notesList[position].noteId.toString()
    }

}