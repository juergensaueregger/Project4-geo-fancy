package com.udacity.project4.locationreminders.savereminder

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)

class SaveReminderViewModelTest {
    private lateinit var fakeDataSource: FakeDataSource
    private lateinit var saveReminderViewModel: SaveReminderViewModel

    private val notCorrectDataItem = ReminderDataItem(
        "not correct item title",
        "not correct item description",
        "",
        23.08,
        10.10)

    private val dataItem  = ReminderDataItem(
        "correct item title",
        "correct item description",
        "correct item location",
        23.08,
        10.10 )

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Before
    fun setupViewModel() {
        stopKoin()
        fakeDataSource = FakeDataSource()
        saveReminderViewModel = SaveReminderViewModel(
            ApplicationProvider.getApplicationContext(),
            fakeDataSource)
    }
    @Test
    fun isLoadingStateCorrect() = runBlockingTest {
        mainCoroutineRule.pauseDispatcher()
        saveReminderViewModel.saveReminder(dataItem)
        saveReminderViewModel.showLoading.value?.let { assertTrue(it) }
        mainCoroutineRule.resumeDispatcher()
        saveReminderViewModel.showLoading.value?.let { assertFalse(it) }
    }

    @Test
    fun validateEnteredDataFalse() = runBlockingTest  {
        val result = saveReminderViewModel.validateEnteredData(notCorrectDataItem)
        assertFalse(result)
    }

    @Test
    fun validateEnteredDataTrue() = runBlockingTest  {
        val result = saveReminderViewModel.validateEnteredData(dataItem)
        assertTrue(result)
    }
}