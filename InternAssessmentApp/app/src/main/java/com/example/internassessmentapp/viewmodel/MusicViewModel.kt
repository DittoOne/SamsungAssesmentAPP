package com.example.internassessmentapp.viewmodel

import android.app.Application
import android.media.MediaPlayer
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.internassessmentapp.R
import com.example.internassessmentapp.data.Song
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

class MusicViewModel(application: Application) : AndroidViewModel(application) {

    private var mediaPlayer: MediaPlayer? = null
    private var progressUpdateJob: Job? = null

    // Pre-defined music list
    private val playlist = listOf(
        Song("Samsung Theme 1", R.raw.song1),
        Song("Samsung Theme 2", R.raw.song2)
    )

    var currentIndex = mutableIntStateOf(0)
    var isPlaying = mutableStateOf(false)
    var isShuffleOn = mutableStateOf(false)
    var isRepeatOn = mutableStateOf(false)

    // Progress tracking
    var progress = mutableFloatStateOf(0f)
    var currentPosition = mutableIntStateOf(0)
    var duration = mutableIntStateOf(0)

    val playlistSize get() = playlist.size
    val currentSongTitle get() = playlist[currentIndex.intValue].title

    fun togglePlayPause() {
        if (mediaPlayer == null) {
            initializeMediaPlayer()
        }

        if (isPlaying.value) {
            mediaPlayer?.pause()
            isPlaying.value = false
            stopProgressUpdate()
        } else {
            mediaPlayer?.start()
            isPlaying.value = true
            startProgressUpdate()
        }
    }

    fun stopMusic() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        isPlaying.value = false
        progress.floatValue = 0f
        currentPosition.intValue = 0
        stopProgressUpdate()
    }

    fun playNext() {
        stopMusic()
        currentIndex.intValue = if (isShuffleOn.value) {
            (0 until playlist.size).random()
        } else {
            (currentIndex.intValue + 1) % playlist.size
        }
        togglePlayPause()
    }

    fun playPrevious() {
        stopMusic()
        currentIndex.intValue = if (currentIndex.intValue - 1 < 0) {
            playlist.size - 1
        } else {
            currentIndex.intValue - 1
        }
        togglePlayPause()
    }

    fun toggleShuffle() {
        isShuffleOn.value = !isShuffleOn.value
    }

    fun toggleRepeat() {
        isRepeatOn.value = !isRepeatOn.value
    }

    private fun initializeMediaPlayer() {
        mediaPlayer = MediaPlayer.create(
            getApplication(),
            playlist[currentIndex.intValue].resId
        ).apply {
            setOnCompletionListener {
                if (isRepeatOn.value) {
                    seekTo(0)
                    start()
                } else {
                    playNext()
                }
            }
            this@MusicViewModel.duration.intValue = this.duration
        }
    }

    private fun startProgressUpdate() {
        progressUpdateJob?.cancel()
        progressUpdateJob = viewModelScope.launch {
            while (isPlaying.value) {
                mediaPlayer?.let { player ->
                    currentPosition.intValue = player.currentPosition
                    val totalDuration = player.duration
                    duration.intValue = totalDuration

                    progress.floatValue = if (totalDuration > 0) {
                        currentPosition.intValue.toFloat() / totalDuration.toFloat()
                    } else {
                        0f
                    }
                }
                delay(100) // Update every 100ms
            }
        }
    }

    private fun stopProgressUpdate() {
        progressUpdateJob?.cancel()
    }

    override fun onCleared() {
        super.onCleared()
        stopProgressUpdate()
        mediaPlayer?.release()
    }
}