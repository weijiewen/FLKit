package com.wjw.flkitexample.pages.table;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.wjw.flkit.FLAsyncTask;
import com.wjw.flkit.FLTableView;
import com.wjw.flkit.base.FLBaseActivity;
import com.wjw.flkit.base.FLNavigationView;
import com.wjw.flkit.base.FLTabBarActivity;
import com.wjw.flkitexample.databinding.CellMainBinding;
import com.wjw.flkitexample.databinding.CellTableViewBinding;
import com.wjw.flkitexample.databinding.PageTableBinding;
import com.wjw.flkitexample.databinding.SectionHeaderTableBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TablePage extends FLTabBarActivity.FLTabBarPage<PageTableBinding> {
    private List<TableData> datas = new ArrayList<>();
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
        datas = Arrays.asList(
                new TableData("tableView", Arrays.asList(
                        "列表",
                        "分段列表"
                ))
//                , new TableData("collectionView", Arrays.asList(
//                        "列表",
//                        "分段列表"
//                ))
        );
        FLTableView.CreatSection<TableHeader, TableHeader> creatSection = new FLTableView.CreatSection<TableHeader, TableHeader>() {
            @Override
            public int sectionCount() {
                return datas.size();
            }

            @Nullable
            @Override
            public TableHeader getHeader(@NonNull ViewGroup parent) {
                return new TableHeader(SectionHeaderTableBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
            }

            @Nullable
            @Override
            public TableHeader getFooter(@NonNull ViewGroup parent) {
                return null;
            }
        };
        binding.tableView.setCreatSection(creatSection);
        FLTableView.CreatCell<TableViewCell> creatCell = new FLTableView.CreatCell<TableViewCell>() {
            @Override
            public int itemCount(int section) {
                return datas.get(section).strings.size();
            }

            @Override
            public TableViewCell getCell(@NonNull ViewGroup parent) {
                return new TableViewCell(CellMainBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
            }
        };
        binding.tableView.setCreatCell("暂无数据", creatCell);
        binding.tableView.reloadData();
    }
    private class TableData {
        private String name;
        private List<String> strings;
        public TableData(String name, List<String> strings) {
            this.name = name;
            this.strings = strings;
        }
    }
    private class TableHeader extends FLTableView.FLTableViewSection<SectionHeaderTableBinding> {

        public TableHeader(@NonNull SectionHeaderTableBinding binding) {
            super(binding);
        }

        @Override
        protected void bindData(SectionHeaderTableBinding binding, int section) {
            binding.text.setText(datas.get(section).name);
        }
    }
    private class TableViewCell extends FLTableView.FLTableViewCell<CellMainBinding> {
        public TableViewCell(@NonNull CellMainBinding cellBinding) {
            super(cellBinding);
            cellBinding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (section == 0) {
                        if (index == 0) {
                            getActivity().startActivity(new Intent(getActivity(), TableViewActivity.class));
                        }
                        else if (index == 1) {
                            getActivity().startActivity(new Intent(getActivity(), SectionActivity.class));
                        }
                    }
                }
            });
        }

        @Override
        protected void bindData(CellMainBinding cellBinding, int section, int index) {
            cellBinding.text.setText(datas.get(section).strings.get(index));
        }
    }
}
