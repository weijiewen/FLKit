package com.wjw.flkitexample.pages.controls.activities.table;

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
import com.wjw.flkitexample.databinding.ActivityTableEmptyBinding;

public class TableEmptyActivity extends FLBindingActivity<ActivityTableEmptyBinding> {
    @Override
    protected void configNavigation(FLNavigationView navigationView) {
        navigationView.setTitle("自定义empty");
    }

    @Override
    protected ActivityTableEmptyBinding getBinding() {
        return ActivityTableEmptyBinding.inflate(LayoutInflater.from(this));
    }

    @Override
    protected void didLoad() {
        binding.tableView.setConfigEmpty(new FLTableView.ConfigEmpty() {
            @Override
            public View getEmptyView(Context context) {
                return new EmptyView(context);
            }
        });
        binding.tableView.reloadData();
    }

    @Override
    protected void didClick(View view) {

    }
    private class EmptyView extends LinearLayout {

        public EmptyView(Context context) {
            super(context);
            setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            LayoutInflater.from(context).inflate(R.layout.view_table_empty, this);
        }
    }
}