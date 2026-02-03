# ExpenseTracker - Android Mobile App

A native Android application for managing shared expenses within friend groups. Converted from a Java web application with modern Material Design 3 UI.

## Features

- 📊 **Dashboard**: View group statistics, recent expenses, and pending settlements
- 👥 **Group Management**: Create and manage friend groups
- 💰 **Expense Tracking**: Add expenses with equal or custom split
- 💳 **Payment Recording**: Record payments between users
- ⚖️ **Settlement Calculation**: Automatic "who owes whom" calculation
- 🎨 **Modern UI**: Beautiful Material Design 3 with gradient themes

## Getting Started

### Prerequisites
- Android Studio Iguana or newer
- JDK 17
- Android SDK 34

### Running the Project
1. **Clone the repository** to your local machine.
2. **Open Android Studio** and select "Open" to navigate to the project folder.
3. **Wait for Gradle Sync** to complete.
4. **Select a Device**: Choose a physical device or an emulator (API 24+).
5. **Click Run**: Press the green play button or `Shift + F10`.

### Troubleshooting Build Errors
If you encounter a **Gradle Cache Lock** error (`Cannot lock checksums cache`):
1. Close Android Studio.
2. Open Task Manager and end any `java.exe` or `Gradle Daemon` processes.
3. Delete the `.gradle` folder in the project root.
4. Restart Android Studio.

## Sample Data

The app comes pre-loaded with demo data:
- **5 Users**: Alice, Bob, Charlie, Diana, Eve
- **2 Groups**: "Roommates" and "Trip to Goa"
- **3 Expenses** with automatic settlements

## Technical Stack

- **Language**: Java 8
- **UI Framework**: Material Design 3
- **Database**: SQLite (migrated from MySQL)
- **Architecture**: MVVM pattern with DAO
- **Min SDK**: Android 7.0 (API 24)
- **Target SDK**: Android 14 (API 34)

## Building the APK

### Option 1: Android Studio
1. Open project in Android Studio
2. Build → Build APK(s)
3. APK location: `app/build/outputs/apk/debug/app-debug.apk`

### Option 2: Command Line
```bash
./gradlew assembleDebug
```

## App Structure

```
com.expensetracker
├── model/          # Data models (User, Group, Expense, etc.)
├── database/       # SQLite helper and DAOs
├── ui/
│   ├── dashboard/  # Main activity
│   ├── expense/    # Add expense functionality
│   ├── group/      # Group management
│   └── settlement/ # Settlement views
└── service/        # Business logic
```

## License

Academic project for DBMS & Java subjects.
