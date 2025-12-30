# Incident Observability SDK & Android App

A comprehensive Android application featuring a reusable incidents observability SDK library, built with clean architecture principles and modern Android development practices.

## 🚀 How to Run the Project

### Prerequisites
- **Android Studio**: Flamingo or later
- **Minimum SDK**: API 26 (Android 8.0)
- **Target SDK**: API 35 (Android 15)
- **Java**: JDK 11 or later
- **Gradle**: Version 8.1.1+

### Steps to Run

1. **Clone the repository**
   ```bash
   git clone [<repository-url>](https://github.com/csalasm1/observer_project_carlos_gabriel_salas)
   cd ObserverProject
   ```

2. **Open in Android Studio**
   - Launch Android Studio
   - Select "Open an existing Android Studio project"
   - Navigate to the cloned directory and select it

3. **Build the project**
   - Wait for Gradle sync to complete
   - Build → Make Project (Ctrl+F9 / Cmd+F9)

4. **Run the application**
   - Connect an Android device or start an emulator
   - Run → Run 'app' (Shift+F10 / Ctrl+R)

5. **Run tests** (optional)
   ```bash
   ./gradlew :incident-sdk:test
   ```

## 🏗️ Architecture Overview

This project follows **Clean Architecture** principles and is organized into three main modules:

### 📦 Project Structure

```
ObserverProject/
├── incident-sdk/          # Reusable observability SDK library
├── design-system/         # UI component library (Atomic Design)
└── app/                   # Android application module
```

### 🔧 Technical Architecture

#### **Incident SDK (`incident-sdk`)**
- **Clean Architecture Layers**:
  - **Public API**: `IncidentTracker` singleton, configuration classes
  - **Internal Domain**: Core business logic, repository pattern
  - **Storage Layer**: Pluggable storage implementations (Room-based)

- **Key Components**:
  - `IncidentTracker`: Thread-safe singleton facade
  - `IncidentRepository`: Data aggregation and business logic
  - `RoomIncidentStorage`: Room-based persistence with FIFO eviction

#### **Design System (`design-system`)**
- **Atomic Design Pattern**:
  - **Atoms**: Colors, typography, shapes, spacing
  - **Molecules**: Buttons, headers, navigation bars
  - **Organisms**: Complete screen layouts

- **Theme System**: Centralized design tokens with light/dark theme support

#### **Application (`app`)**
- **MVVM + Clean Architecture**:
  - **Presentation**: ViewModels with StateFlow, Jetpack Compose UI
  - **Domain**: Use Cases for business logic encapsulation
  - **Data**: Repository pattern (direct SDK usage)

### 🎯 Key Technical Decisions

#### **Observability SDK Design**
- **Singleton Pattern**: `IncidentTracker` ensures single instance across the app
- **Fail-Fast Initialization**: Clear error messages if SDK not initialized
- **Thread Safety**: Mutex-based synchronization and `@Volatile` fields
- **Fire-and-Forget**: Non-blocking incident tracking with background coroutines
- **Storage Abstraction**: Pluggable storage (currently Room-only with FIFO eviction)

#### **UI Architecture**
- **Jetpack Compose**: Declarative UI with unidirectional data flow
- **StateFlow**: Reactive UI state management
- **Hilt DI**: Dependency injection for ViewModels and Use Cases
- **Atomic Design**: Reusable, composable UI components
- **Material 3**: Modern design system with dynamic theming

#### **Data Flow & State Management**
- **Reactive Streams**: Flow/StateFlow for real-time incident updates
- **Use Case Pattern**: Clean separation of business logic from ViewModels
- **Repository Pattern**: Centralized data access abstraction

#### **Testing Strategy**
- **Unit Tests**: Comprehensive coverage for SDK core logic (31 tests)
- **Fake Storage**: In-memory implementation for isolated testing
- **Coroutine Testing**: `UnconfinedTestDispatcher` for immediate execution
- **Thread Safety**: Verified concurrent operation correctness

#### **Performance Considerations**
- **Background Processing**: Coroutines on `Dispatchers.IO` for storage operations
- **Lazy Initialization**: Room database singleton pattern
- **Memory Management**: Configurable incident limits with automatic cleanup
- **UI Optimization**: `collectAsState()` and `SharedFlow` for efficient updates

## ✨ Features

- **📊 Real-time Incident Tracking**: Monitor app incidents with configurable metadata
- **📈 Interactive Dashboard**: Time-based charts with filtering (15/30/60/90 minutes)
- **🎨 Design System**: Consistent UI components with light/dark theme support
- **🔧 Configurable Storage**: Room-based persistence with size limits
- **🚀 Crash Simulation**: Safe crash testing in debug builds only
- **📱 Responsive UI**: Modern Material 3 design with Jetpack Compose
- **🧪 Comprehensive Testing**: Full unit test coverage for SDK reliability

## 🛠️ Development Notes

- **SDK Initialization**: Must be called in `Application.onCreate()` before any tracking
- **Thread Safety**: All public SDK methods are thread-safe and non-blocking
- **Storage Limits**: Configurable max incidents with automatic oldest-first removal
- **Debug Features**: Crash simulation only works in debug builds
- **Navigation**: Compose Navigation with bottom bar integration

---

## Overview

# Homescreen
<img width="405" height="886" alt="Captura de pantalla 2025-12-30 a la(s) 8 31 29 a  m" src="https://github.com/user-attachments/assets/868c2077-ea36-4516-a38d-df48104c7d4c" />

# Crash Simulation Screen
<img width="407" height="886" alt="Captura de pantalla 2025-12-30 a la(s) 8 33 10 a  m" src="https://github.com/user-attachments/assets/228042f3-a50b-48f6-9321-eb4da17db820" />

# Real-time Dashboard
<img width="409" height="891" alt="Captura de pantalla 2025-12-30 a la(s) 8 33 56 a  m" src="https://github.com/user-attachments/assets/9da4fa5c-051b-49cc-8379-8a3d64e99699" />
<img width="404" height="888" alt="Captura de pantalla 2025-12-30 a la(s) 8 34 13 a  m" src="https://github.com/user-attachments/assets/d22761f9-2a99-41b5-8f2b-f1156b6af749" />


