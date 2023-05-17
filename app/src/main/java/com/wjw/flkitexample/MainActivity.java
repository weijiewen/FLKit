package com.wjw.flkitexample;

import android.graphics.Color;

import com.wjw.flkit.base.FLTabBarActivity;
import com.wjw.flkitexample.pages.example.ExamplePage;
import com.wjw.flkitexample.pages.base.BasePage;
import com.wjw.flkitexample.pages.controls.ControlsPage;
import com.wjw.flkitexample.pages.loading.LoadingPage;
import com.wjw.flkitexample.pages.tool.ToolPage;

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
                new FLTabBarItem("基类功能", R.mipmap.flysywdjtb, R.mipmap.sysytbdjzt, new BasePage(this)),
                new FLTabBarItem("控件", R.mipmap.syfltbwdjzt, R.mipmap.flyfldjtb, new ControlsPage(this)),
                new FLTabBarItem(R.mipmap.syyxtb, 0, 64, 64, 10, new ExamplePage(this)),
                new FLTabBarItem("工具", R.mipmap.gameicon, R.mipmap.gameicon_hov, new ToolPage(this)),
                new FLTabBarItem("权限", R.mipmap.sywdtbwdj, R.mipmap.wdsywdbqltb, new LoadingPage(this))
        );
        setItemList(itemList);
    }
}