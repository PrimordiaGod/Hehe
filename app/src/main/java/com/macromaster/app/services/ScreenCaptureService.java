package com.macromaster.app.services;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import androidx.annotation.Nullable;

import java.nio.ByteBuffer;

/**
 * Service for capturing screenshots using MediaProjection API.
 */
public class ScreenCaptureService extends Service {
    private static final String TAG = "ScreenCaptureService";
    private static final int VIRTUAL_DISPLAY_FLAGS = DisplayManager.VIRTUAL_DISPLAY_FLAG_OWN_CONTENT_ONLY | DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC;
    
    private MediaProjection mediaProjection;
    private VirtualDisplay virtualDisplay;
    private ImageReader imageReader;
    private Handler handler;
    private int width;
    private int height;
    private int density;
    
    private static ScreenCaptureService instance;
    
    // Interface for screenshot callbacks
    public interface ScreenshotCallback {
        void onScreenshotTaken(Bitmap bitmap);
        void onError(String error);
    }
    
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        handler = new Handler(Looper.getMainLooper());
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }
    
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        stopScreenCapture();
        instance = null;
    }
    
    /**
     * Get the singleton instance of the service.
     */
    public static ScreenCaptureService getInstance() {
        return instance;
    }
    
    /**
     * Initialize the screen capture service with a MediaProjection.
     *
     * @param mediaProjection The MediaProjection object from the permission result
     * @param context The context to use for getting display metrics
     */
    public void init(MediaProjection mediaProjection, Context context) {
        this.mediaProjection = mediaProjection;
        
        // Get screen dimensions
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(metrics);
        
        this.width = metrics.widthPixels;
        this.height = metrics.heightPixels;
        this.density = metrics.densityDpi;
        
        // Create ImageReader for capturing screenshots
        imageReader = ImageReader.newInstance(width, height, PixelFormat.RGBA_8888, 2);
    }
    
    /**
     * Start screen capture and take a screenshot.
     *
     * @param callback Callback to receive the screenshot bitmap
     */
    public void takeScreenshot(ScreenshotCallback callback) {
        if (mediaProjection == null) {
            callback.onError("MediaProjection not initialized. Please request permission first.");
            return;
        }
        
        // Create virtual display
        virtualDisplay = mediaProjection.createVirtualDisplay(
                "ScreenCapture",
                width, height, density,
                VIRTUAL_DISPLAY_FLAGS,
                imageReader.getSurface(),
                null, handler);
        
        // Capture image after a short delay to ensure the virtual display is ready
        handler.postDelayed(() -> {
            Image image = null;
            try {
                image = imageReader.acquireLatestImage();
                if (image != null) {
                    Bitmap bitmap = imageToBitmap(image);
                    callback.onScreenshotTaken(bitmap);
                } else {
                    callback.onError("Failed to acquire image from screen capture");
                }
            } catch (Exception e) {
                Log.e(TAG, "Error taking screenshot", e);
                callback.onError("Error taking screenshot: " + e.getMessage());
            } finally {
                if (image != null) {
                    image.close();
                }
                stopScreenCapture();
            }
        }, 100);
    }
    
    /**
     * Convert an Image to a Bitmap.
     *
     * @param image The Image to convert
     * @return The resulting Bitmap
     */
    private Bitmap imageToBitmap(Image image) {
        Image.Plane[] planes = image.getPlanes();
        ByteBuffer buffer = planes[0].getBuffer();
        int pixelStride = planes[0].getPixelStride();
        int rowStride = planes[0].getRowStride();
        int rowPadding = rowStride - pixelStride * width;
        
        // Create bitmap
        Bitmap bitmap = Bitmap.createBitmap(width + rowPadding / pixelStride, height, Bitmap.Config.ARGB_8888);
        bitmap.copyPixelsFromBuffer(buffer);
        
        // Crop if needed due to padding
        if (rowPadding > 0) {
            return Bitmap.createBitmap(bitmap, 0, 0, width, height);
        }
        
        return bitmap;
    }
    
    /**
     * Stop screen capture and release resources.
     */
    private void stopScreenCapture() {
        if (virtualDisplay != null) {
            virtualDisplay.release();
            virtualDisplay = null;
        }
        
        if (imageReader != null) {
            imageReader.close();
            imageReader = null;
        }
    }
    
    /**
     * Request media projection permission.
     *
     * @param activity The activity to request permission from
     * @param requestCode The request code to use for the permission request
     */
    public static void requestMediaProjectionPermission(Activity activity, int requestCode) {
        MediaProjectionManager projectionManager = (MediaProjectionManager) 
                activity.getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        activity.startActivityForResult(projectionManager.createScreenCaptureIntent(), requestCode);
    }
}