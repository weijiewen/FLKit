package com.wjw.flkitexample.pages.tool.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wjw.flkit.base.FLBindingActivity;
import com.wjw.flkit.base.FLNavigationView;
import com.wjw.flkit.ui.FLTableView;
import com.wjw.flkitexample.databinding.ActivityRedisBinding;
import com.wjw.flkitexample.databinding.CellMainBinding;

import java.util.Arrays;
import java.util.List;

public class RedisActivity extends FLBindingActivity<ActivityRedisBinding> {
    private List<String> strings;
    @Override
    protected void configNavigation(FLNavigationView navigationView) {
        navigationView.setTitle("redis全局缓存");
    }

    @Override
    protected ActivityRedisBinding getBinding() {
        return ActivityRedisBinding.inflate(LayoutInflater.from(this));
    }

    @Override
    protected void didLoad() {
        strings = Arrays.asList(
                "同步状态redis",
                "同步进度redis"
        );
        FLTableView.CreatCell<Cell> creatCell = new FLTableView.CreatCell<Cell>() {
            @Override
            public int itemCount(int section) {
                return strings.size();
            }

            @Override
            public Cell getCell(@NonNull ViewGroup parent) {
                return new Cell(CellMainBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
            }
        };
        binding.tableView.setCreatCell(creatCell);
        binding.tableView.reloadData();
    }

    @Override
    protected void didClick(View view) {

    }
    private class Cell extends FLTableView.FLTableViewCell<CellMainBinding> {

        public Cell(@NonNull CellMainBinding cellBinding) {
            super(cellBinding);
            cellBinding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    switch (index) {
                        case 0:
                            getActivity().startActivity(new Intent(getActivity(), RedisStateActivity.class));
                            break;
                        case 1:
                            getActivity().startActivity(new Intent(getActivity(), RedisProgressActivity.class));
                            break;
                    }
                }
            });
        }

        @Override
        protected void bindData(CellMainBinding cellBinding, int section, int index) {
            cellBinding.text.setText(strings.get(index));
        }
    }
}