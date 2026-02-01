# Samsung Assessment App 

[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.0-purple.svg)](https://kotlinlang.org/)
[![Android](https://img.shields.io/badge/Android-24%2B-green.svg)](https://developer.android.com/)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-Latest-blue.svg)](https://developer.android.com/jetpack/compose)


> **Quick Links:** [📥 Download APK](https://drive.google.com/drive/folders/1TI9BhZdgS0S_hVSj3kL4nwWXe5y60MDh?usp=sharing) | [📄 Blog Article](BlogArticle.md) | [📚 Documentation](#-features)

A comprehensive Android application developed as part of the Samsung Developer Tech Support Internship Assessment (January 2026). This project showcases three distinct Android applications integrated into a single cohesive app with a modern Material Design 3 interface.

## 📥 Download APK

Want to try the app? Download the latest APK here:

[![Download APK](https://img.shields.io/badge/Download-APK-blue.svg?style=for-the-badge&logo=android)](https://drive.google.com/drive/folders/1TI9BhZdgS0S_hVSj3kL4nwWXe5y60MDh?usp=sharing)

**Note:** You may need to enable "Install from Unknown Sources" in your Android settings to install the APK.

## 📱 Features

### 1. Scientific Calculator (Task 1 - Application 1)
A fully-featured scientific calculator with advanced mathematical operations and a comprehensive calculation history system.

**Key Features:**
-  Basic arithmetic operations (Addition, Subtraction, Multiplication, Division)
-  Scientific functions (sin, cos, tan, log, ln)
-  Advanced operations (square root, power, square)
-  Parentheses support for complex expressions
-  Calculation history with quick access
-  Interactive cursor positioning for easy editing
-  Real-time expression evaluation
-  Error handling and validation

### 2. Music Player (Task 1 - Application 2)
A modern music player that reads and plays audio files from the device's storage with an intuitive playback interface.

**Key Features:**
-  Automatic scanning of device audio files
-  Media playback controls (Play, Pause, Stop)
-  Track navigation (Next, Previous)
-  Shuffle and Repeat modes
-  Interactive seek bar for precise playback control
-  Full playlist view with all detected songs
-  Album art display
-  Real-time progress tracking
-  Dynamic permission handling (Android 13+)

### 3. Multi-Sensor Dashboard (Task 2)
A comprehensive sensor monitoring application that displays real-time data from multiple device sensors with beautiful visualizations.

**Supported Sensors:**
- 📊 **Accelerometer** - Motion detection with 3-axis visualization
- 🔄 **Gyroscope** - Rotation tracking with bar graph display
- 🧭 **Magnetic Field** - Compass visualization
- 💡 **Light Sensor** - Ambient light detection with dynamic brightness
- 📡 **Proximity Sensor** - Distance detection with visual feedback
- 🌡️ **Pressure Sensor** - Atmospheric pressure with altitude calculation
- 🌡️ **Temperature Sensor** - Ambient temperature (Celsius & Fahrenheit)
- 💧 **Humidity Sensor** - Relative humidity with wave visualization

**Dashboard Features:**
- Real-time sensor data updates
- Custom visualizations for each sensor type
- Automatic sensor availability detection
- Active sensor count display
- Proper lifecycle management
- Smooth animations and transitions

## 🏗️ Architecture

The application follows the **MVVM (Model-View-ViewModel)** architecture pattern with clear separation of concerns:

```
com.example.internassessmentapp/
├── data/                          # Data models
│   └── Song.kt                    # Music track data model
├── ui/
│   ├── calculator/                # Calculator UI
│   │   └── CalculatorScreen.kt
│   ├── music/                     # Music Player UI
│   │   └── MusicPlayerScreen.kt
│   ├── sensor/                    # Sensor Dashboard UI
│   │   └── SensorScreen.kt
│   └── theme/                     # Material Design 3 theming
│       ├── Color.kt
│       ├── Theme.kt
│       └── Type.kt
├── viewmodel/                     # Business logic layer
│   ├── CalculatorViewModel.kt
│   ├── MusicViewModel.kt
│   └── SensorViewModel.kt
└── MainActivity.kt                # Main navigation hub
```

## 🛠️ Technology Stack

- **Language:** Kotlin
- **UI Framework:** Jetpack Compose (Material Design 3)
- **Architecture:** MVVM (Model-View-ViewModel)
- **Minimum SDK:** Android 7.0 (API 24)
- **Target SDK:** Android 14 (API 34)

### Key Libraries & Components:
- **Jetpack Compose** - Modern declarative UI
- **AndroidX Lifecycle** - ViewModel & lifecycle management
- **MediaPlayer** - Audio playback
- **SensorManager** - Hardware sensor access
- **Material Design 3** - UI components and theming
- **Kotlin Coroutines** - Asynchronous programming
- **ContentResolver** - Media file scanning

## 📋 Prerequisites

- Android Studio Hedgehog (2023.1.1) or later
- JDK 17 or higher
- Android SDK with minimum API level 24
- Physical Android device (recommended for sensor testing)

## 🚀 Installation & Setup

1. **Clone the repository:**
```bash
git clone https://github.com/DittoOne/SamsungAssesmentAPP.git
cd intern-assessment-app
```

2. **Open in Android Studio:**
   - Launch Android Studio
   - Select "Open an Existing Project"
   - Navigate to the cloned repository folder
   - Wait for Gradle sync to complete

3. **Configure the project:**
   - Ensure you have the required SDK versions installed
   - Sync project with Gradle files

4. **Run the application:**
   - Connect an Android device or start an emulator
   - Click the "Run" button or use `Shift + F10`
   - Grant necessary permissions when prompted

## 🔐 Required Permissions

The application requires the following permissions:

```xml
<!-- For Music Player (Android 13+) -->
<uses-permission android:name="android.permission.READ_MEDIA_AUDIO" />

<!-- For Music Player (Android 12 and below) -->
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" 
                 android:maxSdkVersion="32" />

<!-- For Sensor Dashboard -->
<!-- Sensors are accessed through the SensorManager API (no special permissions required) -->
```

**Note:** The app handles runtime permissions dynamically for audio file access on Android 13+.

## 📖 Usage Guide

### Calculator
1. Launch the app and select "Calculator"
2. Enter mathematical expressions using the on-screen buttons
3. Tap anywhere in the expression to position the cursor
4. Use scientific functions (sin, cos, tan, log, ln) for advanced calculations
5. Press "=" to evaluate the expression
6. Access calculation history by tapping the history icon

### Music Player
1. Select "Music Player" from the main menu
2. Grant audio file permissions when prompted
3. The app will automatically scan and display your music library
4. Tap any song to start playback
5. Use playback controls to play, pause, skip tracks
6. Enable shuffle/repeat modes as needed
7. Drag the seek bar to navigate within a track
8. Tap the playlist icon to view all available songs

### Sensor Dashboard
1. Choose "Sensor Dashboard" from the main menu
2. The app will display all available sensors on your device
3. Observe real-time sensor data with visual representations
4. Sensors update automatically while the screen is active
5. Unavailable sensors will be marked accordingly



## 📝 Code Quality

The codebase adheres to the following best practices:

-  **Clean Architecture** - Separation of UI, business logic, and data layers
-  **MVVM Pattern** - Proper separation of concerns
-  **Compose Best Practices** - State management, remember, LaunchedEffect
-  **Lifecycle Awareness** - Proper resource cleanup in ViewModels
-  **Error Handling** - Try-catch blocks for media and sensor operations
-  **Material Design 3** - Modern, consistent UI/UX
-  **Kotlin Conventions** - Idiomatic Kotlin code with coroutines
-  **Responsive Design** - Adapts to different screen sizes

 Temperature/Humidity sensors are rare on modern smartphones





## 👨‍💻 Author

**Md Shahriar Rahman Bhuiyan**
- Assessment for: Samsung Developer Tech Support Internship
- Date: January 2026

## 📜 License

This project is created for assessment purposes as part of the Samsung Developer Tech Support Internship application process.

---

## 🙏 Acknowledgments

- Samsung Developer Tech Support Team for the opportunity
- Android documentation and community
- Material Design 3 guidelines
- Jetpack Compose community

---

**Note:** This application was developed as part of an internship assessment and demonstrates proficiency in Android development, Kotlin programming, Jetpack Compose, and proper software architecture patterns.
