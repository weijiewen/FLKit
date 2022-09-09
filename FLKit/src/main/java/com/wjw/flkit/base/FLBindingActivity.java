package com.wjw.flkit.base;

import android.view.View;
import androidx.fragment.app.FragmentActivity;
import androidx.viewbinding.ViewBinding;


public abstract class FLBindingActivity<T extends ViewBinding> extends FLBaseActivity {
    protected T binding;
    @Override
    protected View getView() {
        binding = getBinding();
        return binding.getRoot();
    }
    protected abstract void configNavigation(FLNavigationView navigationView);
    protected abstract T getBinding();
    protected abstract void didLoad();
    protected abstract void didClick(View view);
}