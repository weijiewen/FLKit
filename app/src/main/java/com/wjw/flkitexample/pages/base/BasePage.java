package com.wjw.flkitexample.pages.base;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.wjw.flkit.ui.FLTableView;
import com.wjw.flkit.base.FLBaseActivity;
import com.wjw.flkit.base.FLNavigationView;
import com.wjw.flkit.base.FLTabBarActivity;
import com.wjw.flkitexample.databinding.CellMainBinding;
import com.wjw.flkitexample.databinding.PageBaseBinding;
import com.wjw.flkitexample.pages.base.activities.DialogActivity;
import com.wjw.flkitexample.pages.base.activities.ImageBrowserActivity;
import com.wjw.flkitexample.pages.base.activities.LoadingActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BasePage extends FLTabBarActivity.FLTabBarPage<PageBaseBinding> {
    private List<String> datas = new ArrayList<>();
    public BasePage(Context context) {
        super(context);
    }

    @Override
    protected PageBaseBinding getBinding() {
        return PageBaseBinding.inflate(LayoutInflater.from(getContext()), this, false);
    }

    @Override
    protected void configNavigation(FLNavigationView navigationView) {
        navigationView.setTitle("基类功能");
        navigationView.setBackgroundColor(Color.BLACK);
        navigationView.setForegroundColor(Color.WHITE);
    }

    @Override
    protected void didLoad() {
        setStatusStyle(FLBaseActivity.StatusStyle.light);
        TextView moreText = new TextView(getActivity());
        moreText.setTextColor(Color.WHITE);
        moreText.setText("更多");
        moreText.setGravity(Gravity.CENTER);
        moreText.setLayoutParams(new LinearLayout.LayoutParams(getActivity().dipToPx(50), ViewGroup.LayoutParams.MATCH_PARENT));
        moreText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().showPopup(v, new FLBaseActivity.ConfigPopup() {
                    @Override
                    public void loadPopup(FLBaseActivity.PopupConfig config) {
                        config.addItem("测试弹窗1", new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                getActivity().showTip("测试弹窗1");
                            }
                        });
                        config.addItem("测试弹窗2", new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                getActivity().showTip("测试弹窗2");
                            }
                        });
                        config.addItem("测试弹窗3", new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                getActivity().showTip("测试弹窗3");
                            }
                        });
                    }
                });
            }
        });
        navigationView.addRightItem(moreText);
        datas = Arrays.asList(
                "dialog弹窗",
                "loading-tip弹窗",
                "图片浏览器",
                "添加全屏view"
        );
        FLTableView.CreatCell<BaseCell> creatCell = new FLTableView.CreatCell<BaseCell>() {
            @Override
            public int itemCount(int section) {
                return datas.size();
            }

            @Override
            public BaseCell getCell(@NonNull ViewGroup parent) {
                return new BaseCell(CellMainBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
            }
        };
        binding.tableView.setCreatCell(creatCell);
        binding.tableView.reloadData(true);
    }

    private class BaseCell extends FLTableView.FLBindingCell<CellMainBinding> {
        public BaseCell(@NonNull CellMainBinding cellBinding) {
            super(cellBinding);
        }

        @Override
        protected void bindData(int section, int index) {
            cellBinding.text.setText(datas.get(index));
        }

        @Override
        protected void onClick(int section, int index) {
            super.onClick(section, index);
            switch (index) {
                case 0:
                    getActivity().startActivity(new Intent(getActivity(), DialogActivity.class));
                    break;
                case 1:
                    getActivity().startActivity(new Intent(getActivity(), LoadingActivity.class));
                    break;
                case 2:
                    getActivity().startActivity(new Intent(getActivity(), ImageBrowserActivity.class));
                    break;
                case 3:
                    BaseDemoFullView fullView = new BaseDemoFullView(getActivity(), new BaseDemoFullView.BaseDemoFull() {
                        @Override
                        public void remove(BaseDemoFullView view) {
                            getActivity().removeFullView(view);
                        }

                        @Override
                        public void confirm() {

                        }
                    });
                    getActivity().addFullView(fullView);
                    fullView.show();
                    break;
            }
        }

        @Override
        protected void onLongPress(int section, int index) {
            super.onLongPress(section, index);
            getActivity().showPopup(cellBinding.getRoot(), new FLBaseActivity.ConfigPopup() {
                @Override
                public void loadPopup(FLBaseActivity.PopupConfig config) {
                    config.addItem("测试1", new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            getActivity().showTip("测试1");
                        }
                    });
                    config.addItem("测试2", new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            getActivity().showTip("测试2");
                        }
                    });
                    config.addItem("测试3", new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            getActivity().showTip("测试3");
                        }
                    });
                }
            });
        }
    }


}
