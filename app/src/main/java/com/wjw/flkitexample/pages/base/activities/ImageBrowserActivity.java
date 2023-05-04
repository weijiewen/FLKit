package com.wjw.flkitexample.pages.base.activities;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.wjw.flkit.base.FLBindingActivity;
import com.wjw.flkit.base.FLNavigationView;
import com.wjw.flkit.ui.FLTableView;
import com.wjw.flkitexample.databinding.ActivityImageBrowserBinding;
import com.wjw.flkitexample.databinding.CellImageBinding;

import java.util.Arrays;
import java.util.List;

public class ImageBrowserActivity extends FLBindingActivity<ActivityImageBrowserBinding> {
    List<String> datas;
    @Override
    protected void configNavigation(FLNavigationView navigationView) {
        navigationView.setTitle("图片浏览器");
    }

    @Override
    protected ActivityImageBrowserBinding getBinding() {
        return ActivityImageBrowserBinding.inflate(LayoutInflater.from(this));
    }

    @Override
    protected void didLoad() {
        datas = Arrays.asList(
                "https://www.qqtouxiang.com/d/file/tupian/mx/20180818/jicqmhf342og3.jpg",
                "https://www.qqtouxiang.com/d/file/tupian/mx/20180818/jiu2dtkoqj5dk.jpg",
                "https://www.qqtouxiang.com/d/file/tupian/mx/20180818/jinasjpn5widp.jpg",
                "https://www.qqtouxiang.com/d/file/tupian/mx/20180818/ji0qan53lavro.jpg",
                "http://inews.gtimg.com/newsapp_bt/0/13238917140/1000",
                "https://p.qpic.cn/mwegame/0/19412a45ecc2b39ea3c3db9414f51600/",
                "https://p.qpic.cn/mwegame/0/23b6faad6ed5962990fa0c0778282c98/",
                "https://p.qpic.cn/mwegame/0/56fd08307faa2bc9379e6a4b0601d711/",
                "https://n.sinaimg.cn/spider2020419/351/w1300h651/20200419/8e33-iskepxt6836327.jpg"
        );
        GridLayoutManager manager = new GridLayoutManager(this, 3);
        binding.tableView.setLayoutManager(manager);
        FLTableView.CreatCell<ImageCell> creatCell = new FLTableView.CreatCell<ImageCell>() {
            @Override
            public int itemCount(int section) {
                return datas.size();
            }

            @Override
            public ImageCell getCell(@NonNull ViewGroup parent) {
                return new ImageCell(CellImageBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
            }
        };
        binding.tableView.setCreatCell(creatCell);
        binding.tableView.reloadData();
    }

    @Override
    protected void didClick(View view) {

    }

    private class ImageCell extends FLTableView.FLBindingCell<CellImageBinding> {

        public ImageCell(@NonNull CellImageBinding cellImageBinding) {
            super(cellImageBinding);
            cellImageBinding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getActivity().browserImage(index, datas.size(), new BrowserImageListence() {
                        @Override
                        public void config(int index, ImageView imageView) {
                            Glide.with(getActivity())
                                    .load(datas.get(index))
                                    .into(imageView);
                        }
                    });
                }
            });
        }

        @Override
        protected void bindData(int section, int index) {
            Glide.with(getActivity())
                    .load(datas.get(index))
                    .into(cellBinding.image);
        }
    }
}