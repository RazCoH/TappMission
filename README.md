# react-native-widget-sdk

Android Home Screen Widget SDK for React Native.

Provides a native bridge that lets your React Native app trigger an immediate refresh of all active Wheel Widget instances on the device home screen.

---

## Installation

### 1. Install from a local `.tgz`

Pack the SDK first (run this from the SDK root):

```bash
npm pack
```

This produces `react-native-widget-sdk-1.0.0.tgz`. Copy it into your React Native project and install:

```bash
npm install ./react-native-widget-sdk-1.0.0.tgz
```

### 2. Link the Android module

In your host app's `android/settings.gradle`, add:

```groovy
include ':react-native-widget-sdk'
project(':react-native-widget-sdk').projectDir =
    new File(rootProject.projectDir, '../node_modules/react-native-widget-sdk/android')
```

In your host app's `android/app/build.gradle`, add:

```groovy
dependencies {
    implementation project(':react-native-widget-sdk')
}
```

### 3. Register the package

In your host app's `MainApplication.kt`:

```kotlin
import com.example.tappmission.bridge.WidgetPackage

override fun getPackages() = PackageList(this).packages.apply {
    add(WidgetPackage())
}
```

### 4. Rebuild

```bash
npx react-native run-android
```

---

## Usage

```javascript
import { updateWidget } from 'react-native-widget-sdk';

// Trigger an immediate refresh of all home screen widget instances
await updateWidget();
```

Or with error handling:

```javascript
import { updateWidget } from 'react-native-widget-sdk';

try {
  await updateWidget();
  console.log('Widget refreshed');
} catch (error) {
  // error.code === 'WIDGET_UPDATE_ERROR'
  console.error('Widget refresh failed:', error.message);
}
```

---

## API

### `updateWidget(): Promise<void>`

Sends an `ACTION_APPWIDGET_UPDATE` broadcast to all active widget instances.

- **Android only** — resolves immediately on other platforms (no-op).
- Rejects with code `"WIDGET_UPDATE_ERROR"` if the native broadcast throws.

---

## Requirements

- React Native 0.73+
- Android API 26+ (minSdk)
- The widget must be pinned to the home screen at least once before calling `updateWidget()`.
