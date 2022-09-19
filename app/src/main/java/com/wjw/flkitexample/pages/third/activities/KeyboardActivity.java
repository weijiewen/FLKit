package com.wjw.flkitexample.pages.third.activities;

import android.view.LayoutInflater;
import android.view.View;

import com.king.keyboard.KingKeyboard;
import com.wjw.flkit.base.FLBindingActivity;
import com.wjw.flkit.base.FLNavigationView;
import com.wjw.flkitexample.databinding.ActivityKeyboardBinding;

public class KeyboardActivity extends FLBindingActivity<ActivityKeyboardBinding> {

    KingKeyboard keyboard;
    @Override
    protected void configNavigation(FLNavigationView navigationView) {
        navigationView.setTitle("KingKeyboard键盘");
    }

    @Override
    protected ActivityKeyboardBinding getBinding() {
        return ActivityKeyboardBinding.inflate(LayoutInflater.from(this));
    }

    @Override
    protected void didLoad() {
        keyboard = new KingKeyboard(this, binding.keyboardView);
        keyboard.register(binding.idcard, KingKeyboard.KeyboardType.ID_CARD);
    }

    @Override
    protected void didClick(View view) {

    }
}