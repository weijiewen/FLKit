package com.wjw.flkit.ui;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.widget.OverScroller;

import com.wjw.flkit.base.FLBaseActivity;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class FLImageBrowser extends LinearLayout {
    public interface FLImageBrowserListence {
        void config(int index, ImageView imageView);
        void touch(int index);
    }
    public FLImageBrowser(Context context) {
        super(context);
    }

    public void setListence(FragmentActivity activity, int showIndex, int size, FLImageBrowserListence listence) {
        this.index = showIndex;
        this.size = size;
        this.listence = listence;
        for (int i = 0; i < size; i++) {
            fragments.add(new ImageBrowserFragment(i, listence));
        }
        viewPager = new ViewPager2(getContext());
        viewPager.setLayoutParams(new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        addView(viewPager);
        viewPager.setOffscreenPageLimit(2);
        viewPager.setAdapter(new ImageBrowserPagerAdapter(activity));
        viewPager.setCurrentItem(index, false);
    }

    private int index = 0;
    private int size = 0;
    private FLImageBrowserListence listence;
    private List<ImageBrowserFragment> fragments = new ArrayList<>();
    private ViewPager2 viewPager;

    private class ImageBrowserPagerAdapter extends FragmentStateAdapter {


        public ImageBrowserPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return fragments.get(position);
        }

        @Override
        public int getItemCount() {
            return fragments.size();
        }
    }
    public static class ImageBrowserFragment extends Fragment {
        private ZoomImageView zoomImageView;
        private FLImageBrowserListence listence;
        private int index;

        public ImageBrowserFragment(int index, FLImageBrowserListence listence) {
            super();
            this.index = index;
            this.listence = listence;
        }

        @Override
        public void onPause() {
            super.onPause();
            zoomImageView.reloadScale();
        }

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            LinearLayout layout = new LinearLayout(getContext());
            layout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            zoomImageView = new ZoomImageView(getContext());
            zoomImageView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            zoomImageView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    listence.touch(index);
                }
            });
            layout.addView(zoomImageView);
            listence.config(index, zoomImageView);
            return layout;
        }
    }


    //?????? https://blog.csdn.net/jingzz1/article/details/84663067
    private static class ZoomImageView extends AppCompatImageView implements ViewTreeObserver.OnGlobalLayoutListener {

        private boolean mIsOneLoad = true;

        //??????????????????,?????????????????????
        private float mInitScale;
        //??????????????????
        private float mMaxScale;
        //??????????????????????????????
        private float mMidScale;

        private Matrix mScaleMatrix;
        //????????????????????????
        private ScaleGestureDetector mScaleGestureDetector;

        //??????
        private GestureDetector gestureDetector;

        //??????
        private boolean isEnlarge = false;//????????????
        private ValueAnimator mAnimator; //??????????????????

        //??????
        private OverScroller scroller;
        private int mCurrentX, mCurrentY;
        private ValueAnimator translationAnimation; //??????????????????

        //??????
        private OnClickListener onClickListener;//????????????

        public ZoomImageView(Context context) {
            this(context, null);
        }

        public ZoomImageView(Context context, AttributeSet attrs) {
            this(context, attrs, 0);
        }

        public ZoomImageView(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            //?????????????????????ScaleType?????????ScaleType.MATRIX?????????????????????
            setScaleType(ScaleType.MATRIX);

            scroller = new OverScroller(context);
            mScaleMatrix = new Matrix();
            //????????????
            mScaleGestureDetector = new ScaleGestureDetector(context, new ScaleGestureDetector.SimpleOnScaleGestureListener() {
                @Override
                public boolean onScale(ScaleGestureDetector detector) {

                    scale(detector);
                    return true;
                }

                @Override
                public void onScaleEnd(ScaleGestureDetector detector) {
                    scaleEnd(detector);
                }
            });

            //?????????????????????
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {

                @Override
                public boolean onDown(MotionEvent e) {
                    RectF rectF = getMatrixRectF();
                    if (rectF == null) {
                        return false;
                    }
                    int left = (int) rectF.left;
                    int right = (int) (getWidth() - rectF.right);
                    if (left > -1 || right > -1) {
                        getParent().requestDisallowInterceptTouchEvent(false);
                        return false;
                    }
                    else {
                        getParent().requestDisallowInterceptTouchEvent(true);
                        return true;
                    }
                }

                @Override
                public boolean onScroll(MotionEvent e1, MotionEvent e2, final float distanceX, final float distanceY) {
                    RectF rectF = getMatrixRectF();
                    if (rectF == null) {
                        return false;
                    }
                    int left = (int) rectF.left;
                    int right = (int) (getWidth() - rectF.right);
                    if ((distanceX < 0 && left > -1) || (distanceX > 0 && right > -1)) {
                        getParent().requestDisallowInterceptTouchEvent(false);
                        return false;
                    }
                    else {
                        getParent().requestDisallowInterceptTouchEvent(true);
                        //????????????
                        onTranslationImage(-distanceX, -distanceY);
                        return true;
                    }
                }

                @Override
                public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                    RectF rectF = getMatrixRectF();
                    if (rectF == null) {
                        return false;
                    }
                    int left = (int) rectF.left;
                    int right = (int) (getWidth() - rectF.right);
                    if ((velocityX < 0 && left > -1) || (velocityX > 0 && right > -1)) {
                        getParent().requestDisallowInterceptTouchEvent(false);
                        return false;
                    }
                    else {
                        getParent().requestDisallowInterceptTouchEvent(true);
                        //????????????
//                        onTranslationImage(-distanceX, -distanceY);
                        return true;
                    }
                }

                @Override
                public boolean onDoubleTap(MotionEvent e) {
                    //????????????
                    onDoubleDrowScale(e.getX(), e.getY());
                    return true;
                }

                @Override
                public boolean onSingleTapConfirmed(MotionEvent e) {
                    //????????????
                    if(onClickListener != null)
                        onClickListener.onClick(ZoomImageView.this);
                    return true;
                }
            });

        }

        @Override
        public void setOnClickListener(OnClickListener onClickListener) {
            this.onClickListener = onClickListener;
        }

        @Override
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            getViewTreeObserver().addOnGlobalLayoutListener(this);
        }

        @Override
        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            getViewTreeObserver().removeOnGlobalLayoutListener(this);
        }

        /**
         * imageView??????????????????????????????imageView??????????????????????????????
         */
        @Override
        public void onGlobalLayout() {
            if (mIsOneLoad) {

                //????????????????????????
                int width = getWidth();
                int height = getHeight();

                //????????????,?????????????????????????????????
                Drawable d = getDrawable();
                if (d == null)
                    return;
                //????????????????????????
                int dw = d.getIntrinsicWidth();
                int dh = d.getIntrinsicHeight();

                float scale = 1.0f;
                if (dw > width && dh <= height) {
                    scale = width * 1.0f / dw;
                }
                if (dw <= width && dh > height) {
                    scale = height * 1.0f / dh;
                }
                if ((dw <= width && dh <= height) || (dw >= width && dh >= height)) {
                    scale = Math.min(width * 1.0f / dw, height * 1.0f / dh);
                }

                //??????????????????????????????????????????????????????
                mInitScale = scale;
                //??????????????????????????????
                mMidScale = mInitScale * 2;
                //???????????????????????????
                mMaxScale = mInitScale * 4;

                //??????????????????,??????????????????????????????????????????
                float translationX = width * 1.0f / 2 - dw / 2;
                float translationY = height * 1.0f / 2 - dh / 2;

                mScaleMatrix.postTranslate(translationX, translationY);
                mScaleMatrix.postScale(mInitScale, mInitScale, width * 1.0f / 2, height * 1.0f / 2);
                setImageMatrix(mScaleMatrix);
                mIsOneLoad = false;
            }
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            return mScaleGestureDetector.onTouchEvent(event)|
                    gestureDetector.onTouchEvent(event);
        }

        public void reloadScale() {
            scaleAnimation(mInitScale, getWidth() / 2, getHeight() / 2);
        }

        //????????????????????????
        public void scale(ScaleGestureDetector detector) {

            Drawable drawable = getDrawable();
            if (drawable == null)
                return;

            float scale = getScale();
            //????????????????????????,scaleFactor>1???????????????<1???????????????
            float scaleFactor = detector.getScaleFactor();
            //?????????????????????????????????????????????????????????[mInitScale,mMaxScale]?????????????????????
            mScaleMatrix.postScale(scaleFactor, scaleFactor, detector.getFocusX(), detector.getFocusY());
            setImageMatrix(mScaleMatrix);
            removeBorderAndTranslationCenter();

        }

        //??????????????????
        public void scaleEnd(ScaleGestureDetector detector) {
            float scale = getScale();
            scale = detector.getScaleFactor() * scale;
            if (scale < mInitScale) {
                scaleAnimation(mInitScale, getWidth() / 2, getHeight() / 2);
            } else if (scale > mMaxScale) {
                scaleAnimation(mMaxScale, getWidth() / 2, getHeight() / 2);
            }
        }

        //????????????????????????
        private void onTranslationImage(float dx, float dy) {

            if (getDrawable() == null) {
                return;
            }

            RectF rect = getMatrixRectF();

            //??????????????????????????????????????????????????????
            if (rect.width() <= getWidth())
                dx = 0.0f;
            //?????????????????????????????????????????????????????????
            if (rect.height() <= getHeight())
                dy = 0.0f;

            //??????????????????0???????????????????????????
            if (dx != 0.0f || dy != 0.0f) {
                mScaleMatrix.postTranslate(dx, dy);
                setImageMatrix(mScaleMatrix);
                //??????????????????
                removeBorderAndTranslationCenter();
            }
        }

        //?????????????????????????????????????????????
        private void removeBorderAndTranslationCenter() {
            RectF rectF = getMatrixRectF();
            if (rectF == null)
                return;

            int width = getWidth();
            int height = getHeight();
            float widthF = rectF.width();
            float heightF = rectF.height();
            float left = rectF.left;
            float right = rectF.right;
            float top = rectF.top;
            float bottom = rectF.bottom;
            float translationX = 0.0f, translationY = 0.0f;

            if (left > 0) {
                //???????????????
                if (widthF > width) {
                    //??????????????????????????????????????????????????????
                    translationX = -left;
                } else {
                    //????????????????????????????????????????????????
                    translationX = width * 1.0f / 2f - (widthF * 1.0f / 2f + left);
                }
            } else if (right < width) {
                //???????????????
                if (widthF > width) {
                    //??????????????????????????????????????????????????????
                    translationX = width - right;
                } else {
                    //????????????????????????????????????????????????
                    translationX = width * 1.0f / 2f - (widthF * 1.0f / 2f + left);
                }
            }

            if (top > 0) {
                //???????????????
                if (heightF > height) {
                    //???????????????????????????????????????????????????
                    translationY = -top;
                } else {
                    //????????????????????????????????????????????????
                    translationY = height * 1.0f / 2f - (top + heightF * 1.0f / 2f);
                }
            } else if (bottom < height) {
                //???????????????
                if (heightF > height) {
                    //???????????????????????????????????????????????????
                    translationY = height - bottom;
                } else {
                    //????????????????????????????????????????????????
                    translationY = height * 1.0f / 2f - (top + heightF * 1.0f / 2f);
                }
            }

            mScaleMatrix.postTranslate(translationX, translationY);
            setImageMatrix(mScaleMatrix);
        }

        /**
         * ??????????????????
         *
         * @param x ??????????????????
         * @param y ??????????????????
         */
        private void onDoubleDrowScale(float x, float y) {
            //???????????????????????????????????????????????????????????????
            if (mAnimator != null && mAnimator.isRunning())
                return;

            float drowScale = getDoubleDrowScale();
            //???????????????????????????????????????
            scaleAnimation(drowScale, x, y);
        }

        /**
         * ????????????
         *
         * @param drowScale ???????????????
         * @param x         ?????????
         * @param y         ?????????
         */
        private void scaleAnimation(final float drowScale, final float x, final float y) {
            if (mAnimator != null && mAnimator.isRunning())
                return;
            mAnimator = ObjectAnimator.ofFloat(getScale(), drowScale);
            mAnimator.setDuration(300);
            mAnimator.setInterpolator(new AccelerateInterpolator());
            mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float value = ((float) animation.getAnimatedValue()) / getScale();
                    mScaleMatrix.postScale(value, value, x, y);
                    setImageMatrix(mScaleMatrix);
                    removeBorderAndTranslationCenter();
                }
            });

            mAnimator.start();
        }


        //????????????????????????????????????(???????????????????????????deviation?????????)
        private float getDoubleDrowScale() {
            float deviation = 0.05f;
            float drowScale = 1.0f;
            float scale = getScale();

            if (Math.abs(mInitScale - scale) < deviation)
                scale = mInitScale;
            if (Math.abs(mMidScale - scale) < deviation)
                scale = mMidScale;
            if (Math.abs(mMaxScale - scale) < deviation)
                scale = mMaxScale;

            if (scale != mMidScale) {
                //?????????????????????mMidScale,????????????mMidScale
                drowScale = mMidScale;
                isEnlarge = scale < mMidScale;
            } else {
                //????????????mMidScale??????????????????????????????
                //?????????????????????????????????????????????????????????????????????????????????????????????
                if (isEnlarge) {
                    //??????
                    drowScale = mMaxScale;
                } else {
                    //??????
                    drowScale = mInitScale;
                }
            }
            return drowScale;
        }


        //??????????????????????????????????????????
        private RectF getMatrixRectF() {

            Drawable drawable = getDrawable();
            if (drawable == null) {
                return null;
            }
            RectF rectF = new RectF(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            Matrix matrix = getImageMatrix();
            matrix.mapRect(rectF);

            return rectF;
        }


        /**
         * ??????????????????????????????
         *
         * @return
         */
        private float getScale() {
            float[] values = new float[9];
            mScaleMatrix.getValues(values);
            return values[Matrix.MSCALE_X];
        }


        /**
         * ?????????????????????????????? ?????????????????????????????????????????????true
         *
         * @param direction
         * @return true ?????????????????????
         */
        @Override
        public boolean canScrollHorizontally(int direction) {
            RectF rect = getMatrixRectF();
            if (rect == null || rect.isEmpty())
                return false;

            if (direction > 0) {
                return rect.right >= getWidth() + 1;
            } else {
                return rect.left <= 0 - 1;
            }

        }

        /**
         * ?????????
         *
         * @param direction
         * @return
         */
        @Override
        public boolean canScrollVertically(int direction) {
            RectF rect = getMatrixRectF();
            if (rect == null || rect.isEmpty())
                return false;

            if (direction > 0) {
                return rect.bottom >= getHeight() + 1;
            } else {
                return rect.top <= 0 - 1;
            }
        }


    }
}
