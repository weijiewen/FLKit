package com.wjw.flkitexample.pages.loading;

import android.content.Context;
import android.view.LayoutInflater;

import com.wjw.flkit.base.FLNavigationView;
import com.wjw.flkit.base.FLTabBarActivity;
import com.wjw.flkitexample.databinding.PageLoadingBinding;

public class LoadingPage extends FLTabBarActivity.FLTabBarPage<PageLoadingBinding> {
    public LoadingPage(Context context) {
        super(context);
    }
    @Override
    protected PageLoadingBinding getBinding() {
        return PageLoadingBinding.inflate(LayoutInflater.from(getContext()), this, false);
    }
    @Override
    protected void configNavigation(FLNavigationView navigationView) {
        navigationView.setTitle("");
    }
    @Override
    protected void didLoad() {

    }

}
