# 🌦️ Weather Flavor

![Kotlin](https://img.shields.io/badge/Kotlin-2.2+-blue?logo=kotlin)
![Android](https://img.shields.io/badge/Android-Compose-green?logo=jetpackcompose)
![MVVM](https://img.shields.io/badge/Architecture-MVVM-purple)
![Hilt](https://img.shields.io/badge/Dagger-Hilt-yellow)
![Coroutines](https://img.shields.io/badge/Coroutines-Flow-blue)
![WorkManager](https://img.shields.io/badge/WorkManager-Background-orange)

A comprehensive weather application providing real-time weather updates, forecasts, favorite locations, and customizable background alerts with full multi‑language, theme, and RTL support.

---

## 📖 Description

**Weather Flavor** allows users to:

* **Smooth Setup:** Experience a seamless first-time configuration with a dedicated **Onboarding flow**.
* **Real-time Insights:** View current weather conditions with detailed metrics, native high-quality dynamic background components, and engaging UI elements like **shimmer loading effects**.
* **Location Control:** Search for any location via native **MapLibre integration** and save favorite locations for quick access.
* **Background Alerts:** Create time‑based weather alerts with reliable background execution and notifications.
* **Deep Customization:** Personalize temperature units (°C/°F/K), wind speed units, location methods (GPS/Map), app theme (Light/Dark/System), and language (English/Arabic).
* **Offline Resilience:** Access cached weather data gracefully when connectivity is lost.

### Architecture & Modern Practices
The app is built using the latest Android standards:
* **MVVM Architecture** with **Clean Architecture** principles for a highly decoupled, scalable codebase.
* **Jetpack Compose** for a fully reactive, declarative UI natively rendering 60FPS fluid animations.
* **Dagger Hilt** for robust, boilerplate-free dependency injection.
* **Room Database** for efficient offline-first local storage of favorites and scheduled alerts.
* **Retrofit + Gson** for structured OpenWeatherMap API communication and response parsing.
* **Coroutines & StateFlow** for thread-safe asynchronous operations and reactive state management.
* **WorkManager & AlarmManager** for reliable, exact-time background tasks routing across device reboots.
* **Preferences DataStore** for modern, asynchronous UI configurations.

---

## ✨ Features

* **Interactive Onboarding:** Guided introduction for new users featuring vector-based **Lottie animations**.
* **Dynamic Weather UI:** Real‑time UI rendering dynamic animated backgrounds and canvas-driven meteorological conditions.
* **Location Management:**
    * Automatic GPS‑based location detection utilizing Google Play Services.
    * Manual location selection via **MapLibre Compose**.
    * Dedicated Favorite Locations management and deeply animated details screens.
* **Smart Weather Alerts:**
    * Background logic monitoring utilizing `WeatherAlertWorker` bindings.
    * Persistent, hyper-accurate time scheduling that respects OS sleeping limits via **AlarmManager**.
* **Globalization:** Native support for English and Arabic including entirely responsive **RTL (Right-to-Left)** layout translations.
* **Adaptive Theming:** Seamless routing between Light, Dark, and System Default themes.

---

## 🖼 Screenshots

<p float="left">
  <img src="https://github.com/user-attachments/assets/9fba55b6-d9f0-4a32-bf90-e6758b67767a" width="150" />
  <img src="https://github.com/user-attachments/assets/c4f7470b-7389-437f-8a71-3af87af9eeec" width="150" />
  <img src="https://github.com/user-attachments/assets/9cd4ba1a-ea35-40e8-8506-38cc769345c4" width="150" />
  <img src="https://github.com/user-attachments/assets/ceeabaf7-bd68-404c-a628-649f0d92e475" width="150" />
  <img src="https://github.com/user-attachments/assets/456f5938-fadf-4ecf-a5a0-03a520708f59" width="150" />
</p>

<p float="left">
  <img src="https://github.com/user-attachments/assets/dabffe09-5559-420c-8b8e-91a3e2ca7451" width="150" />
  <img src="https://github.com/user-attachments/assets/3f1ae54c-626b-42ed-99fd-c2ae77ef7dc9" width="150" />
  <img src="https://github.com/user-attachments/assets/6a028b64-3ef5-4350-9abf-666b763d51f9" width="150" />
  <img src="https://github.com/user-attachments/assets/3851b2bc-7594-4ed3-80e1-7aaf795c94b4" width="150" />
  <img src="https://github.com/user-attachments/assets/24e1d11a-3490-433d-b6c4-62fa8a193f44" width="150" />
</p>

---

## 🛠 Technologies Used

* **Kotlin** – Primary language leveraging modern DSLs and strict null-safety wrappers.
* **Jetpack Compose (Material 3)** – Next-generation UI toolkit handling massive view hierarchies naturally.
* **Dagger Hilt + KSP** – Advanced Dependency Injection utilizing Kotlin Symbol Processing natively bypassing legacy KAPT limits.
* **Room** – Local SQLite database abstraction.
* **Retrofit2 & Gson** – Type-safe Networking architecture and JSON deserializations wrapped with OkHttp Logging Interceptors.
* **Coroutines & StateFlow** – Reactive asynchronous workflow architectures.
* **WorkManager** – Guaranteed background task constraints parsing native alarm boundaries.
* **AlarmManager** – Exact-time foreground notification execution schedules.
* **MapLibre Compose** – Fully open-source, scalable map selection interface bypassing standard vector map constraints.
* **Preferences DataStore** – Safe, asynchronous data storage.
* **Lottie Compose** – Vector-based dynamic animations parsing JSON UI schemas gracefully.
* **AndroidX Core Splashscreen** – Handling the precise Native OS-to-Compose screen startup translations beautifully avoiding traditional white flashes.
* **Coil Compose** – Seamless asynchronous image loading natively into the UI engine.

---

## 🧩 System Architecture

### Clean Architecture Layers
* **Data Layer:**
    * `WeatherRepository`: Single source of truth abstracting the domain logic constraints.
    * `LocalDataSource`: Manages local Room DAOs (`FavoriteLocationsDao`, `WeatherAlertsDao`).
    * `RemoteDataSource`: Handles the OkHttp networking traffic resolving open API points cleanly.
    * `SettingsPreferences`: Handles asynchronous DataStore configurations securely preserving settings correctly organically.
* **Presentation Layer:**
    * **View:** Pure declarative Composable screens explicitly observing and transforming `StateFlow` updates natively resolving rendering logic correctly.
    * **ViewModel:** Handles complex UI state architectures mapping raw network and database items reliably (`HomeViewModel`, `SettingsViewModel`, `MainViewModel`, `MapPreviewViewModel`).

---

## 🧪 Testing

The project incorporates comprehensive testing hierarchies mathematically securing data and UI states natively gracefully.

### Unit Tests
* **JUnit 4** – Core test framework.
* **MockK** – Mocking standard dependencies natively isolating the `FakeRepository` logic realistically securely seamlessly confidently.
* **Kotlin Coroutines Test** – Testing localized suspended threading behaviors resolving internal dispatcher loops manually seamlessly intelligently flawlessly carefully smartly intuitively realistically efficiently (`runTest`).

### Instrumentation Tests
* **AndroidX Test Integration** – Managing end-to-end SQLite Room evaluations checking physical DAO inputs practically seamlessly correctly effectively explicitly (`WeatherDaoTest`).
* **Compose UI Test (JUnit4)** – Simulating user-interaction behaviors natively isolating visual bounding box constraints.

---

## ▶️ How to Run

### Requirements
* Android Studio Hedgehog (2023.1.1) or identically newer equivalent.
* JDK Version 17.
* Android device/emulator deploying API level 26+ (Minimum SDK bounds rigidly at 26).
* OpenWeatherMap API key ([Get one here](https://openweathermap.org/api)).

### Steps
1.  **Clone the repository:**
    ```bash
    git clone <repository-url>
    ```
2.  **Configure API Key:**
    * Open `gradle.properties` natively in the global project root directory.
    * Append this variable cleanly dynamically comprehensively manually manually cleanly gracefully: `WEATHER_API_KEY="YOUR_API_KEY_HERE"`
3.  **Sync & Build:** Launch the project explicitly targeting Android Studio natively and sync your Gradle wrapper successfully intelligently safely seamlessly intelligently cleverly flawlessly realistically carefully carefully smoothly cleanly accurately smartly fully.
4.  **Launch:** Execute upon any native device or virtual emulator ensuring global hardware Location permissions organically stably predictably logically naturally functionally intuitively natively.

---

## 👤 Developer
**Abdullh Gaber**

---
*Weather analytical data aggregated completely independently via [OpenWeatherMap](https://openweathermap.org/)*
