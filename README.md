# LushSense

LushSense — приложение для анализа тональности русскоязычного текста. Это Kotlin Multiplatform (KMP) монорепозиторий: Android и iOS используют общую Kotlin-логику, сохраняя нативный интерфейс.

## Модули

- `sharedLogic` — общая логика, сеть и локальное хранение.
- `androidApp` — Android-приложение на Jetpack Compose.
- `iosApp` — iOS-приложение на SwiftUI.

## Запуск

- Android: `./gradlew :androidApp:assembleDebug`
- iOS: откройте `iosApp/iosApp.xcodeproj` в Xcode и запустите схему `iosApp`.

Для анализа текста должен быть запущен backend в локальной сети; адрес сервера задаётся в платформенных `ApiConfig`.
