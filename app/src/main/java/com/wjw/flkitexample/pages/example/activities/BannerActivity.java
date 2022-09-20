package com.wjw.flkitexample.pages.example.activities;

import android.view.LayoutInflater;
import android.view.View;

import com.bumptech.glide.Glide;
import com.wjw.flkit.base.FLBindingActivity;
import com.wjw.flkit.base.FLNavigationView;
import com.wjw.flkitexample.databinding.ActivityBannerBinding;
import com.youth.banner.adapter.BannerImageAdapter;
import com.youth.banner.holder.BannerImageHolder;
import com.youth.banner.indicator.CircleIndicator;
import com.youth.banner.listener.OnBannerListener;

import java.util.Arrays;
import java.util.List;

/**
 implementation 'io.github.youth5201314:banner:2.2.2'
 */

public class BannerActivity extends FLBindingActivity<ActivityBannerBinding> {
    private List<String> banners;

    @Override
    protected void configNavigation(FLNavigationView navigationView) {
        navigationView.setTitle("轮播图示例");
    }

    @Override
    protected ActivityBannerBinding getBinding() {
        return ActivityBannerBinding.inflate(LayoutInflater.from(this));
    }

    @Override
    protected void onResume() {
        super.onResume();
        binding.banner1.start();
        binding.banner2.start();
        binding.banner3.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        binding.banner1.stop();
        binding.banner2.stop();
        binding.banner3.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding.banner1.destroy();
        binding.banner2.destroy();
        binding.banner3.destroy();
    }

    @Override
    protected void didLoad() {
        banners = Arrays.asList(
                "https://img1.baidu.com/it/u=4246197667,283231099&fm=253&fmt=auto&app=138&f=JPEG?w=800&h=500",
                "https://img2.baidu.com/it/u=1856897750,428519737&fm=253&fmt=auto&app=138&f=JPEG?w=889&h=500",
                "https://img0.baidu.com/it/u=235994405,2067784025&fm=253&fmt=auto&app=138&f=JPEG?w=889&h=500",
                "https://img0.baidu.com/it/u=2848898831,3870905915&fm=253&fmt=auto&app=138&f=JPEG?w=800&h=500"
        );
        binding.banner1.addBannerLifecycleObserver(this)
                .setAdapter(new BannerImageAdapter<String>(banners) {
                    @Override
                    public void onBindView(BannerImageHolder holder, String data, int position, int size) {
                        Glide.with(holder.itemView)
                                .load(data)
                                .into(holder.imageView);
                    }
                })
                .setIndicator(new CircleIndicator(this))
                .setOnBannerListener(new OnBannerListener<String>() {
                    @Override
                    public void OnBannerClick(String data, int position) {
                        showTip(data);
                    }
                });

        binding.banner2.addBannerLifecycleObserver(this)
                .setAdapter(new BannerImageAdapter<String>(banners) {
                    @Override
                    public void onBindView(BannerImageHolder holder, String data, int position, int size) {
                        Glide.with(holder.itemView)
                                .load(data)
                                .into(holder.imageView);
                    }
                })
                .setIndicator(new CircleIndicator(this))
                .setBannerGalleryMZ(20);

        binding.banner3.addBannerLifecycleObserver(this)
                .setAdapter(new BannerImageAdapter<String>(banners) {
                    @Override
                    public void onBindView(BannerImageHolder holder, String data, int position, int size) {
                        Glide.with(holder.itemView)
                                .load(data)
                                .into(holder.imageView);
                    }
                })
                .setIndicator(new CircleIndicator(this))
                .setBannerGalleryEffect(20, 10);
    }

    @Override
    protected void didClick(View view) {

    }
}