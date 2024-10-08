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
import com.wjw.flkitexample.pages.controls.activities.FlexLayoutActivity;
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
                "验证码倒计时",
                "FLCardView",
                "FLFlexLayout"
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
    private class Cell extends FLTableView.FLBindingCell<CellMainBinding> {

        public Cell(@NonNull CellMainBinding cellMainBinding) {
            super(cellMainBinding);
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
                    //"列表"
                    getActivity().startActivity(new Intent(getActivity(), TableViewActivity.class));
                    break;
                case 1:
                    //"验证码倒计时"
                    getActivity().startActivity(new Intent(getActivity(), SmscodeActivity.class));
                    break;
                case 2:
                    //"FLCardView"
                    getActivity().startActivity(new Intent(getActivity(), CardViewsActivity.class));
                    break;
                case 3:
                    //"FLFlexLayout"
                    getActivity().startActivity(new Intent(getActivity(), FlexLayoutActivity.class));
                    break;
            }
        }
    }
}
