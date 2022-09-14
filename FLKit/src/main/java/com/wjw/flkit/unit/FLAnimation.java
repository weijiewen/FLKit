package com.wjw.flkit.unit;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FLAnimation {
    static public void startHorizontalVibrate(View view) {
        startHorizontalVibrate(view, 10);
    }
    static public void startHorizontalVibrate(View view, int amplitude) {
        stopAnimation(view);
        AnimatorSet animationSet = new AnimatorSet();
        float startX = view.getTranslationX();
        float animationX = startX;
        Integer onceCount = 4;
        Integer groupCount = 4;
        Integer groupDuration = 5;
        AnimatorSet.Builder builder = null;
        List<Animator> animatorList = new ArrayList<Animator>();
        for (Integer i = 0; i < onceCount * groupCount; i ++) {
            Integer index = i % 4;
            float moveX = 0;
            if (index == 0) {
                moveX = -amplitude;
            }
            else if (index == 2) {
                moveX = amplitude;
            }
            animatorList.add(ObjectAnimator.ofFloat(view, "translationX", animationX, startX + moveX));
            animationX = startX + moveX;
        }
        animationSet.playSequentially(animatorList);
        animationSet.setDuration(groupDuration * groupCount);
        animationSet.start();
    }

    static public void startRotateVibrate(View view) {
        startRotateVibrate(view, 10);
    }
    static public void startRotateVibrate(View view, int amplitude) {
        stopAnimation(view);
        AnimatorSet animationSet = new AnimatorSet();
        float start = view.getRotation();
        float animation = 0;
        Integer onceCount = 4;
        Integer groupCount = 4;
        Integer groupDuration = 5;
        AnimatorSet.Builder builder = null;
        List<Animator> animatorList = new ArrayList<Animator>();
        for (Integer i = 0; i < onceCount * groupCount; i ++) {
            Integer index = i % 4;
            float move = 0;
            if (index == 0) {
                move = -amplitude;
            }
            else if (index == 2) {
                move = amplitude;
            }
            animatorList.add(ObjectAnimator.ofFloat(view, "rotation", animation, start + move));
            animation = start + move;
        }
        animationSet.playSequentially(animatorList);
        animationSet.setDuration(groupDuration * groupCount);
        animationSet.start();
    }

    static public void startScaleVibrate(View view) {
        startScaleVibrate(view, 0.1F);
    }
    static public void startScaleVibrate(View view, float amplitude) {
        stopAnimation(view);
        float startX = view.getScaleX();
        float startY = view.getScaleY();
        float animationX = startX;
        float animationY = startY;
        Integer onceCount = 4;
        Integer groupCount = 4;
        Integer groupDuration = 10;
        List<Animator> animatorList = new ArrayList<Animator>();
        for (Integer i = 0; i < onceCount * groupCount; i ++) {
            Integer index = i % 4;
            float moveX = 0;
            float moveY = 0;
            if (index == 0) {
                moveX = -amplitude;
                moveY = -amplitude;
            }
            else if (index == 2) {
                moveX = amplitude;
                moveY = amplitude;
            }
            PropertyValuesHolder holderX = PropertyValuesHolder.ofFloat("scaleX", animationX, startX + moveX);
            PropertyValuesHolder holderY = PropertyValuesHolder.ofFloat("scaleY", animationY, startY + moveY);
            animatorList.add(ObjectAnimator.ofPropertyValuesHolder(view, holderX, holderY));
            animationX = startX + moveX;
            animationY = startY + moveY;
        }
        AnimatorSet animationSet = new AnimatorSet();
        animationSet.playSequentially(animatorList);
        animationSet.setDuration(groupDuration * groupCount);
        animationSet.start();
    }

    static public void startStampVibrate(View vibrateView, View stampView) {
        startStampVibrate(vibrateView, stampView, 0.7F);
    }

    static public void startStampVibrate(View vibrateView, View stampView, float amplitude) {
        stopAnimation(vibrateView);
        stopAnimation(stampView);

        List<Animator> animatorList = new ArrayList<Animator>();

        PropertyValuesHolder alphaHolder = PropertyValuesHolder.ofFloat("alpha", 0, 1);
        PropertyValuesHolder translationXHolder = PropertyValuesHolder.ofFloat("translationX", 0, stampView.getTranslationX());
        PropertyValuesHolder translationYHolder = PropertyValuesHolder.ofFloat("translationY", 0, stampView.getTranslationY());
        PropertyValuesHolder scaleXHolder = PropertyValuesHolder.ofFloat("scaleX", 4, 1);
        PropertyValuesHolder scaleYHolder = PropertyValuesHolder.ofFloat("scaleY", 4, 1);
        ObjectAnimator stampAnimator = ObjectAnimator.ofPropertyValuesHolder(stampView, alphaHolder, translationXHolder, translationYHolder, scaleXHolder, scaleYHolder);
        stampAnimator.setInterpolator(new AccelerateInterpolator());
        stampAnimator.setDuration(500);
        animatorList.add(stampAnimator);

        float start = stampView.getRotation();
        float animation = 0;
        Integer onceCount = 4;
        Integer groupCount = 4;
        for (Integer i = 0; i < onceCount * groupCount; i ++) {
            Integer index = i % 4;
            float move = 0;
            if (index == 0) {
                move = -amplitude;
            }
            else if (index == 2) {
                move = amplitude;
            }
            ObjectAnimator animator = ObjectAnimator.ofFloat(vibrateView, "rotation", animation, start + move);
            animator.setDuration(30);
            animatorList.add(animator);
            animation = start + move;
        }

        AnimatorSet animationSet = new AnimatorSet();
        animationSet.playSequentially(animatorList);
        animationSet.start();
    }

    static public void startRotateOnce(View view) {
        startRotateOnce(view, 1000);
    }
    static public void startRotateOnce(View view, long duration) {
        startRotate(view, duration, 1);
    }
    static public void startRotate(View view, long duration) {
        startRotate(view, duration, Animation.INFINITE);
    }
    static public void startRotate(View view, long duration, int repeatCount) {
        stopAnimation(view);
        RotateAnimation animation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(duration);
        animation.setRepeatCount(repeatCount);
        animation.setInterpolator(new LinearInterpolator());
        view.setAnimation(animation);
        view.startAnimation(animation);
    }

    static public void stopAnimation(View view) {
        view.clearAnimation();
    }
}
