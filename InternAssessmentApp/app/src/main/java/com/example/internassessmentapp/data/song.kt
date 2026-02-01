package com.example.internassessmentapp.data

import android.net.Uri

data class Song(
    val id: Long = 0,
    val title: String,
    val artist: String = "Unknown Artist",
    val album: String = "Unknown Album",
    val duration: Long = 0,
    val uri: Uri = Uri.EMPTY,
    val albumArtUri: Uri? = null,
    val path: String? = null,
    val resId: Int? = null // For backward compatibility with hardcoded songs
)