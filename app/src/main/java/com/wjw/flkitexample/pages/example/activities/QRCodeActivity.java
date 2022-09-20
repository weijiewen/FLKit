package com.wjw.flkitexample.pages.example.activities;

import static com.wjw.flkitexample.pages.units.FLZXingActivity.FL_SCAN_QRCODE_RESULT;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;

import com.donkingliang.imageselector.utils.ImageSelector;
import com.wjw.flkit.base.FLBindingActivity;
import com.wjw.flkit.base.FLNavigationView;
import com.wjw.flkitexample.R;
import com.wjw.flkitexample.databinding.ActivityQrcodeBinding;
import com.wjw.flkitexample.pages.units.FLZXingActivity;

/**
 implementation 'com.github.zxing.zxing:core:zxing-3.5.0'

 <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" /> <!-- 储存卡的读写权限 -->
 <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
 <uses-permission android:name="android.permission.CAMERA" /> <!-- 扫描二维码 -->

 */

public class QRCodeActivity extends FLBindingActivity<ActivityQrcodeBinding> {

    @Override
    protected void configNavigation(FLNavigationView navigationView) {
        navigationView.setTitle("ZXing二维码");
    }

    @Override
    protected ActivityQrcodeBinding getBinding() {
        return ActivityQrcodeBinding.inflate(LayoutInflater.from(this));
    }

    @Override
    protected void didLoad() {
        FLZXingActivity.loadQrcode(this, "test", binding.qrcodeImage);
        binding.scanQrcode.setOnClickListener(this);
        binding.getQrcode.setOnClickListener(this);
    }

    private ActivityResultLauncher<Intent> scanQrcode = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            Intent data = result.getData();
            if (result.getResultCode() == RESULT_OK) {
                binding.qrcodeResult.setText(data.getStringExtra(FL_SCAN_QRCODE_RESULT));
            }
        }
    });

    @Override
    protected void didClick(View view) {
        switch (view.getId()) {
            case R.id.scan_qrcode:
                scanQrcode.launch(new Intent(this, FLZXingActivity.class));
                break;
            case R.id.get_qrcode:
                ImageSelector.builder()
                        .useCamera(true)
                        .setSingle(true)
                        .start(getActivity(), 1);
                break;
        }
    }
}