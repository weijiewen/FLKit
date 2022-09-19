package com.wjw.flkitexample.pages.middle;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.wjw.flkit.base.FLBaseActivity;
import com.wjw.flkit.base.FLNavigationView;
import com.wjw.flkit.base.FLTabBarActivity;
import com.wjw.flkit.ui.FLTableView;
import com.wjw.flkitexample.databinding.CellMainBinding;
import com.wjw.flkitexample.databinding.PageMiddleBinding;
import com.wjw.flkitexample.pages.middle.activities.ImagePickerActivity;
import com.wjw.flkitexample.pages.middle.activities.KeyboardActivity;
import com.wjw.flkitexample.pages.middle.activities.QRCodeActivity;

import java.util.Arrays;
import java.util.List;

public class MiddlePage extends FLTabBarActivity.FLTabBarPage<PageMiddleBinding> {
    private List<String> strings;

    public MiddlePage(Context context) {
        super(context);
    }

    @Override
    protected PageMiddleBinding getBinding() {
        return PageMiddleBinding.inflate(LayoutInflater.from(getContext()), this, false);
    }

    @Override
    protected void configNavigation(FLNavigationView navigationView) {
        navigationView.setForegroundColor(Color.WHITE);
        navigationView.setTitle("第三方示例");
    }

    @Override
    protected FLBaseActivity.FLOffsetStyle offsetStyle() {
        return FLBaseActivity.FLOffsetStyle.None;
    }

    @Override
    protected void didLoad() {
        setStatusStyle(FLBaseActivity.StatusStyle.light);
        strings = Arrays.asList(
                "KingKeyboard键盘",
                "ImageSelector图片选择器",
                "ZXing二维码"
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
                            getActivity().startActivity(new Intent(getActivity(), KeyboardActivity.class));
                            break;
                        case 1:
                            getActivity().startActivity(new Intent(getActivity(), ImagePickerActivity.class));
                            break;
                        case 2:
                            getActivity().startActivity(new Intent(getActivity(), QRCodeActivity.class));
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
