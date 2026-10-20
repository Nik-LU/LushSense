package com.example.iiushka.data

import androidx.room.Room
import androidx.room.RoomDatabase
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSUserDomainMask
actual fun getDatabaseBuilder(): RoomDatabase.Builder<AppDatabase> {
    val documents = NSSearchPathForDirectoriesInDomains(
        directory = NSDocumentDirectory,
        domainMask = NSUserDomainMask,
        expandTilde = true
    ).first() as String

    val path = "$documents/$DATABASE_NAME"
    return Room.databaseBuilder<AppDatabase>(name = path)
}
