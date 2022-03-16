package com.udacity.project4

import android.app.Activity
import android.app.Application
import android.app.PendingIntent.getActivity
import android.view.View
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.longClick
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.RootMatchers.withDecorView
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.udacity.project4.locationreminders.RemindersActivity
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.local.LocalDB
import com.udacity.project4.locationreminders.data.local.RemindersLocalRepository
import com.udacity.project4.locationreminders.reminderslist.RemindersListViewModel
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import com.udacity.project4.util.DataBindingIdlingResource
import com.udacity.project4.util.monitorActivity
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.not
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.AutoCloseKoinTest
import org.koin.test.get

@RunWith(AndroidJUnit4::class)
@LargeTest
//END TO END test to black box test the app
class RemindersActivityTest :
    AutoCloseKoinTest() {// Extended Koin Test - embed autoclose @after method to close Koin after every test

    private lateinit var repository: ReminderDataSource
    private lateinit var appContext: Application
    private val dataBindingIdlingResource = DataBindingIdlingResource()
     /**
     * As we use Koin as a Service Locator Library to develop our code, we'll also use Koin to test our code.
     * at this step we will initialize Koin related code to be able to use it in out testing.
     */
    @Before
    fun init() {
        stopKoin()//stop the original app koin
        appContext = getApplicationContext()
        val myModule = module {
            viewModel {
                RemindersListViewModel(
                    appContext,
                    get() as ReminderDataSource
                )
            }
            single {
                SaveReminderViewModel(
                    appContext,
                    get() as ReminderDataSource
                )
            }
            single { RemindersLocalRepository(get()) }

            single<ReminderDataSource> {
                get<RemindersLocalRepository>()
            }
            single { LocalDB.createRemindersDao(appContext) }
        }
        //declare a new koin module
        startKoin {
            modules(listOf(myModule))
        }
        //Get our real repository
        repository = get()

        //clear the data to start fresh
        runBlocking {
            repository.deleteAllReminders()
        }
    }
    /**
     * Idling resources tell Espresso that the app is idle or busy. This is needed when operations
     * are not scheduled in the main Looper (for example when executed on a different thread).
     */
    @Before
    fun registerIdlingResource() {

        IdlingRegistry.getInstance().register(dataBindingIdlingResource)
    }
    /**
     * Unregister your Idling Resource so it can be garbage collected and does not leak any memory.
     */
    @After
    fun unregisterIdlingResource() {
        IdlingRegistry.getInstance().unregister(dataBindingIdlingResource)
    }

    @Test
    fun TitleSnackBarTest(){
        val activityScenario = ActivityScenario.launch(RemindersActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)
        Espresso.onView(withId(R.id.addReminderFAB)).perform(click())
        Espresso.onView(withId(R.id.saveReminder)).perform(click())
        Espresso.onView(withText("Please enter title")).check(matches(isDisplayed()))
        activityScenario.close()
    }

    @Test
    fun setReminderTest() = runBlocking {
        val activityScenario = ActivityScenario.launch(RemindersActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)
        lateinit var activity: RemindersActivity
        activityScenario.onActivity {
            activity = it
        }
        Espresso.onView(withId(R.id.addReminderFAB)).perform(click())
        Espresso.onView(withId(R.id.reminderTitle)).perform(ViewActions.replaceText("New Location"))
        Espresso.onView(withId(R.id.reminderDescription)).perform(ViewActions.replaceText("New Location Description"))
        Espresso.onView(withId(R.id.selectLocation)).perform(click())
        Espresso.onView(withId(R.id.map)).perform(longClick())
        Espresso.onView(withId(R.id.select_bt)).perform(click())
        Espresso.onView(withId(R.id.saveReminder)).perform(click())
        Espresso.onView(withText("New Location")).check(matches(isDisplayed()))
        Espresso.onView(withText(R.string.reminder_saved))
            .inRoot(withDecorView(not(CoreMatchers.`is`(activity.window.decorView))))
            .check(matches(isDisplayed()))

        activityScenario.close()
    }

    @Test
    fun unsuffientDataTest() {
        val activityScenario = ActivityScenario.launch(RemindersActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)
        Espresso.onView(withId(R.id.addReminderFAB)).perform(click())
        Espresso.onView(withId(R.id.saveReminder)).perform(click())
        val snackBarText = appContext.getString(R.string.err_enter_title)
        Espresso.onView(withText(snackBarText))
            .check(matches(isDisplayed()))
        activityScenario.close()
    }

}
