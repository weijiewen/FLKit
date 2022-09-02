package com.wjw.flkitexample.pages.browser;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.wjw.flkit.base.FLBaseActivity;
import com.wjw.flkit.base.FLNavigationView;
import com.wjw.flkit.base.FLTabBarActivity;
import com.wjw.flkitexample.databinding.PageBrowserBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BrowserPage extends FLTabBarActivity.FLTabBarPage<PageBrowserBinding> {
    private List<String> urls = new ArrayList<>();
    private List<ImageView> imageViews = new ArrayList<>();
    public BrowserPage(Context context) {
        super(context);
    }

    @Override
    protected PageBrowserBinding getBinding() {
        return PageBrowserBinding.inflate(LayoutInflater.from(getContext()), this, false);
    }

    @Override
    protected void configNavigation(FLNavigationView navigationView) {
        navigationView.setForegroundColor(Color.WHITE);
        navigationView.setTitle("图片浏览器");
    }

    @Override
    protected boolean offsetNavigation() {
        return false;
    }

    @Override
    protected void didLoad() {
        setStatusStyle(FLBaseActivity.StatusStyle.light);
        urls = Arrays.asList(
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
        imageViews = Arrays.asList(
                binding.image1,
                binding.image2,
                binding.image3,
                binding.image4,
                binding.image5,
                binding.image6,
                binding.image7,
                binding.image8,
                binding.image9
        );
        for (int i = 0; i < 9; i++) {
            Glide.with(this)
                    .load(urls.get(i))
                    .into(imageViews.get(i));
            int finalI = i;
            imageViews.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getActivity().browserImage(finalI, 9, new FLBaseActivity.BrowserImageListence() {
                        @Override
                        public void config(int index, ImageView imageView) {
                            Glide.with(getActivity())
                                    .load(urls.get(index))
                                    .into(imageView);
                        }
                    });
                }
            });
        }
    }
}
