package com.wjw.flkitexample.pages.controls.activities;

import android.view.LayoutInflater;
import android.view.View;

import com.wjw.flkit.base.FLBindingActivity;
import com.wjw.flkit.base.FLNavigationView;
import com.wjw.flkitexample.databinding.ActivityColorsBinding;

public class ColorsActivity extends FLBindingActivity<ActivityColorsBinding> {

    @Override
    protected void configNavigation(FLNavigationView navigationView) {
        navigationView.setTitle("渐变控件");
    }

    @Override
    protected ActivityColorsBinding getBinding() {
        return ActivityColorsBinding.inflate(LayoutInflater.from(this));
    }

    @Override
    protected void didLoad() {

    }

    @Override
    protected void didClick(View view) {

    }
}