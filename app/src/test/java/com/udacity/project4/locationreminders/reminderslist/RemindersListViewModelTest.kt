package com.udacity.project4.locationreminders.reminderslist

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class RemindersListViewModelTest {
    private lateinit var fakeDataSource: FakeDataSource
    private lateinit var remindersListViewModel: RemindersListViewModel

    private val dataItem = ReminderDTO(
        "correct item title",
        "correct item description",
        "correct item location",
        23.08,
        10.10
    )

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()


    @Before
    fun setupViewModel() {
        stopKoin()
        fakeDataSource = FakeDataSource()
        remindersListViewModel = RemindersListViewModel(
            ApplicationProvider.getApplicationContext(),
            fakeDataSource
        )
    }

    @Test
    fun isLoadingStateCorrect() = runBlockingTest {
        mainCoroutineRule.pauseDispatcher()
        fakeDataSource.saveReminder(dataItem)
        remindersListViewModel.loadReminders()
        remindersListViewModel.showLoading.value?.let { assertTrue(it) }
        mainCoroutineRule.resumeDispatcher()
        remindersListViewModel.showLoading.value?.let { assertFalse(it) }
    }

    @Test
    fun testWithError() = runBlockingTest {
        fakeDataSource.setShouldReturnError(true)
        fakeDataSource.saveReminder(dataItem)
        val result = fakeDataSource.getReminder("23332") as Result.Error
        assertEquals("element not found",result.message )
    }
}