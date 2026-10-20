package com.example.iiushka.data

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

/** Application-context, инициализируется в LushSenseApp.onCreate(). */
object AndroidAppContext {
    lateinit var applicationContext: Context
        private set

    fun init(context: Context) {
        applicationContext = context.applicationContext
    }
}

actual fun getDatabaseBuilder(): RoomDatabase.Builder<AppDatabase> {
    val context = AndroidAppContext.applicationContext
    val dbFile = context.getDatabasePath(DATABASE_NAME)
    return Room.databaseBuilder<AppDatabase>(
        context = context,
        name = dbFile.absolutePath
    )
}
