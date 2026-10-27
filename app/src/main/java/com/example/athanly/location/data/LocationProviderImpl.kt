package com.example.athanly.location.data

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.athanly.athanTime.local.domain.modelData.City
import com.example.athanly.location.domain.repository.LocationProvider
import com.example.athanly.qibla.local.domain.modelData.Coordinates
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
    @param:ApplicationContext private val context: Context,
    private val fusedLocationProviderClient: FusedLocationProviderClient
) : LocationProvider {
    override suspend fun getCurrentCity(): City? {
        val location = fetchCurrentLocation() ?: return null
        val name = getNameOfLocation(location) ?: return null

        return City(
            name = name,
            hijriDate = "",
            latitude = location.latitude,
            longitude = location.longitude
        )
    }

    // Reuses the same GPS fix as getCurrentCity() but skips getNameOfLocation() — Qibla's
    // bearing math only needs lat/lng, so there's no reason to pay for a geocode lookup here.
    override suspend fun getCurrentCoordinates(): Coordinates? {
        val location = fetchCurrentLocation() ?: return null

        return Coordinates(
            latitude = location.latitude,
            longitude = location.longitude
        )
    }

    // Public so callers (e.g. QiblaViewModel, via UserLocationRepository) can check permission
    // state on its own, separate from fetchCurrentLocation()'s "not granted == null fix" fallback.
    override fun hasLocationPermission(): Boolean {
        val fineGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val coarseGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        return fineGranted || coarseGranted
    }

    // LocationManager.isLocationEnabled() needs API 28, which is this app's minSdk, so no
    // version check is required. Reads the system-wide toggle, not the app permission.
    override fun isLocationEnabled(): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        return locationManager.isLocationEnabled
    }

    // Permission is checked here rather than trusted from the caller — calling the Play
    // Services API without it throws a SecurityException instead of failing gracefully, so
    // this returns null (same as "no fix") instead of ever reaching that call ungranted.
    // getCurrentLocation's callback-based Task API is wrapped in suspendCancellableCoroutine
    // rather than pulling in the kotlinx-coroutines-play-services dependency just for
    // Task.await() — this is the only Play Services call in the project.
    private suspend fun fetchCurrentLocation(): Location? {
        if (!hasLocationPermission())
            return null

        // Bridges Google's callback API into a suspend function: pauses here and hands us
        // `continuation`; the value passed to continuation.resume() later is what this returns.
        return suspendCancellableCoroutine { continuation ->
            // Google's request knows nothing about coroutines, so this is the stop button for
            // it: its .token goes to Google, and .cancel() below fires if our coroutine is
            // canceled (e.g. user leaves the screen), saving the GPS work.
            val locationRequestController = CancellationTokenSource()
            continuation.invokeOnCancellation { locationRequestController.cancel() }

            if (
                ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
                == PackageManager.PERMISSION_GRANTED
                ||
                ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
                == PackageManager.PERMISSION_GRANTED
            ) {
                fusedLocationProviderClient.getCurrentLocation(
                    Priority.PRIORITY_BALANCED_POWER_ACCURACY,
                    locationRequestController.token
                ).addOnSuccessListener { location ->
                    // Wakes the paused coroutine; this value becomes fetchCurrentLocation()'s
                    // return value. (location can itself be null if Google has no fix.)
                    continuation.resume(location)
                }.addOnFailureListener {
                    // Must still resume, or the coroutine would wait forever — null = "no fix".
                    continuation.resume(null)
                }
            }
            else {
                // Permission vanished after the check at the top — resume with null so the
                // coroutine never hangs.
                continuation.resume(null)
            }
        }
    }

    // Geocoder.getFromLocation's synchronous overload is deprecated on API 33+ in favor of
    // the async GeocodeListener overload — both are handled here so this works across this
    // app's minSdk 28 – compileSdk 37 range.
    private suspend fun getNameOfLocation(location: Location): String? {
        // Geocoder turns coordinates into an address (reverse geocoding) by asking a system
        // backend, usually over the network — so it can be slow, and fail or come back empty.
        val cityLookup = Geocoder(context, Locale.getDefault())

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            suspendCancellableCoroutine { continuation ->
                cityLookup.getFromLocation(
                    location.latitude,
                    location.longitude,
                    1,
                    object : Geocoder.GeocodeListener {
                        override fun onGeocode(addresses: MutableList<Address>) {
                            continuation.resume(
                                addresses.firstOrNull()?.locality
                                    ?: addresses.firstOrNull()?.subAdminArea
                            )
                        }
                        override fun onError(errorMessage: String?) {
                            continuation.resume(null)
                        }
                    }
                )
            }
        } else {
            withContext(Dispatchers.IO) {
                @Suppress("DEPRECATION")
                val addresses = cityLookup.getFromLocation(location.latitude, location.longitude, 1)

                addresses?.firstOrNull()?.locality ?: addresses?.firstOrNull()?.subAdminArea
            }
        }
    }
}