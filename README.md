# 🌦️ WeatherApp

![Kotlin](https://img.shields.io/badge/Kotlin-1.9+-blue?logo=kotlin)
![Android](https://img.shields.io/badge/Android-Compose-green?logo=jetpackcompose)
![MVVM](https://img.shields.io/badge/Architecture-MVVM-purple)
![Hilt](https://img.shields.io/badge/Dagger-Hilt-yellow)
![Coroutines](https://img.shields.io/badge/Coroutines-Flow-blue)
![WorkManager](https://img.shields.io/badge/WorkManager-Background-orange)

A comprehensive weather application providing real-time weather updates, forecasts, favorite locations, and customizable background alerts with full multi‑language, theme, and RTL support.

---

## 📖 Description

WeatherApp allows users to:

* **Smooth Setup:** Experience a seamless first-time configuration with a dedicated **Onboarding flow**.
* **Real-time Insights:** View current weather conditions with detailed metrics and engaging UI elements like **shimmer loading effects**.
* **Location Control:** Search for any location via **Google Maps integration** and save favorite locations for quick access.
* **Background Alerts:** Create time‑based weather alerts with reliable background execution and notifications.
* **Deep Customization:** Personalize temperature units (°C/°F/K), wind speed units, location methods (GPS/Map), app theme (Light/Dark/System), and language (English/Arabic).
* **Offline Resilience:** Access cached weather data gracefully when connectivity is lost.

### Architecture & Modern Practices
The app is built using the latest Android standards:
* **MVVM Architecture** with **Clean Architecture** principles for a scalable codebase.
* **Jetpack Compose** for a fully reactive and declarative UI.
* **Dagger Hilt** for robust dependency injection.
* **Room** for efficient local storage of favorites and alerts.
* **Retrofit + Gson** for OpenWeatherMap API integration.
* **Coroutines & Flow** for thread-safe asynchronous operations and reactive state management.
* **WorkManager & AlarmManager** for reliable, exact-time background tasks.
* **DataStore** for modern, asynchronous user preferences storage.

---

## ✨ Features

* **Interactive Onboarding:** Guided introduction for new users featuring **Lottie animations**.
* **Dynamic UI:** Real‑time conditions with smooth state transitions and shimmer loading states.
* **Location Management:**
    * Automatic GPS‑based location detection.
    * Manual location selection via **Google Maps Compose**.
    * Dedicated Favorite Locations management and details screens.
* **Smart Weather Alerts:**
    * Background monitoring using `WeatherAlertWorker` and `WeatherAlarmReceiver`.
    * Persistent scheduling that respects system reboots via **AlarmManager**.
* **Globalization:** Native support for English and Arabic with full **RTL (Right-to-Left)** layout adaptation.
* **Adaptive Theming:** Seamless switching between Light, Dark, and System Default themes.

---

## 🖼 Screenshots

<p float="left">
  <img src="assets/splash.jpeg" width="170" />
  <img src="assets/settings.jpeg" width="170" />
  <img src="assets/home1.jpeg" width="170" />
  <img src="assets/home2.jpeg" width="170" />
  <img src="assets/home_ar.jpeg" width="170" />
</p>

<p float="left">
  <img src="assets/permission.jpeg" width="170" />
  <img src="assets/map.jpeg" width="170" />
  <img src="assets/fav.jpeg" width="170" />
  <img src="assets/alerts.jpeg" width="170" />
  <img src="assets/notification.jpeg" width="170" />
</p>

---

## 🛠 Technologies Used

* **Kotlin** – Primary language.
* **Jetpack Compose** – Modern UI toolkit.
* **Dagger Hilt** – Dependency injection.
* **Room** – Local SQLite database abstraction.
* **Retrofit2 & Gson** – Networking and JSON serialization.
* **Coroutines & StateFlow** – Reactive programming.
* **WorkManager** – Background task scheduling.
* **AlarmManager** – Exact-time notification scheduling.
* **Google Maps Compose** – Map selection interface.
* **Preferences DataStore** – Safe, asynchronous data storage.
* **Lottie** – Vector-based animations.
* **Accompanist** – System UI and Permission helpers.

---

## 🧩 System Architecture

### Clean Architecture Layers
* **Data Layer:**
    * `WeatherRepository`: Single source of truth for UI data.
    * `LocalDataSource`: Manages Room DAOs (`FavoriteLocationsDao`, `WeatherAlertsDao`).
    * `SettingsPreferences`: Handles DataStore configurations.
* **Presentation Layer:**
    * **View:** Composable screens observing `StateFlow` from ViewModels.
    * **ViewModel:** Handles UI state and user events (`HomeViewModel`, `SettingsViewModel`, `MainViewModel`).

---

## 🧪 Testing

The project includes comprehensive testing at both unit and instrumented levels to ensure reliability.

### Unit Tests
* **JUnit 4** – Core test framework.
* **Mockk** – Mocking dependencies for `FakeRepository` and data sources.
* **Kotlin Coroutines Test** – Testing suspending functions with `runTest` and `StandardTestDispatcher`.
* **InstantTaskExecutorRule / MainDispatcherRule** – Managing background execution in view models.

### Instrumentation Tests
* **AndroidX Test** – For testing Room DAOs (`WeatherDaoTest`) and local database integrity.

---

## ▶️ How to Run

### Requirements
* Android Studio Hedgehog (2023.1.1) or newer.
* JDK 17.
* Android device/emulator with **API level 26+**.
* OpenWeatherMap API key ([Get one here](https://openweathermap.org/api)).

### Steps
1.  **Clone the repository:**
    ```bash
    git clone <repository-url>
    ```
2.  **Configure API Key:**
    * Open `gradle.properties` in the project root.
    * Add: `WEATHER_API_KEY="YOUR_API_KEY_HERE"`
3.  **Sync & Build:** Open the project in Android Studio and sync Gradle.
4.  **Launch:** Run on your device/emulator. Ensure location services are enabled.

---

## 👤 Developer
**Abdullh Gaber**

---
*Weather data provided by [OpenWeatherMap](https://openweathermap.org/)*
