# SmartWriter ✨

SmartWriter is an Android app that showcases how to use the latest **ML Kit GenAI APIs** to build on-device generative AI features. It's built with **Jetpack Compose**, **MVVM**, and a clean, modular architecture using **Kotlin** and **Gradle Version Catalogs**.

> 🎉 This project is part of a blog series on Medium. You can read the first article [here](https://medium.com/@your-profile/smartwriter-part1).

---

## 📉 Features

SmartWriter includes the following AI-powered capabilities:

- ✅ **Text Summarisation**: Generate concise summaries from long-form text (currently supports English, Korean, and Japanese).
- ⚡ **Proofreading**: (Coming soon)
- ⚖️ **Text Rewriting**: (Coming soon)
- 📸 **Image Description**: (Coming soon)

Each feature is accessible via its own dedicated screen.

---

## 🛠️ Tech Stack

- **Android SDK 34+**
- **Jetpack Compose** UI
- **Hilt** for dependency injection
- **ViewModel + StateFlow** for state management
- **ML Kit GenAI APIs** (on-device)
- **Gradle Version Catalogs**
- **Kotlin Coroutines + Tasks.await()** bridge

---

## 🚀 Getting Started

### 1. Clone the repository:

```bash
git clone https://github.com/josegbel/smart-writer.git
cd smartwriter
```

### 2. Requirements

- Android Studio **Giraffe** or newer
- Kotlin **1.9+**
- Device with **Gemini Nano support**:
  - e.g. **Samsung Galaxy S25 Ultra**, **Pixel 8 Pro**, etc.
- **Android Emulator is NOT supported** by GenAI APIs.

### 3. Run the app

Open the project in Android Studio and run it on a compatible device.

---

## 🛡 Known Limitations

- ⛔ ML Kit GenAI APIs only support a few languages currently: **English**, **Korean**, and **Japanese**.
- ❌ Emulators do not support Gemini Nano and will fail to run GenAI features.
- ⚠️ First-time usage may trigger a model download. You can check feature availability via `checkFeatureStatus()`.
- ⚡ Currently uses `InputType.ARTICLE`. `InputType.CONVERSATION` will be added soon.

---

## 📖 Blog Series

This repository supports a series of articles on Medium, with each part focusing on a different feature:

| Part | Title             | Link                                                               |
| ---- | ----------------- | ------------------------------------------------------------------ |
| 1    | Summarisation     | [Read it here](https://medium.com/@your-profile/smartwriter-part1) |
| 2    | Proofreading      | Coming soon                                                        |
| 3    | Rewriting         | Coming soon                                                        |
| 4    | Image Description | Coming soon                                                        |

---

## 🙌 Contributions

Contributions, feedback or feature requests are welcome! Feel free to open an issue or submit a PR.

---

## 👨‍💻 Author

**Jose Garcia** — Android developer passionate about AI and future-ready tools.

Follow the blog series on [Medium](https://medium.com/@jose.gbel) for updates.

