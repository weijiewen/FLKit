package com.wjw.flkitexample.pages.example;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.wjw.flkit.base.FLBaseActivity;
import com.wjw.flkit.base.FLNavigationView;
import com.wjw.flkit.base.FLTabBarActivity;
import com.wjw.flkit.ui.FLTableView;
import com.wjw.flkit.ui.FLTableView.FLTableViewBaseSection;
import com.wjw.flkitexample.databinding.CellMainBinding;
import com.wjw.flkitexample.databinding.PageExampleBinding;
import com.wjw.flkitexample.databinding.SectionHeaderBinding;
import com.wjw.flkitexample.pages.example.activities.BannerActivity;
import com.wjw.flkitexample.pages.example.activities.ImagePickerActivity;
import com.wjw.flkitexample.pages.example.activities.KeyboardActivity;
import com.wjw.flkitexample.pages.example.activities.QRCodeActivity;
import com.wjw.flkitexample.pages.example.activities.ViewPagerActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ExamplePage extends FLTabBarActivity.FLTabBarPage<PageExampleBinding> {
    private List<HashMap<String, Object>> datas = new ArrayList<>();

    public ExamplePage(Context context) {
        super(context);
    }

    @Override
    protected PageExampleBinding getBinding() {
        return PageExampleBinding.inflate(LayoutInflater.from(getContext()), this, false);
    }

    @Override
    protected void configNavigation(FLNavigationView navigationView) {
        navigationView.setForegroundColor(Color.WHITE);
        navigationView.setTitle("示例代码");
    }

    @Override
    protected FLBaseActivity.FLOffsetStyle offsetStyle() {
        return FLBaseActivity.FLOffsetStyle.None;
    }

    @Override
    protected void didLoad() {
        setStatusStyle(FLBaseActivity.StatusStyle.light);
        HashMap thirdMap = new HashMap();
        thirdMap.put("name", "第三方示例");
        thirdMap.put("list",
                Arrays.asList(
                        "KingKeyboard键盘",
                        "ImageSelector图片选择器",
                        "ZXing二维码",
                        "youth轮播图",
                        "ViewPage2 + TabLayout"
                )
        );
        datas.add(thirdMap);
        HashMap androidMap = new HashMap();
        androidMap.put("name", "android示例");
        androidMap.put("list",
                Arrays.asList(

                )
        );
//        datas.add(androidMap);
        FLTableView.CreatSection<HeaderSection, FLTableViewBaseSection> creatSection = new FLTableView.CreatSection<HeaderSection, FLTableViewBaseSection>() {
            @Override
            public int sectionCount() {
                return datas.size();
            }

            @Nullable
            @Override
            public HeaderSection getHeader(@NonNull ViewGroup parent) {
                return new HeaderSection(SectionHeaderBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
            }

            @Nullable
            @Override
            public FLTableViewBaseSection getFooter(@NonNull ViewGroup parent) {
                return new FLTableViewBaseSection(FLTableViewBaseSection.PlaceholderView(parent, getActivity().dipToPx(10)));
            }
        };
        binding.tableView.setCreatSection(creatSection);
        FLTableView.CreatCell<Cell> creatCell = new FLTableView.CreatCell<Cell>() {
            @Override
            public int itemCount(int section) {
                List list = (List) datas.get(section).get("list");
                return list.size();
            }

            @Override
            public Cell getCell(@NonNull ViewGroup parent) {
                return new Cell(CellMainBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
            }
        };
        binding.tableView.setCreatCell(creatCell);
        binding.tableView.reloadData();
    }

    private class HeaderSection extends FLTableView.FLTableViewSection<SectionHeaderBinding> {

        public HeaderSection(@NonNull SectionHeaderBinding sectionBinding) {
            super(sectionBinding);
        }

        @Override
        protected void bindData(SectionHeaderBinding sectionBinding, int section) {
            sectionBinding.text.setText((CharSequence) datas.get(section).get("name"));
        }
    }

    private class Cell extends FLTableView.FLBindingCell<CellMainBinding> {

        public Cell(@NonNull CellMainBinding cellMainBinding) {
            super(cellMainBinding);
            cellMainBinding.getRoot().setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    switch (section) {
                        case 0:
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
                                case 3:
                                    getActivity().startActivity(new Intent(getActivity(), BannerActivity.class));
                                    break;
                                case 4:
                                    getActivity().startActivity(new Intent(getActivity(), ViewPagerActivity.class));
                                    break;
                            }
                            break;
                        case 1:
                            break;
                    }
                }
            });
        }

        @Override
        protected void bindData(int section, int index) {
            List list = (List) datas.get(section).get("list");
            cellBinding.text.setText((CharSequence) list.get(index));
        }
    }
}
