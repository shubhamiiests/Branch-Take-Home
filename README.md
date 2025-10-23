# 📱 Branch Take-Home Project

A simple mobile version of Branch’s customer support chat app — built using **Kotlin**, **Jetpack Compose**, and **MVVM architecture**.  
This app allows customer service agents to log in, view customer message threads, and respond on the go.

---

## 🚀 Features

| Feature | Description |
|----------|-------------|
| 🔐 **Login** | Agent login using email and reversed password (`password = email.reversed()`). |
| 💬 **Threads List** | Displays all message threads with the latest message preview. |
| 💭 **Chat Screen** | Shows full conversation with the customer and allows agent replies. |
| ♻️ **Reset Messages** | Clears sent messages (via `/api/reset`). |
| 🚪 **Logout** | Securely logs out and clears auth token. |

---

## 🧩 Tech Stack

| Layer | Library / Tech |
|-------|----------------|
| **UI** | [Jetpack Compose](https://developer.android.com/jetpack/compose) + Material 3 |
| **Architecture** | MVVM (ViewModel + StateFlow + Repository) |
| **DI (Dependency Injection)** | [Hilt](https://dagger.dev/hilt/) |
| **Networking** | [Retrofit 2](https://square.github.io/retrofit/), [OkHttp](https://square.github.io/okhttp/), [kotlinx.serialization](https://github.com/Kotlin/kotlinx.serialization) |
| **Local Storage** | [DataStore Preferences](https://developer.android.com/topic/libraries/architecture/datastore) |
| **Async / Concurrency** | Kotlin Coroutines |
| **Logging** | [Timber](https://github.com/JakeWharton/timber) |

---

## ⚙️ Setup & Installation

### 1️⃣ Prerequisites
- **Android Studio Ladybug+ (or newer)**  
- **JDK 17+**
- **Android SDK 36** installed
- Internet connection (for REST API requests)

### 2️⃣ Clone the project
```bash
git clone <your-private-repo-url>
cd BranchTakeHome
### 🧩 3️⃣ Open in Android Studio

1. Open **Android Studio** → select **“Open Project”** → choose this folder.  
2. Wait for **Gradle Sync** to complete.  
3. If prompted, install the **Android SDK Platform 36** and **Build Tools 36.x**.  
4. Make sure your JDK version is set to **17 or higher** (File → Settings → Build Tools → Gradle → JDK).  

---

### ▶️ 4️⃣ Run the App

1. Click **Run ▶️** or press **Shift + F10** to build and launch the app.  
2. On the **Login Screen**, enter your real email address.  
3. The **password** should be **your email reversed**.  

**Example:**  
| Field | Value |
|--------|--------|
| Email | `john@example.com` |
| Password | `moc.elpmaxe@nhoj` |

> 🔒 **Note:** The app uses your email only to simulate login with the Branch test API — no personal data is stored.

