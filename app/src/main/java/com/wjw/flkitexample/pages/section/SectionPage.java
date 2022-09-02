package com.wjw.flkitexample.pages.section;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.wjw.flkit.FLAsyncTask;
import com.wjw.flkit.FLTableView;
import com.wjw.flkit.base.FLNavigationView;
import com.wjw.flkit.base.FLTabBarActivity;
import com.wjw.flkitexample.databinding.CellTableViewBinding;
import com.wjw.flkitexample.databinding.PageSectionBinding;
import com.wjw.flkitexample.databinding.SectionFooterBinding;
import com.wjw.flkitexample.databinding.SectionHeaderBinding;

import java.util.ArrayList;
import java.util.List;

public class SectionPage extends FLTabBarActivity.FLTabBarPage<PageSectionBinding> {
    int value = 1;
    private List<List<Integer>> datas = new ArrayList<>();
    public SectionPage(Context context) {
        super(context);
    }

    @Override
    protected PageSectionBinding getBinding() {
        return PageSectionBinding.inflate(LayoutInflater.from(getContext()), this, false);
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
            binding.getRoot().setOnClickListener(new OnClickListener() {
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
            binding.getRoot().setOnClickListener(new OnClickListener() {
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

        public Cell(@NonNull CellTableViewBinding binding) {
            super(binding);
            binding.getRoot().setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    getActivity().showTip(binding.text.getText().toString());
                }
            });
        }

        @Override
        protected void bindData(CellTableViewBinding binding, int section, int index) {
            binding.text.setText("section：" + section + " index：" + index + " value：" + datas.get(section).get(index));
        }
    }
}
