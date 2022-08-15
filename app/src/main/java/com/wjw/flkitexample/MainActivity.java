package com.wjw.flkitexample;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.wjw.flkit.FLTableView;

import java.util.ArrayList;

import com.wjw.flkit.base.FLBaseActivity;
import com.wjw.flkitexample.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    ArrayList<String> strings = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FLBaseActivity.setDefalutBackImgaeID(R.mipmap.ptbzjttb);
        strings.add("分页tableView");
        strings.add("加载dialog");
        strings.add("加载loading");
        strings.add("图片浏览器");

        binding = ActivityMainBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());
        FLTableView.DataSource<MainCell> dataSource = new FLTableView.DataSource<MainCell>() {
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
            public MainCell createItem(View itemView, int viewType) {
                return new MainCell(itemView);
            }
            @Override
            public void bindItem(MainCell view, int index) {
                view.bindData(index, strings.get(index));
            }
        };
        binding.tableView.setDataSource("暂无数据", dataSource);
        binding.tableView.reloadData(true);
    }

    private class MainCell extends FLTableView.FLTableViewCell<String> {
        public MainCell(@NonNull View itemView) {
            super(itemView);
        }
        @Override
        protected void configItem() {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    switch (itemIndex) {
                        case 0:
                            startActivity(new Intent(MainActivity.this, TableViewActivity.class));
                            break;
                        case 1:
                            startActivity(new Intent(MainActivity.this, DialogActivity.class));
                            break;
                        case 2:
                            startActivity(new Intent(MainActivity.this, LoadingActivity.class));
                            break;
                        case 3:
                            startActivity(new Intent(MainActivity.this, ImageBrowserActivity.class));
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