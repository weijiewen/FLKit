package com.wjw.flkitexample.pages.example.activities;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.wjw.flkit.base.FLBindingActivity;
import com.wjw.flkit.base.FLBindingFragment;
import com.wjw.flkit.base.FLNavigationView;
import com.wjw.flkit.ui.FLTableView;
import com.wjw.flkit.unit.FLAsyncTask;
import com.wjw.flkitexample.databinding.ActivityViewPagerBinding;
import com.wjw.flkitexample.databinding.CellMainBinding;
import com.wjw.flkitexample.databinding.FragmentViewPagerBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 implementation 'com.google.android.material:material:1.4.0'
 */

public class ViewPagerActivity extends FLBindingActivity<ActivityViewPagerBinding> {
    private List<ViewPagerCategory> categories = new ArrayList<>();
    @Override
    protected void configNavigation(FLNavigationView navigationView) {
        navigationView.setTitle("viewPager");
    }

    @Override
    protected ActivityViewPagerBinding getBinding() {
        return ActivityViewPagerBinding.inflate(LayoutInflater.from(this));
    }

    @Override
    protected void didLoad() {
        categories = Arrays.asList(
                new ViewPagerCategory("第1页"),
                new ViewPagerCategory("第2页"),
                new ViewPagerCategory("第3页"),
                new ViewPagerCategory("第4页"),
                new ViewPagerCategory("第5页"),
                new ViewPagerCategory("第6页"),
                new ViewPagerCategory("第7页"),
                new ViewPagerCategory("第8页"),
                new ViewPagerCategory("第9页"),
                new ViewPagerCategory("第10页"),
                new ViewPagerCategory("第11页"),
                new ViewPagerCategory("第12页"),
                new ViewPagerCategory("第13页"),
                new ViewPagerCategory("第14页"),
                new ViewPagerCategory("第15页"),
                new ViewPagerCategory("第16页"),
                new ViewPagerCategory("第17页"),
                new ViewPagerCategory("第18页"),
                new ViewPagerCategory("第19页"),
                new ViewPagerCategory("第20页")
        );
        binding.viewPager.setAdapter(new FragmentStateAdapter(getSupportFragmentManager(), getLifecycle()) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                return new ViewPagerFragment(categories.get(position));
            }

            @Override
            public int getItemCount() {
                return categories.size();
            }
        });
        new TabLayoutMediator(binding.tabLayout, binding.viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                TextView textView = new TextView(getActivity());
                textView.setGravity(Gravity.CENTER);
                int[][] states = new int[2][];
                states[0] = new int[]{android.R.attr.state_selected};
                states[1] = new int[]{};
                int[] colors = new int[]{Color.parseColor("#FF8A00"), Color.parseColor("#595550")};
                ColorStateList colorStateList = new ColorStateList(states, colors);
                textView.setText(categories.get(position).name);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
                textView.setTextColor(colorStateList);
                tab.setCustomView(textView);
            }
        }).attach();
    }

    @Override
    protected void didClick(View view) {

    }

    public static class ViewPagerFragment extends FLBindingFragment<FragmentViewPagerBinding> {
        private ViewPagerCategory category;
        public ViewPagerFragment(ViewPagerCategory category) {
            this.category = category;
        }
        @Override
        protected FragmentViewPagerBinding getBinding(LayoutInflater inflater, ViewGroup viewGroup) {
            return FragmentViewPagerBinding.inflate(inflater, viewGroup, false);
        }

        @Override
        protected void didLoad() {
            FLTableView.CreatCell<Cell> creatCell = new FLTableView.CreatCell<Cell>() {
                @Override
                public int itemCount(int section) {
                    return category.datas.size();
                }

                @Override
                public Cell getCell(@NonNull ViewGroup parent) {
                    return new Cell(CellMainBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
                }
            };
            fragmentBinding.tableView.setCreatCell(creatCell);
            fragmentBinding.tableView.addHeaderRefresh(new FLTableView.RefreshInterface() {
                @Override
                public void enterRefreshing() {
                    fragmentBinding.tableView.startLoading();
                    category.refresh(new Result() {
                        @Override
                        public void result() {
                            fragmentBinding.tableView.reloadData();
                        }
                    });
                }
            });
            fragmentBinding.tableView.addFooterRefresh(new FLTableView.RefreshInterface() {
                @Override
                public void enterRefreshing() {
                    category.addDatas(new Result() {
                        @Override
                        public void result() {
                            fragmentBinding.tableView.reloadData(true);
                        }
                    });
                }
            });
            fragmentBinding.tableView.startLoading();
            category.refresh(new Result() {
                @Override
                public void result() {
                    fragmentBinding.tableView.reloadData();
                }
            });
        }

        private class Cell extends FLTableView.FLBindingCell<CellMainBinding> {

            public Cell(@NonNull CellMainBinding cellBinding) {
                super(cellBinding);
            }

            @Override
            protected void bindData(int section, int index) {
                cellBinding.text.setText(category.datas.get(index).name);
            }
        }
    }
    private interface Result {
        public void result();
    }
    private class ViewPagerCategory {
        private String name;
        private List<ViewPagerData> datas = new ArrayList<>();
        public ViewPagerCategory(String name) {
            this.name = name;
        }
        private void refresh(Result result) {
            datas = new ArrayList<>();
            addDatas(result);
        }
        private void addDatas(Result result) {
            FLAsyncTask.start(new FLAsyncTask.FLAsyncCallback() {
                @Override
                public void doInBack() {
                    try {
                        Thread.sleep(300);
                        int page = datas.size();
                        datas.addAll(Arrays.asList(
                                new ViewPagerData(name + "，index：" + page),
                                new ViewPagerData(name + "，index：" + (page + 1)),
                                new ViewPagerData(name + "，index：" + (page + 2)),
                                new ViewPagerData(name + "，index：" + (page + 3)),
                                new ViewPagerData(name + "，index：" + (page + 4)),
                                new ViewPagerData(name + "，index：" + (page + 5)),
                                new ViewPagerData(name + "，index：" + (page + 6)),
                                new ViewPagerData(name + "，index：" + (page + 7)),
                                new ViewPagerData(name + "，index：" + (page + 8)),
                                new ViewPagerData(name + "，index：" + (page + 9)),
                                new ViewPagerData(name + "，index：" + (page + 10)),
                                new ViewPagerData(name + "，index：" + (page + 11)),
                                new ViewPagerData(name + "，index：" + (page + 12)),
                                new ViewPagerData(name + "，index：" + (page + 13)),
                                new ViewPagerData(name + "，index：" + (page + 14)),
                                new ViewPagerData(name + "，index：" + (page + 15)),
                                new ViewPagerData(name + "，index：" + (page + 16)),
                                new ViewPagerData(name + "，index：" + (page + 17)),
                                new ViewPagerData(name + "，index：" + (page + 18)),
                                new ViewPagerData(name + "，index：" + (page + 19))
                        ));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void doInMain() {
                    result.result();
                }
            });
        }
    }
    private class ViewPagerData {
        private String name;
        public ViewPagerData(String name) {
            this.name = name;
        }
    }
}