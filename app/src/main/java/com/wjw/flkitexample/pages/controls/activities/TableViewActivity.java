package com.wjw.flkitexample.pages.controls.activities;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.wjw.flkit.ui.FLTableView;
import com.wjw.flkit.base.FLBindingActivity;
import com.wjw.flkit.base.FLNavigationView;
import com.wjw.flkitexample.databinding.ActivityTableViewBinding;
import com.wjw.flkitexample.databinding.CellMainBinding;
import com.wjw.flkitexample.pages.controls.activities.table.TableEmptyActivity;
import com.wjw.flkitexample.pages.controls.activities.table.TableErrorActivity;
import com.wjw.flkitexample.pages.controls.activities.table.TableLoadingActivity;
import com.wjw.flkitexample.pages.controls.activities.table.TableNormalActivity;
import com.wjw.flkitexample.pages.controls.activities.table.TableSectionActivity;

import java.util.Arrays;
import java.util.List;

public class TableViewActivity extends FLBindingActivity<ActivityTableViewBinding> {
    private List<String> strings;

    @Override
    protected ActivityTableViewBinding getBinding() {
        return ActivityTableViewBinding.inflate(LayoutInflater.from(this));
    }

    @Override
    protected void configNavigation(FLNavigationView navigationView) {
        navigationView.setTitle("列表");
    }

    @Override
    protected void didLoad() {
        strings = Arrays.asList(
                "列表",
                "分段列表",
                "自定义loading",
                "自定义empty",
                "自定义error"
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

    private class Cell extends FLTableView.FLBindingCell<CellMainBinding> {

        public Cell(@NonNull CellMainBinding cellBinding) {
            super(cellBinding);
        }

        @Override
        protected void bindData(int section, int index) {
            cellBinding.text.setText(strings.get(index));
        }

        @Override
        protected void onClick(int section, int index) {
            super.onClick(section, index);
            switch (index) {
                case 0:
                    startActivity(new Intent(getActivity(), TableNormalActivity.class));
                    break;
                case 1:
                    startActivity(new Intent(getActivity(), TableSectionActivity.class));
                    break;
                case 2:
                    startActivity(new Intent(getActivity(), TableLoadingActivity.class));
                    break;
                case 3:
                    startActivity(new Intent(getActivity(), TableEmptyActivity.class));
                    break;
                case 4:
                    startActivity(new Intent(getActivity(), TableErrorActivity.class));
                    break;
            }
        }
    }
}