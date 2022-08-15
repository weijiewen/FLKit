package com.wjw.flkitexample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.wjw.flkit.FLImageBrowser;
import com.wjw.flkit.base.FLBaseActivity;
import com.wjw.flkitexample.databinding.ActivityImageBrowserBinding;

import java.util.ArrayList;
import java.util.List;

public class ImageBrowserActivity extends FLBaseActivity<ActivityImageBrowserBinding> {
    private List<String> urls = new ArrayList<>();
    private List<ImageView> imageViews = new ArrayList<>();
    @Override
    protected ActivityImageBrowserBinding creatBinding() {
        return ActivityImageBrowserBinding.inflate(LayoutInflater.from(this));
    }

    @Override
    protected void didLoad() {
        navigationView.setTitle("图片浏览器");
        urls.add("http://pic.bizhi360.com/bpic/16/10716.jpg");
        urls.add("http://pic.bizhi360.com/bpic/82/10682.jpg");
        urls.add("http://pic.bizhi360.com/bpic/80/10680.jpg");
        urls.add("http://pic.bizhi360.com/bpic/79/10679.jpg");
        urls.add("http://pic.bizhi360.com/bpic/78/10678.jpg");
        urls.add("http://pic.bizhi360.com/bpic/76/10676.jpg");
        urls.add("http://pic.bizhi360.com/bpic/74/10674.jpg");
        urls.add("http://pic.bizhi360.com/bpic/43/10643.jpg");
        urls.add("http://pic.bizhi360.com/bpic/42/10642.jpg");
        imageViews.add(binding.image1);
        imageViews.add(binding.image2);
        imageViews.add(binding.image3);
        imageViews.add(binding.image4);
        imageViews.add(binding.image5);
        imageViews.add(binding.image6);
        imageViews.add(binding.image7);
        imageViews.add(binding.image8);
        imageViews.add(binding.image9);

        for (int i = 0; i < 9; i++) {
            Glide.with(this)
                    .load(urls.get(i))
                    .into(imageViews.get(i));
            int finalI = i;
            imageViews.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    browserImage(finalI, 9, new BrowserImageListence() {
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

    @Override
    protected void didClick(View view) {

    }
}