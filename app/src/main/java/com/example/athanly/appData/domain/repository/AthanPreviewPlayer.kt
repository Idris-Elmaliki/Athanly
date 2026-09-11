package com.example.athanly.appData.domain.repository

import androidx.annotation.RawRes

// Plays a short Athan preview clip when a user taps a Muadhdhin card in Settings or onboarding.
// Kept behind this interface (implemented by AthanPreviewPlayerImpl with MediaPlayer) so the
// ViewModels that use it don't depend on Android media APIs directly.
interface AthanPreviewPlayer {
    // onCompletion fires once the clip finishes on its own, so the caller can clear whichever
    // "currently playing" state drives the pause icon back to play
    fun play(
        @RawRes audioRes: Int, 
        onCompletion: () -> Unit
    )
    fun stop()
}