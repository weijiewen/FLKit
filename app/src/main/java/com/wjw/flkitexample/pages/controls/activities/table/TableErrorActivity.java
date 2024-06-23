package com.wjw.flkitexample.pages.controls.activities.table;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.wjw.flkit.base.FLBindingActivity;
import com.wjw.flkit.base.FLNavigationView;
import com.wjw.flkit.ui.FLTableView;
import com.wjw.flkit.unit.FLAsyncTask;
import com.wjw.flkitexample.databinding.ActivityTableErrorBinding;
import com.wjw.flkitexample.databinding.ViewTableErrorBinding;

public class TableErrorActivity extends FLBindingActivity<ActivityTableErrorBinding> {

    @Override
    protected void configNavigation(FLNavigationView navigationView) {
        navigationView.setTitle("自定义error");
    }

    @Override
    protected ActivityTableErrorBinding getBinding() {
        return ActivityTableErrorBinding.inflate(LayoutInflater.from(this));
    }

    @Override
    protected void didLoad() {
        binding.tableView.setConfigErrorView(new FLTableView.ConfigError() {
            @Override
            public FLTableView.FLBindingErrorView getErrorView(Context context, String error) {
                return new ErrorView(context, error);
            }
        });
        request();
    }

    private void request() {
        binding.tableView.startLoading();
        FLAsyncTask.start(new FLAsyncTask.FLAsyncCallback() {
            @Override
            public void doInBack() {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    
                }
            }

            @Override
            public void doInMain() {
                binding.tableView.reloadData("自定义错误", new FLTableView.Retry() {
                    @Override
                    public void retryRequest() {
                        request();
                    }
                });
            }
        });
    }

    @Override
    protected void didClick(View view) {

    }
    private class ErrorView extends FLTableView.FLBindingErrorView<ViewTableErrorBinding> {


        public ErrorView(Context context, String error) {
            super(context, error);
            errorBinding.error.setText(error);
        }

        @Override
        protected ViewTableErrorBinding getBinding() {
            return ViewTableErrorBinding.inflate(LayoutInflater.from(getContext()), this, true);
        }

        @Override
        protected View getReloadView() {
            return errorBinding.reload;
        }
    }
}