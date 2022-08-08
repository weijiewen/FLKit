package com.wjw.flkit.base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;

import com.google.android.material.navigation.NavigationView;

public abstract class FLBaseActivity<T extends ViewBinding> extends Activity implements View.OnClickListener {
    protected T binding;
    protected FLNavigationView navigationView;
    @Override
    protected final void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout superLayout = new LinearLayout(this);
        superLayout.setGravity(Gravity.CENTER);
        superLayout.setOrientation(LinearLayout.VERTICAL);

        navigationView = new FLNavigationView(this);
        superLayout.addView(navigationView);

        binding = creatBinding();
        superLayout.addView(binding.getRoot());
        setContentView(superLayout);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);


        didLoad();
    }
    @Override
    public final void onClick(View view) {
        didClick(view);
    }

    protected abstract T creatBinding();
    protected abstract void didLoad();
    protected abstract void didClick(View view);

    private int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}