package com.wjw.flkit.base;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Space;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewbinding.ViewBinding;

import com.wjw.flkit.FLImageBrowser;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public abstract class FLBaseActivity<T extends ViewBinding> extends FragmentActivity implements View.OnClickListener {
    private static int defaultBackgroundColor = Color.parseColor("#F4F4F3");
    private static int defalutBackImgaeID = 0;

    public final static void setDefaultBackgroundColor(int defaultBackgroundColor) {
        FLBaseActivity.defaultBackgroundColor = defaultBackgroundColor;
    }

    public final static void setDefalutBackImgaeID(int defalutBackImgaeID) {
        FLBaseActivity.defalutBackImgaeID = defalutBackImgaeID;
    }
    private int backgroundColor;

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
        superLayout.setBackgroundColor(backgroundColor);
    }

    protected RelativeLayout superLayout;
    protected RelativeLayout annexLayout;
    protected T binding;
    protected FLNavigationView navigationView;
    @Override
    protected final void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);//设置绘画模式
        setStatusStyle(StatusStyle.drak);

        superLayout = new RelativeLayout(this);
        superLayout.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        superLayout.setBackgroundColor(defaultBackgroundColor);

        RelativeLayout.LayoutParams navigationParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        navigationParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        navigationParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        navigationView = new FLNavigationView(this);
        navigationView.setLayoutParams(navigationParams);
        navigationView.setPadding(0, getStatusHeight(), 0, 0);
        configNavigation(navigationView);
        navigationView.creatLayout();

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
            public void onClick(View view) {}
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
        binding = getBinding();
        RelativeLayout.LayoutParams rootParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        if (!isFillParent()) {
            rootParams.topMargin = getStatusHeight() + dipToPx(44);
        }
        binding.getRoot().setLayoutParams(rootParams);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        superLayout.addView(binding.getRoot());
        superLayout.addView(annexLayout);
        superLayout.addView(navigationView);
        setContentView(superLayout);

        if (!isTaskRoot()) {
            if (defalutBackImgaeID == 0) {
                navigationView.addBack(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (dialogContent == null && progressBar == null) {
                            stopTimer();
                            finish();
                        }
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
                        if (dialogContent == null && progressBar == null) {
                            stopTimer();
                            finish();
                        }
                    }
                });
                navigationView.addLeftItem(backImage);
            }
        }
        didLoad();
    }

    @Override
    public final void onBackPressed() {
        if (dialogContent == null && progressBar == null) {
            if (willBackPressed()) {
                stopTimer();
                super.onBackPressed();
            }
        }
    }

    protected boolean willBackPressed() {
        return true;
    }

    @Override
    public final void onClick(View view) {
        endEdit();
        didClick(view);
    }
    protected abstract T getBinding();
    protected abstract void didLoad();
    protected abstract void didClick(View view);
    protected void configNavigation(FLNavigationView navigationView) {

    }
    //是否填充整个activity，返回false向下偏移一个导航栏的高度
    protected boolean isFillParent() {
        return false;
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

    protected final int dipToPx(float pxValue) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (pxValue * scale + 0.5f);
    }

    protected final int getStatusHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    protected final void endEdit() {
        View focusView = getWindow().getDecorView().findFocus();
        if (focusView != null) {
            focusView.clearFocus();//取消焦点
            InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(focusView.getWindowToken(), 0);
        }
    }

    private int animationDuration = 200;
    private View loadingContent;
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
        View loadingContent = this.loadingContent;
        this.loadingContent = null;
        if (loadingContent != null) {
            loadingContent.animate().alpha(0.f).scaleX(0.5F).scaleY(0.5F).setDuration(animationDuration);
        }
        if (hideDialog) {
            annexLayout.animate().alpha(0.f).setDuration(animationDuration);
        }
    }
    protected final void dismissLoading() {
        dismissLoading(true);
    }
    protected final void showLoading() {
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
        progressBar.setLayoutParams(new LinearLayoutCompat.LayoutParams(dipToPx(50), dipToPx(50)));
        layout.addView(progressBar);

        annexLayout.animate().alpha(1.f).setDuration(animationDuration);
        loadingContent.animate().alpha(1.f).scaleX(1).scaleY(1).setDuration(animationDuration);
    }
    protected final void showTip(String tip) {
        showTip(tip, 15, Color.BLACK);
    }
    protected final void showTip(String tip, int dipTextSize, @ColorInt int colorId) {
        showTip(Color.WHITE, tip, dipTextSize, colorId);
    }
    protected final void showTip(@ColorInt int backgroundColor, String tip, int dipTextSize, @ColorInt int colorId) {
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
    protected final void showProgress() {

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
    protected final void changeProgress(float progress) {
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

    private FLAlertDialog dialogContent;
    protected enum FLDialogStyle {
        Alert,
        ActionSheet,
    }
    protected interface FLAlertDialogConfig {
        void addItems(FLAlertDialog dialog);
    }
    protected interface FLAlertDialogTouch {
        void touch();
    }
    protected final void showDialogAlert(String title, String content, FLDialogStyle style, FLAlertDialogConfig config) {
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
                FLAlertDialog content = dialogContent;
                dialogContent = null;
                if (content != null) {
                    content.animate().setListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animator) {}
                        @Override
                        public void onAnimationEnd(Animator animator) {
                            if (content.getAlpha() < 0.5) {
                                annexLayout.removeView(content);
                            }
                        }
                        @Override
                        public void onAnimationCancel(Animator animator) {}
                        @Override
                        public void onAnimationRepeat(Animator animator) {}
                    });
                    if (content.style == FLDialogStyle.Alert) {
                        content.animate().alpha(0.f).scaleX(0.5F).scaleY(0.5F).setDuration(animationDuration);
                    }
                    else {
                        content.linearContent.animate().translationY(content.getHeight()).setDuration(animationDuration);
                        content.animate().alpha(0.f).setDuration(animationDuration);
                    }
                }
                annexLayout.animate().alpha(0.f).setDuration(animationDuration);
            }
        });
        dialog.setLayoutParams(linearParams);
        dialog.setAlpha(0.f);
        if (style == FLDialogStyle.Alert) {
            dialog.setScaleX(0.5F);
            dialog.setScaleY(0.5F);
        }
        removeViewWhenDismiss(dialog);
        annexLayout.addView(dialog);
        config.addItems(dialog);
        dialog.show();
        dialogContent = dialog;
    }
    protected interface FLAlertDialogLayout {
        void show();
        void dismiss();
    }
    protected static class FLAlertDialog extends LinearLayout {
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
            linearContent.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            linearContent.setGravity(Gravity.CENTER);
            linearContent.setOrientation(VERTICAL);

            LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            CardView cardView = new CardView(getContext());
            cardView.setCardElevation(0);
            cardView.setRadius(dipToPx(8));
            cardView.setCardBackgroundColor(Color.WHITE);

            LinearLayout view = new LinearLayout(getContext());
            view.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
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
                LinearLayout.LayoutParams lineParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dipToPx(1));
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

            LinearLayout.LayoutParams cancelCardParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            CardView cancelCardView = null;
            LinearLayout textLinearLayout = new LinearLayout(getContext());
            textLinearLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            textLinearLayout.setGravity(Gravity.CENTER);
            switch (style) {
                case Alert:
                    cardParams.setMargins(dipToPx(70), 0, dipToPx(70), 0);
                    if (textViews.size() < 2) {
                        textLinearLayout.setOrientation(HORIZONTAL);
                        boolean didAddCancel = false;
                        if (cancel != null) {
                            didAddCancel = true;
                            cancel.setLayoutParams(new LinearLayout.LayoutParams(0, dipToPx(40), 1));
                            textLinearLayout.addView(cancel);
                        }
                        if (didAddCancel) {
                            LinearLayout.LayoutParams lineParams = new LinearLayout.LayoutParams(dipToPx(1), dipToPx(40));
                            View lineView = new View(getContext());
                            lineView.setLayoutParams(lineParams);
                            lineView.setBackgroundColor(Color.parseColor("#EEEEEE"));
                            textLinearLayout.addView(lineView);
                        }
                        if (textViews.size() == 1) {
                            textViews.get(0).setLayoutParams(new LinearLayout.LayoutParams(0, dipToPx(40), 1));
                            textLinearLayout.addView(textViews.get(0));
                        }
                    }
                    else {
                        textLinearLayout.setOrientation(VERTICAL);
                        for (int i = 0; i < textViews.size(); i++) {
                            textViews.get(i).setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dipToPx(40)));
                            textLinearLayout.addView(textViews.get(i));
                            if (i < textViews.size() - 1) {
                                LinearLayout.LayoutParams lineParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dipToPx(1));
                                View lineView = new View(getContext());
                                lineView.setLayoutParams(lineParams);
                                lineView.setBackgroundColor(Color.parseColor("#EEEEEE"));
                                textLinearLayout.addView(lineView);
                            }
                        }
                        if (cancel != null) {
                            LinearLayout.LayoutParams lineParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dipToPx(1));
                            View lineView = new View(getContext());
                            lineView.setLayoutParams(lineParams);
                            lineView.setBackgroundColor(Color.parseColor("#EEEEEE"));
                            textLinearLayout.addView(lineView);

                            cancel.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dipToPx(40)));
                            textLinearLayout.addView(cancel);
                        }
                    }

                    break;
                case ActionSheet:
                    Space space = new Space(getContext());
                    space.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1));
                    addView(space);
                    cardParams.setMargins(dipToPx(20), 0, dipToPx(20), 0);
                    textLinearLayout.setOrientation(VERTICAL);
                    for (int i = 0; i < textViews.size(); i++) {
                        textViews.get(i).setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dipToPx(40)));
                        textLinearLayout.addView(textViews.get(i));
                        if (i < textViews.size() - 1) {
                            LinearLayout.LayoutParams lineParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dipToPx(1));
                            View lineView = new View(getContext());
                            lineView.setLayoutParams(lineParams);
                            lineView.setBackgroundColor(Color.parseColor("#EEEEEE"));
                            textLinearLayout.addView(lineView);
                        }
                    }
                    if (cancel != null) {
                        LinearLayout.LayoutParams cancelParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        cancelParams.setMargins(dipToPx(20), dipToPx(10), dipToPx(20), dipToPx(20));
                        cancelCardView = new CardView(getContext());
                        cancelCardView.setLayoutParams(cancelParams);
                        cancelCardView.setCardElevation(0);
                        cancelCardView.setRadius(dipToPx(8));
                        cancelCardView.setCardBackgroundColor(Color.WHITE);

                        cancel.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dipToPx(40)));
                        cancelCardView.addView(cancel);
                    }

                    break;
            }
            view.addView(textLinearLayout);
            cardView.setLayoutParams(cardParams);
            linearContent.addView(cardView);
            if (cancelCardView == null) {
                Space space = new Space(getContext());
                space.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dipToPx(20)));
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
                        touch.touch();
                    }
                });
                cancel = textView;
            }
        }
    }
    private Timer timer;
    public interface FLTimerListencener {
        void run();
    }
    protected final void startTimer(long delay, long period, FLTimerListencener listencener) {
        stopTimer();
        timer = new Timer();
        timer.schedule(new FLTimerTask(listencener), delay, period);
    }
    protected final void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }
    private class FLTimerTask extends TimerTask {
        private Handler handler = new Handler();
        private FLTimerListencener listencener;
        public FLTimerTask(FLTimerListencener listencener) {
            super();
            this.listencener = listencener;
        }
        @Override
        public void run() {
            if (listencener != null) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        listencener.run();
                    }
                });
            }
        }
    }

    private StatusStyle imageBrowserStatusStyle;
    private FLImageBrowser imageBrowser;
    protected interface BrowserImageListence {
        void config(int index, ImageView imageView);
    }
    protected final void browserImage(int showIndex, int size, BrowserImageListence listence) {
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
}