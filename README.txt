# Submission Structure:

"Group 9 Final Report.pdf" -> Final Report
"Group 9 Debt Ledger Demonstration.mp4" -> Video Demonstration
"NavigationalMap.png" -> A copy of the navigational map as an image (also included in pdf)
debt-ledger.apk -> Android package file which can be used to install the application
DebtLedger.zip -> This directory contains the new project's Android source code
DebtLedger-backend.zip -> This directory contains the new project's backend, consisting of PHP API files and SQL schema

# General Information about the Application:

DISCLAIMER: The passwords are stored in plain text, so do NOT use a legitimate password when signing up.

The application is not very useful until you add a contact, which requires requesting a contact and
that request being accepted. For this reason, there are existing accounts which are setup to enable
full use of the application.

Timothy Lee:
Email: tlee4@uwo.ca
Password: password

Jim Carr:
Email: jcarr@gmail.com
Password: password

# How to Run the Application:

## Method 1: Using physical Android device (min. Android 5.0)

1. Allow installation of apps from unknown source, steps may differ depending on device and Android version
   - NOTE: you may choose to (or be required to) complete this after step 6, as Android should (or would) prompt you
   - e.g. Settings -> Security -> Unknown source
2. Plug your Android device into your computer
3. Using your computer, copy `debt-ledger.apk` into your `Download` folder on your Android device
4. Unplug your Android device from your computer
5. Using your Android device, browse your Download folder using a File Manager/Explorer
   - NOTE: the file may not show up in your Downloads application since it was not marked as a download in Android's system, so ensure you use a general File Manager/Explorer
   - e.g. Settings -> Storage -> Explore (this should use Android's built in File Explorer, capable of installing APKs)
6. Click on the APK file `debt-ledger.apk`
   - NOTE: if you have not completed step 1, your device should now prompt you to allow installation from unknown source
7. Follow instructions to install the app

## Method 2: Using emulated Android device

1. Download and Install Android Studio: https://developer.android.com/studio
2. Download and Unzip the Android Source Code (`DebtLedger.zip`)
3. Open Android Studio
4. Click Open an existing Android Studio project
5. Find the location where you downloaded and unzipped the source code, and click the DebtLedger directory (should have an Android Studio icon next to it)
6. Click Open
7. Let the project download and process resources
8. Click Tools -> AVD Manager
9. Click Create Virtual Device (in bottom left)
10. Choose Pixel 2 and click Next
11. Click Download next to Pie
12. Choose Pie and click Next
13. Click Finish
14. Click Tools -> AVD Manager
15. Click the green Play icon next to the created Virtual Device (to run the device)
16. Click Run -> Run 'app'
17. Use the app in the Virtual Device
