package com.wjw.flkitexample.pages.tool;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.alibaba.fastjson.JSONObject;
import com.wjw.flkit.ui.FLTableView;
import com.wjw.flkit.base.FLNavigationView;
import com.wjw.flkit.base.FLTabBarActivity;
import com.wjw.flkit.unit.FLLog;
import com.wjw.flkitexample.databinding.CellMainBinding;
import com.wjw.flkitexample.databinding.PageToolBinding;
import com.wjw.flkitexample.pages.network.api.TestApi;
import com.wjw.flkitexample.pages.network.loader.TestLoader;
import com.wjw.flkitexample.pages.network.respon.BaseObjectRespon;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import io.reactivex.internal.observers.BlockingBaseObserver;

public class ToolPage extends FLTabBarActivity.FLTabBarPage<PageToolBinding> {
    private List<String> datas = new ArrayList<>();
    public ToolPage(Context context) {
        super(context);
    }

    @Override
    protected PageToolBinding getBinding() {
        return PageToolBinding.inflate(LayoutInflater.from(getContext()), this, false);
    }

    @Override
    protected void configNavigation(FLNavigationView navigationView) {
        navigationView.setTitle("工具类");
    }

    @Override
    protected void didLoad() {
        datas = Arrays.asList(
                "动画",
                "请求结果输出"
        );
        FLTableView.CreatCell<TableViewCell> creatCell = new FLTableView.CreatCell<TableViewCell>() {
            @Override
            public int itemCount(int section) {
                return datas.size();
            }

            @Override
            public TableViewCell getCell(@NonNull ViewGroup parent) {
                return new TableViewCell(CellMainBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
            }
        };
        binding.tableView.setCreatCell("暂无数据", creatCell);
        binding.tableView.reloadData();
    }
    private class TableViewCell extends FLTableView.FLTableViewCell<CellMainBinding> {
        public TableViewCell(@NonNull CellMainBinding cellBinding) {
            super(cellBinding);
            cellBinding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (section == 0) {
                        if (index == 0) {
                            getActivity().startActivity(new Intent(getActivity(), AnimationActivity.class));
                        }
                        else if (index == 1) {
                            getActivity().showLoading();
                            TestLoader.getTest().subscribe(new BlockingBaseObserver<BaseObjectRespon<JSONObject>>() {
                                @Override
                                public void onNext(BaseObjectRespon<JSONObject> jsonObjectBaseObjectRespon) {
                                    getActivity().showTip("请求成功");
                                }

                                @Override
                                public void onError(Throwable e) {
                                    e.printStackTrace();
                                    getActivity().showTip("请求失败" + e.getMessage());
                                }
                            });
//                            HashMap header = new HashMap();
//                            header.put("token", "qenfaklfjakfejlajefkaljef");
//                            HashMap parameters = new HashMap();
//                            parameters.put("name", "哈哈哈");
//                            parameters.put("age", "23");
//                            parameters.put("sex", "女");
//                            String json = "";
//                            FLLog.log("https://www.baidu.com", header, parameters, json);
                        }
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
