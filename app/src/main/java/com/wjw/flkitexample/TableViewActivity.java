package com.wjw.flkitexample;

import android.graphics.Color;
import android.os.Handler;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.wjw.flkit.FLAsyncTask;
import com.wjw.flkit.FLTableView;
import com.wjw.flkit.base.FLBaseActivity;
import com.wjw.flkitexample.databinding.ActivityTableViewBinding;

import java.util.Random;

public class TableViewActivity extends FLBaseActivity<ActivityTableViewBinding> {
    private int size = 0;
    private int page = 0;

    @Override
    protected void didLoad() {
        navigationView.setTitle("分页tableView");
        FLTableView.DataSource<TableViewCell> dataSource = new FLTableView.DataSource<TableViewCell>() {
            @Override
            public int itemCount() {
                return size;
            }

            @Override
            public int itemType(int index) {
                return 0;
            }

            @Override
            public int getItemLayout(int itemType) {
                return R.layout.cell_table_view;
            }

            @Override
            public TableViewCell createItem(View itemView, int viewType) {
                return new TableViewCell(itemView);
            }

            @Override
            public void bindItem(TableViewCell view, int index) {
                view.bindData(index, Integer.valueOf(index));
            }
        };
        binding.tableView.addHeader(new FLTableView.RefreshInterface() {
            @Override
            public void enterRefreshing() {
                requestData(true);
            }
        });
        binding.tableView.addFooter(new FLTableView.RefreshInterface() {
            @Override
            public void enterRefreshing() {
                requestData(false);
            }
        });
        binding.tableView.setDataSource("暂无数据", dataSource);
        binding.tableView.setRetryRequest(new FLTableView.RetryRequest() {
            @Override
            public void retryRequest() {
                requestData(true);
            }
        });
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
                    binding.tableView.reloadData("错误示例");
                }
            }
        });
    }

    private class TableViewCell extends FLTableView.FLTableViewCell<Integer> {

        public TableViewCell(@NonNull View itemView) {
            super(itemView);
        }

        @Override
        protected void configItem() {
            //配置点击事件之类
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showTip("" + itemData);
                }
            });
        }

        @Override
        protected void dataUpdated(Integer oldData) {
            setText(R.id.text, "" + itemData);
        }
    }
}