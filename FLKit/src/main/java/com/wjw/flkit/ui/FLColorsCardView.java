package com.wjw.flkit.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.wjw.flkit.R;

public class FLColorsCardView extends CardView {
    View colorsView;
    public FLColorsCardView(@NonNull Context context) {
        this(context, null);
    }

    public FLColorsCardView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FLColorsCardView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        colorsView = new View(context);
        colorsView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        addView(colorsView);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.FLColorsCardView, defStyleAttr, 0);
        setCardElevation(array.getDimensionPixelSize(R.styleable.FLColorsCardView_cardElevation, 0));
        setRadius(array.getDimensionPixelSize(R.styleable.FLColorsCardView_cardCornerRadius, 0));
        int orientation = array.getInt(R.styleable.FLColorsCardView_linearOrientation, 0);
        String colorString = array.getString(R.styleable.FLColorsCardView_linearColors);
        if (colorString == null) {
            colorString = "";
        }
        int[] colors = null;
        if (colorString.isEmpty()) {
            colors = new int[]{
                    Color.parseColor("#00000000"),
                    Color.parseColor("#00000000")
            };
        }
        else {
            if (colorString.contains(" ")) {
                colorString = colorString.replace(" ", "");
            }
            if (colorString.contains(",")) {
                String[] colorStrings = colorString.split(",");
                colors = new int[colorStrings.length];
                for (int i = 0; i < colorStrings.length; i++) {
                    colors[i] = Color.parseColor(colorStrings[i]);
                }
            }
            else {
                colors = new int[2];
                colors[0] = Color.parseColor(colorString);
                colors[1] = Color.parseColor(colorString);
            }
        }
        GradientDrawable drawable = new GradientDrawable(GradientDrawable.Orientation.values()[orientation], colors);
        colorsView.setBackground(drawable);
        array.recycle();
    }
}
