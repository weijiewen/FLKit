package com.wjw.flkit.ui;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.wjw.flkit.R;
import com.wjw.flkit.base.FLBaseActivity;
import com.wjw.flkit.unit.FLTimer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;

public class FLTimeButton extends FLCardView {
    private TextView textView;
    private int backgroundColor;
    private ColorStateList foregroundColor;
    private String normalText;
    private int interval = 60000;
    private HashMap<String, FLSmscodeCache> cacheHashMap;
    private FLSmscodeCache smscodeCache;
    private FLTimer timer = new FLTimer();

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public final String checkTimer(String phone) {
        return checkTimer(phone, 0);
    }

    public final String checkTimer(String phone, int type) {
        String key = phone + "_" + type;
        FLSmscodeCache cache = cacheHashMap.get(key);
        if (cache != null) {
            long timestamp = System.currentTimeMillis();
            if (timestamp < cache.timestamp + interval) {
                smscodeCache = cache;
                startTimer();
                return cache.value;
            }
            smscodeCache = null;
            cacheHashMap.remove(phone);
            writeCache();
        }
        return null;
    }
    public final void startTimer(String phone, String value) {
        startTimer(phone, 0, value);
    }
    public final void startTimer(String phone, int type, String value) {
        FLSmscodeCache cache = new FLSmscodeCache();
        cache.key = phone + "_" + type;
        cache.timestamp = System.currentTimeMillis();
        cache.value = value == null ? "" : value;
        cacheHashMap.put(cache.key, cache);
        writeCache();
        smscodeCache = cache;
        startTimer();
    }
    public final void cancelTimer() {
        stopTimer();
        smscodeCache = null;
        textView.setText(normalText);
    }
    public void setGravity(int gravity) {
        textView.setGravity(gravity);
    }

    public void setText(String text) {
        normalText = text;
        textView.setText(text);
    }
    public void setTextSize(int size) {
        setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
    }
    public void setTextSize(int unit, int size) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            textView.setAutoSizeTextTypeUniformWithConfiguration(1, size, 1, unit);
        }
        else {
            textView.setTextSize(unit, size);
        }
    }
    public void setTextColor(int color) {
        textView.setTextColor(color);
    }
    public void setHintTextColor(int color) {
        textView.setHintTextColor(color);
    }
    public void setIndeterminateTintList(ColorStateList color) {
        foregroundColor = color == null ? ColorStateList.valueOf(Color.WHITE) : color;
    }
    @Override
    public void setCardBackgroundColor(int color) {
        super.setCardBackgroundColor(color);
        backgroundColor = color;
    }

    @Override
    public void setCardBackgroundColor(@Nullable ColorStateList color) {
        setCardBackgroundColor(color.getDefaultColor());
    }

    public FLTimeButton(@NonNull Context context) {
        this(context, null);
    }

    public FLTimeButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FLTimeButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        readCache();

        textView = new TextView(context);
        textView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        textView.setGravity(Gravity.CENTER);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            textView.setAutoSizeTextTypeWithDefaults(TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM);
        }
        addView(textView);

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.FLTimeButton, defStyleAttr, 0);
        setCardBackgroundColor(array.getColor(R.styleable.FLTimeButton_cardBackgroundColor, Color.WHITE));
        setCardElevation(array.getDimensionPixelSize(R.styleable.FLTimeButton_cardElevation, 0));
        setRadius(array.getDimensionPixelSize(R.styleable.FLTimeButton_cardCornerRadius, 0));
        setGravity(array.getInt(R.styleable.FLTimeButton_android_gravity, Gravity.CENTER));
        setTextSize(TypedValue.COMPLEX_UNIT_PX, array.getDimensionPixelSize(R.styleable.FLTimeButton_android_textSize, 15));
        setText(array.getString(R.styleable.FLTimeButton_android_text));
        setTextColor(array.getColor(R.styleable.FLTimeButton_android_textColor, Color.BLACK));
        setHintTextColor(array.getColor(R.styleable.FLTimeButton_android_textColorHint, Color.GRAY));
        setIndeterminateTintList(array.getColorStateList(R.styleable.FLTimeButton_android_indeterminateTint));
        array.recycle();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        startTimer();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopTimer();
    }

    @Override
    public void startLoading() {
        startLoading(backgroundColor, foregroundColor.getDefaultColor());
    }

    private void startTimer() {
        stopLoading();
        long timestamp = System.currentTimeMillis();
        stopTimer();
        if (smscodeCache != null && timestamp < smscodeCache.timestamp + interval) {
            int seconds = (int) ((smscodeCache.timestamp + interval - timestamp) / 1000);
            setSeconds(seconds);
            setEnabled(false);
            timer.startTimer(0, 1000, new FLTimer.FLTimerListencener() {
                @Override
                public void run() {
                    long timestamp = System.currentTimeMillis();
                    if (smscodeCache != null && smscodeCache.timestamp + interval > timestamp) {
                        int seconds = (int) ((smscodeCache.timestamp + interval - timestamp) / 1000);
                        setSeconds(seconds);
                    }
                    else {
                        textView.setText(normalText);
                        stopTimer();
                    }
                }
            });
        }
        else {
            textView.setText(normalText);
        }
    }
    private void stopTimer() {
        setEnabled(true);
        if (timer != null) {
            timer.stopTimer();
        }
    }
    private void setSeconds(int seconds) {
        textView.setText("");
        textView.setHint(seconds + "s");
    }
    private void writeCache() {
        if (cacheHashMap != null && !cacheHashMap.isEmpty()) {
            try {
                String path = getContext().getFileStreamPath("FLTimeSmscodeCache.txt").getPath();
                ObjectOutputStream objectOutputStream =
                        new ObjectOutputStream( new FileOutputStream(new File(path)));
                objectOutputStream.writeObject(cacheHashMap);
                objectOutputStream.close();
            } catch (IOException e) {
                
            }
        }
    }
    private void readCache() {
        try {
            String path = getContext().getFileStreamPath("FLTimeSmscodeCache.txt").getPath();
            ObjectInputStream objectInputStream =
                    new ObjectInputStream( new FileInputStream( new File(path) ) );
            cacheHashMap = (HashMap<String, FLSmscodeCache>) objectInputStream.readObject();
            objectInputStream.close();
        } catch (Exception e) {
            
        }
        if (cacheHashMap == null) {
            cacheHashMap = new HashMap<>();
        }
    }
    private static class FLSmscodeCache implements Serializable {
        private static final long serialVersionUID = 1L;
        private String key;
        private long timestamp;
        private String value;

        public void setKey(String key) {
            this.key = key;
        }

        public String getKey() {
            return key;
        }

        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}
