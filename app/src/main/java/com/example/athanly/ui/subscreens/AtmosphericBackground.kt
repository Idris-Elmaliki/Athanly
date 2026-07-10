package com.example.athanly.ui.subscreens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.athanly.R

// Crop + Center on a single full-resolution image reproduces "anchored-center, reveal-outward"
// across all 3 window size classes with no per-breakpoint cropping logic: Crop scales by
// max(containerW/imageW, containerH/imageH), so a tall narrow Compact container crops tightly
// to a centered vertical sliver, while a wider Medium/Expanded container reveals more of the
// image symmetrically from center outward.
@Composable
fun AtmosphericBackground(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(R.drawable.home_atmospheric_background),
        contentDescription = null, // purely decorative — no information conveyed
        contentScale = ContentScale.Crop,
        alignment = Alignment.Center,
        modifier = modifier.fillMaxSize()
    )
}