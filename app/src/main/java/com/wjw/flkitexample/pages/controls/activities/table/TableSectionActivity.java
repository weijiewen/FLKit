package com.wjw.flkitexample.pages.controls.activities.table;


import android.graphics.Color;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.wjw.flkit.ui.FLTableView;
import com.wjw.flkit.base.FLBindingActivity;
import com.wjw.flkit.base.FLNavigationView;
import com.wjw.flkitexample.databinding.ActivityTableSectionBinding;
import com.wjw.flkitexample.databinding.CellTableViewBinding;
import com.wjw.flkitexample.databinding.SectionFooterBinding;
import com.wjw.flkitexample.databinding.SectionHeaderBinding;

import java.util.ArrayList;
import java.util.List;

public class TableSectionActivity extends FLBindingActivity<ActivityTableSectionBinding> {
    int value = 1;
    private List<List<Integer>> datas = new ArrayList<>();

    @Override
    protected ActivityTableSectionBinding getBinding() {
        return ActivityTableSectionBinding.inflate(LayoutInflater.from(this));
    }

    @Override
    protected void configNavigation(FLNavigationView navigationView) {
        navigationView.setTitle("分段");
    }

    @Override
    protected void didLoad() {
        FLTableView.CreatSection<Header, Footer> creatSection = new FLTableView.CreatSection<Header, Footer>() {
            @Override
            public int sectionCount() {
                return datas.size();
            }

            @Nullable
            @Override
            public Header getHeader(@NonNull ViewGroup parent) {
                return new Header(SectionHeaderBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
            }

            @Nullable
            @Override
            public Footer getFooter(@NonNull ViewGroup parent) {
                return new Footer(SectionFooterBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
            }
        };
        binding.tableView.setCreatSection(creatSection);
        FLTableView.CreatCell<Cell> creatCell = new FLTableView.CreatCell<Cell>() {
            @Override
            public int itemCount(int section) {
                return datas.get(section).size();
            }

            @Override
            public Cell getCell(@NonNull ViewGroup parent) {
                return new Cell(CellTableViewBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
            }
        };
        binding.tableView.setCreatCell(creatCell);
        binding.tableView.addHeaderRefresh(new FLTableView.RefreshInterface() {
            @Override
            public void enterRefreshing() {
                request(true);
            }
        });
        binding.tableView.addFooterRefresh(new FLTableView.RefreshInterface() {
            @Override
            public void enterRefreshing() {
                request(false);
            }
        });
        request(true);
    }

    @Override
    protected void didClick(View view) {

    }
    private void request(boolean reload) {
        if (reload) {
            binding.tableView.startLoading();
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (reload) {
                    value = 1;
                }
                List<Integer> list = new ArrayList<>();
                for (int i = 0; i < 5; i++) {
                    list.add(value ++);
                }
                if (reload) {
                    datas = new ArrayList<>();
                    datas.add(list);
                }
                else {
                    int index = datas.size() - 1;
                    if (datas.get(index).size() < 10) {
                        datas.get(index).addAll(list);
                    }
                    else {
                        datas.add(list);
                    }
                }
                binding.tableView.reloadData(datas.size() < 3);
            }
        }, 1000);
    }
    private class Header extends FLTableView.FLTableViewSection<SectionHeaderBinding> {
        public Header(@NonNull SectionHeaderBinding binding) {
            super(binding);
            binding.getRoot().setBackgroundColor(Color.BLUE);
            binding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getActivity().showTip("点击分段header：" + section);
                }
            });
        }

        @Override
        protected void bindData(SectionHeaderBinding binding, int section) {
            binding.text.setText("header：" + section);
        }
    }
    private class Footer extends FLTableView.FLTableViewSection<SectionFooterBinding> {
        public Footer(@NonNull SectionFooterBinding binding) {
            super(binding);
            binding.getRoot().setBackgroundColor(Color.GREEN);
            binding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getActivity().showTip("点击分段footer：" + section);
                }
            });
        }

        @Override
        protected void bindData(SectionFooterBinding binding, int section) {
            binding.text.setText("footer：" + section);
        }
    }
    private class Cell extends FLTableView.FLBindingCell<CellTableViewBinding> {

        public Cell(@NonNull CellTableViewBinding cellBinding) {
            super(cellBinding);
        }

        @Override
        protected void bindData(int section, int index) {
            cellBinding.text.setText("section：" + section + " index：" + index + " value：" + datas.get(section).get(index));
        }

        @Override
        protected void onClick(int section, int index) {
            super.onClick(section, index);
            getActivity().showTip(cellBinding.text.getText().toString());
        }

        @Override
        protected void onLongPress(int section, int index) {
            super.onLongPress(section, index);
            getActivity().showDialogAlert(FLDialogStyle.ActionSheet, null, null, new FLAlertDialogConfig() {
                @Override
                public void addItems(FLAlertDialog dialog) {
                    dialog.addItem("删除", 15, Color.RED, new FLAlertDialogTouch() {
                        @Override
                        public void touch() {
                            datas.get(section).remove(index);
                            binding.tableView.reloadData();
                        }
                    });
                    dialog.addCancel(new FLAlertDialogTouch() {
                        @Override
                        public void touch() {

                        }
                    });
                }
            });
        }
    }
}