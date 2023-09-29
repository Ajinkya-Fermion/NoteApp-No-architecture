package com.aj.noteappajkotlin.models

import java.io.Serializable

//Serializable interface used data transfer as 'object'
class NotesModel : Serializable{
    var noteId : Long = 0
    var noteTitle = ""
    var noteContent = ""
    var userId : Long = 0

    //constructor defining becomes compulsory when using serializable
    constructor(mNoteId: Long, mNoteTitle: String, mNoteContent: String,mUserId : Long){
        noteId = mNoteId
        noteTitle = mNoteTitle
        noteContent = mNoteContent
        userId = mUserId
    }

    constructor()
    }
