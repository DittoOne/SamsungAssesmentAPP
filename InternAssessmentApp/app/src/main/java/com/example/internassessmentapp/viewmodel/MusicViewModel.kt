package com.example.internassessmentapp.viewmodel

import android.app.Application
import android.content.ContentUris
import android.media.MediaPlayer
import android.net.Uri
import android.provider.MediaStore
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.internassessmentapp.data.Song
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.core.net.toUri

class MusicViewModel(application: Application) : AndroidViewModel(application) {

    private var mediaPlayer: MediaPlayer? = null
    private var progressUpdateJob: Job? = null

    // Playlist management
    private val _playlist = mutableStateOf<List<Song>>(emptyList())
    val playlist: List<Song> get() = _playlist.value

    var currentIndex = mutableIntStateOf(0)
    var isPlaying = mutableStateOf(false)
    var isShuffleOn = mutableStateOf(false)
    var isRepeatOn = mutableStateOf(false)
    var showPlaylist = mutableStateOf(false)

    // Progress tracking
    var progress = mutableFloatStateOf(0f)
    var currentPosition = mutableIntStateOf(0)
    var duration = mutableIntStateOf(0)
    var isSeeking = mutableStateOf(false)

    val playlistSize get() = playlist.size
    val currentSong get() = if (playlist.isNotEmpty() && currentIndex.intValue < playlist.size)
        playlist[currentIndex.intValue] else null

    init {
        loadMusicFromDevice()
    }

    private fun loadMusicFromDevice() {
        viewModelScope.launch(Dispatchers.IO) {
            val songs = mutableListOf<Song>()

            val projection = arrayOf(
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.ALBUM_ID
            )

            val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
            val sortOrder = "${MediaStore.Audio.Media.TITLE} ASC"

            try {
                getApplication<Application>().contentResolver.query(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    projection,
                    selection,
                    null,
                    sortOrder
                )?.use { cursor ->
                    val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                    val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
                    val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
                    val albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
                    val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
                    val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
                    val albumIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)

                    while (cursor.moveToNext()) {
                        val id = cursor.getLong(idColumn)
                        val title = cursor.getString(titleColumn) ?: "Unknown"
                        val artist = cursor.getString(artistColumn) ?: "Unknown Artist"
                        val album = cursor.getString(albumColumn) ?: "Unknown Album"
                        val duration = cursor.getLong(durationColumn)
                        val data = cursor.getString(dataColumn)
                        val albumId = cursor.getLong(albumIdColumn)

                        val contentUri = ContentUris.withAppendedId(
                            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                            id
                        )

                        val albumArtUri = ContentUris.withAppendedId(
                            "content://media/external/audio/albumart".toUri(),
                            albumId
                        )

                        songs.add(
                            Song(
                                id = id,
                                title = title,
                                artist = artist,
                                album = album,
                                duration = duration,
                                uri = contentUri,
                                albumArtUri = albumArtUri,
                                path = data
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            withContext(Dispatchers.Main) {
                _playlist.value = songs
            }
        }
    }

    fun togglePlayPause() {
        if (playlist.isEmpty()) return

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

    fun playSongAtIndex(index: Int) {
        if (index in playlist.indices) {
            stopMusic()
            currentIndex.intValue = index
            togglePlayPause()
            showPlaylist.value = false
        }
    }

    fun toggleShuffle() {
        isShuffleOn.value = !isShuffleOn.value
    }

    fun toggleRepeat() {
        isRepeatOn.value = !isRepeatOn.value
    }

    fun togglePlaylistView() {
        showPlaylist.value = !showPlaylist.value
    }

    fun seekTo(position: Float) {
        val newPosition = (position * duration.intValue).toInt()
        mediaPlayer?.seekTo(newPosition)
        currentPosition.intValue = newPosition
        progress.floatValue = position
    }

    fun onSeekStart() {
        isSeeking.value = true
        stopProgressUpdate()
    }

    fun onSeekEnd() {
        isSeeking.value = false
        if (isPlaying.value) {
            startProgressUpdate()
        }
    }

    private fun initializeMediaPlayer() {
        if (playlist.isEmpty()) return

        currentSong?.let { song ->
            try {
                mediaPlayer = MediaPlayer().apply {
                    setDataSource(getApplication(), song.uri)
                    prepare()
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
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun startProgressUpdate() {
        progressUpdateJob?.cancel()
        progressUpdateJob = viewModelScope.launch {
            while (isPlaying.value && !isSeeking.value) {
                mediaPlayer?.let { player ->
                    if (player.isPlaying) {
                        currentPosition.intValue = player.currentPosition
                        val totalDuration = player.duration
                        duration.intValue = totalDuration

                        progress.floatValue = if (totalDuration > 0) {
                            currentPosition.intValue.toFloat() / totalDuration.toFloat()
                        } else {
                            0f
                        }
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