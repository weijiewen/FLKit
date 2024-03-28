package com.wjw.flkitexample.pages.controls.activities;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;

import com.wjw.flkit.unit.FLAnimation;
import com.wjw.flkit.unit.FLAsyncTask;
import com.wjw.flkit.base.FLBindingActivity;
import com.wjw.flkit.base.FLNavigationView;
import com.wjw.flkitexample.R;
import com.wjw.flkitexample.databinding.ActivitySmscodeBinding;

public class SmscodeActivity extends FLBindingActivity<ActivitySmscodeBinding> {

    @Override
    protected ActivitySmscodeBinding getBinding() {
        return ActivitySmscodeBinding.inflate(LayoutInflater.from(this));
    }

    @Override
    protected void configNavigation(FLNavigationView navigationView) {
        navigationView.setTitle("验证码倒计时");
    }

    @Override
    protected void didLoad() {
        binding.phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String phone = editable.toString();
                if (phone.length() == 11) {
                    if (binding.sendSmscode.checkTimer(phone) != null) {
                        endEdit();
                    }
                }
                else {
                    binding.sendSmscode.cancelTimer();
                }
            }
        });
        binding.sendSmscode.setOnClickListener(this);
    }

    @Override
    protected void didClick(View view) {
        switch (view.getId()) {
            case R.id.send_smscode:
                String phone = binding.phone.getText().toString();
                if (phone.length() == 11) {
                    binding.sendSmscode.startLoading();
                    FLAsyncTask.start(new FLAsyncTask.FLAsyncCallback() {
                        @Override
                        public void doInBack() {
                            try {
                                Thread.sleep(1000);
                            } catch (Exception e) {
                                
                            }
                        }

                        @Override
                        public void doInMain() {
                            binding.sendSmscode.stopLoading();
                            binding.sendSmscode.startTimer(phone, "123");
                        }
                    });
                }
                else {
                    showTip("请输入手机号");
                    FLAnimation.startHorizontalVibrate(binding.phone);
                }
                break;
        }
    }
}