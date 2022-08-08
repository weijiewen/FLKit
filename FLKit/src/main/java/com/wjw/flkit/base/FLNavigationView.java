package com.wjw.flkit.base;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

public class FLNavigationView extends LinearLayout {
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
        creatLayout(context);
    }

    private LinearLayout leftLayout;
    private TextView textView;
    private LinearLayout rightLayout;
    private void creatLayout(Context context) {
        setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        setOrientation(HORIZONTAL);
        setBackgroundColor(defaultBackgroundColor);

        leftLayout = new LinearLayout(context);
        leftLayout.setBackgroundColor(defaultBackgroundColor);
        leftLayout.setBackgroundColor(Color.YELLOW);
        leftLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, dipToPx(context, 44)));
        leftLayout.setOrientation(LinearLayout.HORIZONTAL);
        leftLayout.setGravity(Gravity.CENTER);
        addView(leftLayout);

        textView = new TextView(context);
        textView.setLayoutParams(new LinearLayout.LayoutParams(0, dipToPx(context, 44), 1));
        textView.setPadding(dipToPx(context, 15), 0, dipToPx(context, 15), 0);
        textView.setGravity(Gravity.CENTER);
        textView.setMaxLines(1);
        textView.setEllipsize(TextUtils.TruncateAt.END);
        textView.setTextColor(defaultForegroundColor);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, defaultTitleSize);
        addView(textView);

        rightLayout = new LinearLayout(context);
        rightLayout.setBackgroundColor(defaultBackgroundColor);
        rightLayout.setBackgroundColor(Color.BLUE);
        rightLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, dipToPx(context, 44)));
        rightLayout.setOrientation(LinearLayout.HORIZONTAL);
        rightLayout.setGravity(Gravity.CENTER);
        addView(rightLayout);
    }

    public final void setTitle(String text) {
        textView.setText(text);
    }

    private int dipToPx(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue * scale + 0.5f);
    }
}
