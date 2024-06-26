package com.wjw.flkitexample.pages.controls.activities;


import android.view.LayoutInflater;
import android.view.View;

import com.wjw.flkit.base.FLBindingActivity;
import com.wjw.flkit.base.FLNavigationView;
import com.wjw.flkitexample.databinding.ActivityFlexLayoutBinding;

public class FlexLayoutActivity extends FLBindingActivity<ActivityFlexLayoutBinding> {

    @Override
    protected void configNavigation(FLNavigationView navigationView) {

    }

    @Override
    protected ActivityFlexLayoutBinding getBinding() {
        return ActivityFlexLayoutBinding.inflate(LayoutInflater.from(this));
    }

    @Override
    protected void didLoad() {

    }

    @Override
    protected void didClick(View view) {

    }
}