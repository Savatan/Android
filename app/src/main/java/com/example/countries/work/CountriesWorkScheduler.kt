package com.example.countries.work

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CountriesWorkScheduler @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val networkConstraint = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()

    fun schedulePeriodicRefresh(intervalHours: Int) {
        val request = PeriodicWorkRequestBuilder<RefreshCountriesWorker>(
            intervalHours.toLong().coerceAtLeast(1),
            TimeUnit.HOURS
        ).setConstraints(networkConstraint).build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            PERIODIC_WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
    }

    fun requestImmediateRefresh() {
        val request = OneTimeWorkRequestBuilder<RefreshCountriesWorker>()
            .setConstraints(networkConstraint)
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            IMMEDIATE_WORK_NAME,
            ExistingWorkPolicy.KEEP,
            request
        )
    }

    companion object {
        const val PERIODIC_WORK_NAME = "refresh_countries_periodic"
        const val IMMEDIATE_WORK_NAME = "refresh_countries_now"
    }
}
