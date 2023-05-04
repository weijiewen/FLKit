package com.wjw.flkitexample.pages.controls.activities.table;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.wjw.flkit.base.FLBindingActivity;
import com.wjw.flkit.base.FLNavigationView;
import com.wjw.flkit.ui.FLTableView;
import com.wjw.flkitexample.R;
import com.wjw.flkitexample.databinding.ActivityTableLoadingBinding;
import com.wjw.flkitexample.databinding.CellTableViewBinding;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class TableLoadingActivity extends FLBindingActivity<ActivityTableLoadingBinding> {

    @Override
    protected void configNavigation(FLNavigationView navigationView) {
        navigationView.setTitle("自定义loading");
    }

    @Override
    protected ActivityTableLoadingBinding getBinding() {
        return ActivityTableLoadingBinding.inflate(LayoutInflater.from(this));
    }

    @Override
    protected void didLoad() {
        binding.tableView.setConfigLoading(new FLTableView.ConfigLoading() {
            @Override
            public View getLoadingView(Context context) {
                return new Loading(context);
            }
        });
        binding.tableView.startLoading();
    }

    @Override
    protected void didClick(View view) {

    }
    private class Loading extends LinearLayout {

        public Loading(Context context) {
            super(context);
            LayoutInflater.from(context).inflate(R.layout.view_table_loading, this);
        }
    }
}