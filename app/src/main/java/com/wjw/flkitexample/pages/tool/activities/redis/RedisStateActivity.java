package com.wjw.flkitexample.pages.tool.activities.redis;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wjw.flkit.base.FLBindingActivity;
import com.wjw.flkit.base.FLNavigationView;
import com.wjw.flkit.ui.FLTableView;
import com.wjw.flkit.unit.FLRedis;
import com.wjw.flkitexample.R;
import com.wjw.flkitexample.databinding.ActivityRedisStateBinding;
import com.wjw.flkitexample.databinding.CellRedisStateBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.internal.operators.flowable.FlowableCreate;

public class RedisStateActivity extends FLBindingActivity<ActivityRedisStateBinding> {
    private List<Data> datas = new ArrayList<>();

    @Override
    protected void configNavigation(FLNavigationView navigationView) {
        navigationView.setTitle("同步状态redis");
    }

    @Override
    protected ActivityRedisStateBinding getBinding() {
        return ActivityRedisStateBinding.inflate(LayoutInflater.from(this));
    }

    @Override
    protected void didLoad() {
        for (int i = 0; i < 20; i++) {
            datas.add(new Data(i % 3 + 1));
        }
        FLTableView.CreatCell<Cell> creatCell = new FLTableView.CreatCell<Cell>() {
            @Override
            public int itemCount(int section) {
                return datas.size();
            }

            @Override
            public Cell getCell(@NonNull ViewGroup parent) {
                return new Cell(CellRedisStateBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
            }
        };
        binding.tableView.setCreatCell(creatCell);
        binding.tableView.reloadData();
    }

    @Override
    protected void didClick(View view) {

    }
    private class Cell extends FLTableView.FLBindingCell<CellRedisStateBinding> implements FLRedis.FLRedisListener<Integer, Boolean> {
        public Cell(@NonNull CellRedisStateBinding cellBinding) {
            super(cellBinding);
        }

        @Override
        protected void bindData(int section, int index) {
            Data data = datas.get(index);
            FLRedis.addListener(Data.class, data.id, this);
            cellBinding.textId.setText("id：" + data.id);
            cellBinding.image.setImageResource(data.getSelected() ? R.mipmap.yxysctb : R.mipmap.yxywsctb);
        }

        @Override
        protected void onClick(int section, int index) {
            super.onClick(section, index);
            datas.get(index).setSelected(!datas.get(index).getSelected());
        }

        @Override
        public void redisValueChange(String name, Integer key, Boolean value) {
            Data data = datas.get(index);
            if (key.equals(data.id)) {
                cellBinding.image.setImageResource(data.getSelected() ? R.mipmap.yxysctb : R.mipmap.yxywsctb);
            }
        }
    }
    private class Data {
        Integer id;
        public Data(Integer id) {
            this.id = id;
        }

        public void setSelected(Boolean selected) {
            FLRedis.addValue(Data.class, id, selected);
        }

        public Boolean getSelected() {
            Boolean value = FLRedis.getValue(Data.class, id);
            return value == null ? false : value;
        }
    }
}