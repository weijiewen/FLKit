package com.wjw.flkitexample.pages.tool;

import android.view.LayoutInflater;
import android.view.View;

import com.wjw.flkit.unit.FLAnimation;
import com.wjw.flkit.base.FLBindingActivity;
import com.wjw.flkit.base.FLNavigationView;
import com.wjw.flkitexample.databinding.ActivityStampBinding;

public class StampActivity extends FLBindingActivity<ActivityStampBinding> {

    @Override
    protected void configNavigation(FLNavigationView navigationView) {
        navigationView.setTitle("盖章动画");
    }

    @Override
    protected ActivityStampBinding getBinding() {
        return ActivityStampBinding.inflate(LayoutInflater.from(this));
    }

    @Override
    protected void didLoad() {
        float value = getIntent().getFloatExtra("value", 0);
        if (value == 0) {
            FLAnimation.startStampVibrate(binding.cardView, binding.stamp);
        }
        else {
            FLAnimation.startStampVibrate(binding.cardView, binding.stamp, value);
        }
    }

    @Override
    protected void didClick(View view) {

    }
}