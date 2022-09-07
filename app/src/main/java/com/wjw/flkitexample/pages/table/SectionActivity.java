package com.wjw.flkitexample.pages.table;


import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.wjw.flkit.FLAsyncTask;
import com.wjw.flkit.FLTableView;
import com.wjw.flkit.base.FLBindingActivity;
import com.wjw.flkit.base.FLNavigationView;
import com.wjw.flkitexample.databinding.ActivitySectionBinding;
import com.wjw.flkitexample.databinding.CellTableViewBinding;
import com.wjw.flkitexample.databinding.SectionFooterBinding;
import com.wjw.flkitexample.databinding.SectionHeaderBinding;

import java.util.ArrayList;
import java.util.List;

public class SectionActivity extends FLBindingActivity<ActivitySectionBinding> {
    int value = 1;
    private List<List<Integer>> datas = new ArrayList<>();

    @Override
    protected ActivitySectionBinding getBinding() {
        return ActivitySectionBinding.inflate(LayoutInflater.from(this));
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
        FLAsyncTask.start(new FLAsyncTask.FLAsyncCallback() {
            @Override
            public void doInBack() {
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void doInMain() {
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
        });
    }
    private class Header extends FLTableView.FLTableViewSection<SectionHeaderBinding> {
        public Header(@NonNull SectionHeaderBinding binding) {
            super(binding);
            binding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getActivity().showTip("点击分段header：" + section);
                }
            });
        }

        @Override
        protected void bindData(SectionHeaderBinding binding, int section) {
            binding.text.setText(String.valueOf(section));
        }
    }
    private class Footer extends FLTableView.FLTableViewSection<SectionFooterBinding> {
        public Footer(@NonNull SectionFooterBinding binding) {
            super(binding);
            binding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getActivity().showTip("点击分段footer：" + section);
                }
            });
        }

        @Override
        protected void bindData(SectionFooterBinding binding, int section) {
            binding.text.setText(String.valueOf(section));
        }
    }
    private class Cell extends FLTableView.FLTableViewCell<CellTableViewBinding> {

        public Cell(@NonNull CellTableViewBinding cellBinding) {
            super(cellBinding);
            cellBinding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getActivity().showTip(cellBinding.text.getText().toString());
                }
            });
            cellBinding.getRoot().setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
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
                    return true;
                }
            });
        }

        @Override
        protected void bindData(CellTableViewBinding binding, int section, int index) {
            binding.text.setText("section：" + section + " index：" + index + " value：" + datas.get(section).get(index));
        }
    }
}