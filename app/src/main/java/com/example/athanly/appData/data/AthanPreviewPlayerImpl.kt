package com.example.athanly.appData.data

import android.content.Context
import android.media.MediaPlayer
import androidx.annotation.RawRes
import com.example.athanly.appData.domain.repository.AthanPreviewPlayer
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

// Singleton — only one Muadhdhin preview can ever be playing at a time, so one MediaPlayer
// instance shared across whichever screen (Settings or onboarding) is currently showing is fine.
@Singleton
class AthanPreviewPlayerImpl @Inject constructor(
    @param:ApplicationContext private val context: Context
) : AthanPreviewPlayer {
    private var mediaPlayer: MediaPlayer? = null

    override fun play(
        @RawRes audioRes: Int,
        onCompletion: () -> Unit
    ) {
        // Releases whatever was playing before starting the new clip — only one preview
        // plays at a time, so there's never a reason to keep the old player around
        stop()

        mediaPlayer = MediaPlayer.create(context, audioRes)?.apply {
            setOnCompletionListener {
                onCompletion()
                stop()
            }
            start()
        }
    }

    override fun stop() {
        mediaPlayer?.release()
        mediaPlayer = null
    }
}