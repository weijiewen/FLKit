package com.wjw.flkitexample.pages.tool.activities.redis;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.wjw.flkit.base.FLBindingActivity;
import com.wjw.flkit.base.FLNavigationView;
import com.wjw.flkit.ui.FLTableView;
import com.wjw.flkit.unit.FLRedis;
import com.wjw.flkit.unit.FLTimer;
import com.wjw.flkitexample.databinding.ActivityRedisProgressBinding;
import com.wjw.flkitexample.databinding.CellRedisProgressBinding;

import java.util.ArrayList;
import java.util.List;

public class RedisProgressActivity extends FLBindingActivity<ActivityRedisProgressBinding> {
    private List<Data> datas = new ArrayList<>();
    @Override
    protected void configNavigation(FLNavigationView navigationView) {
        navigationView.setTitle("同步进度redis");
    }

    @Override
    protected ActivityRedisProgressBinding getBinding() {
        return ActivityRedisProgressBinding.inflate(LayoutInflater.from(this));
    }

    @Override
    protected void didLoad() {
        for (int i = 0; i < 15; i++) {
            datas.add(new Data(i % 3 + 1));
        }
        FLTableView.CreatCell<Cell> creatCell = new FLTableView.CreatCell<Cell>() {
            @Override
            public int itemCount(int section) {
                return datas.size();
            }

            @Override
            public Cell getCell(@NonNull ViewGroup parent) {
                return new Cell(CellRedisProgressBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
            }
        };
        binding.tableView.setCreatCell(creatCell);
        binding.tableView.reloadData();
    }

    @Override
    protected void didClick(View view) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private class Cell extends FLTableView.FLBindingCell<CellRedisProgressBinding> implements FLRedis.FLRedisListener<Integer, Double> {

        public Cell(@NonNull CellRedisProgressBinding cellBinding) {
            super(cellBinding);
        }

        @Override
        protected void bindData(int section, int index) {
            Data data = datas.get(index);
            FLRedis.addListener(Data.class, data.id, this);
            cellBinding.text.setText("id：" + data.id);
            cellBinding.progress.setProgress((int) (1000 * data.getProgress()));
            cellBinding.progressText.setText((int) (data.getProgress() * 100) + "/100");
        }

        @Override
        protected void onClick(int section, int index) {
            super.onClick(section, index);
            datas.get(index).download();
        }

        @Override
        public void redisValueChange(String name, Integer key, Double value) {
            if (key == datas.get(index).id) {
                cellBinding.progress.setProgress((int) (1000 * value));
                cellBinding.progressText.setText((int) (value * 100) + "/100");
            }
        }
    }
    private class Data {
        private Integer id;
        FLTimer timer;

        public Data(Integer id) {
            this.id = id;
        }

        public void setProgress(double progress) {
            FLRedis.addValue(Data.class, id, progress);
        }

        public double getProgress() {
            Double progress = FLRedis.getValue(Data.class, id);
            return progress == null ? 0 : progress;
        }

        public void download() {
            if (timer != null) {
                return;
            }
            setProgress(0);
            timer = new FLTimer();
            timer.startTimer(200, 200, new FLTimer.FLTimerListencener() {
                @Override
                public void run() {
                    if (getProgress() < 1) {
                        setProgress(getProgress() + 0.05);
                    }
                    else {
                        timer.stopTimer();
                        timer = null;
                        setProgress(1);
                    }
                }
            });
        }
    }
}