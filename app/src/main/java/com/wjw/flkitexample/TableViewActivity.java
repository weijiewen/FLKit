package com.wjw.flkitexample;

import android.view.LayoutInflater;
import android.view.View;

import com.wjw.flkit.base.FLBaseActivity;
import com.wjw.flkitexample.databinding.ActivityTableViewBinding;

public class TableViewActivity extends FLBaseActivity<ActivityTableViewBinding> {

    @Override
    protected ActivityTableViewBinding creatBinding() {
        return ActivityTableViewBinding.inflate(LayoutInflater.from(this));
    }

    @Override
    protected void didLoad() {
        navigationView.setTitle("哈哈");
    }

    @Override
    protected void didClick(View view) {

    }
}