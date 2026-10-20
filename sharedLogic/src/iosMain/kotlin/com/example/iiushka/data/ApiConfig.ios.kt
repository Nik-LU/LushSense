package com.example.iiushka.data

import platform.Foundation.NSProcessInfo

actual object ApiConfig {

    actual val baseUrl: String = if (isSimulator()) {
        // iOS-симулятор использует сеть Mac напрямую.
        "http://127.0.0.1:5001"
    } else {
        // Реальный iPhone в Wi-Fi → IP Mac'а, на котором слушает сервер.
        "http://10.8.1.1:5001"
    }

    private fun isSimulator(): Boolean =
        NSProcessInfo.processInfo.environment["SIMULATOR_DEVICE_NAME"] != null
}
