# Project Overview: Matchplay.events Native Client

This file provides context for the AI assistant based on the project's technical design document (`design.md`).

Always use `design.md` as context.

## Core Technologies

* **Language:** Kotlin
* **Framework:** Kotlin Multiplatform (KMP)
* **UI:** Jetpack Compose (Android), Compose for Desktop (JVM)
* **Target Platforms:** Android, Desktop (JVM). iOS is a future consideration.

## Architecture & Patterns

* **Pattern:** Model-View-ViewModel (MVVM) is the recommended pattern.
* **Structure:** Standard KMP structure using the Compose Multiplatform template:
  * `composeApp`: The main KMP module containing shared code and platform-specific source sets.
    * `commonMain`: Core logic, ViewModels, Repositories, Use Cases, Data Models, API Client, DI definitions, shared Compose resources.
    * `androidMain`: Android-specific UI (Compose), platform implementations (`actual`), DI initialization, AndroidManifest.xml, Android resources.
    * `desktopMain`: Desktop-specific UI (Compose), platform implementations (`actual`), DI initialization, main entry point.
    * `iosMain`: iOS-specific platform implementations (`actual`), DI initialization (if needed for shared logic used by iOS).
  * `iosApp`: The Xcode project for the iOS application, containing SwiftUI/UIKit code, Info.plist, assets, and configuration. Depends on `composeApp`.
* **Dependency Injection:** Koin is recommended.
* **Asynchronous Operations:** Kotlin Coroutines (`Flow`, `StateFlow`, `suspend` functions).

## Key Libraries & Dependencies

* **Networking:** Ktor Client (with platform-specific engines like OkHttp/CIO).
* **Serialization:** kotlinx.serialization (integrated with Ktor).
* **Testing:**
  * kotlin.test (unit tests in `commonTest`).
  * Ktor MockEngine (for API service testing).
  * Limited integration tests against the live API (in platform test sets).
* **Platform Abstraction:** KMP `expect`/`actual` mechanism for:
  * Secure API key storage (Matchplay, Vision API).
  * Image capture (CameraX/Activity Result API on Android, Webcam/File Picker on Desktop).
  * File system access (Android/JVM APIs, potentially abstracted via Okio/Korio/FileKit).
* **Potential:** Mockk (mocking), Okio/Korio/FileKit (file I/O), Google Cloud Vision AI (OCR).

## Development Process & Methodology

* **TDD:** Test-Driven Development is mandatory. Tests should precede implementation.
* **Testing Focus:** Heavy emphasis on unit tests in `commonTest`. Use exported Matchplay JSON data for mock API responses.
* **Code Style:** All Kotlin code should be formatted using ktfmt with the `kotlinlang` style.

## Core Functionality & Features

* **API Interaction:** Communicates with the Matchplay.events REST API.
* **Features:**
  * Tournament Discovery (nearby, registered).
  * Tournament Details View (focus on "Group Match Play" format).
  * Real-time Player Status Pane (rank, current game/round).
  * Manual Score Suggestion.
  * LLM/OCR-based Score Extraction from Images (using Cloud Vision API recommended).
* **Authentication:** Requires Matchplay Premium API Key for certain actions. Vision API key needed for OCR feature.

## Important Considerations

* **State Management:** Handle complex UI state derived from multiple async API calls (e.g., tournament details + standings + rounds) within ViewModels using `StateFlow`.
* **LLM Feature:** Requires robust error handling, user confirmation UI for extracted scores, and secure management of the Vision API key.
* **Platform Differences:** Use `expect`/`actual` diligently for platform-specific APIs.
