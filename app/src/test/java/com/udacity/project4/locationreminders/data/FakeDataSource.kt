package com.udacity.project4.locationreminders.data

import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result

class FakeDataSource( val reminders: MutableList<ReminderDTO> = mutableListOf<ReminderDTO>()) : ReminderDataSource {

    private var shouldReturnError = false

    fun setShouldReturnError(value: Boolean) {
        shouldReturnError = value
    }

    override suspend fun getReminders(): Result<List<ReminderDTO>> =
        if (shouldReturnError)
            Result.Error("no reminders")
        else
            Result.Success(ArrayList(reminders))

    override suspend fun saveReminder(reminder: ReminderDTO) {
        reminders.add(reminder)
    }
    override suspend fun getReminder(id: String): Result<ReminderDTO> {
        val element: ReminderDTO? = reminders.find { it.id == id }
        if ( element != null && !shouldReturnError){
            return Result.Success(element)
        }
        return Result.Error("element not found")
    }
    override suspend fun deleteAllReminders() {
        reminders.clear()
    }
}