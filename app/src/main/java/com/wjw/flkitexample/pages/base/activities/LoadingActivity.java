package com.wjw.flkitexample.pages.base.activities;

import androidx.annotation.NonNull;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wjw.flkit.ui.FLTableView;
import com.wjw.flkit.base.FLBaseActivity;
import com.wjw.flkit.base.FLBindingActivity;
import com.wjw.flkit.base.FLNavigationView;
import com.wjw.flkit.unit.FLTimer;
import com.wjw.flkitexample.databinding.ActivityLoadingBinding;
import com.wjw.flkitexample.databinding.CellMainBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LoadingActivity extends FLBindingActivity<ActivityLoadingBinding> {
    private List<String> datas = new ArrayList<>();
    @Override
    protected ActivityLoadingBinding getBinding() {
        return ActivityLoadingBinding.inflate(LayoutInflater.from(this));
    }

    @Override
    protected void configNavigation(FLNavigationView navigationView) {
        navigationView.setTitle("loading-tip弹窗");
    }

    @Override
    protected void didLoad() {
        datas = Arrays.asList(
                "loading加载",
                "tip提示",
                "loading后提示",
                "提示时loading",
                "进度提示loading"
        );
        FLTableView.CreatCell<LoadingCell> creatCell = new FLTableView.CreatCell<LoadingCell>() {
            @Override
            public int itemCount(int section) {
                return datas.size();
            }

            @Override
            public LoadingCell getCell(@NonNull ViewGroup parent) {
                return new LoadingCell(CellMainBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
            }
        };
        binding.tableView.setCreatCell(creatCell);
        binding.tableView.reloadData(true);
    }

    @Override
    protected void didClick(View view) {

    }
    private class LoadingCell extends FLTableView.FLTableViewCell<CellMainBinding> {
        float progress = 0.f;

        public LoadingCell(@NonNull CellMainBinding cellBinding) {
            super(cellBinding);
            String tip = "塞德里克飞机上课了巨额罚款了解放了快速减肥了苦涩就发了苦涩解放啦卡死机而非卢卡斯荆防颗粒撒巨额罚款拉瑟九分裤阿里kg九色鹿开发塞德里克飞机上课了巨额罚款了解放了快速减肥了苦涩就发了苦涩解放啦卡死机而非卢卡斯荆防颗粒撒巨额罚款拉瑟九分裤阿里kg九色鹿开发";
            cellBinding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    switch (index) {
                        case 0:
                            //"loading加载"
                            getActivity().showLoading();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    getActivity().dismissLoading();
                                }
                            }, 1000);
                            break;
                        case 1:
                            //"tip提示"
                            getActivity().showTip(tip);
                            break;
                        case 2:
                            //"loading后提示"
                            getActivity().showLoading();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    getActivity().showTip(tip);
                                }
                            }, 1000);
                            break;
                        case 3:
                            //"提示时loading"
                            getActivity().showTip(tip);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    getActivity().showLoading();
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            getActivity().dismissLoading();
                                        }
                                    }, 1200);
                                }
                            }, 1000);
                            break;
                        case 4:
                            //"进度提示loading"
                            getActivity().showProgress();
                            getActivity().startTimer(500, 500, new FLTimer.FLTimerListencener() {
                                @Override
                                public void run() {
                                    progress += 0.1;
                                    getActivity().changeProgress(progress);
                                    if (progress >= 1) {
                                        progress = 0;
                                        getActivity().stopTimer();
                                        getActivity().dismissLoading();
                                    }
                                }
                            });
                            break;
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