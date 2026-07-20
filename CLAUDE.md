# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project overview

VSCP PokerTimer is a native Android app (Kotlin) for managing a poker sit'n'go tournament — it tracks blind levels and plays audio cues at configurable intervals. The project serves as a learning vehicle for native Android development.

- **minSdk 25** (Android 7.1 Nougat) — **targetSdk / compileSdk 33** (Android 13 Tiramisu)
- Supported managed-device test targets: API 27, 28, 30, 33 (only 28 currently active in CI)

## Common commands

```bash
# Build (debug + release)
./gradlew clean build

# Unit tests only
./gradlew testDebugUnitTest

# Single unit test class
./gradlew testDebugUnitTest --tests "com.github.arburk.vscp.app.service.TimerServiceTest"

# Single test method
./gradlew testDebugUnitTest --tests "com.github.arburk.vscp.app.service.TimerServiceTest.verifyTimeLeftFormat"

# Instrumented tests (requires connected device or emulator)
./gradlew connectedCheck

# Managed virtual device tests (downloads/boots emulator automatically)
./gradlew nexus5api28DebugAndroidTest

# Lint
./gradlew lint

# JaCoCo coverage report (HTML + XML under app/build/reports/jacoco/)
./gradlew jacocoTestReport

# Sonar analysis (requires SONAR_TOKEN env var)
./gradlew jacocoTestReport sonar
```

## Architecture

### Entry point and navigation

`MainActivity` is the single activity. It uses the **Navigation Component** (`nav_graph.xml`) to host three fragments:

| Fragment | Purpose |
|---|---|
| `MainScreen` | Landing screen with "Start" button |
| `PokerTimer` | Active timer UI (play/pause, blind display, level nav) |
| `RoundSettings` | ListView for editing blind levels (add/remove) |

`AppSettingsActivity` is a separate activity launched from the action bar — it is **not** part of the nav graph.

Deep navigation from a notification is handled in `MainActivity.deepNavigationHandler()` by reading a `targetFragment` string extra on the launch intent.

### TimerService — the heart of the app

`TimerService` is a **started + bound** service (`android.app.Service`). It is started explicitly so it outlives the activity, and bound so the activity can call its API directly.

Key responsibilities:
- Owns the `ConfigModel` (minPerRound, minPerWarning, `Array<Blind>`)
- Runs a `java.util.Timer` on a background `HandlerThread` (tick every 1 s)
- Plays audio cues: fight-countdown at t=6 s, one-minute warning at t=62 s
- Fires `NotificationCompat` when a new blind level starts
- Listens to `SharedPreferences` changes to update config live

**Non-standard ViewModel pattern**: `TimerService` holds a list of `PokerTimerViewModel` instances and calls `viewModel.update(this)` (via `postValue`) on every tick. Fragments call `timerService.registerViewModel(vm)` / `unregisterViewModel(vm)` in `onViewCreated` / `onDestroyView`. This bypasses the normal `ViewModelProvider` lifecycle — the ViewModel is created per-fragment but updated by the service.

### SharedPreferences — two separate stores

| Store | Access | Used for |
|---|---|---|
| `<packageName>_preferences` | `context.getSharedPreferences(packageName + "_preferences", MODE_PRIVATE)` | Timer settings: `min_per_round`, `min_per_warning` |
| Default preferences | `PreferenceManager.getDefaultSharedPreferences(context)` | Sound selections: `sound_next_round`, `sound_warning_of_next_round` |

Preference key constants are defined at top-level in `AppSettingsActivity.kt` and imported throughout.

### Notification channels (API ≥ 26)

`NotificationManagerWrapper.createNotificationChannel()` creates the channel once; subsequent calls are no-ops (the OS ignores channel recreation after first creation). Sound on the notification for API ≥ 26 must be set on the channel, not the notification builder — this is a known open TODO.

### Testing approach

- **Unit tests**: JUnit 5 (Jupiter) + Mockito. `TimerService` exposes `@VisibleForTesting internal` fields (`sharedPreferences`, `config`, `currentRound`) so tests inject a `MockSharedPreferences` directly and call `onCreate()` manually without an Android runtime.
- **Instrumented tests**: JUnit 5 via `de.mannodermaus.junit5` plugin + Espresso. Requires `DexOpener` (`com.github.tmurakami:dexopener`) to open final classes for Mockito on Android.
- Parallel test execution is enabled via `junit.jupiter.execution.parallel.enabled=true`.

### Build / dependency notes

- AGP `8.13.2`, Kotlin `1.9.22`, JVM target `1.8`
- JaCoCo `0.8.11` — coverage report task is `jacocoTestReport` (not the default Gradle one)
- SonarCloud project: `arburk_vscp` / org `arburk`
- `dexopener` is pulled from JitPack (`https://jitpack.io`)
