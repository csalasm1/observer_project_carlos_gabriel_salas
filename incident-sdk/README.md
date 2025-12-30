# Incident SDK

A lightweight incidents observability SDK for Android applications written in Kotlin.

## Features

- **Clean Architecture**: Separation between public API, internal domain, and storage layers
- **Persistent Storage**: Uses Room database for reliable incident persistence
- **Thread-Safe**: All operations are thread-safe with proper synchronization
- **Non-Blocking**: Uses Kotlin coroutines for async operations
- **Automatic Metadata**: Automatically enriches incidents with app version and environment

## Installation

Add the module dependency to your app's `build.gradle.kts`:

```kotlin
dependencies {
    implementation(project(":incident-sdk"))
}
```

## Quick Start

### 1. Initialize the SDK

Initialize the SDK in your `Application.onCreate()`:

```kotlin
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        val config = IncidentConfig(
            appVersion = BuildConfig.VERSION_NAME,
            environment = if (BuildConfig.DEBUG) "debug" else "release",
            maxStoredIncidents = 500
        )

        IncidentTracker.init(this, config)
    }
}
```

### 2. Track Screens

Track screen navigation to automatically associate incidents with screens:

```kotlin
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        IncidentTracker.trackScreen("MainActivity")
    }
}

// With Compose
@Composable
fun ProfileScreen() {
    LaunchedEffect(Unit) {
        IncidentTracker.trackScreen("ProfileScreen")
    }
    // Screen content
}
```

### 3. Track Incidents

Track incidents when errors or notable events occur:

```kotlin
// Basic usage
IncidentTracker.trackIncident(
    errorCode = "NETWORK_001",
    severity = Severity.HIGH,
    message = "Failed to fetch user data"
)

// With custom metadata
try {
    apiService.fetchUserData(userId)
} catch (e: Exception) {
    IncidentTracker.trackIncident(
        errorCode = "API_ERROR",
        severity = Severity.CRITICAL,
        message = "API call failed: ${e.message}",
        screenName = "ProfileScreen",  // Optional: override current screen
        metadata = mapOf(
            "userId" to userId,
            "endpoint" to "/api/users",
            "errorType" to e::class.simpleName.orEmpty()
        )
    )
}
```

### 4. Get Incident Summary

Retrieve aggregated statistics from a ViewModel or coroutine:

```kotlin
class DashboardViewModel : ViewModel() {
    private val _summary = MutableStateFlow<IncidentSummary?>(null)
    val summary: StateFlow<IncidentSummary?> = _summary.asStateFlow()

    fun loadSummary() {
        viewModelScope.launch {
            _summary.value = IncidentTracker.getSummary()
        }
    }
}
```

## API Reference

### IncidentConfig

Configuration for SDK initialization:

| Property | Type | Default | Description |
|----------|------|---------|-------------|
| `appVersion` | String | Required | Your app's version |
| `environment` | String | "debug" | Environment (debug/staging/production) |
| `maxStoredIncidents` | Int | 1000 | Maximum incidents to store (FIFO) |

### Severity Levels

| Level | Use Case |
|-------|----------|
| `LOW` | Minor issues, cosmetic bugs |
| `MEDIUM` | Degraded functionality, workarounds available |
| `HIGH` | Core functionality impacted |
| `CRITICAL` | Crashes, security issues, data loss |

### IncidentSummary

Summary data returned by `getSummary()`:

| Property | Type | Description |
|----------|------|-------------|
| `totalIncidents` | Int | Total stored incidents |
| `incidentsByScreen` | Map<String, Int> | Count per screen |
| `incidentsBySeverity` | Map<Severity, Int> | Count per severity |

## Architecture

```
incident-sdk/
├── com.example.incidentsdk/           # Public API
│   ├── IncidentConfig.kt
│   ├── IncidentSummary.kt
│   ├── IncidentTracker.kt
│   ├── Severity.kt
│   └── StorageType.kt
├── com.example.incidentsdk.internal/  # Internal domain
│   ├── Incident.kt
│   ├── IncidentRepository.kt
│   └── IncidentStorage.kt
└── com.example.incidentsdk.storage/   # Room storage implementation
    ├── IncidentDao.kt
    ├── IncidentDatabase.kt
    ├── IncidentEntity.kt
    ├── IncidentMapper.kt
    └── RoomIncidentStorage.kt
```

## Thread Safety

- `IncidentTracker` uses `@Synchronized` for initialization
- Screen tracking uses `@Volatile` for visibility
- Room storage is inherently thread-safe
