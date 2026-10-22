package com.example.pr5moskvina

import androidx.navigationevent.NavigationEventDispatcher
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movies")
data class Movie(
    @PrimaryKey(autoGenerate = true) val id:Int=0,
    val title: String,
    val year: String,
    val imdbID: String
)
