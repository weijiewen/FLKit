package com.wjw.flkitexample.pages.tool.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;

import com.wjw.flkit.base.FLBindingActivity;
import com.wjw.flkit.base.FLNavigationView;
import com.wjw.flkit.unit.FLRegexInputFilter;
import com.wjw.flkitexample.databinding.ActivityRegexInputBinding;

public class RegexInputActivity extends FLBindingActivity<ActivityRegexInputBinding> {
    @Override
    protected void configNavigation(FLNavigationView navigationView) {
        navigationView.setTitle("RegexInput");
    }

    @Override
    protected ActivityRegexInputBinding getBinding() {
        return ActivityRegexInputBinding.inflate(LayoutInflater.from(this));
    }

    @Override
    protected void didLoad() {
        binding.money.setFilters(new InputFilter[]{new FLRegexInputFilter(FLRegexInputFilter.VaildMoney)});
        binding.number.setFilters(new InputFilter[]{new FLRegexInputFilter(FLRegexInputFilter.VaildNumber)});
        binding.phone.setFilters(new InputFilter[]{new FLRegexInputFilter(FLRegexInputFilter.VaildPhone)});
        binding.email.setFilters(new InputFilter[]{new FLRegexInputFilter(FLRegexInputFilter.VaildEmail)});
        binding.password.setFilters(new InputFilter[]{new FLRegexInputFilter(FLRegexInputFilter.VaildPassword)});
        binding.idcard.setFilters(new InputFilter[]{new FLRegexInputFilter(FLRegexInputFilter.VaildIDCard)});
    }

    @Override
    protected void didClick(View view) {

    }
}