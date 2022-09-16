package com.wjw.flkitexample.pages.tool.activities;

import androidx.annotation.NonNull;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wjw.flkit.unit.FLAnimation;
import com.wjw.flkit.ui.FLTableView;
import com.wjw.flkit.base.FLBindingActivity;
import com.wjw.flkit.base.FLNavigationView;
import com.wjw.flkitexample.databinding.ActivityAnimationBinding;
import com.wjw.flkitexample.databinding.CellMainBinding;

import java.util.Arrays;
import java.util.List;

public class AnimationActivity extends FLBindingActivity<ActivityAnimationBinding> {
    private List<String> strings;
    @Override
    protected void configNavigation(FLNavigationView navigationView) {
        navigationView.setTitle("动画");
    }

    @Override
    protected ActivityAnimationBinding getBinding() {
        return ActivityAnimationBinding.inflate(LayoutInflater.from(this));
    }

    @Override
    protected void didLoad() {
        strings = Arrays.asList(
                "水平震动",
                "水平震动-设置振幅",
                "旋转震动",
                "旋转震动-设置振幅",
                "缩放震动",
                "缩放震动-设置振幅",
                "盖章动画",
                "盖章动画-设置振幅"
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

        public Cell(@NonNull CellMainBinding cellMainBinding) {
            super(cellMainBinding);
            cellMainBinding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    switch (index) {
                        case 0:
                            //"水平震动"
                            FLAnimation.startHorizontalVibrate(cellMainBinding.getRoot());
                            break;
                        case 1:
                            //"水平震动-设置振幅",
                            FLAnimation.startHorizontalVibrate(cellMainBinding.getRoot(), 20);
                            break;
                        case 2:
                            //"旋转震动"
                            FLAnimation.startRotateVibrate(cellMainBinding.getRoot());
                            break;
                        case 3:
                            //"旋转震动-设置振幅"
                            FLAnimation.startRotateVibrate(cellMainBinding.getRoot(), 20);
                            break;
                        case 4:
                            //"缩放震动"
                            FLAnimation.startScaleVibrate(cellMainBinding.getRoot());
                            break;
                        case 5:
                            //"缩放震动-设置振幅"
                            FLAnimation.startScaleVibrate(cellMainBinding.getRoot(), 0.5F);
                            break;
                        case 6:
                            //"盖章动画"
                            startActivity(new Intent(getActivity(), StampActivity.class));
                            break;
                        case 7:
                            //"盖章动画-设置振幅"
                            Intent intent = new Intent(getActivity(), StampActivity.class);
                            intent.putExtra("value", 4.F);
                            startActivity(intent);
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