# AI-Native Android Architecture Sample

![GitHub Repo stars](https://img.shields.io/github/stars/dev-vikas-soni/nano-banana-andorid?style=social)
![GitHub forks](https://img.shields.io/github/forks/dev-vikas-soni/nano-banana-andorid?style=social)

A portfolio-quality Android application demonstrating **AI-Native Architecture** using real, on-device **Gemini Nano via Google AICore**. This project moves beyond simple "AI chatbot" examples to showcase a professional, scalable approach to building **Agentic Workflows** on Android.

## Why this project?

As on-device AI capabilities grow, integrating models directly into applications requires strong architectural foundations. This sample provides Android engineers with a clear, production-ready blueprint for:
- **Separation of Concerns:** Isolating raw AI SDKs from domain and UI logic.
- **Runtime AI Engine Swapping:** Dynamically switching between a Simulated Agent and the Real NPU at runtime.
- **Agent Orchestration:** Managing multi-step AI tasks (e.g., reading context, executing tools, drafting responses).
- **Observability:** Exposing the internal "thinking" of the AI agent to the user or developer in real-time.

## Key Features

*   **Real On-Device Gemini Nano:** Powered by `com.google.ai.edge.aicore`, running 100% locally on supported devices (Pixel 8+, Galaxy S24).
*   **Runtime UI Toggle:** Don't have a supported device? No problem! Toggle between the "Simulated NPU" and the "Real NPU" right from the Chat UI.
*   **"Summarize & Draft" Flow:** Users can open a chat, hit "Generate Reply", and watch the agent summarize the thread and draft a professional response.
*   **Agent Observability Sheet:** A real-time UI that traces the AI agent's internal execution state (Tool Start, Tool Complete, Generation) to prove its agentic nature.
*   **Highly Modular Architecture:** Codebase broken down by feature and layer to ensure scalability.
*   **Comprehensive Testing:** Fully unit-tested ViewModels and Orchestrators utilizing Kotlin Coroutines Testing (`StandardTestDispatcher`, flow testing).

## Tech Stack

*   **UI:** Jetpack Compose (Material 3)
*   **Architecture:** MVVM + Clean Architecture principles
*   **Dependency Injection:** Dagger Hilt
*   **Local Data:** Room Database
*   **Asynchronous:** Kotlin Coroutines & StateFlow/SharedFlow
*   **AI SDK Integration:** Google AICore edge SDK.

## Documentation

Dive deep into how this application was designed and engineered:
*   [High-Level Design (HLD)](HLD.md): System context, module boundaries, and broad design decisions.
*   [Low-Level Design (LLD)](LLD.md): Class relationships, data flow diagrams, and state management specifics.

## Getting Started

### Prerequisites
*   Android Studio Ladybug (or newer)
*   Android Gradle Plugin (AGP) 9.0+
*   JDK 17+
*   A physical device running Android 12+ (API 31) with AICore installed (if you wish to test the *Real* Gemini Nano feature).

### Building the Project
1.  Clone the repository.
2.  Open the project in Android Studio.
3.  Sync Gradle dependencies.
4.  Run `./gradlew assembleDebug` or click the **Run** button.

> **Note on Gemini Nano Hardware Requirements:** To test the real on-device Gemini Nano, you must run the app on a supported device (like a Pixel 9) and toggle the **Real NPU** switch in the UI. If you are on an emulator or older device, simply leave the switch off, and the architecture will seamlessly use the `FakeGeminiNanoClient` simulation!

## License

This project is open-source and available under the MIT License.
