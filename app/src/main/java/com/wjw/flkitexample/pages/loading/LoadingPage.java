package com.wjw.flkitexample.pages.loading;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.wjw.flkit.base.FLBaseActivity;
import com.wjw.flkit.base.FLNavigationView;
import com.wjw.flkit.base.FLTabBarActivity;
import com.wjw.flkit.ui.FLTableView;
import com.wjw.flkitexample.databinding.CellMainBinding;
import com.wjw.flkitexample.databinding.PageLoadingBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LoadingPage extends FLTabBarActivity.FLTabBarPage<PageLoadingBinding> {
    private List<String> datas = new ArrayList<>();
    public LoadingPage(Context context) {
        super(context);
    }
    @Override
    protected PageLoadingBinding getBinding() {
        return PageLoadingBinding.inflate(LayoutInflater.from(getContext()), this, false);
    }
    @Override
    protected void configNavigation(FLNavigationView navigationView) {
        navigationView.setTitle("系统权限");
        navigationView.setBackgroundColor(Color.BLACK);
        navigationView.setForegroundColor(Color.WHITE);
    }
    @Override
    protected void didLoad() {
        setStatusStyle(FLBaseActivity.StatusStyle.light);
        datas = Arrays.asList(
                "相机权限",
                "安装apk权限",
                "存储权限"
        );
        FLTableView.CreatCell<LoadingCell> creatCell = new FLTableView.CreatCell<LoadingCell>() {
            @Override
            public int itemCount(int section) {
                return datas.size();
            }

            @Override
            public LoadingCell getCell(@NonNull ViewGroup parent) {
                return new LoadingCell(CellMainBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
            }
        };
        binding.tableView.setCreatCell(creatCell);
        binding.tableView.reloadData(true);
    }
    private class LoadingCell extends FLTableView.FLBindingCell<CellMainBinding> {
        public LoadingCell(@NonNull CellMainBinding cellBinding) {
            super(cellBinding);
            cellBinding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    switch (index) {
                        case 0:
                            getActivity().requestCamera(new FLBaseActivity.PermissionsResult() {
                                @Override
                                public void didGranted() {
                                    getActivity().openCamera(new FLBaseActivity.PickCallback() {
                                        @Override
                                        public void pickData(Bitmap image) {
                                            getActivity().browserImage(0, 1, new FLBaseActivity.BrowserImageListence() {
                                                @Override
                                                public void config(int index, ImageView imageView) {
                                                    imageView.setImageBitmap(image);
                                                }
                                            });
                                        }
                                    });
                                }

                                @Override
                                public void didDenied() {

                                }
                            });
                            break;
                        case 1:
                            getActivity().requestInstall(new FLBaseActivity.PermissionsResult() {
                                @Override
                                public void didGranted() {

                                }

                                @Override
                                public void didDenied() {

                                }
                            });
                            break;
                        case 2:
                            getActivity().requestStorage(new FLBaseActivity.PermissionsResult() {
                                @Override
                                public void didGranted() {
                                    getActivity().openAlbum(new FLBaseActivity.PickCallback() {
                                        @Override
                                        public void pickData(Bitmap image) {
                                            getActivity().browserImage(0, 1, new FLBaseActivity.BrowserImageListence() {
                                                @Override
                                                public void config(int index, ImageView imageView) {
                                                    imageView.setImageBitmap(image);
                                                }
                                            });
                                        }
                                    });
                                }

                                @Override
                                public void didDenied() {

                                }
                            });
                            break;
                    }
                }
            });
        }

        @Override
        protected void bindData(int section, int index) {
            cellBinding.text.setText(datas.get(index));
        }
    }
}
