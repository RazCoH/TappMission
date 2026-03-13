/**
 * Triggers an immediate refresh of all active home screen widget instances.
 *
 * Android only — resolves immediately (no-op) on other platforms.
 *
 * @returns Resolves on success. Rejects with code `"WIDGET_UPDATE_ERROR"`
 *   if the native broadcast fails.
 *
 * @example
 * import { updateWidget } from 'react-native-widget-sdk';
 * await updateWidget();
 */
export declare function updateWidget(): Promise<void>;

declare const WidgetSDK: {
  updateWidget: () => Promise<void>;
};

export default WidgetSDK;
