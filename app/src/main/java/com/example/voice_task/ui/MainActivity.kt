package com.example.voice_task.ui

import android.annotation.SuppressLint
import android.app.SearchManager
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import com.example.voice_task.R
import com.example.voice_task.databinding.ActivityMainBinding
import com.example.voice_task.helper.VoiceCommandHelper


class MainActivity : VoiceCommandHelper() {
    private lateinit var binding: ActivityMainBinding


    @SuppressLint("QueryPermissionsNeeded")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setAction()
    }


    private fun setAction() {
        binding.startSongCommand.setOnClickListener {
            takeSongName()
        }

        binding.startAppCommand.setOnClickListener {
            takeAppName()
        }
    }

    override fun openSong(songTitle: String) {
        binding.songNotes.text = getString(R.string.song_temp, songTitle)
        val intent = Intent(MediaStore.INTENT_ACTION_MEDIA_PLAY_FROM_SEARCH)
        intent.putExtra(MediaStore.EXTRA_MEDIA_FOCUS, MediaStore.Audio.Artists.ENTRY_CONTENT_TYPE)
        intent.putExtra(MediaStore.EXTRA_MEDIA_TITLE, title)
        intent.putExtra(SearchManager.QUERY, songTitle)
        startActivity(intent)
    }

    override fun errorSong() {
        binding.songNotes.text = getString(R.string.error_song_temp)
    }


    override fun openApp(appPackageName: String , appName: String) {
        binding.appNotes.text = getString(R.string.app_temp, appName)
        val launchIntent = packageManager.getLaunchIntentForPackage(appPackageName)
        launchIntent?.let { startActivity(it) }
    }

    override fun errorApp() {
        binding.appNotes.text = getString(R.string.error_app_temp)

    }


}


