package com.example.countries.work

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.countries.domain.repository.CountryRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class RefreshCountriesWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val countryRepository: CountryRepository
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return countryRepository.refresh().fold(
            onSuccess = { Result.success() },
            onFailure = { Result.retry() }
        )
    }
}
