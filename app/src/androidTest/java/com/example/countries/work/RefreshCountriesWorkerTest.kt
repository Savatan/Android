package com.example.countries.work

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import androidx.work.testing.TestListenableWorkerBuilder
import com.example.countries.fake.FakeWorkerRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RefreshCountriesWorkerTest {

    private fun buildWorker(repository: FakeWorkerRepository): RefreshCountriesWorker {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val factory = object : WorkerFactory() {
            override fun createWorker(
                appContext: Context,
                workerClassName: String,
                workerParameters: WorkerParameters
            ): ListenableWorker =
                RefreshCountriesWorker(appContext, workerParameters, repository)
        }
        return TestListenableWorkerBuilder<RefreshCountriesWorker>(context)
            .setWorkerFactory(factory)
            .build()
    }

    @Test
    fun successfulRefreshReturnsSuccess() = runBlocking {
        val repository = FakeWorkerRepository(Result.success(Unit))

        val result = buildWorker(repository).doWork()

        assertEquals(ListenableWorker.Result.success(), result)
        assertTrue(repository.refreshCount >= 1)
    }

    @Test
    fun failedRefreshRequestsRetry() = runBlocking {
        val repository = FakeWorkerRepository(Result.failure(RuntimeException("offline")))

        val result = buildWorker(repository).doWork()

        assertEquals(ListenableWorker.Result.retry(), result)
    }
}
