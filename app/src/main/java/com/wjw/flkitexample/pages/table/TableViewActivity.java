package com.wjw.flkitexample.pages.table;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.wjw.flkit.FLAsyncTask;
import com.wjw.flkit.FLTableView;
import com.wjw.flkit.base.FLBindingActivity;
import com.wjw.flkit.base.FLNavigationView;
import com.wjw.flkitexample.databinding.ActivityTableViewBinding;
import com.wjw.flkitexample.databinding.CellTableViewBinding;

import java.util.Random;

public class TableViewActivity extends FLBindingActivity<ActivityTableViewBinding> {
    private int size = 0;
    private int page = 0;

    @Override
    protected ActivityTableViewBinding getBinding() {
        return ActivityTableViewBinding.inflate(LayoutInflater.from(this));
    }

    @Override
    protected void configNavigation(FLNavigationView navigationView) {
        super.configNavigation(navigationView);
        navigationView.setTitle("分页tableView");
    }

    @Override
    protected void didLoad() {
        binding.tableView.addHeaderRefresh(new FLTableView.RefreshInterface() {
            @Override
            public void enterRefreshing() {
                requestData(true);
            }
        });
        binding.tableView.addFooterRefresh(new FLTableView.RefreshInterface() {
            @Override
            public void enterRefreshing() {
                requestData(false);
            }
        });
        FLTableView.CreatCell<TableViewCell> creatCell = new FLTableView.CreatCell<TableViewCell>() {
            @Override
            public int itemCount(int section) {
                return size;
            }

            @Override
            public TableViewCell getCell(@NonNull ViewGroup parent) {
                return new TableViewCell(CellTableViewBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
            }
        };
        binding.tableView.setCreatCell("暂无数据", creatCell);
        requestData(true);
    }

    @Override
    protected void didClick(View view) {

    }

    private void requestData(boolean reload) {
        int currenPage = page;
        if (reload) {
            currenPage = 0;
        }
        binding.tableView.startLoading();
        int finalCurrenPage = currenPage;
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
                if (new Random().nextInt(2) == 0) {
                    if (finalCurrenPage == 0) {
                        if (new Random().nextInt(2) == 0) {
                            size = 10;
                        }
                        else  {
                            size = 0;
                        }
                    }
                    else {
                        size += 10;
                    }
                    page = finalCurrenPage + 1;
                    binding.tableView.reloadData(size < 50);
                }
                else {
                    if (size > 0) {
                        showTip("错误示例");
                    }
                    binding.tableView.reloadData("错误示例", new FLTableView.Retry() {
                        @Override
                        public void retryRequest() {
                            requestData(true);
                        }
                    });
                }
            }
        });
    }

    private class TableViewCell extends FLTableView.FLTableViewCell<CellTableViewBinding> {

        public TableViewCell(@NonNull CellTableViewBinding cellBinding) {
            super(cellBinding);
            cellBinding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showTip(String.valueOf(index));
                }
            });
        }

        @Override
        protected void bindData(CellTableViewBinding cellBinding, int section, int index) {
            cellBinding.text.setText(String.valueOf(index));
        }
    }
}