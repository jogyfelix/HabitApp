package com.jolabs.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.jolabs.database.dao.HabitDao
import com.jolabs.database.entity.HabitEntryTable
import com.jolabs.database.entity.HabitTable
import com.jolabs.database.entity.RepeatTable
import com.jolabs.database.entity.StreakTable

@Database(
    entities = [HabitTable::class, HabitEntryTable::class, RepeatTable::class, StreakTable::class],
    version = 2
)
abstract class HabitDatabase : RoomDatabase() {
    abstract fun habitDao(): HabitDao
    companion object {
        const val DATABASE_NAME = "habit_db"
    }
}