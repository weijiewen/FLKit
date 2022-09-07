package com.wjw.flkitexample;

import android.graphics.Color;

import com.wjw.flkit.base.FLTabBarActivity;
import com.wjw.flkitexample.pages.browser.BrowserPage;
import com.wjw.flkitexample.pages.dialog.DialogPage;
import com.wjw.flkitexample.pages.controls.ControlsPage;
import com.wjw.flkitexample.pages.loading.LoadingPage;
import com.wjw.flkitexample.pages.table.TablePage;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends FLTabBarActivity {
    @Override
    protected void didLoad() {

    }

    @Override
    protected void configPage() {
        setTabBarSelectedColor(Color.parseColor("#026BEE"));
        List<FLTabBarItem> itemList = Arrays.asList(
                new FLTabBarItem("模态窗", R.mipmap.flysywdjtb, R.mipmap.sysytbdjzt, new DialogPage(this)),
                new FLTabBarItem("加载窗", R.mipmap.syfltbwdjzt, R.mipmap.flyfldjtb, new LoadingPage(this)),
                new FLTabBarItem(R.mipmap.syyxtb, 0, 64, 64, 10, new BrowserPage(this)),
                new FLTabBarItem("列表", R.mipmap.gameicon, R.mipmap.gameicon_hov, new TablePage(this)),
                new FLTabBarItem("控件", R.mipmap.sywdtbwdj, R.mipmap.wdsywdbqltb, new ControlsPage(this))
        );
        setItemList(itemList);
    }
}