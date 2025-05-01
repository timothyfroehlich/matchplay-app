# **Matchplay.events Native Client: Technical Design Document**

## **1\. Introduction**

### **1.1. Project Vision**

This document outlines the technical design for a native, cross-platform client application for the Matchplay.events platform. The application will be developed using Kotlin Multiplatform (KMP) and Jetpack Compose, providing a native user experience on Android and Desktop (JVM) platforms initially. The primary focus is on delivering core player-centric use cases, specifically tailored for the "Group match play" tournament format. Key features include tournament discovery, viewing tournament details (standings, rounds), a persistent player status display, manual score suggestion capabilities, and an innovative feature leveraging Large Language Model (LLM) technology for automated score extraction from pinball machine images. Development will strictly adhere to the Test-Driven Development (TDD) methodology. While immediate implementation targets Android and Desktop, the architecture will be designed with future extensibility to iOS in mind.

### **1.2. Document Purpose**

This design document serves as the definitive technical blueprint and specification for the Matchplay.events client application. Its primary audience is the development agent (potentially an LLM) responsible for implementing the software. It details the chosen architecture, frameworks, libraries, feature specifications, API interactions, testing strategies, and a granular breakdown of development tasks required to build the initial version of the application.

### **1.3. Target Platforms**

The initial target platforms for this application are:

* **Android:** Leveraging Jetpack Compose for the user interface.
* **Desktop (JVM):** Utilizing Compose for Desktop for the user interface, running on any platform supporting a Java Virtual Machine (JVM).

Future support for **iOS** is a consideration that influences the design of the shared codebase, particularly regarding library choices and the use of platform-agnostic patterns, although iOS-specific implementation is outside the scope of the initial development effort.

## **2\. Matchplay.events API & Functionality Overview**

### **2.1. Matchplay.events Context**

Matchplay.events is an online platform designed for organizing and participating in competitive pinball events. It facilitates tournament creation, player registration, management of various tournament formats, real-time scoring, and calculation of standings. This application aims to provide a dedicated, native interface for players interacting with the platform's services.

### **2.2. Core Player Functionality Mapping**

The initial version of this application will focus on the following key player interactions, derived from the Matchplay.events documentation:

* **Tournament Discovery:** Enabling players to find active pinball tournaments near their current location. The application should prioritize displaying tournaments for which the player is already registered.
* **Tournament Viewing (Group Match Play):** Providing access to the detailed view of a specific tournament, particularly those using the "Group match play" format. This includes viewing current tournament standings and information about current and past rounds.
* **Real-time Player Status:** Within the context of viewing a specific tournament, displaying a persistent, readily accessible pane showing the logged-in player's current rank, the details of their current round and game (including opponents and player order).
* **Score Suggestion:** Empowering players to submit suggested scores for the games they have just completed directly through the application.

### **2.3. Relevant REST API Endpoints & Data Models**

Analysis of the Matchplay.events REST API documentation reveals several key endpoints critical for implementing the specified player features. Communication with these endpoints will be performed using standard HTTP requests.

* **GET /tournaments**: This endpoint is central to tournament discovery.
  * **Purpose:** Retrieve a list of tournaments based on specified criteria.
  * **Method:** GET
  * **Key Parameters:** Location (lat, lng, radius), status (active), potentially player\_id to identify tournaments the user is registered for or participating in.
  * **Response:** Expected to return a JSON array of Tournament summary objects.
* **GET /tournaments/{id}**: Used to fetch comprehensive details for a single tournament.
  * **Purpose:** Retrieve detailed information about a specific tournament identified by its unique ID.
  * **Method:** GET
  * **Response:** Expected to return a detailed Tournament JSON object, including format-specific information.
* **GET /tournaments/{id}/standings**: Fetches the current ranking of players within a tournament.
  * **Purpose:** Retrieve the current standings list for a given tournament ID.
  * **Method:** GET
  * **Response:** Expected to return a JSON array of Standing objects, typically containing player\_id, rank, points, and player name/details.
* **GET /tournaments/{id}/rounds**: Retrieves information about the rounds played or currently active in a tournament.
  * **Purpose:** Fetch data about tournament rounds, crucial for displaying current game information and past results. May support filtering parameters (e.g., status=active).
  * **Method:** GET
  * **Response:** Expected to return a JSON array of Round objects. For "Group Match Play", these objects likely contain details about player groupings (Game objects), arenas, and potentially scores if completed.
* **GET /rounds/{id}**: Potentially required for fetching granular details of a specific round or game, if not fully covered by the /tournaments/{id}/rounds endpoint.
  * **Purpose:** Retrieve fine-grained details of a single round, such as specific player order within a game, arena status, etc.
  * **Method:** GET
  * **Response:** Expected to return a detailed Round or Game JSON object.
* **POST /rounds/{round\_id}/scores/suggest**: Allows authenticated players to submit suggested scores for a game they participated in.
  * **Purpose:** Submit player-reported scores for a specific game within a round.
  * **Method:** POST
  * **Authentication:** Required.
  * **Payload:** Requires a JSON body containing the scores for each player in the game.
  * **Response:** Likely indicates success or failure of the suggestion submission.

**Data Models:** Based on the API endpoints, the application will need Kotlin data classes to represent the JSON structures returned by the Matchplay API. Key models will include (but are not limited to): Tournament, Player, Standing, Round, Game, Score, ScoreSuggestionPayload. These models will be defined in the shared KMP module and annotated for use with kotlinx.serialization.

### **2.4. Authentication**

Certain API interactions, notably suggesting scores and potentially fetching personalized tournament lists, require authentication. The Matchplay API likely uses API keys for this purpose. The application must securely store and utilize the user's Premium Matchplay account API key to authenticate these requests. Secure storage mechanisms will be platform-specific and handled using KMP's expect/actual pattern.

### **2.5. API Interaction Considerations**

The structure of the Matchplay API necessitates careful consideration in the application design. Displaying the comprehensive player view envisioned (tournament list \-\> details \-\> standings \-\> current round \-\> persistent status pane) will likely involve orchestrating multiple API calls. For instance, loading the tournament details screen might require sequential or concurrent calls to GET /tournaments/{id}, GET /tournaments/{id}/standings, and GET /tournaments/{id}/rounds. The persistent player status pane, which needs rank and current game information, derives its data from the results of the standings and rounds calls. This implies that the application's state management layer must efficiently handle multiple asynchronous data streams, aggregate the results, and manage distinct loading and error states for different parts of the UI. Implementing caching strategies for frequently accessed, relatively static data (like tournament details or past rounds) could significantly improve performance and reduce API load.

Furthermore, the specific focus on the "Group Match Play" format requires that the data models and UI components accurately reflect its structure. API responses for rounds in this format are expected to detail player groupings per game instance and the specific order of play within those groups. The application must parse this information correctly and display it intuitively, especially within the persistent player status pane.

## **3\. Framework: Kotlin Multiplatform & Jetpack Compose**

### **3.1. Kotlin Multiplatform (KMP) Overview**

Kotlin Multiplatform (KMP) is a technology provided by JetBrains that allows developers to share Kotlin code across multiple platforms, including Android, iOS, JVM (Desktop, Backend), JavaScript (Web), and native binaries (Linux, macOS, Windows). The core principle is to write platform-agnostic code (business logic, data layers, presentation logic) once in a common module and implement platform-specific code (UI, platform API interactions) only where necessary.

### **3.2. Project Structure**

This project will adopt a standard KMP project structure to maximize code sharing while accommodating platform-specific needs:

* **shared Module:** This is the central KMP module containing code shared across all target platforms.
  * commonMain: Contains platform-independent Kotlin code. This includes core business logic, data models (annotated for serialization), repository interfaces and implementations (using shared networking client), ViewModel/Presenter classes, use cases, utility functions, and expect declarations for platform-specific functionality. The vast majority of the application's logic will reside here.
  * androidMain: Contains Kotlin code specific to the Android platform. This includes actual implementations for expect declarations in commonMain (e.g., for accessing Android-specific APIs like sensors, file system, secure storage), Android-specific configurations (e.g., database drivers, Ktor engine setup), and potentially platform-specific utility functions. Depends on commonMain.
  * desktopMain: Contains Kotlin code specific to the Desktop (JVM) platform. Similar to androidMain, it provides actual implementations for commonMain's expect declarations (e.g., file system access, Desktop-specific API key storage), JVM-specific configurations (e.g., Ktor engine), and any Desktop-only utilities. Depends on commonMain.
  * iosMain (Stub): A placeholder source set will be included. While no iOS implementation is planned initially, its presence facilitates future integration and encourages writing commonMain code that is mindful of iOS compatibility (e.g., avoiding JVM-exclusive libraries in shared code unless absolutely necessary).
* **androidApp Module:** The Android application module.
  * Depends on the shared module.
  * Contains Android-specific components: Activities, Jetpack Compose UI screens and composables, AndroidManifest.xml, resources (layouts, drawables, strings), dependency injection setup (initializing Koin), and code for interacting with Android SDK features.
* **desktopApp Module:** The Desktop application module.
  * Depends on the shared module.
  * Contains Desktop-specific components: The main application entry point (main function), Compose for Desktop UI screens and composables, window setup and management, dependency injection setup (initializing Koin), and any Desktop-specific integration code.

### **3.3. Jetpack Compose & UI Strategy**

Jetpack Compose is Android's recommended modern toolkit for building native UIs using a declarative programming model. Compose for Desktop allows the same declarative approach for building UIs on JVM Desktop platforms, sharing much of the same API and concepts.

The UI for both Android and Desktop will be implemented using Jetpack Compose within their respective platform modules (androidApp, desktopApp). While the Composable functions themselves reside in platform-specific modules, they will observe and react to state exposed by ViewModels (or similar presentation pattern components) located in the shared/commonMain module. This strategy ensures that the UI state logic is shared, promoting consistency in behavior and presentation across platforms, even though the UI rendering code is platform-specific.

### **3.4. Benefits for this Project**

The combination of KMP and Jetpack Compose offers significant advantages for this project:

* **Code Reusability:** The primary benefit is maximizing the sharing of code related to business logic, data handling, API communication, state management (ViewModels), and utility functions across Android and Desktop, drastically reducing development effort and potential for inconsistencies.
* **Consistency:** Shared logic ensures that the core application behavior remains identical on both target platforms.
* **Native Performance & Look-and-Feel:** By utilizing Jetpack Compose (rendering to native Android views) and Compose for Desktop (typically rendering via Skia), the application benefits from native performance characteristics and can more easily adopt platform-idiomatic UI conventions if desired.
* **Future Extensibility:** The KMP architecture inherently supports adding new target platforms, such as iOS. This would involve adding an iosMain source set for actual implementations and an iosApp module for the SwiftUI or UIKit interface, while reusing the substantial investment in the commonMain module.

### **3.5. Managing Platform Differences**

While KMP aims to maximize shared code, certain functionalities are inherently tied to the underlying platform (e.g., accessing device hardware like cameras, interacting with the file system, secure data storage). KMP addresses this through the expect/actual mechanism. Developers can declare expected functions, classes, or properties (expect declarations) in commonMain, defining a common API contract. Each platform module (androidMain, desktopMain, iosMain, etc.) then provides the concrete implementation (actual declarations) using platform-specific APIs. This pattern is crucial for abstracting platform dependencies and will be employed for features like image capture, persistent image storage, and secure API key handling, ensuring the core logic in commonMain remains platform-agnostic. Careful design of these expect interfaces is necessary to create effective abstractions.

## **4\. API Communication Strategy**

### **4.1. Requirement**

A robust, asynchronous, and KMP-compatible networking library is required to handle REST API communication between the shared application logic (commonMain) and the Matchplay.events backend API.

### **4.2. Recommended Library: Ktor Client**

Ktor Client is the recommended networking library for this project.

* **Description:** Developed by JetBrains, Ktor Client is a multiplatform asynchronous HTTP client built natively with Kotlin and designed with coroutines and KMP as first-class citizens.
* **Advantages:** Excellent KMP support out-of-the-box, idiomatic Kotlin API, seamless integration with Kotlin Coroutines, flexible pluggable engine architecture (e.g., OkHttp for Android, Java HTTP Client or CIO for JVM), built-in support for kotlinx.serialization, actively developed and supported by JetBrains.
* **Testability:** Provides a MockEngine specifically designed for unit testing network requests and responses within KMP's commonTest without actual network calls, simplifying test setup.
* **Justification:** Its primary advantage lies in its native, first-class support for Kotlin Multiplatform. This aligns perfectly with the project's technology choices and simplifies development by allowing the networking client configuration and API service definitions to reside directly within the shared/commonMain module. Ktor's seamless integration with Kotlin Coroutines and its dedicated MockEngine for testing further streamline development and testing workflows within the KMP structure.

### **4.3. Implementation Details**

* The Ktor HttpClient instance will be configured in commonMain. Platform-specific engines (e.g., OkHttp for Android, Java or CIO for Desktop) will be provided via expect/actual or platform-specific DI modules.
* kotlinx.serialization will be integrated with Ktor for automatic parsing of JSON request/response bodies into the Kotlin data models defined earlier.
* API calls to Matchplay endpoints will be implemented as suspend functions within a dedicated service interface (e.g., MatchplayApiService).
* Authentication will be handled by configuring Ktor to automatically add the necessary API key (e.g., as an HTTP header) to relevant requests. This can be achieved using Ktor's defaultRequest configuration block or potentially a custom Feature/Plugin. The secure retrieval of the API key itself will use the expect/actual pattern.

## **5\. Testing Approach (TDD Focus)**

### **5.1. TDD Mandate**

Test-Driven Development (TDD) is a core requirement for this project. All new functionality, from API interactions to UI logic, must begin with the creation of failing tests that define the desired behavior. Implementation code will then be written solely to make these tests pass, followed by a refactoring step to improve code quality while ensuring tests remain green. Tests must be written clearly, serving as executable documentation for the implemented features.

### **5.2. Unit Testing Strategy**

* **Location:** The vast majority of unit tests will reside in the shared/commonTest source set, allowing tests for shared logic to be written once and run on all configured target platforms.
* **Scope:** Unit tests will cover business logic (Use Cases/Interactors), presentation logic (ViewModels), data transformations, utility functions, and repository implementations. Dependencies, particularly the API client and data sources, will be mocked or faked.
* **Tools:** Standard Kotlin testing frameworks (kotlin.test) will be used. For mocking dependencies, libraries like Mockk (verifying its KMP compatibility status is recommended) or manually implemented fakes and stubs are options. Ktor Client's MockEngine is essential for testing the MatchplayApiService implementation by simulating API responses without making actual network calls, simplifying test setup.
* **Clarity:** Test function names should clearly describe the scenario and expected outcome (e.g., using given\_when\_then patterns). Each test should focus on verifying a single aspect of behavior. Assertions provided by kotlin.test should be used to check post-conditions clearly.

### **5.3. Integration Testing Strategy**

* **Requirement:** A limited set of host-based integration tests are required to verify interactions with the live Matchplay.events API, utilizing the provided Premium account credentials.
* **Scope:** These tests aim to confirm that the application's understanding of the Matchplay API contract (request formats, authentication, response structures) is correct. They will focus on critical end-to-end flows from the application's perspective, such as fetching nearby tournaments, retrieving standings for a known tournament, and submitting a score suggestion. These are *not* UI tests but rather tests of the application's service layer interacting with the real backend.
* **Location:** Due to dependencies on platform context (network access, secure credential handling), these tests are best placed in platform-specific test source sets (androidTest for Android, desktopTest for Desktop) or potentially within a separate, dedicated KMP module configured specifically for integration testing. While they exercise code within the shared module (e.g., Repositories, ApiService), they execute within a specific platform environment.
* **Execution:** Integration tests must be clearly distinguished from unit tests (e.g., using Gradle source sets, test tags, or naming conventions). They should be run selectively, typically during CI builds or specific QA phases, rather than during routine local development, due to their reliance on external services and credentials.
* **Caution:** The number of live API tests should be minimized. Over-reliance can lead to flaky tests (due to network issues or API changes), potential API rate limiting, and the need to manage test data within the live Matchplay account. Their primary value is in validating the API contract assumptions.

### **5.4. Test Data Management**

* **Requirement:** Investigate using exported data from the user's premium Matchplay account for testing purposes.
* **Feasibility:** Matchplay.events allows users to export tournament data as a JSON document via the "export" tab within a tournament. This feature seems available regardless of account type, but having a premium account ensures access to all tournament features that might generate relevant data. The export format is described as a dump of database models, with timestamps in UTC. CSV export is also available specifically for ratings data.
* **Strategy:**
  * **Export Available:** Since Matchplay provides a mechanism to export tournament data as JSON, this exported data should be captured (using the premium account for representative data) and stored within the commonTest/resources directory. These files will serve as high-fidelity inputs for the MockEngine in unit tests, ensuring tests run against realistic API response structures.
  * **Live Test Data:** For the limited integration tests running against the live API, specific test entities (e.g., a dedicated test tournament, registered test players) may need to be manually created and maintained within the premium Matchplay account. The required setup for these tests must be documented.

### **5.5. Testing Considerations in KMP**

The standard software testing pyramid model is highly applicable to KMP development. The base of the pyramid should consist of a large number of fast, reliable unit tests located in commonTest, verifying the shared logic in isolation using mocks and fakes. Integration tests, forming the middle layer, provide valuable checks against the real API but should be used judiciously due to their higher cost and potential for flakiness. Full end-to-end UI tests (top layer) are outside the initial scope but would reside entirely within the platform-specific modules (androidApp, desktopApp). Adopting an architecture like MVVM or MVI, where logic is concentrated in testable units within commonMain, naturally supports this testing approach.

A critical aspect of running integration tests is the secure handling of the premium account API key. Hardcoding credentials directly into test code is unacceptable. The API key must be injected securely at runtime, typically through environment variables or secure configuration files managed by the build system or CI/CD environment where these tests are executed. The platform-specific test setup code (androidTest, desktopTest) will be responsible for reading this configuration and providing it to the API client during test execution.

## **6\. LLM-Powered Score Extraction**

### **6.1. Feature Goal**

This feature aims to enhance the score suggestion process by allowing players to photograph a pinball machine's score display. The application will then utilize an LLM or Vision API to automatically extract player scores from the image. These extracted scores will be presented to the user for review and confirmation before being submitted to Matchplay via the existing score suggestion API endpoint. The captured image should also be saved by the app for future reference.

### **6.2. Workflow**

1. **Capture:** The user initiates the image capture process from the score suggestion interface within the app. The appropriate platform-specific camera API (Android/Desktop) is invoked.
2. **Store (Temporary):** The captured image data (e.g., as a byte array or platform bitmap) is temporarily stored locally on the device.
3. **Analyze:** The image data is prepared (potentially resized or format-converted) and sent to a designated LLM/Vision API endpoint for analysis and score extraction.
4. **Parse & Present:** The API response, expected to contain structured data (e.g., JSON listing player identifiers and their corresponding scores), is received and parsed by the application. The extracted scores are then used to pre-fill the score entry fields in a confirmation UI presented to the user.
5. **Confirm & Submit:** The user reviews the pre-filled scores, makes any necessary corrections, and confirms the results. Upon confirmation, the application submits the scores using the POST /rounds/{round\_id}/scores/suggest Matchplay API endpoint.
6. **Store (Persistent):** As requested, the original captured image is saved persistently on the device, potentially associated with the tournament and round for which the scores were submitted.

### **6.3. Image Capture Strategy (KMP)**

Image capture is inherently platform-specific. This will be managed using the expect/actual pattern:

* **commonMain:** Define an expect declaration for an interface or function responsible for initiating image capture, e.g., expect class ImageCaptureService { suspend fun captureImage(): PlatformBitmap? } (where PlatformBitmap might be another expect/actual type alias or a simple ByteArray).
* **androidMain:** Provide the actual implementation using Android's CameraX library or the Activity Result APIs for capturing photos. Consider using existing KMP libraries like peekaboo which abstract image picking/capture for Android/iOS.
* **desktopMain:** Provide the actual implementation. This could involve integrating a JVM library for webcam access (e.g., Webcam Capture API \- requires evaluation for stability and cross-platform compatibility) or, as a potentially simpler initial approach for Desktop, implementing a file picker (JFileChooser or similar) allowing the user to select an existing image file from their system. Libraries like FileKit or compose-multiplatform-file-picker might provide KMP abstractions for this.

### **6.4. Image Storage Strategy**

File system access also varies across platforms and requires abstraction:

* **commonMain:** Define expect declarations for services handling image storage. This might include temporary storage (for processing) and persistent storage. Example: expect class ImageStorage { fun saveTemporaryImage(imageData: ByteArray): FilePath?; fun savePersistentImage(imageData: ByteArray, suggestedName: String): FilePath? }. Alternatively, leverage a KMP library like Okio or Korio which provides multiplatform file system abstractions. FileKit also offers file operation capabilities.
* **androidMain:** Implement actual storage logic using Android's file system APIs, saving temporary images to the app's cache directory and persistent images to internal or external app-specific storage.
* **desktopMain:** Implement actual storage logic using Java's file I/O APIs (java.io or java.nio). Temporary files can go to the system's temp directory. Persistent images should be saved to a user-accessible location, such as a dedicated sub-folder within the user's standard 'Documents' or 'Pictures' directory, potentially making the location configurable.

### **6.5. LLM / Vision API Options**

The core requirement is to analyze an image of a pinball score display and extract player scores accurately.

* **Candidates:**
  * **Cloud-based Services (Recommended):** These offer powerful, pre-trained models accessible via standard REST APIs. Examples include Google Cloud Vision AI (specifically its Text Detection/OCR capabilities), Azure Computer Vision (OCR), or potentially newer multimodal LLMs like GPT-4 Vision (if its API supports this use case effectively).
    * *Advantages:* High accuracy due to sophisticated models, capable of handling diverse image conditions (lighting, angles, display types), managed infrastructure (no model deployment needed).
    * *Disadvantages:* Requires network connectivity, incurs costs based on API usage, potential network latency during analysis.
  * **On-device Models:** Frameworks like Google's ML Kit (primarily Android/iOS, KMP integration is complex) or TensorFlow Lite allow running machine learning models directly on the user's device.
    * *Advantages:* Works offline, potentially lower latency for analysis, no per-call API costs.
    * *Disadvantages:* Models can be large, potentially impacting app size; performance limitations on device hardware; significant complexity in integrating and managing models within a KMP project (likely requires extensive platform-specific code); potentially lower accuracy compared to cloud services, especially for varied pinball displays; model updates require distributing new app versions.
* **Recommendation:** Begin with a **Cloud-based Vision API**, specifically one focused on Optical Character Recognition (OCR), such as **Google Cloud Vision AI's Text Detection**. This approach offers the best balance of accuracy and implementation feasibility within a KMP project. Sending the image data and receiving structured results can be handled using standard HTTP requests from commonMain via the chosen Ktor client.
* **API Interaction:** Logic for preparing the image data (e.g., Base64 encoding, resizing), sending it to the Vision API, and parsing the JSON response will reside in commonMain, likely within a dedicated repository or use case. Secure management of the Vision API key will be necessary, potentially using the same expect/actual mechanism as the Matchplay API key or via configuration files.

### **6.6. User Confirmation UI**

A crucial component is the UI where users confirm the LLM-extracted scores. This should clearly display a thumbnail of the captured image alongside the extracted scores, pre-filled into the standard score entry fields. Importantly, these fields **must remain editable**, allowing the user to easily correct any errors made by the OCR process before submitting. This UI could be implemented as a modal dialog or a distinct section within the score suggestion screen.

### **6.7. Handling Inaccuracy and Errors**

It is critical to acknowledge that OCR/LLM-based score extraction will not be infallible. Pinball machine displays exhibit significant variation (dot-matrix, LCD, segmented LEDs, different fonts, reflections, viewing angles). The Vision API might misread digits, confuse player scores, fail to identify all players, or return errors. Therefore, the user confirmation step is non-negotiable. The application must implement robust error handling for the Vision API calls (e.g., network errors, API errors, responses indicating low confidence). The score parsing logic must be resilient, capable of handling unexpected or malformed data from the API. If the API returns unusable results, the system should gracefully fall back to requiring full manual entry, clearly informing the user. The UI design must prioritize ease of correction for the user.

### **6.8. Cost and API Key Management (LLM)**

Using cloud-based Vision APIs typically involves costs based on the number of requests or amount of data processed. This needs consideration during development and deployment. Usage patterns could lead to operational expenses. Furthermore, the API key for the Vision service must be managed securely. Bundling it directly in the client application is insecure. Options include:

1. Managing it similarly to the Matchplay API key (user-provided, stored securely using platform mechanisms via expect/actual). Suitable if the user base is very small (e.g., initially just the requester).
2. Using a backend proxy service that holds the key and forwards requests to the Vision API. This adds significant infrastructure complexity but is more secure for wider distribution. The cost implications and chosen key management strategy should be documented and potentially communicated to the user.

## **7\. High-Level Application Architecture**

### **7.1. Architectural Pattern**

To structure the application logic, particularly the separation of UI, state management, and data fetching, either the **Model-View-ViewModel (MVVM)** or **Model-View-Intent (MVI)** pattern is recommended.

* **MVVM:** A widely adopted pattern, especially well-suited for declarative UI toolkits like Jetpack Compose. UI state is encapsulated within ViewModels (located in commonMain). The UI (Composable functions in platform modules) observes this state (e.g., via Kotlin StateFlow) and renders accordingly. User interactions trigger methods on the ViewModel, which in turn interact with data layers (Repositories). MVVM promotes testability by decoupling UI from logic.
* **MVI:** An evolution of MVVM/MVP that emphasizes unidirectional data flow and immutable state. UI events are translated into Intents, which are processed to produce new State objects. This can lead to highly predictable state transitions, beneficial for complex UIs. Several KMP libraries exist to facilitate MVI implementation (e.g., MVIKotlin by Arkadii Ivanov).

**Recommendation:** **MVVM** is recommended as the starting point for this project. Its concepts are well-understood, it integrates naturally with Jetpack Compose's state observation mechanisms, and it provides a good balance of structure and simplicity, likely sufficient for the initial feature set. This choice should also be relatively straightforward for an LLM agent to implement based on common patterns.

### **7.2. Key Components (shared/commonMain)**

The shared/commonMain module will contain the core architectural components:

* **ViewModels:** Classes responsible for holding and managing UI state for specific screens or logical UI blocks (e.g., TournamentListViewModel, TournamentDetailViewModel, PlayerStatusViewModel, ScoreSuggestViewModel). They will fetch data via Repositories, perform presentation logic, and expose UI state using KMP-compatible observable types like kotlinx.coroutines.flow.StateFlow.
* **Repositories:** Abstract the data sources. A TournamentRepository interface, for example, will define functions for fetching tournament data (getTournaments, getTournamentDetails, getStandings, getRounds, suggestScore). The implementation(s) of this interface will use the configured Ktor API client (MatchplayApiService) to interact with the Matchplay REST API. Repositories might later incorporate caching or database interactions.
* **Use Cases/Interactors (Optional but Recommended):** Single-purpose classes that encapsulate specific business logic operations (e.g., FindNearbyTournamentsUseCase, SubmitScoreSuggestionUseCase, ExtractScoresFromImageUseCase). They sit between ViewModels and Repositories, promoting cleaner architecture, better separation of concerns, and enhanced testability by isolating complex logic.
* **Data Models:** Kotlin data classes representing the entities from the Matchplay API (e.g., Tournament, Standing, Round) and any necessary request/response structures. These will be annotated for use with kotlinx.serialization.
* **API Client:** The implementation of the interface communicating with the Matchplay REST API, built using Ktor Client as detailed in Section 4\.

### **7.3. Dependency Injection (DI)**

Managing the instantiation and wiring of dependencies (ViewModels, Repositories, ApiClient, UseCases, etc.) across the KMP project requires a dependency injection framework.

* **Need:** Avoids manual dependency creation (improves testability and maintainability).
* **Options:**
  * *Manual DI:* Feasible for very small projects but quickly becomes complex and error-prone.
  * *Koin:* A popular, pragmatic DI framework known for its Kotlin-first API and good KMP support. Relatively easy to set up.
  * *Kodein-DI:* Another established DI framework offering KMP support. Powerful but potentially steeper learning curve than Koin.
  * *Hilt/Dagger:* Primarily Android-centric DI frameworks. While usable in KMP, integrating them seamlessly across all platforms, especially commonMain, can be less straightforward than Koin or Kodein. Annotation processing can add build time overhead.
* **Recommendation:** Use **Koin** for this project. Its simplicity, KMP support, and ease of setup make it a suitable choice. DI modules defining how to construct various components will be defined in commonMain, and Koin will be initialized in the platform-specific application entry points (androidApp's Application class, desktopApp's main function).

### **7.4. State Management Considerations**

A key challenge in this application will be managing the UI state derived from multiple asynchronous API calls, particularly for the Tournament Details screen which combines information from tournament details, standings, and rounds endpoints. The ViewModel responsible for this screen needs to orchestrate these calls (potentially concurrently using Kotlin Coroutines' async/await), combine the results into a coherent UI state object, and handle loading and error states individually for each data source (e.g., showing standings successfully while indicating an error fetching rounds). Using kotlinx.coroutines.flow.StateFlow or SharedFlow within the ViewModels is well-suited for exposing this potentially complex, aggregated state to the Compose UI in a reactive manner, ensuring the UI updates correctly as different pieces of data arrive or fail to load.

