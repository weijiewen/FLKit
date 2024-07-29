package com.wjw.flkit.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.wjw.flkit.R;

public class FLFlexLayout extends ViewGroup {
    public final void setHorizontalSpace(int horizontalSpace) {
        this.horizontalSpace = horizontalSpace;
        requestLayout();
        invalidate();
    }

    public final void setVerticalSpace(int verticalSpace) {
        this.verticalSpace = verticalSpace;
        requestLayout();
        invalidate();
    }
    public FLFlexLayout(@NonNull Context context) {
        this(context, null);
    }

    public FLFlexLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FLFlexLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public FLFlexLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.FLFlexLayout, defStyleAttr, defStyleRes);
        horizontalSpace = array.getDimensionPixelSize(R.styleable.FLFlexLayout_horizontalSpace, 0);
        verticalSpace = array.getDimensionPixelSize(R.styleable.FLFlexLayout_verticalSpace, 0);
        mScreenWidth = context.getResources().getDisplayMetrics().widthPixels;
        mDensity = context.getResources().getDisplayMetrics().density;
    }

    private int mScreenWidth;
    private int horizontalSpace, verticalSpace;
    private float mDensity;//设备密度，用于将dp转为px
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int childCount = getChildCount();


        int left = getPaddingLeft();

        int maxHeightInLine = 0;

        int allHeight = 0;
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
            if (child.getMeasuredHeight() > maxHeightInLine) {
                maxHeightInLine = child.getMeasuredHeight();
            }

            if (left > getPaddingLeft()) {
                left += dip2px(horizontalSpace);
            }
            left += child.getMeasuredWidth();
            if (left > widthSize - getPaddingRight() - getPaddingLeft()) {//换行
                left = getPaddingLeft() + child.getMeasuredWidth();
                if (allHeight > 0) {
                    allHeight += dip2px(verticalSpace);
                }
                allHeight += maxHeightInLine;
                maxHeightInLine = child.getMeasuredHeight();
            }
        }
        if (maxHeightInLine > 0) {
            allHeight += maxHeightInLine + dip2px(verticalSpace);
        }

        if (widthMode != MeasureSpec.EXACTLY) {
            widthSize = mScreenWidth;
        }
        if (heightMode != MeasureSpec.EXACTLY) {
            heightSize = allHeight + getPaddingBottom() + getPaddingTop();
        }
        setMeasuredDimension(widthSize, heightSize);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (changed) {
            View child = null;
            int left = getPaddingLeft();
            int top = getPaddingTop();
            int maxHeightInLine = 0;
            for (int i = 0, len = getChildCount(); i < len; i++) {
                child = getChildAt(i);
                if (i > 0) {
                    if (getChildAt(i - 1).getMeasuredHeight() > maxHeightInLine) {
                        maxHeightInLine = getChildAt(i - 1).getMeasuredHeight();
                    }
                    left += getChildAt(i - 1).getMeasuredWidth() + dip2px(horizontalSpace);
                    if (left + child.getMeasuredWidth() >= getWidth() - getPaddingRight() - getPaddingLeft()) {//这一行所有子view相加的宽度大于容器的宽度，需要换行
                        left = getPaddingLeft();
                        top += maxHeightInLine + dip2px(verticalSpace);
                        maxHeightInLine = 0;
                    }
                }
                child.layout(left, top, left + child.getMeasuredWidth(), top + child.getMeasuredHeight());
            }
        }
    }



    private int dip2px(float dpValue) {
        return (int) (dpValue * mDensity + 0.5f);
    }
}