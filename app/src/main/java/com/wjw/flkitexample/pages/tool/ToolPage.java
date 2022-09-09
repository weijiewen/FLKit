package com.wjw.flkitexample.pages.tool;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.wjw.flkit.ui.FLTableView;
import com.wjw.flkit.base.FLNavigationView;
import com.wjw.flkit.base.FLTabBarActivity;
import com.wjw.flkitexample.databinding.CellMainBinding;
import com.wjw.flkitexample.databinding.PageToolBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ToolPage extends FLTabBarActivity.FLTabBarPage<PageToolBinding> {
    private List<String> datas = new ArrayList<>();
    public ToolPage(Context context) {
        super(context);
    }

    @Override
    protected PageToolBinding getBinding() {
        return PageToolBinding.inflate(LayoutInflater.from(getContext()), this, false);
    }

    @Override
    protected void configNavigation(FLNavigationView navigationView) {
        navigationView.setTitle("工具类");
    }

    @Override
    protected void didLoad() {
        datas = Arrays.asList(
                "动画"
        );
        FLTableView.CreatCell<TableViewCell> creatCell = new FLTableView.CreatCell<TableViewCell>() {
            @Override
            public int itemCount(int section) {
                return datas.size();
            }

            @Override
            public TableViewCell getCell(@NonNull ViewGroup parent) {
                return new TableViewCell(CellMainBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
            }
        };
        binding.tableView.setCreatCell("暂无数据", creatCell);
        binding.tableView.reloadData();
    }
    private class TableViewCell extends FLTableView.FLTableViewCell<CellMainBinding> {
        public TableViewCell(@NonNull CellMainBinding cellBinding) {
            super(cellBinding);
            cellBinding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (section == 0) {
                        if (index == 0) {
                            getActivity().startActivity(new Intent(getActivity(), AnimationActivity.class));
                        }
                    }
                }
            });
        }

        @Override
        protected void bindData(CellMainBinding cellBinding, int section, int index) {
            cellBinding.text.setText(datas.get(index));
        }
    }
}
