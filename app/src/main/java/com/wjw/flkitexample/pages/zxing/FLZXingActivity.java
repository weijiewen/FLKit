package com.wjw.flkitexample.pages.zxing;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Process;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.EncodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.NotFoundException;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.ResultPoint;
import com.google.zxing.ResultPointCallback;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.wjw.flkit.base.FLBaseActivity;
import com.wjw.flkit.base.FLNavigationView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FLZXingActivity extends FLBaseActivity implements SurfaceHolder.Callback {
    public static void loadQrcode(Context context, String string, ImageView imageView) {
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        hints.put(EncodeHintType.MARGIN, 1);
        int imageWidth = imageView.getWidth();
        if (imageWidth == 0) {
            imageWidth = 500;
        }
        int imageHeight = imageView.getHeight();
        if (imageHeight == 0) {
            imageHeight = 500;
        }
        try {
            BitMatrix bitMatrix = new MultiFormatWriter().encode(string, BarcodeFormat.QR_CODE, imageWidth, imageHeight, hints);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            int[] pixels = new int[width * height];
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    pixels[y * width + x] = bitMatrix.get(x, y) ? 0xff000000 : 0xffffffff;
                }
            }
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
            imageView.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }
    public static String getQRCode(String path) {
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        if (bitmap != null) {
            return getQRCode(bitmap);
        }
        return null;
    }
    public static String getQRCode(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        QRCodeReader reader = new QRCodeReader();
        Map<DecodeHintType, Object> hints = new EnumMap<>(DecodeHintType.class);
        hints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);//优化精度
        hints.put(DecodeHintType.CHARACTER_SET,"utf-8");//解码设置编码方式为：utf-8
        try {
            Result result = reader.decode(new BinaryBitmap(new HybridBinarizer(new RGBLuminanceSource(width, height, pixels))), hints);
            return result.getText();
        } catch (NotFoundException e) {
            e.printStackTrace();
        } catch (ChecksumException e) {
            e.printStackTrace();
        } catch (FormatException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static final String FL_SCAN_QRCODE_RESULT = "fl_scan_qrcode_result";
    private static final long VIBRATE_DURATION = 50L;
    private static final long AUTO_FOCUS_INTERVAL_MS = 2500L;

    private FLCamera FLCamera;
    private Vibrator vibrator;
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private ScannerView scannerView;
    private HandlerThread cameraThread;
    private Handler cameraHandler;
    private final Runnable openRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                final Camera camera = FLCamera.open(surfaceHolder,
                        !DISABLE_CONTINUOUS_AUTOFOCUS);

                final Rect framingRect = FLCamera.getFrame();
                final Rect framingRectInPreview = FLCamera
                        .getFramePreview();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        scannerView.setFraming(framingRect,
                                framingRectInPreview);
                    }
                });

                final String focusMode = camera.getParameters().getFocusMode();
                final boolean nonContinuousAutoFocus = Camera.Parameters.FOCUS_MODE_AUTO
                        .equals(focusMode)
                        || Camera.Parameters.FOCUS_MODE_MACRO.equals(focusMode);

                if (nonContinuousAutoFocus)
                    cameraHandler.post(new AutoFocusRunnable(camera));

                cameraHandler.post(fetchAndDecodeRunnable);
            } catch (final IOException x) {
                Log.i("problem opening camera", x.toString());
                finish();
            } catch (final RuntimeException x) {
                Log.i("problem opening camera", x.toString());
                finish();
            }
        }
    };
    private final Runnable closeRunnable = new Runnable() {
        @Override
        public void run() {
            FLCamera.close();

            // cancel background thread
            cameraHandler.removeCallbacksAndMessages(null);
            cameraThread.quit();
        }
    };
    private final Runnable fetchAndDecodeRunnable = new Runnable() {
        private final QRCodeReader reader = new QRCodeReader();
        private final Map<DecodeHintType, Object> hints = new EnumMap<DecodeHintType,
                Object>(DecodeHintType.class);

        @Override
        public void run() {
            if (fromGallery) {
                cameraHandler.postDelayed(fetchAndDecodeRunnable, 500);
                return;
            }
            FLCamera.requestPreviewFrame(new Camera.PreviewCallback() {
                @Override
                public void onPreviewFrame(final byte[] data, final Camera camera) {
                    decode(data);
                }
            });
        }

        private void decode(final byte[] data) {
            final PlanarYUVLuminanceSource source = FLCamera.buildLuminanceSource(data);
            final BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

            try {
                hints.put(DecodeHintType.NEED_RESULT_POINT_CALLBACK,
                        new ResultPointCallback() {
                            @Override
                            public void foundPossibleResultPoint(
                                    final ResultPoint dot) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        scannerView.addDot(dot);
                                    }
                                });
                            }
                        });
                final Result scanResult = reader.decode(bitmap, hints);
                if (!resultValid(scanResult.getText())) {
                    cameraHandler.post(fetchAndDecodeRunnable);
                    return;
                }
                final int thumbnailWidth = source.getThumbnailWidth();
                final int thumbnailHeight = source.getThumbnailHeight();
                final float thumbnailScaleFactor = (float) thumbnailWidth
                        / source.getWidth();

                final Bitmap thumbnailImage = Bitmap.createBitmap(
                        thumbnailWidth, thumbnailHeight,
                        Bitmap.Config.ARGB_8888);
                thumbnailImage.setPixels(source.renderThumbnail(), 0,
                        thumbnailWidth, 0, 0, thumbnailWidth, thumbnailHeight);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        handleResult(scanResult, thumbnailImage,
                                thumbnailScaleFactor);
                    }
                });
            } catch (final Exception x) {
                cameraHandler.post(fetchAndDecodeRunnable);
            } finally {
                reader.reset();
            }
        }
    };
    private boolean fromGallery;

    private static boolean DISABLE_CONTINUOUS_AUTOFOCUS = Build.MODEL.equals("GT-I9100") //
            // Galaxy S2
            || Build.MODEL.equals("SGH-T989") // Galaxy S2
            || Build.MODEL.equals("SGH-T989D") // Galaxy S2 X
            || Build.MODEL.equals("SAMSUNG-SGH-I727") // Galaxy S2 Skyrocket
            || Build.MODEL.equals("GT-I9300") // Galaxy S3
            || Build.MODEL.equals("GT-N7000"); // Galaxy Note


    @Override
    protected FLOffsetStyle offsetStyle() {
        return FLOffsetStyle.None;
    }

    @Override
    protected void configNavigation(FLNavigationView navigationView) {
        navigationView.setBackgroundColor(Color.parseColor("#61000000"));
        navigationView.setForegroundColor(Color.WHITE);
    }

    @Override
    protected View getView() {
        ConstraintLayout constraintLayout = new ConstraintLayout(this);

        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID;
        layoutParams.rightToRight = ConstraintLayout.LayoutParams.PARENT_ID;
        layoutParams.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
        layoutParams.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;

        surfaceView = new SurfaceView(this);
        surfaceView.setLayoutParams(layoutParams);
        constraintLayout.addView(surfaceView);

        surfaceHolder = surfaceView.getHolder();

        scannerView = new ScannerView(this, null);
        scannerView.setLayoutParams(layoutParams);
        constraintLayout.addView(scannerView);
        return constraintLayout;
    }

    @Override
    protected void didLoad() {
        setStatusStyle(StatusStyle.light);
        FLCamera = new FLCamera();
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        cameraThread = new HandlerThread("cameraThread", Process.THREAD_PRIORITY_BACKGROUND);
        cameraThread.start();
        cameraHandler = new Handler(cameraThread.getLooper());
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    @Override
    protected void onPause() {
        cameraHandler.post(closeRunnable);
        surfaceHolder.removeCallback(this);
        super.onPause();
    }

    @Override
    protected void didClick(View view) {

    }

    @Override
    public void surfaceCreated(final SurfaceHolder holder) {
        cameraHandler.post(openRunnable);
    }

    @Override
    public void surfaceDestroyed(final SurfaceHolder holder) {
    }

    @Override
    public void surfaceChanged(final SurfaceHolder holder, final int format,final int width, final int height) {

    }

    public boolean resultValid(String result) {
        return true;
    }

    public void handleResult(final Result scanResult, Bitmap thumbnailImage,
                             final float thumbnailScaleFactor) {
        vibrator.vibrate(VIBRATE_DURATION);
        // superimpose dots to highlight the key features of the qr code
        final ResultPoint[] points = scanResult.getResultPoints();
        if (points != null && points.length > 0) {
            final Paint paint = new Paint();
            paint.setColor(Color.parseColor("#C099CC00"));
            paint.setStrokeWidth(10.0f);

            final Canvas canvas = new Canvas(thumbnailImage);
            canvas.scale(thumbnailScaleFactor, thumbnailScaleFactor);
            for (final ResultPoint point : points)
                canvas.drawPoint(point.getX(), point.getY(), paint);
        }

        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        thumbnailImage = Bitmap.createBitmap(thumbnailImage, 0, 0,
                thumbnailImage.getWidth(), thumbnailImage.getHeight(), matrix,
                false);
        scannerView.drawResultBitmap(thumbnailImage);

        final Intent result = getIntent();
        result.putExtra(FL_SCAN_QRCODE_RESULT,scanResult.getText());
        setResult(RESULT_OK, result);

        // delayed finish
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        });
    }

    private final class AutoFocusRunnable implements Runnable {
        private final Camera camera;

        public AutoFocusRunnable(final Camera camera) {
            this.camera = camera;
        }

        @Override
        public void run() {
            camera.autoFocus(new Camera.AutoFocusCallback() {
                @Override
                public void onAutoFocus(final boolean success,
                                        final Camera camera) {
                    // schedule again
                    cameraHandler.postDelayed(AutoFocusRunnable.this,
                            AUTO_FOCUS_INTERVAL_MS);
                }
            });
        }
    }
    public class ScannerView extends View {
        private static final long LASER_ANIMATION_DELAY_MS = 100l;

        private final Paint maskPaint;
        private Bitmap resultBitmap;
        private final int maskColor;
        private final int resultColor;
        private final Map<ResultPoint, Long> dots = new HashMap<ResultPoint, Long>(
                16);
        private Rect frame, framePreview;
        private final Paint textPaint;


        public ScannerView(final Context context, final AttributeSet attrs) {
            super(context, attrs);

            final Resources res = getResources();
            maskColor = Color.parseColor("#60000000");
            resultColor = Color.parseColor("#B0000000");

            maskPaint = new Paint();
            maskPaint.setStyle(Paint.Style.FILL);

            textPaint = new Paint();
            textPaint.setColor(Color.parseColor("#FFFFFF"));
            textPaint.setStyle(Paint.Style.FILL_AND_STROKE);
            textPaint.setAntiAlias(true);
            textPaint.setTextAlign(Paint.Align.CENTER);
            textPaint.setTextSize(dipToPx(16));
        }

        public void setFraming(final Rect frame,
                               final Rect framePreview) {
            this.frame = frame;
            this.framePreview = framePreview;

            invalidate();
        }

        public void drawResultBitmap(final Bitmap bitmap) {
            resultBitmap = bitmap;

            invalidate();
        }

        public void addDot(final ResultPoint dot) {
            dots.put(dot, System.currentTimeMillis());

            invalidate();
        }

        @Override
        public void onDraw(final Canvas canvas) {
            if (frame == null)
                return;

            final long now = System.currentTimeMillis();

            final int width = canvas.getWidth();
            final int height = canvas.getHeight();

            // draw mask darkened
            maskPaint.setColor(resultBitmap != null ? resultColor : maskColor);
            canvas.drawRect(0, 0, width, frame.top, maskPaint);
            canvas.drawRect(0, frame.top, frame.left, frame.bottom + 1, maskPaint);
            canvas.drawRect(frame.right + 1, frame.top, width, frame.bottom + 1,
                    maskPaint);
            canvas.drawRect(0, frame.bottom + 1, width, height, maskPaint);

            Rect rect = new Rect();
            textPaint.getTextBounds("将二维码放入框内，即可自动扫描", 0, "将二维码放入框内，即可自动扫描".length(), rect);
            canvas.drawText("将二维码放入框内，即可自动扫描", Resources.getSystem().getDisplayMetrics().widthPixels / 2,frame.bottom + rect.height() + dipToPx(7), textPaint);

            if (resultBitmap != null) {
                canvas.drawBitmap(resultBitmap, null, frame, maskPaint);
            } else {
                postInvalidateDelayed(LASER_ANIMATION_DELAY_MS);
            }
        }
    }
    private static class FLCamera {
        private static final int MIN_FRAME_SIZE = 320;
        private static final int MAX_FRAME_SIZE = 1000;
        private static final int MIN_PREVIEW_PIXELS = 470 * 320; // normal screen
        private static final int MAX_PREVIEW_PIXELS = 1280 * 720;

        private Camera camera;
        private Camera.Size cameraResolution;
        private Rect frame;
        private Rect framePreview;

        public Rect getFrame() {
            return frame;
        }

        public Rect getFramePreview() {
            return framePreview;
        }

        public Camera open(final SurfaceHolder holder,
                           final boolean continuousAutoFocus) throws IOException {
            // try back-facing camera
            camera = Camera.open();

            // fall back to using front-facing camera
            if (camera == null) {
                final int cameraCount = Camera.getNumberOfCameras();
                final Camera.CameraInfo cameraInfo = new Camera.CameraInfo();

                // search for front-facing camera
                for (int i = 0; i < cameraCount; i++) {
                    Camera.getCameraInfo(i, cameraInfo);
                    if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                        camera = Camera.open(i);
                        break;
                    }
                }
            }

            camera.setDisplayOrientation(90);
            camera.setPreviewDisplay(holder);

            final Camera.Parameters parameters = camera.getParameters();

            final Rect surfaceFrame = holder.getSurfaceFrame();
            cameraResolution = findBestPreviewSizeValue(parameters, surfaceFrame);

            final int surfaceWidth = surfaceFrame.width();
            final int surfaceHeight = surfaceFrame.height();

            final int rawSize = Math.min(surfaceWidth * 4 / 5,
                    surfaceHeight * 4 / 5);
            final int frameSize = Math.max(MIN_FRAME_SIZE,
                    Math.min(MAX_FRAME_SIZE, rawSize));

            final int leftOffset = (surfaceWidth - frameSize) / 2;
            final int topOffset = (surfaceHeight - frameSize) / 2;
            frame = new Rect(leftOffset, topOffset, leftOffset + frameSize,
                    topOffset + frameSize);
            framePreview = new Rect(frame.left * cameraResolution.height
                    / surfaceWidth, frame.top * cameraResolution.width
                    / surfaceHeight, frame.right * cameraResolution.height
                    / surfaceWidth, frame.bottom * cameraResolution.width
                    / surfaceHeight);

            final String savedParameters = parameters == null ? null : parameters
                    .flatten();

            try {
                setDesiredCameraParameters(camera, cameraResolution,
                        continuousAutoFocus);
            } catch (final RuntimeException x) {
                if (savedParameters != null) {
                    final Camera.Parameters parameters2 = camera.getParameters();
                    parameters2.unflatten(savedParameters);
                    try {
                        camera.setParameters(parameters2);
                        setDesiredCameraParameters(camera, cameraResolution,
                                continuousAutoFocus);
                    } catch (final RuntimeException x2) {
                        x2.printStackTrace();
                    }
                }
            }

            camera.startPreview();

            return camera;
        }

        public void close() {
            if (camera != null) {
                camera.stopPreview();
                camera.release();
                camera = null;
            }
        }

        private static final Comparator<Camera.Size> numPixelComparator = new Comparator<Camera.Size>() {
            @Override
            public int compare(final Camera.Size size1, final Camera.Size size2) {
                final int pixels1 = size1.height * size1.width;
                final int pixels2 = size2.height * size2.width;

                if (pixels1 < pixels2)
                    return 1;
                else if (pixels1 > pixels2)
                    return -1;
                else
                    return 0;
            }
        };

        private static Camera.Size findBestPreviewSizeValue(
                final Camera.Parameters parameters, Rect surfaceResolution) {
            if (surfaceResolution.height() > surfaceResolution.width())
                surfaceResolution = new Rect(0, 0, surfaceResolution.height(),
                        surfaceResolution.width());

            final float screenAspectRatio = (float) surfaceResolution.width()
                    / (float) surfaceResolution.height();

            final List<Camera.Size> rawSupportedSizes = parameters
                    .getSupportedPreviewSizes();
            if (rawSupportedSizes == null)
                return parameters.getPreviewSize();

            // sort by size, descending
            final List<Camera.Size> supportedPreviewSizes = new ArrayList<Camera.Size>(
                    rawSupportedSizes);
            Collections.sort(supportedPreviewSizes, numPixelComparator);

            Camera.Size bestSize = null;
            float diff = Float.POSITIVE_INFINITY;

            for (final Camera.Size supportedPreviewSize : supportedPreviewSizes) {
                final int realWidth = supportedPreviewSize.width;
                final int realHeight = supportedPreviewSize.height;
                final int realPixels = realWidth * realHeight;
                if (realPixels < MIN_PREVIEW_PIXELS
                        || realPixels > MAX_PREVIEW_PIXELS)
                    continue;

                final boolean isCandidatePortrait = realWidth < realHeight;
                final int maybeFlippedWidth = isCandidatePortrait ? realHeight
                        : realWidth;
                final int maybeFlippedHeight = isCandidatePortrait ? realWidth
                        : realHeight;
                if (maybeFlippedWidth == surfaceResolution.width()
                        && maybeFlippedHeight == surfaceResolution.height())
                    return supportedPreviewSize;

                final float aspectRatio = (float) maybeFlippedWidth
                        / (float) maybeFlippedHeight;
                final float newDiff = Math.abs(aspectRatio - screenAspectRatio);
                if (newDiff < diff) {
                    bestSize = supportedPreviewSize;
                    diff = newDiff;
                }
            }

            if (bestSize != null)
                return bestSize;
            else
                return parameters.getPreviewSize();
        }

        @SuppressLint("InlinedApi")
        private static void setDesiredCameraParameters(final Camera camera,
                                                       final Camera.Size cameraResolution,
                                                       final boolean continuousAutoFocus) {
            final Camera.Parameters parameters = camera.getParameters();
            if (parameters == null)
                return;

            final List<String> supportedFocusModes = parameters
                    .getSupportedFocusModes();
            final String focusMode = continuousAutoFocus ? findValue(
                    supportedFocusModes,
                    Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE,
                    Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO,
                    Camera.Parameters.FOCUS_MODE_AUTO,
                    Camera.Parameters.FOCUS_MODE_MACRO) : findValue(
                    supportedFocusModes, Camera.Parameters.FOCUS_MODE_AUTO,
                    Camera.Parameters.FOCUS_MODE_MACRO);
            if (focusMode != null)
                parameters.setFocusMode(focusMode);

            parameters.setPreviewSize(cameraResolution.width,
                    cameraResolution.height);

            camera.setParameters(parameters);
        }

        public void requestPreviewFrame(final Camera.PreviewCallback callback) {
            camera.setOneShotPreviewCallback(callback);
        }

        public PlanarYUVLuminanceSource buildLuminanceSource(final byte[] data) {
            return new PlanarYUVLuminanceSource(data, cameraResolution.width,
                    cameraResolution.height, framePreview.top, framePreview.left,
                    framePreview.height(), framePreview.width(), false);
        }

        public void setTorch(final boolean enabled) {
            if(camera == null){
                return;
            }
            if (enabled != getTorchEnabled(camera))
                setTorchEnabled(camera, enabled);
        }

        public boolean torchEnabled() {
            if(camera == null){
                return false;
            }
            return getTorchEnabled(camera);
        }

        private static boolean getTorchEnabled(final Camera camera) {
            final Camera.Parameters parameters = camera.getParameters();
            if (parameters != null) {
                final String flashMode = camera.getParameters().getFlashMode();
                return flashMode != null
                        && (Camera.Parameters.FLASH_MODE_ON.equals(flashMode) || Camera.Parameters.FLASH_MODE_TORCH
                        .equals(flashMode));
            }

            return false;
        }

        private static void setTorchEnabled(final Camera camera,
                                            final boolean enabled) {
            final Camera.Parameters parameters = camera.getParameters();

            final List<String> supportedFlashModes = parameters
                    .getSupportedFlashModes();
            if (supportedFlashModes != null) {
                final String flashMode;
                if (enabled) {
                    flashMode = findValue(supportedFlashModes,
                            Camera.Parameters.FLASH_MODE_TORCH,
                            Camera.Parameters.FLASH_MODE_ON);
                }
                else {
                    flashMode = findValue(supportedFlashModes,
                            Camera.Parameters.FLASH_MODE_OFF);
                }
                if (flashMode != null) {
                    camera.cancelAutoFocus(); // autofocus can cause conflict

                    parameters.setFlashMode(flashMode);
                    camera.setParameters(parameters);
                }
            }
        }

        private static String findValue(final Collection<String> values,
                                        final String... valuesToFind) {
            for (final String valueToFind : valuesToFind)
                if (values.contains(valueToFind))
                    return valueToFind;

            return null;
        }
    }
}