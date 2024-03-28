package com.wjw.flkit.ui;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.wjw.flkit.R;

public class FLCardView extends CardView {
    public FLCardView(@NonNull Context context) {
        this(context, null);
    }

    public FLCardView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FLCardView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        loadLayout(context, attrs, defStyleAttr);
    }
    private View colorsView;
    private void loadLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        colorsView = new View(context);
        colorsView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        addView(colorsView);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.FLCardView, defStyleAttr, 0);
        setCardElevation(array.getDimensionPixelSize(R.styleable.FLCardView_cardElevation, 0));
        setRadius(array.getDimensionPixelSize(R.styleable.FLCardView_cardCornerRadius, 0));
        int orientation = array.getInt(R.styleable.FLCardView_linearOrientation, 0);
        String colorsString = array.getString(R.styleable.FLCardView_linearColors);
        setLinearColors(orientation, colorsString);
        array.recycle();
    }
    public void setLinearColors(int orientation, String colorsString) {
        if (colorsString == null) {
            colorsString = "";
        }
        int[] colors = null;
        if (colorsString.isEmpty()) {
            colors = new int[]{
                    Color.parseColor("#00000000"),
                    Color.parseColor("#00000000")
            };
        }
        else {
            if (colorsString.contains(" ")) {
                colorsString = colorsString.replace(" ", "");
            }
            if (colorsString.contains(",")) {
                String[] colorStrings = colorsString.split(",");
                colors = new int[colorStrings.length];
                for (int i = 0; i < colorStrings.length; i++) {
                    colors[i] = Color.parseColor(colorStrings[i]);
                }
            }
            else {
                colors = new int[2];
                colors[0] = Color.parseColor(colorsString);
                colors[1] = Color.parseColor(colorsString);
            }
        }
        setLinearColors(orientation, colors);
    }
    public void setLinearColors(int orientation, int[] colors) {
        GradientDrawable drawable = new GradientDrawable(GradientDrawable.Orientation.values()[orientation], colors);
        colorsView.setBackground(drawable);
    }
    private ConstraintLayout progressView;
    private String progressParameter;
    public boolean isLoading() {
        return progressView != null;
    }
    public void startLoading() {
        startLoading(dipToPx(30));
    }
    public void startLoading(int maxSize) {
        startLoading(Color.WHITE, Color.parseColor("#247BEF"), maxSize);
    }
    public void startLoading(int backgroundColor, int foregroundColor) {
        startLoading(backgroundColor, foregroundColor, dipToPx(30));
    }
    public final void startLoading(int backgroundColor, int foregroundColor, int maxSize) {
        String progressParameter = "b:" + backgroundColor + "f:" + foregroundColor + "r:" + maxSize;
        if (this.progressParameter != null && this.progressParameter.equals(progressParameter)) {
            return;
        }
        this.progressParameter = progressParameter;
        stopLoading();
        progressView = new ConstraintLayout(getContext());
        progressView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        progressView.setBackgroundColor(backgroundColor);
        progressView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        addView(progressView);

        ConstraintLayout.LayoutParams progressBarParams = new ConstraintLayout.LayoutParams(0, 0);
        progressBarParams.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID;
        progressBarParams.rightToRight = ConstraintLayout.LayoutParams.PARENT_ID;
        progressBarParams.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
        progressBarParams.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
        progressBarParams.matchConstraintPercentWidth = 0.7F;
        progressBarParams.matchConstraintPercentHeight = 0.7F;
        if (maxSize > 0) {
            progressBarParams.matchConstraintMaxWidth = maxSize;
            progressBarParams.matchConstraintMaxHeight = maxSize;
        }
        ProgressBar progressBar = new ProgressBar(getContext());
        progressBar.setLayoutParams(progressBarParams);
        progressBar.setIndeterminateTintList(ColorStateList.valueOf(foregroundColor));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && maxSize > 0) {
            progressBar.setMaxWidth(maxSize);
            progressBar.setMaxHeight(maxSize);
        }
        progressView.addView(progressBar);
    }
    public final void stopLoading() {
        if (progressView != null) {
            removeView(progressView);
            progressView = null;
            progressParameter = null;
        }
    }
    protected final int dipToPx(float dpValue) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
