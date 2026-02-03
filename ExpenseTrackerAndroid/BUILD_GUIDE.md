# Building the Android APK

## Prerequisites

1. **Android Studio** (recommended) OR
2. **Gradle Command Line** (if Android Studio is not available)

## Option 1: Using Android Studio (Recommended)

### Step 1: Open Project
1. Launch Android Studio
2. Click "Open an Existing Project"
3. Navigate to: `c:\Users\Acer\OneDrive\Desktop\dbms java final\ExpenseTrackerAndroid`
4. Click OK

### Step 2: Sync Gradle
- Android Studio will automatically sync Gradle
- Wait for sync to complete (may take a few minutes on first build)
- If prompted to download SDKs, click "Accept" and download

### Step 3: Build APK
1. Click **Build → Build Bundle(s) / APK(s) → Build APK(s)**
2. Wait for build to complete
3. APK location: `app\build\outputs\apk\debug\app-debug.apk`

### Step 4: Install on Device
**Physical Device:**
1. Connect phone via USB
2. Enable USB Debugging on phone (Settings → Developer Options)
3. Click the green "Run" button in Android Studio
4. Select your device

**Emulator:**
1. Click **Tools → Device Manager**
2. Create a new virtual device (Pixel 6, Android 13+)
3. Click the green "Run" button
4. Select your emulator

## Option 2: Using Gradle Command Line

### Step 1: Navigate to Project
```powershell
cd "c:\Users\Acer\OneDrive\Desktop\dbms java final\ExpenseTrackerAndroid"
```

### Step 2: Build Debug APK
```powershell
.\gradlew assembleDebug
```

### Step 3: Locate APK
APK will be at:
```
app\build\outputs\apk\debug\app-debug.apk
```

### Step 4: Install APK
**On Phone (Manual):**
1. Copy `app-debug.apk` to your Android phone
2. Open the file on your phone
3. Allow installation from unknown sources if prompted
4. Tap "Install"

**Using ADB:**
```powershell
adb install app\build\outputs\apk\debug\app-debug.apk
```

## Expected Build Time
- **First Build**: 2-5 minutes (downloads dependencies)
- **Subsequent Builds**: 30-60 seconds

## APK Size
- **Debug APK**: ~5-8 MB
- **Release APK** (with ProGuard): ~3-5 MB

## Minimum Android Version
- Android 7.0 (API Level 24) and above

## Troubleshooting

### Build Fails: SDK Not Found
- Open Android Studio
- Go to **Tools → SDK Manager**
- Install Android SDK Platform 34 and Build Tools 34.0.0

### Build Fails: Java Version
- Ensure Java 8 or higher is installed
- Check with: `java -version`

### Gradle Sync Issues
- Click **File → Invalidate Caches → Invalidate and Restart**

## Testing Checklist

Once installed, verify:
- [x] App launches successfully
- [x] Dashboard shows sample data (2 groups)
- [x] Group selector dropdown works
- [x] Statistics cards show correct numbers
- [x] Recent expenses list displays
- [x] Settlements ("Who Owes Whom") list displays
- [x] Beautiful gradient header visible
- [x] Material Design cards with proper shadows

## Next Steps

To build production APK:
1. Create a keystore (for signing)
2. Configure signing in `app/build.gradle`
3. Run: `.\gradlew assembleRelease`

For Google Play Store distribution, you'll need a signed release APK.
