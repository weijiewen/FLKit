package com.wjw.flkit.base;

import android.Manifest;
import android.animation.Animator;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Space;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.wjw.flkit.ui.FLImageBrowser;
import com.wjw.flkit.unit.FLTimer;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class FLBaseActivity extends FragmentActivity implements View.OnClickListener {
    private static int defaultBackgroundColor = Color.parseColor("#F4F4F3");
    private static int defalutBackImgaeID = 0;

    public final static void setDefaultBackgroundColor(int defaultBackgroundColor) {
        FLBaseActivity.defaultBackgroundColor = defaultBackgroundColor;
    }

    public final static void setDefalutBackImgaeID(int defalutBackImgaeID) {
        FLBaseActivity.defalutBackImgaeID = defalutBackImgaeID;
    }
    private int backgroundColor;
    public void setBackgroundColor(int color) {
        superLayout.setBackgroundColor(color);
    }

    protected RelativeLayout superLayout;
    protected RelativeLayout annexLayout;
    private View loadingContent;
    private FLAlertDialog dialogContent;
    protected View view;
    protected FLNavigationView navigationView;
    @Override
    protected final void onCreate(@Nullable Bundle savedInstanceState) {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);//设置绘画模式
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setStatusStyle(StatusStyle.drak);
        super.onCreate(savedInstanceState);

        superLayout = new RelativeLayout(this);
        superLayout.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        superLayout.setBackgroundColor(defaultBackgroundColor);
        superLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                endEdit();
            }
        });

        if (addNavigation()) {
            RelativeLayout.LayoutParams navigationParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            navigationParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            navigationParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            navigationView = new FLNavigationView(this);
            navigationView.setLayoutParams(navigationParams);
            navigationView.setPadding(0, getStatusHeight(), 0, 0);
            configNavigation(navigationView);
            navigationView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    endEdit();
                }
            });
        }

        RelativeLayout.LayoutParams loadingParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        loadingParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        loadingParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        annexLayout = new RelativeLayout(this);
        annexLayout.setLayoutParams(loadingParams);
        annexLayout.setBackgroundColor(Color.parseColor("#1E000000"));
        annexLayout.setGravity(Gravity.CENTER);
        annexLayout.setAlpha(0.f);
        annexLayout.setVisibility(View.INVISIBLE);
        annexLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                endEdit();
            }
        });
        annexLayout.animate().setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                if (annexLayout.getAlpha() < 0.5) {
                    annexLayout.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onAnimationEnd(Animator animator) {
                if (annexLayout.getAlpha() < 0.5) {
                    annexLayout.setVisibility(View.INVISIBLE);
                }
            }
            @Override
            public void onAnimationCancel(Animator animator) {}
            @Override
            public void onAnimationRepeat(Animator animator) {}
        });
        view = getView();
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                endEdit();
            }
        });
        RelativeLayout.LayoutParams rootParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        FLOffsetStyle offsetStyle = offsetStyle();
        switch (offsetStyle) {
            case None:
                rootParams.topMargin = 0;
                break;
            case StatusBar:
                rootParams.topMargin = getStatusHeight();
                break;
            case NavigationBar:
                rootParams.topMargin = getNavigationHeight();
                break;
        }
        view.setLayoutParams(rootParams);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        superLayout.addView(view);
        if (navigationView != null) {
            superLayout.addView(navigationView);
        }
        superLayout.addView(annexLayout);
        setContentView(superLayout);

        if (!isTaskRoot() && navigationView != null) {
            if (defalutBackImgaeID == 0) {
                navigationView.addBack(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        endEdit();
                        onBackPressed();
                    }
                });
            }
            else {
                ImageView backImage = new ImageView(this);
                backImage.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, dipToPx(44)));
                backImage.setMinimumWidth(dipToPx(44));
                backImage.setScaleType(ImageView.ScaleType.CENTER);
                backImage.setImageResource(defalutBackImgaeID);
                backImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        endEdit();
                        if (dialogContent == null && progressBar == null) {
                            finish();
                        }
                    }
                });
                navigationView.addLeftItem(backImage);
            }
        }
        didLoad(savedInstanceState);
    }

    @Override
    public final void onBackPressed() {
        if (dialogContent == null && progressBar == null) {
            willBackPressed(new WillBackCallback() {
                @Override
                public void backPressed() {
                    FLBaseActivity.super.onBackPressed();
                }
            });
        }
    }

    public interface WillBackCallback {
        void backPressed();
    }
    protected void willBackPressed(WillBackCallback backCallback) {
        backCallback.backPressed();
    }

    protected boolean addNavigation() {
        return true;
    }

    protected void didLoad(@Nullable Bundle savedInstanceState) {
        didLoad();
    }

    @Override
    public final void onClick(View view) {
        endEdit();
        didClick(view);
    }
    protected abstract void configNavigation(FLNavigationView navigationView);
    protected abstract View getView();
    protected abstract void didLoad();
    protected abstract void didClick(View view);
    public enum FLOffsetStyle {
        None,           //不偏移
        StatusBar,      //偏移状态栏高度
        NavigationBar,  //偏移导航栏高度
    }
    protected FLOffsetStyle offsetStyle() {
        return FLOffsetStyle.NavigationBar;
    }

    protected final FLBaseActivity getActivity() {
        return this;
    }

    public enum StatusStyle {
        light,
        drak,
    }
    private StatusStyle statusStyle;
    public final StatusStyle getStatusStyle() {
        return statusStyle;
    }
    public final void setStatusStyle(StatusStyle style) {
        statusStyle = style;
        switch (style) {
            case light:
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);//白色
                break;
            case drak:
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//黑色
                break;
        }
    }

    public final int dipToPx(float pxValue) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (pxValue * scale + 0.5f);
    }

    public final int getStatusHeight() {
        int height = 0;
        int resourceId = getApplicationContext().getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            height = getApplicationContext().getResources().getDimensionPixelSize(resourceId);
        }
        return height;
    }

    public final int getNavigationHeight() {
        return getStatusHeight() + dipToPx(FLNavigationView.navigationHeight);
    }

    public final void endEdit() {
        View focusView = getWindow().getDecorView().findFocus();
        if (focusView != null) {
            focusView.clearFocus();//取消焦点
            InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(focusView.getWindowToken(), 0);
        }
    }

    private int animationDuration = 200;
    private void removeViewWhenDismiss(View view) {
        view.animate().setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {}
            @Override
            public void onAnimationEnd(Animator animator) {
                if (view.getAlpha() < 0.5) {
                    annexLayout.removeView(view);
                }
            }
            @Override
            public void onAnimationCancel(Animator animator) {}
            @Override
            public void onAnimationRepeat(Animator animator) {}
        });
    }
    private void dismissLoading(boolean hideDialog) {
        progressBar = null;
        progressTextView = null;
        View view = this.loadingContent;
        loadingContent = null;
        if (view != null) {
            view.animate().alpha(0.f).scaleX(0.5F).scaleY(0.5F).setDuration(animationDuration);
        }
        if (hideDialog && loadingContent == null && dialogContent == null) {
            annexLayout.animate().alpha(0.f).setDuration(animationDuration);
        }
    }
    public final void dismissLoading() {
        dismissLoading(true);
    }
    public final void showLoading() {
        showLoading(Color.parseColor("#247BEF"));
    }
    public final void showLoading(int color) {
        dismissLoading(false);
        RelativeLayout.LayoutParams linearParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        linearParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        linearParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setLayoutParams(linearParams);
        linearLayout.setGravity(Gravity.CENTER);
        linearLayout.setAlpha(0.f);
        linearLayout.setScaleX(0.5F);
        linearLayout.setScaleY(0.5F);
        removeViewWhenDismiss(linearLayout);
        annexLayout.addView(linearLayout);
        loadingContent = linearLayout;

        CardView cardView = new CardView(this);
        cardView.setLayoutParams(new LinearLayout.LayoutParams(dipToPx(100), dipToPx(100)));
        cardView.setCardElevation(0);
        cardView.setRadius(dipToPx(8));
        cardView.setCardBackgroundColor(Color.WHITE);
        linearLayout.addView(cardView);

        LinearLayout layout = new LinearLayout(this);
        layout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        layout.setGravity(Gravity.CENTER);
        cardView.addView(layout);

        ProgressBar progressBar = new ProgressBar(this);
        progressBar.setLayoutParams(new LinearLayoutCompat.LayoutParams(dipToPx(45), dipToPx(45)));
        progressBar.setIndeterminateTintList(ColorStateList.valueOf(color));
        layout.addView(progressBar);

        annexLayout.animate().alpha(1.f).setDuration(animationDuration);
        loadingContent.animate().alpha(1.f).scaleX(1).scaleY(1).setDuration(animationDuration);
    }
    public final void showTip(String tip) {
        showTip(tip, 15, Color.BLACK);
    }
    public final void showTip(String tip, int dipTextSize, @ColorInt int colorId) {
        showTip(Color.WHITE, tip, dipTextSize, colorId);
    }
    public final void showTip(@ColorInt int backgroundColor, String tip, int dipTextSize, @ColorInt int colorId) {
        dismissLoading(false);
        RelativeLayout.LayoutParams linearParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        linearParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        linearParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setLayoutParams(linearParams);
        linearLayout.setGravity(Gravity.CENTER);
        linearLayout.setAlpha(0.f);
        linearLayout.setScaleX(0.5F);
        linearLayout.setScaleY(0.5F);
        removeViewWhenDismiss(linearLayout);
        annexLayout.addView(linearLayout);
        loadingContent = linearLayout;
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (loadingContent == linearLayout) {
                    dismissLoading();
                }
            }
        });

        CardView cardView = new CardView(this);
        cardView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        cardView.setCardElevation(0);
        cardView.setRadius(dipToPx(8));
        cardView.setCardBackgroundColor(backgroundColor);
        linearLayout.addView(cardView);

        TextView textView = new TextView(this);
        textView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        textView.setGravity(Gravity.CENTER);
        textView.setPadding(dipToPx(22), dipToPx(10), dipToPx(22), dipToPx(10));
        textView.setMinWidth(dipToPx(50));
        textView.setMaxWidth(annexLayout.getWidth() - dipToPx(40 * 2));
        textView.setMaxLines(999);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, dipTextSize);
        textView.setTextColor(colorId);
        textView.setText(tip);
        cardView.addView(textView);

        int delay = tip.length() / 4 * 300;
        if (delay < 1200) {
            delay = 1200;
        }
        if (delay > 3200) {
            delay = 3200;
        }
        annexLayout.animate().alpha(1.f).setDuration(animationDuration);
        loadingContent.animate().alpha(1.f).scaleX(1).scaleY(1).setDuration(animationDuration);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (loadingContent == linearLayout) {
                    dismissLoading();
                }
            }
        }, delay);
    }
    private ProgressBar progressBar;
    private TextView progressTextView;
    public final void showProgress() {

        dismissLoading(false);
        RelativeLayout.LayoutParams linearParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        linearParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        linearParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setLayoutParams(linearParams);
        linearLayout.setGravity(Gravity.CENTER);
        linearLayout.setAlpha(0.f);
        linearLayout.setScaleX(0.5F);
        linearLayout.setScaleY(0.5F);
        removeViewWhenDismiss(linearLayout);
        annexLayout.addView(linearLayout);
        loadingContent = linearLayout;

        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        cardParams.setMargins(dipToPx(80), 0, dipToPx(80), 0);
        CardView cardView = new CardView(this);
        cardView.setLayoutParams(cardParams);
        cardView.setCardElevation(0);
        cardView.setRadius(dipToPx(8));
        cardView.setCardBackgroundColor(Color.WHITE);
        linearLayout.addView(cardView);

        LinearLayout layout = new LinearLayout(this);
        layout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        layout.setGravity(Gravity.CENTER);
        layout.setOrientation(LinearLayout.VERTICAL);
        cardView.addView(layout);

        Space space = new Space(this);
        space.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dipToPx(22)));
        layout.addView(space);

        GradientDrawable progressBg = new GradientDrawable();
        progressBg.setCornerRadius(dipToPx(3));
        progressBg.setColor(Color.parseColor("#EEEEEE"));

        GradientDrawable progressContent = new GradientDrawable();
        progressContent.setCornerRadius(dipToPx(3));
        progressContent.setColor(Color.parseColor("#4169E1"));

        ClipDrawable progressClip = new ClipDrawable(progressContent, Gravity.LEFT, ClipDrawable.HORIZONTAL);
        Drawable[] progressDrawables = {progressBg, progressClip};
        LayerDrawable progressLayerDrawable = new LayerDrawable(progressDrawables);
        progressLayerDrawable.setId(0, android.R.id.background);
        progressLayerDrawable.setId(1, android.R.id.progress);
        LinearLayoutCompat.LayoutParams progressParams = new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dipToPx(6));
        progressParams.setMargins(dipToPx(30), dipToPx(5), dipToPx(30), dipToPx(5));
        progressBar = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
        progressBar.setMax(1000);
        progressBar.setLayoutParams(progressParams);
        progressBar.setProgressDrawable(progressLayerDrawable);
        layout.addView(progressBar);
        progressBar.setProgress(0);

        LinearLayout.LayoutParams progressTextParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, dipToPx(20));
        progressTextParams.setMargins(0, 0, 0, dipToPx(10));
        progressTextView = new TextView(this);
        progressTextView.setLayoutParams(progressTextParams);
        progressTextView.setTextColor(Color.BLACK);
        progressTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
        progressTextView.setText("0%");
        layout.addView(progressTextView);

        annexLayout.animate().alpha(1.f).setDuration(animationDuration);
        loadingContent.animate().alpha(1.f).scaleX(1).scaleY(1).setDuration(animationDuration);
    }
    public final void changeProgress(float progress) {
        if (progressBar == null) {
            showProgress();
        }
        int currentProgress = (int) (progress * 1000);
        if (currentProgress < 0) {
            currentProgress = 0;
        }
        if (currentProgress > 1000) {
            currentProgress = 1000;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            progressBar.setProgress(currentProgress, true);
        }
        else {
            progressBar.setProgress(currentProgress);
        }
        progressTextView.setText(currentProgress / 10 + "%");
    }

    public enum FLDialogStyle {
        Alert,
        ActionSheet,
    }
    public interface FLAlertDialogConfig {
        void addItems(FLAlertDialog dialog);
    }
    public interface FLAlertDialogTouch {
        void touch();
    }
    public final void showDialogAlert(FLDialogStyle style, String title, String content, FLAlertDialogConfig config) {
        RelativeLayout.LayoutParams linearParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        linearParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        linearParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        FLAlertDialog dialog = new FLAlertDialog(this, title, content, style);
        dialog.setLayout(new FLAlertDialogLayout() {
            @Override
            public void show() {
                if (style == FLDialogStyle.Alert) {
                    dialog.animate().alpha(1.f).scaleX(1.f).scaleY(1.f).setDuration(animationDuration);
                }
                else {
                    int height = dialog.linearContent.getHeight();
                    dialog.setTranslationY(dialog.linearContent.getHeight());
                    dialog.animate().alpha(1.f).translationY(0).setDuration(animationDuration);
                }
                annexLayout.animate().alpha(1.f).setDuration(animationDuration);
            }
            @Override
            public void dismiss() {

                if (dialog != null) {
                    dialog.animate().setListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animator) {}
                        @Override
                        public void onAnimationEnd(Animator animator) {
                            if (dialog.getAlpha() < 0.5) {
                                annexLayout.removeView(dialog);
                            }
                        }
                        @Override
                        public void onAnimationCancel(Animator animator) {}
                        @Override
                        public void onAnimationRepeat(Animator animator) {}
                    });
                    if (dialog.style == FLDialogStyle.Alert) {
                        dialog.animate().alpha(0.f).scaleX(0.5F).scaleY(0.5F).setDuration(animationDuration);
                    }
                    else {
                        dialog.linearContent.animate().translationY(dialog.getHeight()).setDuration(animationDuration);
                        dialog.animate().alpha(0.f).setDuration(animationDuration);
                    }
                }
                dialogContent = null;
                if (dialogContent == null && loadingContent == null) {
                    annexLayout.animate().alpha(0.f).setDuration(animationDuration);
                }
            }
        });
        dialog.setLayoutParams(linearParams);
        dialog.setAlpha(0.f);
        if (style == FLDialogStyle.Alert) {
            dialog.setScaleX(0.5F);
            dialog.setScaleY(0.5F);
        }
        annexLayout.addView(dialog);
        config.addItems(dialog);
        dialog.show();
        dialogContent = dialog;
    }
    public interface FLAlertDialogLayout {
        void show();
        void dismiss();
    }
    public static class FLAlertDialog extends LinearLayout {
        private TextView cancel;
        private ArrayList<TextView> textViews = new ArrayList<>();
        private LinearLayout linearContent;
        private String title;
        private String content;
        private FLDialogStyle style;
        private FLAlertDialogLayout layout;
        private FLAlertDialog(@NonNull Context context, String title, String content, FLDialogStyle style) {
            super(context);
            setGravity(Gravity.CENTER);
            setOrientation(VERTICAL);
            this.title = title;
            this.content = content;
            this.style = style;
            setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (cancel == null && textViews.isEmpty()) {
                        layout.dismiss();
                    }
                }
            });
        }
        @Override
        protected void onLayout(boolean changed, int l, int t, int r, int b) {
            super.onLayout(changed, l, t, r, b);
            layout.show();
        }

        private void setLayout(FLAlertDialogLayout layout) {
            this.layout = layout;
        }

        private void show() {
            linearContent = new LinearLayout(getContext());
            linearContent.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            linearContent.setGravity(Gravity.CENTER);
            linearContent.setOrientation(VERTICAL);

            LayoutParams cardParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            CardView cardView = new CardView(getContext());
            cardView.setCardElevation(0);
            cardView.setRadius(dipToPx(8));
            cardView.setCardBackgroundColor(Color.WHITE);

            LinearLayout view = new LinearLayout(getContext());
            view.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            view.setGravity(Gravity.CENTER);
            view.setOrientation(VERTICAL);
            cardView.addView(view);
            boolean didAddText = false;
            if (title != null && !title.isEmpty()) {
                didAddText = true;
                TextView textView = new TextView(getContext());
                textView.setLayoutParams(new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                textView.setTextColor(Color.BLACK);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, style == FLDialogStyle.Alert ? 16 : 14);
                textView.setText(title);
                textView.setMaxLines(999);
                textView.setGravity(Gravity.CENTER);
                textView.setPadding(dipToPx(15), dipToPx(15), dipToPx(15), 0);
                view.addView(textView);
            }
            if (content != null && !content.isEmpty()) {
                didAddText = true;
                TextView textView = new TextView(getContext());
                textView.setLayoutParams(new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                textView.setTextColor(Color.parseColor("#5D5C5C"));
                textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, style == FLDialogStyle.Alert ? 14 : 12);
                textView.setText(content);
                textView.setMaxLines(999);
                textView.setGravity(Gravity.CENTER);
                textView.setPadding(dipToPx(15), dipToPx(10), dipToPx(15), 0);
                view.addView(textView);
            }
            if (didAddText) {
                LayoutParams lineParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dipToPx(1));
                if (style == FLDialogStyle.Alert) {
                    lineParams.setMargins(0, dipToPx(25), 0, 0);
                }
                else {
                    lineParams.setMargins(0, dipToPx(15), 0, 0);
                }
                View lineView = new View(getContext());
                lineView.setLayoutParams(lineParams);
                lineView.setBackgroundColor(Color.parseColor("#EEEEEE"));
                view.addView(lineView);
            }

            LayoutParams cancelCardParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            CardView cancelCardView = null;
            LinearLayout textLinearLayout = new LinearLayout(getContext());
            textLinearLayout.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            textLinearLayout.setGravity(Gravity.CENTER);
            switch (style) {
                case Alert:
                    cardParams.setMargins(dipToPx(60), 0, dipToPx(60), 0);
                    if (textViews.size() < 2) {
                        textLinearLayout.setOrientation(HORIZONTAL);
                        boolean didAddCancel = false;
                        if (cancel != null) {
                            didAddCancel = true;
                            cancel.setLayoutParams(new LayoutParams(0, dipToPx(40), 1));
                            textLinearLayout.addView(cancel);
                        }
                        if (didAddCancel) {
                            LayoutParams lineParams = new LayoutParams(dipToPx(1), dipToPx(40));
                            View lineView = new View(getContext());
                            lineView.setLayoutParams(lineParams);
                            lineView.setBackgroundColor(Color.parseColor("#EEEEEE"));
                            textLinearLayout.addView(lineView);
                        }
                        if (textViews.size() == 1) {
                            textViews.get(0).setLayoutParams(new LayoutParams(0, dipToPx(40), 1));
                            textLinearLayout.addView(textViews.get(0));
                        }
                    }
                    else {
                        textLinearLayout.setOrientation(VERTICAL);
                        for (int i = 0; i < textViews.size(); i++) {
                            textViews.get(i).setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dipToPx(40)));
                            textLinearLayout.addView(textViews.get(i));
                            if (i < textViews.size() - 1) {
                                LayoutParams lineParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dipToPx(1));
                                View lineView = new View(getContext());
                                lineView.setLayoutParams(lineParams);
                                lineView.setBackgroundColor(Color.parseColor("#EEEEEE"));
                                textLinearLayout.addView(lineView);
                            }
                        }
                        if (cancel != null) {
                            LayoutParams lineParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dipToPx(1));
                            View lineView = new View(getContext());
                            lineView.setLayoutParams(lineParams);
                            lineView.setBackgroundColor(Color.parseColor("#EEEEEE"));
                            textLinearLayout.addView(lineView);

                            cancel.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dipToPx(40)));
                            textLinearLayout.addView(cancel);
                        }
                    }

                    break;
                case ActionSheet:
                    Space space = new Space(getContext());
                    space.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1));
                    addView(space);
                    cardParams.setMargins(dipToPx(20), 0, dipToPx(20), 0);
                    textLinearLayout.setOrientation(VERTICAL);
                    for (int i = 0; i < textViews.size(); i++) {
                        textViews.get(i).setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dipToPx(40)));
                        textLinearLayout.addView(textViews.get(i));
                        if (i < textViews.size() - 1) {
                            LayoutParams lineParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dipToPx(1));
                            View lineView = new View(getContext());
                            lineView.setLayoutParams(lineParams);
                            lineView.setBackgroundColor(Color.parseColor("#EEEEEE"));
                            textLinearLayout.addView(lineView);
                        }
                    }
                    if (cancel != null) {
                        LayoutParams cancelParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        cancelParams.setMargins(dipToPx(20), dipToPx(10), dipToPx(20), dipToPx(20));
                        cancelCardView = new CardView(getContext());
                        cancelCardView.setLayoutParams(cancelParams);
                        cancelCardView.setCardElevation(0);
                        cancelCardView.setRadius(dipToPx(8));
                        cancelCardView.setCardBackgroundColor(Color.WHITE);

                        cancel.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dipToPx(40)));
                        cancelCardView.addView(cancel);
                    }

                    break;
            }
            view.addView(textLinearLayout);
            cardView.setLayoutParams(cardParams);
            linearContent.addView(cardView);
            if (cancelCardView == null) {
                Space space = new Space(getContext());
                space.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dipToPx(20)));
                linearContent.addView(space);
            }
            else {
                linearContent.addView(cancelCardView);
            }
            addView(linearContent);
        }
        private final int dipToPx(float pxValue) {
            final float scale = getResources().getDisplayMetrics().density;
            return (int) (pxValue * scale + 0.5f);
        }
        public final void addItem(String text, FLAlertDialogTouch touch) {
            addItem(text, 15, Color.parseColor("#4169E1"), touch);
        }
        public final void addItem(String text, int dipTextSize, @ColorInt int colorId, FLAlertDialogTouch touch) {
            TextView textView = new TextView(getContext());
            textView.setGravity(Gravity.CENTER);
            textView.setText(text);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, dipTextSize);
            textView.setTextColor(colorId);
            textView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    layout.dismiss();
                    touch.touch();
                }
            });
            textViews.add(textView);
        }
        public final void addCancel(FLAlertDialogTouch touch) {
            addCancel("取消", 15, Color.parseColor("#5D5C5C"), touch);
        }
        public final void addCancel(String text, int dipTextSize, @ColorInt int colorId, FLAlertDialogTouch touch) {
            if (cancel == null) {
                TextView textView = new TextView(getContext());
                textView.setGravity(Gravity.CENTER);
                textView.setText(text);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, dipTextSize);
                textView.setTextColor(colorId);
                textView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        layout.dismiss();
                        if (touch != null) {
                            touch.touch();
                        }
                    }
                });
                cancel = textView;
            }
        }
    }
    public void showPopup(View touchView, ConfigPopup config) {
        endEdit();
        PopupView popupView = new PopupView(getActivity());
        PopupConfig popupConfig = new PopupConfig(popupView);
        config.loadPopup(popupConfig);
        addFullView(popupView);
        popupView.show(touchView, popupConfig);
    }

    public enum PopupOrientation {
        HORIZONTAL, VERTICAL;
    }
    public interface ConfigPopup {
        void loadPopup(PopupConfig config);
    }
    public class PopupConfig {
        private PopupView popupView;
        private PopupConfig(PopupView popupView) {
            this.popupView = popupView;
        }
        public void setOrientation(PopupOrientation orientation) {
            this.orientation = orientation == PopupOrientation.HORIZONTAL ? LinearLayout.HORIZONTAL : LinearLayout.VERTICAL;
        }

        public void setMaskColor(int maskColor) {
            this.maskColor = maskColor;
        }

        public void setPopupColor(int popupColor) {
            this.popupColor = popupColor;
        }

        public void setImageHeight(int imageHeight) {
            this.imageHeight = imageHeight;
        }

        public void setTextSize(int textSize) {
            this.textSize = textSize;
        }

        public void setTextColor(int textColor) {
            this.textColor = textColor;
        }

        public void addItem(String text, View.OnClickListener listener) {
            addItem(0, text, listener);
        }
        public void addItem(String text, int textColor, View.OnClickListener listener) {
            addItem(0, text, textColor, listener);
        }
        public void addItem(@DrawableRes int image, String text, View.OnClickListener listener) {
            addItem(image, text, textColor, listener);
        }
        public void addItem(@DrawableRes int image, String text, int textColor, View.OnClickListener listener) {
            PopupView.PopupItemView itemView = new PopupView.PopupItemView(getActivity(), this);
            if (image != 0) {
                itemView.imageView.setImageResource(image);
            }
            else {
                itemView.imageView.setVisibility(View.GONE);
            }
            itemView.textView.setTextColor(textColor);
            itemView.textView.setText(text);
            addItem(itemView, listener);
        }
        public void addItem(View itemView, View.OnClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onClick(v);
                    popupView.animate().alpha(0).setDuration(300);
                    popupView.cardView.animate().scaleY(0).setDuration(300);
                }
            });
            itemViewList.add(itemView);
        }
        private int orientation = LinearLayout.VERTICAL;
        private int maskColor = Color.parseColor("#11000000");
        private int popupColor = Color.parseColor("#FFFFFF");
        private int imageHeight = 14;
        private int textSize = 14;
        private int textColor = Color.parseColor("#333333");
        private List<View> itemViewList = new ArrayList<>();
    }
    private static class PopupView extends FrameLayout {
        FLBaseActivity context;
        public PopupView(@NonNull FLBaseActivity context) {
            super(context);
            this.context = context;
            setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            cardView = new CardView(context);
            cardView.setRadius(context.dipToPx(4));
            cardView.setCardElevation(context.dipToPx(1));
            addView(cardView, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            itemLayout = new LinearLayout(context);
            itemLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            itemLayout.setPadding(0, context.dipToPx(4), 0, context.dipToPx(4));
            cardView.addView(itemLayout);
            animate().setListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(@NonNull Animator animation) {}
                @Override
                public void onAnimationEnd(@NonNull Animator animation) {
                    if (getAlpha() < 0.5) {
                        context.removeFullView(PopupView.this);
                    }
                }
                @Override
                public void onAnimationCancel(@NonNull Animator animation) {}
                @Override
                public void onAnimationRepeat(@NonNull Animator animation) {}
            });
            setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    animate().alpha(0).setDuration(100);
                    cardView.animate().scaleY(0).setDuration(100);
                }
            });
        }
        public void show(View showView, PopupConfig config) {
            setBackgroundColor(config.maskColor);
            cardView.setCardBackgroundColor(config.popupColor);
            itemLayout.setOrientation(config.orientation);
            itemLayout.removeAllViews();
            for (int i = 0; i < config.itemViewList.size(); i ++) {
                View itemview = config.itemViewList.get(i);
                itemLayout.addView(itemview, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                if (i < config.itemViewList.size() - 1) {
                    int lineWidth = config.orientation == LinearLayout.HORIZONTAL ? context.dipToPx(1) : ViewGroup.LayoutParams.MATCH_PARENT;
                    int lineHeight = config.orientation == LinearLayout.VERTICAL ? context.dipToPx(1) : ViewGroup.LayoutParams.MATCH_PARENT;
                    LinearLayout.LayoutParams lineParams = new LinearLayout.LayoutParams(lineWidth, lineHeight);
                    View line = new View(getContext());
                    line.setBackgroundColor(Color.parseColor("#F0F0F0"));
                    itemLayout.addView(line, lineParams);
                }
            }
            cardView.measure(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            Activity activity = (Activity) getContext();

            DisplayMetrics dm = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
            int screenWidth = dm.widthPixels;
            int screenHeight = dm.heightPixels;

            int width = cardView.getMeasuredWidth();
            int height = cardView.getMeasuredHeight();

            int[] location = new int[2];
            showView.getLocationOnScreen(location);
            int x = location[0] + showView.getWidth() / 2 - width / 2;
            int y = location[1] + showView.getHeight() - context.dipToPx(5);
            if (x < 0) {
                x = context.dipToPx(5);
            } else if (x + width > screenWidth) {
                x = screenWidth - width - context.dipToPx(5);
            }
            if (y + height > screenHeight) {
                y = y - height + context.dipToPx(5);
            }
            FrameLayout.LayoutParams cardParams = (LayoutParams) cardView.getLayoutParams();
            cardParams.setMargins(x, y, 0, 0);
            cardView.setPivotY(0);
            setAlpha(0);
            cardView.setScaleY(0);
            setVisibility(VISIBLE);
            animate().alpha(1).setDuration(100);
            cardView.animate().scaleY(1).setDuration(100);
        }
        private CardView cardView;
        private LinearLayout itemLayout;
        private static class PopupItemView extends LinearLayout {
            private ImageView imageView;
            private TextView textView;
            public PopupItemView(FLBaseActivity context, PopupConfig config) {
                super(context);
                setOrientation(HORIZONTAL);
                setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                setGravity(Gravity.CENTER_VERTICAL);
                setPadding(context.dipToPx(15), context.dipToPx(6), context.dipToPx(15), context.dipToPx(6));
                LinearLayout.LayoutParams imageParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, context.dipToPx(config.imageHeight));
                imageParams.setMargins(0, 0, context.dipToPx(5), 0);
                imageView = new ImageView(context);
                imageView.setLayoutParams(imageParams);
                addView(imageView);
                textView = new TextView(context);
                textView.setTextColor(config.textColor);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, config.textSize);
                addView(textView);
            }
        }
    }

    private FLTimer timer;
    public final void startTimer(long delay, long period, FLTimer.FLTimerListencener listencener) {
        stopTimer();
        if (timer == null) {
            timer = new FLTimer();
        }
        timer.startTimer(delay, period, listencener);
    }
    public final void stopTimer() {
        if (timer != null) {
            timer.stopTimer();
        }
    }


    private StatusStyle imageBrowserStatusStyle;
    private FLImageBrowser imageBrowser;
    public interface BrowserImageListence {
        void config(int index, ImageView imageView);
    }
    public final void browserImage(int showIndex, int size, BrowserImageListence listence) {
        dismissImageBrowser(-1);
        imageBrowserStatusStyle = getStatusStyle();
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        FLImageBrowser browser = new FLImageBrowser(this);
        imageBrowser = browser;
        browser.setListence(this, showIndex, size, new FLImageBrowser.FLImageBrowserListence() {
            @Override
            public void config(int index, ImageView imageView) {
                listence.config(index, imageView);
            }

            @Override
            public void touch(int index) {
                dismissImageBrowser(index);
            }
        });
        browser.setBackgroundColor(Color.BLACK);
        browser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                superLayout.removeView(browser);
                if (imageBrowser == browser) {
                    imageBrowser = null;
                }
            }
        });
        browser.animate().setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                if (imageBrowser == null) {
                    setStatusStyle(imageBrowserStatusStyle);
                }
            }
            @Override
            public void onAnimationEnd(Animator animator) {
                if (imageBrowser != null) {
                    setStatusStyle(StatusStyle.light);
                }
                if (imageBrowser == null || imageBrowser != browser) {
                    superLayout.removeView(browser);
                }
            }
            @Override
            public void onAnimationCancel(Animator animator) {}
            @Override
            public void onAnimationRepeat(Animator animator) {}
        });
        browser.setLayoutParams(layoutParams);
        browser.setAlpha(0.f);
        superLayout.addView(browser);
        browser.setTranslationY(superLayout.getHeight());
        browser.animate().translationY(0).alpha(1).setDuration(300);
    }

    private void dismissImageBrowser(int index) {
        if (imageBrowser != null) {
            FLImageBrowser browser = imageBrowser;
            imageBrowser = null;
            ImageView sourceView = null;
            browser.animate().translationY(superLayout.getHeight()).alpha(0.f).setDuration(300);
        }
    }

    public final void addFullView(View view) {
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        view.setLayoutParams(layoutParams);
        view.setVisibility(View.VISIBLE);
        superLayout.addView(view, superLayout.indexOfChild(annexLayout));
        view.setVisibility(View.INVISIBLE);
    }
    public final void addFullViewBelowNavigation(View view) {
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        view.setLayoutParams(layoutParams);
        view.setVisibility(View.VISIBLE);
        superLayout.addView(view, superLayout.indexOfChild(navigationView));
        view.setVisibility(View.INVISIBLE);
    }
    public final void removeFullView(View view) {
        superLayout.removeView(view);
    }


    public interface PickCallback {
        void pickData(Bitmap image);
    }
    private PickCallback pickCallback;
    private ActivityResultLauncher<Intent> cameraLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == RESULT_OK) {
                if (pickCallback != null) {
                    pickCallback.pickData((Bitmap) result.getData().getExtras().get("data"));
                }
            }
            pickCallback = null;
        }
    });
    private ActivityResultLauncher<Intent> albumLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == RESULT_OK) {
                if (pickCallback != null) {
                    Bitmap bitmap = null;
                    String path = getUriPath(result.getData().getData());
                    try {
                        bitmap = BitmapFactory.decodeFile(path);
                    } catch (Exception e) {

                    }
                    pickCallback.pickData(bitmap);
                }
            }
            pickCallback = null;
        }
    });
    private String getUriPath(Uri uri) {
        String data = null;
        if (null == uri)
            return null;
        final String scheme = uri.getScheme();
        if (scheme == null)
            data = uri.getPath();
        else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = getContentResolver().query(uri, new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        return data;
    }

    public void openCamera(PickCallback callback) {
        pickCallback = callback;
        Intent intent = new Intent(
                MediaStore.ACTION_IMAGE_CAPTURE, null);
        cameraLauncher.launch(intent);
    }

    public void openAlbum(PickCallback callback) {
        pickCallback = callback;
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        albumLauncher.launch(intent);
    }

    private HashMap<Integer, List<WeakReference<PermissionsResult>>> resultMap = new HashMap<>();
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
    };
    private static final Integer REQUEST_PERMISSION_STORAGE_CODE = 0;
    private static final Integer REQUEST_PERMISSION_CAMERA_CODE = 1;

    public interface PermissionsResult {
        void didGranted();

        void didDenied();
    }

    public void requestStorage(PermissionsResult result) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            List<WeakReference<PermissionsResult>> results = resultMap.get(REQUEST_PERMISSION_STORAGE_CODE);
            if (results == null) {
                results = new ArrayList();
                resultMap.put(REQUEST_PERMISSION_STORAGE_CODE, results);
            }
            results.add(new WeakReference<>(result));
            ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_PERMISSION_STORAGE_CODE);
        } else {
            result.didGranted();
        }
    }

    public void requestCamera(PermissionsResult result) {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            List results = resultMap.get(REQUEST_PERMISSION_CAMERA_CODE);
            if (results == null) {
                results = new ArrayList();
                resultMap.put(REQUEST_PERMISSION_CAMERA_CODE, results);
            }
            results.add(new WeakReference<>(result));
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    REQUEST_PERMISSION_CAMERA_CODE);
        } else {
            result.didGranted();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        List<WeakReference<PermissionsResult>> results = resultMap.get(requestCode);
        if (results != null) {
            resultMap.remove(requestCode);
            for (WeakReference<PermissionsResult> result : results) {
                if (result.get() != null) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        result.get().didGranted();
                    } else {
                        result.get().didDenied();
                    }
                }
            }
        }
    }

    public boolean isPermissionInstall() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            boolean hasInstallPermission = getPackageManager().canRequestPackageInstalls();
            return hasInstallPermission;
        }
        return true;
    }

    private static final Integer REQUEST_PERMISSION_INSTALL_CODE = 2;
    private static final Integer REQUEST_PERMISSION_INSTALL_O_CODE = 3;

    public void requestInstall(PermissionsResult result) {
        List results = resultMap.get(REQUEST_PERMISSION_INSTALL_O_CODE);
        if (results == null) {
            results = new ArrayList();
            resultMap.put(REQUEST_PERMISSION_INSTALL_O_CODE, results);
        }
        results.add(new WeakReference<>(result));
        Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, Uri.parse("package:" + getPackageName()));
        startActivityForResult(intent, REQUEST_PERMISSION_INSTALL_O_CODE);
    }

    public void installApk(Intent intent, PermissionsResult result) {
        if (getPackageManager().queryIntentActivities(intent, 0).size() > 0) {
            //如果APK安装界面存在，携带请求码跳转。使用forResult是为了处理用户 取消 安装的事件。外面这层判断理论上来说可以不要，但是由于国内的定制，这个加上还是比较保险的
            List results = resultMap.get(REQUEST_PERMISSION_INSTALL_CODE);
            if (results == null) {
                results = new ArrayList();
                resultMap.put(REQUEST_PERMISSION_INSTALL_CODE, results);
            }
            results.add(new WeakReference<>(result));
            startActivityForResult(intent, REQUEST_PERMISSION_INSTALL_CODE);
        }
    }

    private static final Integer REQUEST_PERMISSION_FORGROUND_LOCATION = 4;
    private static final Integer REQUEST_PERMISSION_BACKGROUND_LOCATION = 5;
    public void requestFrogroundLocation(PermissionsResult result) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                result.didGranted();
                return;
            }
            List results = resultMap.get(REQUEST_PERMISSION_FORGROUND_LOCATION);
            if (results == null) {
                results = new ArrayList();
                resultMap.put(REQUEST_PERMISSION_FORGROUND_LOCATION, results);
            }
            results.add(new WeakReference<>(result));
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_PERMISSION_FORGROUND_LOCATION);
        }
    }
    public void requestBackgroundLocation(PermissionsResult result) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                result.didGranted();
                return;
            }
            List results = resultMap.get(REQUEST_PERMISSION_BACKGROUND_LOCATION);
            if (results == null) {
                results = new ArrayList();
                resultMap.put(REQUEST_PERMISSION_BACKGROUND_LOCATION, results);
            }
            results.add(new WeakReference<>(result));
            requestPermissions(new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, REQUEST_PERMISSION_BACKGROUND_LOCATION);
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        List<WeakReference<PermissionsResult>> results = resultMap.get(requestCode);
        if (results != null) {
            resultMap.remove(requestCode);
            for (WeakReference<PermissionsResult> result : results) {
                if (result.get() != null) {
                    if (resultCode == RESULT_OK) {
                        result.get().didGranted();
                    } else {
                        result.get().didDenied();
                    }
                }
            }
        }
    }
}