package com.example.iiushka

import android.app.Application
import com.example.iiushka.data.AndroidAppContext

class LushSenseApp : Application() {
    override fun onCreate() {
        super.onCreate()
        AndroidAppContext.init(this)
    }
}
