package com.wjw.flkitexample.pages.tool.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.wjw.flkit.base.FLBindingActivity;
import com.wjw.flkit.base.FLNavigationView;
import com.wjw.flkit.unit.FLAnimation;
import com.wjw.flkit.unit.FLUserDefault;
import com.wjw.flkitexample.R;
import com.wjw.flkitexample.databinding.ActivityUserDefaultBinding;

import org.json.JSONException;
import org.json.JSONObject;

public class UserDefaultActivity extends FLBindingActivity<ActivityUserDefaultBinding> {

    @Override
    protected void configNavigation(FLNavigationView navigationView) {
        navigationView.setTitle("UserDefault");
    }

    @Override
    protected ActivityUserDefaultBinding getBinding() {
        return ActivityUserDefaultBinding.inflate(LayoutInflater.from(this));
    }

    @Override
    protected void didLoad() {
        binding.add.setOnClickListener(this);
        binding.read.setOnClickListener(this);
        binding.delete.setOnClickListener(this);
        readUserDefault();
    }

    @Override
    protected void didClick(View view) {
        String key = binding.key.getText().toString();
        String value = binding.value.getText().toString();
        switch (view.getId()) {
            case R.id.add:
                if (key.isEmpty()) {
                    FLAnimation.startHorizontalVibrate(binding.key);
                }
                else if (value.isEmpty()) {
                    FLAnimation.startHorizontalVibrate(binding.value);
                }
                else {
                    FLUserDefault.userDefault.put(this, key, value);
                    binding.key.setText("");
                    binding.value.setText("");
                    showTip("添加成功");
                    readUserDefault();
                }
                break;
            case R.id.read:
                if (key.isEmpty()) {
                    FLAnimation.startHorizontalVibrate(binding.key);
                }
                else {
                    showTip(FLUserDefault.userDefault.getString(this, key));
                }
                break;
            case R.id.delete:
                if (key.isEmpty()) {
                    FLAnimation.startHorizontalVibrate(binding.key);
                }
                else {
                    FLUserDefault.userDefault.remove(this, key);
                    showTip("删除成功");
                    readUserDefault();
                }
                break;
        }
    }

    private void readUserDefault() {
        String string = null;
        try {
            JSONObject jsonObject = new JSONObject(FLUserDefault.userDefault.getMap(this));
            string = jsonObject.toString(4);
        } catch (JSONException e) {
            
        }
        if (string == null) {
            string = "null";
        }
        else {
            string = string.replace("\\/", "/");
        }
        binding.info.setText(string);
    }
}