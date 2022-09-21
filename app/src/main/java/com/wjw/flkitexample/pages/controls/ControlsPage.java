package com.wjw.flkitexample.pages.controls;

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
import com.wjw.flkitexample.databinding.PageControlsBinding;
import com.wjw.flkitexample.pages.controls.activities.CardViewsActivity;
import com.wjw.flkitexample.pages.controls.activities.SectionActivity;
import com.wjw.flkitexample.pages.controls.activities.SmscodeActivity;
import com.wjw.flkitexample.pages.controls.activities.TableViewActivity;

import java.util.Arrays;
import java.util.List;

public class ControlsPage extends FLTabBarActivity.FLTabBarPage<PageControlsBinding> {
    private List<String> strings;
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
        strings = Arrays.asList(
                "列表",
                "分段列表",
                "验证码倒计时",
                "FLCardView"
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
    private class Cell extends FLTableView.FLTableViewCell<CellMainBinding> {

        public Cell(@NonNull CellMainBinding cellMainBinding) {
            super(cellMainBinding);
            cellMainBinding.getRoot().setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    switch (index) {
                        case 0:
                            //"列表"
                            getActivity().startActivity(new Intent(getActivity(), TableViewActivity.class));
                            break;
                        case 1:
                            //"分段列表"
                            getActivity().startActivity(new Intent(getActivity(), SectionActivity.class));
                            break;
                        case 2:
                            //"验证码倒计时"
                            getActivity().startActivity(new Intent(getActivity(), SmscodeActivity.class));
                            break;
                        case 3:
                            //"FLCardView"
                            getActivity().startActivity(new Intent(getActivity(), CardViewsActivity.class));
                            break;
                    }
                }
            });
        }

        @Override
        protected void bindData(CellMainBinding cellMainBinding, int section, int index) {
            cellMainBinding.text.setText(strings.get(index));
        }
    }
}
