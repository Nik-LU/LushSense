package com.example.iiushka.data

import android.os.Build

actual object ApiConfig {

    actual val baseUrl: String = if (isEmulator()) {
        // 10.0.2.2 — алиас хоста Mac'а для Android-эмулятора.
        "http://10.0.2.2:5001"
    } else {
        // Реальное устройство в Wi-Fi → LAN-IP Mac'а.
        // Обновляй, если IP Mac'а поменялся (ipconfig getifaddr en0).
        "http://192.168.0.23:5001"
    }

    private fun isEmulator(): Boolean {
        return (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
                || Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.HARDWARE.contains("goldfish")
                || Build.HARDWARE.contains("ranchu")          // ← новое, API 30+
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for")
                || Build.MANUFACTURER.contains("Genymotion")
                || Build.PRODUCT.contains("sdk_gphone")       // ← Google API эмуляторы
                || Build.PRODUCT.contains("sdk_google")
                || Build.PRODUCT.contains("google_sdk")
                || Build.PRODUCT.contains("vbox86p")
                || Build.PRODUCT.contains("emulator")
                || Build.PRODUCT.contains("simulator")
    }
}
