package com.example.athanly.location.data

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Build
import androidx.core.content.ContextCompat
import com.example.athanly.athanTime.local.domain.modelData.City
import com.example.athanly.location.domain.repository.LocationProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.util.Locale
import javax.inject.Inject
import kotlin.coroutines.resume

class LocationProviderImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val fusedLocationProviderClient: FusedLocationProviderClient
) : LocationProvider {
    override suspend fun getCurrentCity(): City? {
        val location = fetchCurrentLocation() ?: return null
        val name = reverseGeocode(location) ?: return null

        return City(
            name = name,
            hijriDate = "",
            latitude = location.latitude,
            longitude = location.longitude
        )
    }

    // Permission is checked here rather than trusted from the caller — calling the Play
    // Services API without it throws a SecurityException instead of failing gracefully, so
    // this returns null (same as "no fix") instead of ever reaching that call ungranted.
    // getCurrentLocation's callback-based Task API is wrapped in suspendCancellableCoroutine
    // rather than pulling in the kotlinx-coroutines-play-services dependency just for
    // Task.await() — this is the only Play Services call in the project.
    private suspend fun fetchCurrentLocation(): Location? {
        val fineGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        
        val coarseGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        
        if (!fineGranted && !coarseGranted) 
            return null

        return suspendCancellableCoroutine { continuation ->
            val cancellationTokenSource = CancellationTokenSource()
            continuation.invokeOnCancellation { cancellationTokenSource.cancel() }

            fusedLocationProviderClient.getCurrentLocation(
                Priority.PRIORITY_BALANCED_POWER_ACCURACY,
                cancellationTokenSource.token
            ).addOnSuccessListener { location ->
                continuation.resume(location)
            }.addOnFailureListener {
                continuation.resume(null)
            }
        }
    }

    // Geocoder.getFromLocation's synchronous overload is deprecated on API 33+ in favor of
    // the async GeocodeListener overload — both are handled here so this works across this
    // app's minSdk 28 – compileSdk 37 range.
    private suspend fun reverseGeocode(location: Location): String? {
        val geocoder = Geocoder(context, Locale.getDefault())

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            suspendCancellableCoroutine { continuation ->
                geocoder.getFromLocation(location.latitude, location.longitude, 1) { addresses ->
                    continuation.resume(
                        addresses.firstOrNull()?.locality ?: addresses.firstOrNull()?.subAdminArea
                    )
                }
            }
        } else {
            withContext(Dispatchers.IO) {
                @Suppress("DEPRECATION")
                val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)

                addresses?.firstOrNull()?.locality ?: addresses?.firstOrNull()?.subAdminArea
            }
        }
    }
}