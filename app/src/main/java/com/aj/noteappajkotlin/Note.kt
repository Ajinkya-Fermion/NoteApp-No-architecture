package com.aj.noteappajkotlin

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

//Please check below solution for Foreign key warning.
//https://stackoverflow.com/questions/44480761/android-room-compile-time-warning-about-column-in-foreign-key-not-part-of-an-ind
@Entity(tableName = "notes",foreignKeys = [ForeignKey(
    entity = User::class,
    childColumns = ["userId"],
    parentColumns = ["userId"]
)]) //"Entity" is name of the table
data class Note(
    @ColumnInfo(name = "noteTitle") //"@ColumnInfo" is name of the column in DB
    val noteTitle: String,
    @ColumnInfo(name = "noteContent")
    val noteContent : String,
    @ColumnInfo(name="userId") //This will be used as foreign key referencing 'User' as parent table
    val userId:Long
){
    @PrimaryKey(autoGenerate = true)
    var noteId: Long = 0
}