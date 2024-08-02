package com.wjw.flkit.base;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;

import androidx.annotation.Nullable;

public class FLNavigationView extends LinearLayout {
    public static final int navigationHeight = 48;
    
    private static int defaultBackgroundColor = Color.parseColor("#00FFFFFF");
    private static int defaultForegroundColor = Color.BLACK;
    private static int defaultTitleSize = 15;

    public static void setDefaultBackgroundColor(int defaultBackgroundColor) {
        FLNavigationView.defaultBackgroundColor = defaultBackgroundColor;
    }
    public static void setDefaultForegroundColor(int defaultForegroundColor) {
        FLNavigationView.defaultForegroundColor = defaultForegroundColor;
    }
    public static void setDefaultTitleSize(int defaultTitleSize) {
        FLNavigationView.defaultTitleSize = defaultTitleSize;
    }

    private int foregroundColor;

    public void setForegroundColor(int foregroundColor) {
        this.foregroundColor = foregroundColor;
        textView.setTextColor(foregroundColor);
    }

    public int getForegroundColor() {
        return foregroundColor == 0 ? defaultForegroundColor : foregroundColor;
    }

    private FLNavigationLinearLayout leftLayout;
    private LinearLayout centerLayout;
    private TextView textView;
    private FLNavigationLinearLayout rightLayout;
    public final void setTitle(String text) {
        textView.setText(text);
    }
    public final void setTitleSize(int typedValue, int value) {
        textView.setTextSize(typedValue, value);
    }
    public final void setTypeface(int style) {
        textView.setTypeface(Typeface.defaultFromStyle(style));
    }
    public FLNavigationView(Context context) {
        this(context, null);
    }
    public FLNavigationView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public FLNavigationView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }
    public FLNavigationView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        setOrientation(HORIZONTAL);
        setBackgroundColor(defaultBackgroundColor);

        leftLayout = new FLNavigationLinearLayout(getContext(), new LinearChange() {
            @Override
            public void onSizeChanged(int w, int h) {
                reloadTextMargins();
            }
        });
        leftLayout.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, dipToPx(navigationHeight)));
        leftLayout.setOrientation(LinearLayout.HORIZONTAL);
        leftLayout.setGravity(Gravity.CENTER);
        addView(leftLayout);

        centerLayout = new LinearLayout(getContext());
        centerLayout.setLayoutParams(new LayoutParams(0, dipToPx(navigationHeight), 1));
        centerLayout.setOrientation(HORIZONTAL);
        addView(centerLayout);

        textView = new TextView(getContext());
        textView.setLayoutParams(new LayoutParams(0, dipToPx(navigationHeight), 1));
        textView.setPadding(dipToPx(15), 0, dipToPx(15), 0);
        textView.setEllipsize(TextUtils.TruncateAt.END);
        textView.setTextColor(getForegroundColor());
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, defaultTitleSize);
        textView.setGravity(Gravity.CENTER);
        textView.setMaxLines(1);
        centerLayout.addView(textView);

        rightLayout = new FLNavigationLinearLayout(getContext(), new LinearChange() {
            @Override
            public void onSizeChanged(int w, int h) {
                reloadTextMargins();
            }
        });
        rightLayout.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, dipToPx(navigationHeight)));
        rightLayout.setOrientation(LinearLayout.HORIZONTAL);
        rightLayout.setGravity(Gravity.CENTER);
        addView(rightLayout);
    }
    private FLNavigationBackButton backButton;
    public final void addBack(OnClickListener listener) {
        if (backButton != null) {
            leftLayout.removeView(backButton);
        }
        backButton = new FLNavigationBackButton(getContext());
        backButton.setLayoutParams(new LayoutParams(dipToPx(navigationHeight), dipToPx(navigationHeight)));
        backButton.setOnClickListener(listener);
        leftLayout.addView(backButton, 0);
    }
    public final void removeBack() {
        if (backButton != null) {
            leftLayout.removeView(backButton);
        }
    }
    public final void addLeftItem(View view) {
        leftLayout.addView(view);
    }
    public final void removeLeftItem(View view) {
        leftLayout.removeView(view);
    }
    public final void addCenterToLinearLayout(View view) {
        centerLayout.removeAllViews();
        centerLayout.addView(view);
    }
    public final void addRightItem(View view) {
        rightLayout.addView(view);
    }
    public final void removeRightItem(View view) {
        rightLayout.removeView(view);
    }
    private void reloadTextMargins() {
        int leftWidth = leftLayout.getWidth();
        int rightWidth = rightLayout.getWidth();
        int marginLeft = rightWidth > leftWidth ? rightWidth - leftWidth : 0;
        int marginRight = leftWidth > rightWidth ? leftWidth - rightWidth : 0;
        LayoutParams params = (LayoutParams) textView.getLayoutParams();
        params.setMargins(marginLeft, 0, marginRight, 0);
        textView.setLayoutParams(params);
    }
    private int dipToPx(float pxValue) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (pxValue * scale + 0.5f);
    }
    private interface LinearChange {
        void onSizeChanged(int w, int h);
    }
    private class FLNavigationLinearLayout extends LinearLayout {
        private LinearChange change;
        public FLNavigationLinearLayout(Context context, LinearChange change) {
            super(context);
            this.change = change;
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);
            change.onSizeChanged(w, h);
        }
    }
    private class FLNavigationBackButton extends View {
        public FLNavigationBackButton(Context context) {
            super(context);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setColor(getForegroundColor());
            paint.setStrokeWidth(3);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeCap(Paint.Cap.ROUND);
            float[] points = {
                    dipToPx(25), dipToPx(16), dipToPx(18), dipToPx(22),
                    dipToPx(18), dipToPx(22), dipToPx(25), dipToPx(28)
            };
            canvas.drawLines(points, paint);
        }
    }
}
