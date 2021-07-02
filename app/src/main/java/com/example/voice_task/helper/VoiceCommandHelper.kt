package com.example.voice_task.helper

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import java.util.*

abstract class VoiceCommandHelper : AppCompatActivity(), TextToSpeech.OnInitListener {

    ////////////////////////////////// song command handler ///////////////////////

    /**
     * receive song name
     */
    private val songResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {

                val songName = filterVoiceCommand(result.data)
                songName?.let(::openSong) ?: errorSong()

            }
        }


    private fun filterVoiceCommand(intent: Intent?): String? {
        var command: String? = ""
        val voiceResult = intent?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
        voiceResult?.let { list ->
            list.forEach {
                command += "$it "
            }
        }
        return command?.trim()
    }


    fun takeSongName() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)

        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.ENGLISH)

        songResultLauncher.launch(intent)
    }

    ////////////////////////////////// app command handler ///////////////////////

    /**
     * receive app name
     */
    private val appResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val appName = filterVoiceCommand(result.data)
                appName?.let { app ->
                    getPackNameByAppName(appName) { packageName ->
                        packageName?.let { pn ->
                            openApp(pn, app)
                        } ?: errorApp()
                    }
                }
            }
        }

    fun takeAppName() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)

        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.ENGLISH)

        appResultLauncher.launch(intent)
    }


    private inline fun getPackNameByAppName(name: String, block: (String?) -> Unit) {
        var packName: String? = null
        val pm: PackageManager = this.packageManager
        val l = pm.getInstalledApplications(PackageManager.GET_META_DATA)

        for (ai in l) {
            val n = pm.getApplicationLabel(ai) as String

            if (n.contains(name, true) || name.contains(n, true) || ai.packageName.contains(
                    name,
                    true
                )
            ) {
                packName = ai.packageName
                break
            }
        }
        block(packName)
    }


    override fun onInit(status: Int) {}


    abstract fun openSong(songTitle: String)
    abstract fun errorSong()

    abstract fun openApp(appPackageName: String, appName: String)
    abstract fun errorApp()

}