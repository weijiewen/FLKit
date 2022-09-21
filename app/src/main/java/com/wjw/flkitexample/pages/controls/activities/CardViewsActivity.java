package com.wjw.flkitexample.pages.controls.activities;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;

import com.wjw.flkit.base.FLBindingActivity;
import com.wjw.flkit.base.FLNavigationView;
import com.wjw.flkitexample.R;
import com.wjw.flkitexample.databinding.ActivityCardViewsBinding;

public class CardViewsActivity extends FLBindingActivity<ActivityCardViewsBinding> {

    @Override
    protected void configNavigation(FLNavigationView navigationView) {
        navigationView.setTitle("FLCardView");
    }

    @Override
    protected ActivityCardViewsBinding getBinding() {
        return ActivityCardViewsBinding.inflate(LayoutInflater.from(this));
    }

    @Override
    protected void didLoad() {
        binding.loadingDefault.setOnClickListener(this);
        binding.loadingAuto.setOnClickListener(this);
        binding.loading40dp.setOnClickListener(this);
    }

    @Override
    protected void didClick(View view) {
        switch (view.getId()) {
            case R.id.loading_default:
                if (binding.loadingDefault.isLoading()) {
                    binding.loadingDefault.stopLoading();
                }
                else {
                    binding.loadingDefault.startLoading();
                }
                break;
            case R.id.loading_auto:
                if (binding.loadingAuto.isLoading()) {
                    binding.loadingAuto.stopLoading();
                }
                else {
                    binding.loadingAuto.startLoading(Color.BLACK, Color.WHITE, 0);
                }
                break;
            case R.id.loading_40dp:
                if (binding.loading40dp.isLoading()) {
                    binding.loading40dp.stopLoading();
                }
                else {
                    binding.loading40dp.startLoading(dipToPx(40));
                }
                break;
        }
    }
}