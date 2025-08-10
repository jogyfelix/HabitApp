package com.jolabs.looplog.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.jolabs.looplog.database.dao.HabitDao
import com.jolabs.looplog.database.entity.HabitEntryTable
import com.jolabs.looplog.database.entity.HabitTable
import com.jolabs.looplog.database.entity.RepeatTable
import com.jolabs.looplog.database.entity.StreakTable


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