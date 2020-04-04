# DebtLedger - Android Source Code

## How to Run

### Method 1: Using physical device

1. Allow installation of apps from unknown source (you may also complete this after step 6, as Android should prompt you)
  1. Using your Android device, open settings
  2. Find "Security"
  3. Enable "Unknown source"
2. Plug your Android device into your computer
3. Copy `debt-ledger.apk` into your `Download` or `Downloads` folder on your Android device
4. Unplug your Android device from your computer
5. Using your Android device, open your Download(s) folder
6. Click on (to open) `debt-ledger.apk`

### Method 2: Using emulated device

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
16. Use the app in the Virtual Device

## Structure and UI Implementation Mapping

- Dashboard: fragment_dashboard.xml & DashboardFragment.java
- Create Transaction: activity_create_transaction.xml & CreateTransactionActivity.java
  - Select Who Is Paying: activity_select_who_is_paying.xml & SelectWhoIsPayingActivity.java
  - Select Who Owes: activity_select_who_owes.xml & SelectWhoOwesActivity.java
- Contacts: fragment_contacts.xml & ContactsFragment.java
  - Add Contact: TBD
  - View Contact: activity_view_contact.xml & ViewContactActivity.java
- Groups: fragment_groups.xml & GroupsFragment.java
  - Create/Edit Group: activity_crate_edit_group.xml & CreateEditGroupActivity.java
    - Select Group Members: activity_select_group_members.xml & SelectGroupMembersActivity.java
  - View group: activity_view_group.xml & ViewGroupActivity.java
- Settings: fragment_settings.xml & SettingsFragment.java
