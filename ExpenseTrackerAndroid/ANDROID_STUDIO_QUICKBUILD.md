# Android Studio Quick Build Guide

## 🚀 Build Your APK in 5 Minutes

This guide will walk you through building your Android app using Android Studio - the easiest and fastest method!

---

## Step 1: Download Android Studio

### 1.1 Download
- Go to: https://developer.android.com/studio
- Click the big **"Download Android Studio"** button
- Accept the terms and conditions
- Download size: ~1 GB

### 1.2 Install
1. Run the downloaded installer (`android-studio-xxxx-windows.exe`)
2. Follow the installation wizard
3. **Choose "Standard" installation** when prompted
4. Let it download the Android SDK (this takes 5-10 minutes)
5. Click "Finish" when done

✅ **Android Studio is now installed!**

---

## Step 2: Open Your Project

### 2.1 Launch Android Studio
- Open Android Studio from your Start menu
- If you see a "Welcome" screen, perfect!
- If you see a previous project, close it: **File → Close Project**

### 2.2 Open Your Android Project
1. On the Welcome screen, click **"Open"**
2. Navigate to:
   ```
   c:\Users\Acer\OneDrive\Desktop\dbms java final\ExpenseTrackerAndroid
   ```
3. Select the **`ExpenseTrackerAndroid`** folder (the one containing `build.gradle`)
4. Click **"OK"**

### 2.3 Wait for Gradle Sync
- Android Studio will now open your project
- At the bottom, you'll see: **"Gradle Sync in progress..."**
- This is Android Studio downloading all dependencies
- **Wait time: 2-5 minutes** (first time only)
- ☕ Grab a coffee while it syncs!

✅ When you see **"Gradle build finished"** at the bottom, you're ready!

---

## Step 3: Fix Any Initial Issues (If Needed)

### 3.1 If Prompted to Download SDK Components
If you see a yellow banner saying "Install missing SDK packages":
1. Click **"Install"** or **"Install and Continue"**
2. Accept the license agreements
3. Wait for download to complete
4. Click "Finish"

### 3.2 If Gradle Version Mismatch
If you see an error about Gradle version:
1. A banner will appear: **"Gradle Sync Issues"**
2. Click **"Fix Gradle Wrapper"** or **"Update Gradle"**
3. Wait for re-sync

### 3.3 If Java/JDK Issues
If you see "JDK not found":
1. Go to **File → Project Structure**
2. Under "SDK Location" → "JDK Location"
3. Android Studio will auto-detect JDK
4. Click "OK"

✅ **Project is now ready to build!**

---

## Step 4: Build the APK

### 4.1 Start the Build
1. In the menu bar, click **Build**
2. Select **Build Bundle(s) / APK(s)**
3. Click **Build APK(s)**

![Build Menu Path: Build → Build Bundle(s) / APK(s) → Build APK(s)]

### 4.2 Wait for Build
- At the bottom, you'll see: **"Build started..."**
- Progress bar will show compilation progress
- **Wait time: 30-90 seconds**
- You'll see tasks like:
  - `compileDebugJava`
  - `mergeDebugResources`
  - `packageDebugResources`

### 4.3 Build Complete!
When finished, you'll see a notification at the bottom-right:
```
Build APK(s)
APK(s) generated successfully.
```

🎉 **Your APK is ready!**

---

## Step 5: Locate Your APK

### 5.1 Find the APK
In the notification (bottom-right), you'll see two options:
1. **"locate"** - Opens the folder containing APK
2. **"analyze"** - Shows APK size analysis

Click **"locate"** to open the folder.

### 5.2 APK Location
Your APK will be at:
```
c:\Users\Acer\OneDrive\Desktop\dbms java final\ExpenseTrackerAndroid\app\build\outputs\apk\debug\app-debug.apk
```

### 5.3 APK Details
- **File name**: `app-debug.apk`
- **Size**: ~5-8 MB
- **Type**: Android Application Package

✅ **This is your installable Android app!**

---

## Step 6: Install on Your Android Phone

### Method 1: USB Cable (Recommended)

#### 6.1 Enable Developer Options on Phone
1. On your Android phone, go to **Settings → About Phone**
2. Find **"Build Number"** (usually at the bottom)
3. Tap **"Build Number" 7 times**
4. You'll see: "You are now a developer!"

#### 6.2 Enable USB Debugging
1. Go back to **Settings**
2. Look for **"Developer Options"** or **"System → Developer Options"**
3. Find **"USB Debugging"**
4. Toggle it **ON**
5. Confirm "Allow USB debugging"

#### 6.3 Connect Phone to Computer
1. Connect your phone via USB cable
2. On your phone, you'll see a prompt: **"Allow USB debugging?"**
3. Check "Always allow from this computer"
4. Tap **"OK"**

#### 6.4 Install from Android Studio
1. In Android Studio, click the green **▶ Run** button (top-right)
2. A dialog appears: **"Select Deployment Target"**
3. Your phone should appear in the list
4. Select your phone
5. Click **"OK"**

**The app will install and launch automatically on your phone!** 🎉

---

### Method 2: Direct APK Installation (No USB)

#### 6.5 Transfer APK to Phone
**Option A - USB Cable:**
1. Connect phone to computer
2. Open phone storage in File Explorer
3. Copy `app-debug.apk` to your phone's **Downloads** folder

**Option B - Cloud/Email:**
1. Upload `app-debug.apk` to Google Drive/OneDrive
2. Download it on your phone
3. Or email it to yourself and download from phone

#### 6.6 Install APK on Phone
1. On your phone, open **Files** or **Downloads** app
2. Find `app-debug.apk`
3. Tap on it
4. You may see: **"Install unknown apps"** warning
5. Tap **"Settings"**
6. Enable **"Allow from this source"**
7. Go back and tap `app-debug.apk` again
8. Tap **"Install"**
9. Wait 5-10 seconds
10. Tap **"Open"**

**Your app is now installed!** 🎉

---

## Step 7: Test Your App

### 7.1 Launch the App
- Find "ExpenseTracker" in your app drawer
- Tap to open

### 7.2 What You Should See
✅ **Dashboard Screen** with:
- Beautiful purple/blue gradient header
- "ExpenseTracker" title
- Group selector dropdown at top
- 3 statistics cards (Total, Members, Pending)
- Recent expenses list
- "Who Owes Whom" settlements
- Floating action button (bottom-right)

### 7.3 Test the Features
1. **Select a Group**:
   - Tap the dropdown at top
   - Select "Roommates" or "Trip to Goa"
   - Watch the data update!

2. **Check Statistics**:
   - **Roommates**: Should show ~₹2100 total expenses, 3 members
   - **Trip to Goa**: Should show ~₹9000 total expenses, 3 members

3. **Scroll the Lists**:
   - Recent expenses show descriptions and amounts
   - Settlements show "X owes Y: ₹amount"

4. **UI Elements**:
   - Check the gradient header
   - Cards have nice shadows
   - Everything is smooth and responsive

✅ **If you see all this, your app works perfectly!**

---

## Troubleshooting

### Problem: "Gradle Sync Failed"
**Solution:**
1. Click **File → Invalidate Caches → Invalidate and Restart**
2. Wait for Android Studio to restart
3. Let it sync again

### Problem: "SDK Not Found"
**Solution:**
1. Go to **Tools → SDK Manager**
2. Check **"Android 14.0 (API 34)"** under SDK Platforms
3. Check **"Android SDK Build-Tools 34.0.0"** under SDK Tools
4. Click "Apply" and wait for download

### Problem: Build Error - "Cannot find symbol"
**Solution:**
1. Click **Build → Clean Project**
2. Wait for it to complete
3. Click **Build → Rebuild Project**

### Problem: Phone Not Detected
**Solution:**
1. Unplug and replug USB cable
2. On phone, disable and re-enable USB debugging
3. Try a different USB cable
4. Install phone drivers (Google "your phone model" + "USB drivers")

### Problem: "App Not Installed"
**Solution:**
1. Uninstall any previous version of the app
2. Make sure you have enough storage (at least 50 MB)
3. Try installing again

---

## Building a Release APK (Optional)

If you want a smaller, optimized APK for distribution:

1. Click **Build → Generate Signed Bundle / APK**
2. Select **APK**
3. Click **Next**
4. Click **Create new...** to create a keystore
5. Fill in the keystore details:
   - **Keystore path**: Choose a location and name
   - **Password**: Create a password (remember this!)
   - **Alias**: `expense_key`
   - **Key password**: Same as keystore password
   - Fill in your name/organization
6. Click **OK**
7. Click **Next**
8. Select **release**
9. Click **Finish**

Release APK location:
```
app\build\outputs\apk\release\app-release.apk
```

Release APK is ~3-5 MB (smaller than debug APK).

---

## Summary Checklist

- [x] Downloaded and installed Android Studio
- [x] Opened the ExpenseTrackerAndroid project
- [x] Waited for Gradle sync to complete
- [x] Built the APK successfully
- [x] Located app-debug.apk file
- [x] Installed app on Android phone
- [x] Tested the app - it works perfectly!

---

## Next Steps

Now that you have the APK:
- ✅ Share it with friends to test
- ✅ Install on multiple devices
- ✅ Add more features (see implementation_plan.md)
- ✅ Customize colors and theme
- ✅ Add app icon

**Congratulations! You've successfully built your Android app!** 🎊📱

---

## Quick Reference

| Action | Menu Path |
|--------|-----------|
| Open Project | Welcome → Open |
| Build APK | Build → Build Bundle(s) / APK(s) → Build APK(s) |
| Run on Device | Click green ▶ button |
| Clean Project | Build → Clean Project |
| Rebuild Project | Build → Rebuild Project |
| SDK Manager | Tools → SDK Manager |
| Project Structure | File → Project Structure |

---

**Need Help?** 
- Android Studio issues: Check **View → Tool Windows → Build** for error details
- APK location: Click "locate" link in build notification
- Installation issues: Enable "Install unknown apps" for your file manager

**Your APK is ready to use! Enjoy your mobile expense tracker app!** 🚀
