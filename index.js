import { NativeModules, Platform } from 'react-native';

const LINKING_ERROR =
  "The package 'react-native-widget-sdk' doesn't seem to be linked.\n" +
  'Make sure you rebuilt the app after installing the package.\n' +
  'On RN 0.73+ auto-linking handles this — verify the package appears in ' +
  'android/app/build.gradle dependencies.';

/**
 * Raw reference to the native WidgetSDK module.
 * Throws a clear error at call time if the native side is not linked,
 * instead of crashing with a cryptic null-pointer message.
 */
const WidgetSDK =
  NativeModules.WidgetSDK ??
  new Proxy(
    {},
    {
      get() {
        throw new Error(LINKING_ERROR);
      },
    }
  );

/**
 * Triggers an immediate refresh of all active home screen widget instances.
 *
 * Android only — resolves immediately (no-op) on other platforms.
 *
 * @returns {Promise<void>} Resolves on success. Rejects with code
 *   `"WIDGET_UPDATE_ERROR"` if the native broadcast fails.
 *
 * @example
 * import { updateWidget } from 'react-native-widget-sdk';
 *
 * await updateWidget();
 */
export function updateWidget() {
  if (Platform.OS !== 'android') {
    return Promise.resolve();
  }
  return WidgetSDK.updateWidget();
}

export default { updateWidget };
