package com.aj.noteappajkotlin

class Constants {
    companion object {
        const val NOTE_ACTION = "noteAction"
        const val NOTE_RESULT = "noteResult"

        //All action defined for notes
        const val ADD_NOTE = "addNote"
        const val UPDATE_NOTE = "updateNote"

        //Callback action from NoteActivity to HomeFragment
        const val REFRESH_NOTES = "refreshNotes"

        //Below 2 values for sharePreference purpose.
        const val USER_STORE = "UserStore"
        const val USER_ID_KEY = "userId"

        var userID = -1L //This will be quick static variable for userID using post Login process

    }
}