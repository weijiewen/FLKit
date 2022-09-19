package com.wjw.flkitexample.pages.third.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.donkingliang.imageselector.utils.ImageSelector;
import com.wjw.flkit.base.FLBindingActivity;
import com.wjw.flkit.base.FLNavigationView;
import com.wjw.flkit.ui.FLTableView;
import com.wjw.flkitexample.databinding.ActivityImagePickerBinding;
import com.wjw.flkitexample.databinding.CellMainBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ImagePickerActivity extends FLBindingActivity<ActivityImagePickerBinding> {
    private List<String> strings;
    @Override
    protected void configNavigation(FLNavigationView navigationView) {
        navigationView.setTitle("图片选择器");
    }

    @Override
    protected ActivityImagePickerBinding getBinding() {
        return ActivityImagePickerBinding.inflate(LayoutInflater.from(this));
    }

    @Override
    protected void didLoad() {
        strings = Arrays.asList(
                "单选",
                "单选裁剪",
                "多选",
                "相机",
                "相机裁剪"
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && data != null) {
            ArrayList<String> images = data.getStringArrayListExtra(ImageSelector.SELECT_RESULT);
        }
    }

    private class Cell extends FLTableView.FLTableViewCell<CellMainBinding> {

        public Cell(@NonNull CellMainBinding cellMainBinding) {
            super(cellMainBinding);
            cellMainBinding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    switch (index) {
                        case 0:
                            ImageSelector.builder()
                                    .useCamera(true)
                                    .setSingle(true)
                                    .start(getActivity(), 1);
                            break;
                        case 1:
                            ImageSelector.builder()
                                    .useCamera(true)
                                    .setCrop(true)
                                    .setCropRatio(1.0f)
                                    .setSingle(true)
                                    .start(getActivity(), 1);
                            break;
                        case 2:
                            ImageSelector.builder()
                                    .useCamera(true)
                                    .setSingle(false)
                                    .setMaxSelectCount(9)
                                    .setSelected(new ArrayList<>()) // 把已选的图片传入默认选中。
                                    .start(getActivity(), 1); // 打开相册
                            break;
                        case 3:
                            ImageSelector.builder()
                                    .onlyTakePhoto(true)  // 仅拍照，不打开相册
                                    .start(getActivity(), 1);
                            break;
                        case 4:
                            ImageSelector.builder()
                                    .setCrop(true) // 设置是否使用图片剪切功能。
                                    .setCropRatio(1.0f) // 图片剪切的宽高比,默认1.0f。宽固定为手机屏幕的宽。
                                    .onlyTakePhoto(true)  // 仅拍照，不打开相册
                                    .start(getActivity(), 1);
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