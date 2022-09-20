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
import com.wjw.flkitexample.databinding.ActivityViewPagerBinding;
import com.wjw.flkitexample.databinding.CellMainBinding;
import com.wjw.flkitexample.databinding.FragmentViewPagerBinding;

/**
 implementation 'com.google.android.material:material:1.4.0'
 */

public class ViewPagerActivity extends FLBindingActivity<ActivityViewPagerBinding> {

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
        binding.viewPager.setAdapter(new FragmentStateAdapter(getSupportFragmentManager(), getLifecycle()) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                return new ViewPagerFragment();
            }

            @Override
            public int getItemCount() {
                return 20;
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
                textView.setText("" + position);
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
        @Override
        protected FragmentViewPagerBinding getBinding(LayoutInflater inflater, ViewGroup viewGroup) {
            return FragmentViewPagerBinding.inflate(inflater, viewGroup, false);
        }

        @Override
        protected void didLoad() {
            FLTableView.CreatCell<Cell> creatCell = new FLTableView.CreatCell<Cell>() {
                @Override
                public int itemCount(int section) {
                    return 30;
                }

                @Override
                public Cell getCell(@NonNull ViewGroup parent) {
                    return new Cell(CellMainBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
                }
            };
            fragmentBinding.tableView.setCreatCell(creatCell);
            fragmentBinding.tableView.reloadData();
        }
    }
    private static class Cell extends FLTableView.FLTableViewCell<CellMainBinding> {

        public Cell(@NonNull CellMainBinding cellBinding) {
            super(cellBinding);
        }

        @Override
        protected void bindData(CellMainBinding cellBinding, int section, int index) {
            cellBinding.text.setText("" + index);
        }
    }
}