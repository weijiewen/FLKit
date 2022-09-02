package com.wjw.flkitexample.pages.table;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.wjw.flkit.FLAsyncTask;
import com.wjw.flkit.FLTableView;
import com.wjw.flkit.base.FLNavigationView;
import com.wjw.flkit.base.FLTabBarActivity;
import com.wjw.flkitexample.databinding.CellTableViewBinding;
import com.wjw.flkitexample.databinding.PageTableBinding;

public class TablePage extends FLTabBarActivity.FLTabBarPage<PageTableBinding> {
    private int size = 0;
    private int page = 0;
    public TablePage(Context context) {
        super(context);
    }

    @Override
    protected PageTableBinding getBinding() {
        return PageTableBinding.inflate(LayoutInflater.from(getContext()), this, false);
    }

    @Override
    protected void configNavigation(FLNavigationView navigationView) {
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
                if (finalCurrenPage == 0) {
                    size = 10;
                }
                else {
                    size += 10;
                }
                page = finalCurrenPage + 1;
                binding.tableView.reloadData(size < 50);
            }
        });
    }

    private class TableViewCell extends FLTableView.FLTableViewCell<CellTableViewBinding> {
        public TableViewCell(@NonNull CellTableViewBinding binding) {
            super(binding);
            binding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getActivity().startActivity(new Intent(getActivity(), TableViewActivity.class));
                }
            });
        }

        @Override
        protected void bindData(CellTableViewBinding binding, int section, int index) {
            binding.text.setText(String.valueOf(index));
        }
    }
}
