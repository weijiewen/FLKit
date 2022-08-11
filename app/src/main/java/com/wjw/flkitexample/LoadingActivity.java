package com.wjw.flkitexample;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;

import com.wjw.flkit.FLTableView;
import com.wjw.flkit.base.FLBaseActivity;
import com.wjw.flkitexample.databinding.ActivityLoadingBinding;

import java.util.ArrayList;

public class LoadingActivity extends FLBaseActivity<ActivityLoadingBinding> {
    private ArrayList<String> strings = new ArrayList();
    @Override
    protected ActivityLoadingBinding creatBinding() {
        return ActivityLoadingBinding.inflate(LayoutInflater.from(this));
    }

    @Override
    protected void didLoad() {
        navigationView.setTitle("加载loading");
        strings.add("loading加载");
        strings.add("tip提示");
        strings.add("loading后提示");
        strings.add("提示时loading");
        strings.add("进度提示loading");
        FLTableView.DataSource<LoadingCell> dataSource = new FLTableView.DataSource<LoadingCell>() {
            @Override
            public int itemCount() {
                return strings.size();
            }

            @Override
            public int itemType(int index) {
                return 0;
            }

            @Override
            public int getItemLayout(int itemType) {
                return R.layout.cell_main;
            }

            @Override
            public LoadingCell createItem(View itemView, int viewType) {
                return new LoadingCell(itemView);
            }

            @Override
            public void bindItem(LoadingCell view, int index) {
                view.bindData(index, strings.get(index));
            }
        };
        binding.tableView.setDataSource(dataSource);
        binding.tableView.reloadData(true);
    }

    @Override
    protected void didClick(View view) {

    }

    private class LoadingCell extends FLTableView.FLTableViewCell<String> {

        public LoadingCell(@NonNull View itemView) {
            super(itemView);
        }
        float progress = 0.f;
        @Override
        protected void configItem() {
            String tip = "塞德里克飞机上课了巨额罚款了解放了快速减肥了苦涩就发了苦涩解放啦卡死机而非卢卡斯荆防颗粒撒巨额罚款拉瑟九分裤阿里kg九色鹿开发塞德里克飞机上课了巨额罚款了解放了快速减肥了苦涩就发了苦涩解放啦卡死机而非卢卡斯荆防颗粒撒巨额罚款拉瑟九分裤阿里kg九色鹿开发";
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    switch (itemIndex) {
                        case 0:
                            showLoading();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    dismissLoading();
                                }
                            }, 1000);
                            break;
                        case 1:
                            showTip(tip);
                            break;
                        case 2:
                            showLoading();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    showTip(tip);
                                }
                            }, 1000);
                            break;
                        case 3:
                            showTip(tip);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    showLoading();
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            dismissLoading();
                                        }
                                    }, 1200);
                                }
                            }, 1000);
                            break;
                        case 4:
                            showProgress();
                            startTimer(new FLTimerListencener() {
                                @Override
                                public void run() {
                                    progress += 0.1;
                                    changeProgress(progress);
                                    if (progress >= 1) {
                                        progress = 0;
                                        stopTimer();
                                        dismissLoading();
                                    }
                                }
                            }, 500, 500);
                            break;
                    }
                }
            });
        }

        @Override
        protected void dataUpdated(String oldData) {
            setText(R.id.text, itemData);
        }
    }
}