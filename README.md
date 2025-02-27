# Android Macro App

An Android application that allows users to record, save, and replay macro actions on their Android devices. This app uses Android's Accessibility Service to automate user interactions.

## Features

- Record user interactions (clicks, taps)
- Save recorded macros with custom names
- Replay recorded macros
- Accessibility service integration for system-wide automation

## Requirements

- Android Studio 4.2+
- Android SDK 24+
- Android device with Android 7.0 or higher

## Setup

1. Clone this repository
2. Open the project in Android Studio
3. Build and run the app on your device
4. Enable the accessibility service when prompted

## Usage

1. Launch the app
2. Enable the accessibility service by clicking the "Enable Accessibility Service" button
3. Record a macro by clicking the "Record Macro" button
4. Perform the actions you want to automate
5. Stop recording by clicking the button again
6. Enter a name for your macro
7. Save the macro
8. Run the macro by clicking "Start Macro"

## Permissions

This app requires the following permissions:
- `BIND_ACCESSIBILITY_SERVICE`: Required to monitor and automate user interactions
- `SYSTEM_ALERT_WINDOW`: Required to display overlays on top of other apps
- `FOREGROUND_SERVICE`: Required to keep the service running in the background

## License

This project is licensed under the MIT License - see the LICENSE file for details.