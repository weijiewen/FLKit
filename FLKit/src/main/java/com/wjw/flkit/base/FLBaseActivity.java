package com.wjw.flkit.base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;

public abstract class FLBaseActivity<T extends ViewBinding> extends Activity implements View.OnClickListener {
    protected T binding;

    @Override
    protected final void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = creatBinding();
        setContentView(binding.getRoot());
        didLoad();
    }
    @Override
    public final void onClick(View view) {
        didClick(view);
    }

    protected abstract T creatBinding();
    protected abstract void didLoad();
    protected abstract void didClick(View view);
}