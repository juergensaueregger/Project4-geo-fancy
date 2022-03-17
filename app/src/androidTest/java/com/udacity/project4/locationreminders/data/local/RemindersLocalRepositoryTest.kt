package com.udacity.project4.locationreminders.data.local

import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.runner.RunWith
import org.koin.test.AutoCloseKoinTest

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Medium Test to test the repository
@MediumTest
class RemindersLocalRepositoryTest : AutoCloseKoinTest() {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()
    private lateinit var database: RemindersDatabase
    private lateinit var repo: RemindersLocalRepository

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).build()

        repo = RemindersLocalRepository(database.reminderDao())
    }

    @After
    fun cleanUp() = database.close()

    @Test
    fun insertAndGetDataTest() = runBlocking {

        val data = ReminderDTO(
            "test data",
            "test data description",
            "test location",
            23.0,
            21.0
        )
        repo.saveReminder(data)

        val result = repo.getReminder(data.id) as Result.Success

        Assert.assertEquals(result.data.title, "test data")
        Assert.assertEquals(result.data.description, "test data description")
        Assert.assertEquals(result.data.location, "test location")
        Assert.assertEquals(result.data.latitude, 23.0)
        Assert.assertEquals(result.data.longitude, 21.0)
    }

    @Test
    fun dataNotFoundTest() = runBlocking {
        val result = repo.getReminder("4711") as Result.Error
        assertEquals("Reminder not found!",result.message)
    }
}