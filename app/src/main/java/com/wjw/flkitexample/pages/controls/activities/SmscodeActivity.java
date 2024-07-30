package com.wjw.flkitexample.pages.controls.activities;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.wjw.flkit.unit.FLAnimation;
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
        binding.phone.setText("15000000000");
        binding.sendSmscode.setOnClickListener(this);
    }

    @Override
    protected void didClick(View view) {
        switch (view.getId()) {
            case R.id.send_smscode:
                String phone = binding.phone.getText().toString();
                if (phone.length() == 11) {
                    binding.sendSmscode.startLoading();
                    Log.d("111", "didClick: 12312");
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            binding.sendSmscode.stopLoading();
                            binding.sendSmscode.startTimer(phone, "123");
                        }
                    }, 2000);
                }
                else {
                    showTip("请输入手机号");
                    FLAnimation.startHorizontalVibrate(binding.phone);
                }
                break;
        }
    }
}