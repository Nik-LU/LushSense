package com.example.iiushka.data

import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

/** Имя файла БД. Общее для Android и iOS. */
internal const val DATABASE_NAME = "lushsense.db"

expect fun getDatabaseBuilder(): RoomDatabase.Builder<AppDatabase>

fun buildDatabase(): AppDatabase {
    return getDatabaseBuilder()
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
}
