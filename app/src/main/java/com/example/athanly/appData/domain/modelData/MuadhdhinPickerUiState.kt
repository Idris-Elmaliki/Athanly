package com.example.athanly.appData.domain.modelData

import com.example.athanly.appData.domain.modelData.enums.MuadhdhinId

// Drives MuadhdhinPickerViewModel — kept separate from AppDataUiState since the preview/pending
// fields below are specific to this one screen, not shared with the rest of Settings.
data class MuadhdhinPickerUiState(
    val selectedMuadhdhin: Muadhdhin = muadhhinList.first(),
    val error: String? = null,

    // Which Muadhdhin's preview clip is actively playing, if any — drives the play/pause icon
    // on that one card. Not persisted; reset to null whenever the clip finishes or the picker
    // screen is left.
    val previewingMuadhdhinId: MuadhdhinId? = null,

    // A tapped-but-not-yet-saved Muadhdhin pick, committed to DataStore only when the picker
    // screen is left. Non-null here overrides selectedMuadhdhin for display purposes.
    val pendingMuadhdhin: Muadhdhin? = null,
)