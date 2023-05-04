package com.wjw.flkitexample.pages.base;

import android.animation.Animator;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.wjw.flkitexample.databinding.BaseDemoFullBinding;

public class BaseDemoFullView extends LinearLayout {
    private BaseDemoFull baseDemoFull;
    private BaseDemoFullBinding binding;
    public interface BaseDemoFull {
        void remove(BaseDemoFullView view);
        void confirm();
    }
    public BaseDemoFullView(Context context, BaseDemoFull baseDemoFull) {
        super(context);
        this.baseDemoFull = baseDemoFull;
        binding = BaseDemoFullBinding.inflate(LayoutInflater.from(context), this, false);
        addView(binding.getRoot());
        binding.cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                hide();
            }
        });
        binding.confirm.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                baseDemoFull.confirm();
                hide();
            }
        });
        binding.getRoot().animate().setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {}

            @Override
            public void onAnimationEnd(Animator animator) {
                if (binding.getRoot().getAlpha() < 0.3) {
                    baseDemoFull.remove(BaseDemoFullView.this);
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }
    public void show() {
        binding.getRoot().setAlpha(0);
        float y = binding.contentView.getTranslationY();
        binding.contentView.setTranslationY(400);
        binding.getRoot().animate().alpha(1).setDuration(300);
        binding.contentView.animate().translationY(y).setDuration(300);
    }
    private void hide() {
        binding.getRoot().animate().alpha(0).setDuration(300);
        binding.contentView.animate().translationY(400).setDuration(300);
    }
}
