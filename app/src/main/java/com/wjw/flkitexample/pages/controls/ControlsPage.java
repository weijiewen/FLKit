package com.wjw.flkitexample.pages.controls;

import android.content.Context;
import android.view.LayoutInflater;

import com.wjw.flkit.base.FLNavigationView;
import com.wjw.flkit.base.FLTabBarActivity;
import com.wjw.flkitexample.databinding.PageControlsBinding;

public class ControlsPage extends FLTabBarActivity.FLTabBarPage<PageControlsBinding> {
    public ControlsPage(Context context) {
        super(context);
    }

    @Override
    protected PageControlsBinding getBinding() {
        return PageControlsBinding.inflate(LayoutInflater.from(getContext()), this, false);
    }

    @Override
    protected void configNavigation(FLNavigationView navigationView) {
        navigationView.setTitle("控件");
    }

    @Override
    protected void didLoad() {

    }


}
