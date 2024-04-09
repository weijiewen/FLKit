package com.wjw.flkit.base;

import static android.widget.RelativeLayout.ALIGN_PARENT_BOTTOM;
import static android.widget.RelativeLayout.ALIGN_PARENT_LEFT;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.viewbinding.ViewBinding;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public abstract class FLTabBarActivity extends FLBaseActivity {
    protected abstract void configPage();
    protected boolean willSelected(int index) {
        return true;
    }
    protected void didSelected(int index) {
    }
    protected void selectedIndex(int index) {
        if (index < itemList.size()) {
            selectedItem(itemList.get(index));
        }
    }

    public void setTabBarBackgroudColor(int tabBarBackgroudColor) {
        tabBarBackgroudView.setBackgroundColor(tabBarBackgroudColor);
    }
    public void setTabBarNormalColor(int tabBarNormalColor) {
        this.tabBarNormalColor = tabBarNormalColor;
        reloadItems();
    }
    public void setTabBarSelectedColor(int tabBarSelectedColor) {
        this.tabBarSelectedColor = tabBarSelectedColor;
        reloadItems();
    }
    public void setItemList(List itemList) {
        this.setItemList(itemList, 0);
    }
    public void setItemList(List itemList, int selectedIndex) {
        tabBar.removeAllViews();
        this.itemList = itemList;
        for (int i = 0; i < itemList.size(); i ++) {
            FLTabBarBaseItem item = (FLTabBarBaseItem) itemList.get(i);
            if (i == selectedIndex) {
                selectedItem(item);
            }
            ConstraintLayout constraintLayout = new ConstraintLayout(this);
            constraintLayout.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
            item.setLayoutWeakReference(constraintLayout);
            tabBar.addView(constraintLayout);
        }
    }

    private static int tabBarHeight = 56;
    private LinearLayout tabBarView;
    private LinearLayout tabBarBackgroudView;
    private LinearLayout tabBar;


    private int tabBarNormalColor = Color.parseColor("#727378");
    private int tabBarSelectedColor = Color.BLACK;
    private List<FLTabBarBaseItem> itemList = new ArrayList<>();
    private FLTabBarBaseItem selectedItem;

    private void reloadItems() {
        for (FLTabBarBaseItem item: itemList) {
            if (selectedItem != null && selectedItem == item) {
                item.selectedChange(true);
            }
            else {
                item.selectedChange(false);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (selectedItem != null) {
            selectedItem.tabBarPage.willShow();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (selectedItem != null) {
            selectedItem.tabBarPage.pageWilHide();
        }
    }
    @Override
    protected void configNavigation(FLNavigationView navigationView) {

    }
    @Override
    protected View getView() {
        RelativeLayout relativeLayout = new RelativeLayout(this);

        RelativeLayout.LayoutParams tabBarViewParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        tabBarViewParams.addRule(ALIGN_PARENT_LEFT);
        tabBarViewParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        tabBarViewParams.setMargins(0, 0, 0, dipToPx(tabBarHeight));
        tabBarView = new LinearLayout(this);
        tabBarView.setLayoutParams(tabBarViewParams);
        relativeLayout.addView(tabBarView);

        RelativeLayout.LayoutParams tabBarBackgroundParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dipToPx(tabBarHeight));
        tabBarBackgroundParams.addRule(ALIGN_PARENT_LEFT);
        tabBarBackgroundParams.addRule(ALIGN_PARENT_BOTTOM);
        tabBarBackgroudView = new LinearLayout(this);
        tabBarBackgroudView.setLayoutParams(tabBarBackgroundParams);
        tabBarBackgroudView.setBackgroundColor(Color.WHITE);
        relativeLayout.addView(tabBarBackgroudView);

        RelativeLayout.LayoutParams tabBarParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        tabBarParams.addRule(ALIGN_PARENT_LEFT);
        tabBarParams.addRule(ALIGN_PARENT_BOTTOM);
        tabBar = new LinearLayout(this);
        tabBar.setLayoutParams(tabBarParams);
        tabBar.setOrientation(LinearLayout.HORIZONTAL);
        tabBar.setGravity(Gravity.BOTTOM);
        relativeLayout.addView(tabBar);
        configPage();
        return relativeLayout;
    }

    @Override
    protected FLOffsetStyle offsetStyle() {
        return FLOffsetStyle.None;
    }

    @Override
    protected boolean addNavigation() {
        return false;
    }

    @Override
    protected void didClick(View view) {

    }

    private void selectedItem(FLTabBarBaseItem item) {
        if (selectedItem != null) {
            if (selectedItem == item || !willSelected(itemList.indexOf(item))) {
                return;
            }
        }
        if (selectedItem != null) {
            selectedItem.itemSelected(false);
        }
        item.itemSelected(true);
        selectedItem = item;
        didSelected(itemList.indexOf(item));
    }

    public abstract static class FLTabBarPage<Binding extends ViewBinding> extends RelativeLayout {
        private WeakReference<FLTabBarBaseItem> itemWeakReference;
        private boolean didLoad = false;
        protected FLNavigationView navigationView;
        protected Binding binding;

        public FLTabBarPage(Context context) {
            super(context);
            if (addNavigation()) {
                RelativeLayout.LayoutParams navigationParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                navigationParams.addRule(ALIGN_PARENT_LEFT);
                navigationParams.addRule(ALIGN_PARENT_TOP);
                navigationView = new FLNavigationView(context);
                navigationView.setLayoutParams(navigationParams);
                navigationView.setPadding(0, getActivity().getStatusHeight(), 0, 0);
                configNavigation(navigationView);
            }
            binding = getBinding();
            RelativeLayout.LayoutParams rootParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            rootParams.addRule(ALIGN_PARENT_LEFT);
            rootParams.addRule(ALIGN_PARENT_TOP);
            FLBaseActivity.FLOffsetStyle offsetStyle = offsetStyle();
            switch (offsetStyle) {
                case None:
                    rootParams.topMargin = 0;
                    break;
                case StatusBar:
                    rootParams.topMargin = getActivity().getStatusHeight();
                    break;
                case NavigationBar:
                    rootParams.topMargin = getActivity().getNavigationHeight();
                    break;
            }
            binding.getRoot().setLayoutParams(rootParams);
            this.addView(binding.getRoot());
            if (navigationView != null) {
                this.addView(navigationView);
            }
        }
        protected abstract Binding getBinding();
        protected FLTabBarActivity getActivity() {
            return (FLTabBarActivity) getContext();
        }
        protected boolean addNavigation() {
            return true;
        }
        protected FLBaseActivity.FLOffsetStyle offsetStyle() {
            return FLBaseActivity.FLOffsetStyle.NavigationBar;
        }

        protected abstract void configNavigation(FLNavigationView navigationView);
        protected abstract void didLoad();
        protected void pageWillShow() {

        }
        protected void pageWilHide() {

        }
        private void willShow() {
            setStatusStyle(style);
            if (!didLoad) {
                didLoad = true;
                didLoad();
            }
            pageWillShow();
        }
        private StatusStyle style = getActivity().getStatusStyle();
        public final StatusStyle getStatusStyle() {
            return style;
        }
        public final void setStatusStyle(StatusStyle style) {
            this.style = style;
            getActivity().setStatusStyle(style);
        }

        public final FLTabBarBaseItem getTabBarItem() {
            return itemWeakReference.get();
        }
    }

    public abstract class FLTabBarBaseItem extends LinearLayout {
        private WeakReference<ConstraintLayout> layoutWeakReference;
        private LinearLayout badgeLayout;
        private CardView badgeCard;
        private TextView badgeText;
        private FLTabBarPage tabBarPage;
        public FLTabBarBaseItem(Context context, @Nullable FLTabBarPage tabBarPage) {
            super(context);
            this.tabBarPage = tabBarPage;
            tabBarPage.itemWeakReference = new WeakReference(this);
        }

        private void setLayoutWeakReference(ConstraintLayout constraintLayout) {
            if (this.layoutWeakReference != null && this.layoutWeakReference.get() != null) {
                this.layoutWeakReference.get().removeView(this);
            }
            this.layoutWeakReference = new WeakReference<>(constraintLayout);
            ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.leftToLeft = ConstraintSet.PARENT_ID;
            layoutParams.rightToRight = ConstraintSet.PARENT_ID;
            layoutParams.topToTop = ConstraintSet.PARENT_ID;
            layoutParams.bottomToBottom = ConstraintSet.PARENT_ID;
            setLayoutParams(layoutParams);
            constraintLayout.addView(this);
            constraintLayout.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (itemList.contains(FLTabBarBaseItem.this)) {
                        selectedItem(FLTabBarBaseItem.this);
                    }
                }
            });
        }

        protected abstract void selectedChange(boolean selected);
        private void itemSelected(boolean selected) {
            if (selected) {
                if (tabBarPage != null) {
                    if (selectedItem != null) {
                        tabBarPage.willShow();
                    }
                    tabBarView.addView(tabBarPage);
                }
            }
            else {
                if (tabBarPage != null) {
                    tabBarPage.pageWilHide();
                    tabBarView.removeView(tabBarPage);
                }
            }
            selectedChange(selected);
        }
        public final void setBadge(@Nullable String badge) {
            setBadge(badge, 25, 6);
        }
        public final void setBadge(@Nullable String badge, int leftMargin, int topMargin) {
            if (layoutWeakReference == null || layoutWeakReference.get() == null) {
                return;
            }
            ConstraintLayout constraintLayout = layoutWeakReference.get();
            if (badge == null) {
                if (badgeLayout != null) {
                    constraintLayout.removeView(badgeLayout);
                }
            }
            else {
                if (badgeLayout == null) {
                    ConstraintLayout.LayoutParams badgelayoutParams = new ConstraintLayout.LayoutParams(0, 0);
                    badgelayoutParams.leftToLeft = ConstraintSet.PARENT_ID;
                    badgelayoutParams.rightToRight = ConstraintSet.PARENT_ID;
                    badgelayoutParams.topToTop = ConstraintSet.PARENT_ID;
                    badgelayoutParams.bottomToBottom = ConstraintSet.PARENT_ID;
                    badgeLayout = new LinearLayout(getContext());
                    badgeLayout.setLayoutParams(badgelayoutParams);
                    badgeLayout.setGravity(Gravity.CENTER_HORIZONTAL);
                    constraintLayout.addView(badgeLayout);

                    LinearLayout.LayoutParams badgeCardParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    badgeCardParams.leftMargin = dipToPx(leftMargin);
                    badgeCardParams.topMargin = dipToPx(topMargin);
                    badgeCard = new CardView(getContext());
                    badgeCard.setLayoutParams(badgeCardParams);
                    badgeCard.setCardElevation(0);
                    badgeCard.setCardBackgroundColor(Color.parseColor("#DE4C3D"));
                    badgeLayout.addView(badgeCard);

                    badgeText = new TextView(getContext());
                    badgeText.setLayoutParams(new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    badgeText.setTextColor(Color.WHITE);
                    badgeText.setGravity(Gravity.CENTER);
                    badgeText.setMaxLines(1);
                    badgeCard.addView(badgeText);
                }
                if (badge.isEmpty()) {
                    badgeText.setPadding(0, 0, 0, 0);
                }
                else {
                    badgeText.setPadding(dipToPx(3), 0, dipToPx(3), 0);
                }
                badgeText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, badge.isEmpty() ? 0 : 9);
                badgeText.setMinWidth(dipToPx(badge.isEmpty() ? 6 : 16));
                badgeText.setMinHeight(dipToPx(badge.isEmpty() ? 6 : 16));
                badgeText.setMaxHeight(dipToPx(badge.isEmpty() ? 6 : 16));
                badgeCard.setRadius(dipToPx(badge.isEmpty() ? 3 : 8));
                badgeText.setText(badge);
            }
        }
    }

    public class FLTabBarItem extends FLTabBarBaseItem {
        private int normalImage = 0;
        private int selectedImage = 0;
        private ImageView imageView;
        private TextView textView;

        public FLTabBarItem(@Nullable String name, int normalImage, int selectedImage, @Nullable FLTabBarPage tabBarPage) {
            this(name, normalImage, selectedImage, 20, 20, 0, tabBarPage);
        }

        public FLTabBarItem(int normalImage, int selectedImage, int imageWidth, int imageHeigh, int imageBottom, @Nullable FLTabBarPage tabBarPage) {
            this(null, normalImage, selectedImage, imageWidth, imageHeigh, imageBottom, tabBarPage);
        }

        private FLTabBarItem(@Nullable String name, int normalImage, int selectedImage, int imageWidth, int imageHeigh, int imageBottom, @Nullable FLTabBarPage tabBarPage) {
            super(getActivity(), tabBarPage);
            if (tabBarPage != null) {
                tabBarPage.setLayoutParams(new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            }
            setLayoutParams(new LinearLayoutCompat.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
            setMinimumHeight(dipToPx(tabBarHeight));
            setOrientation(VERTICAL);
            setGravity(Gravity.CENTER);

            this.normalImage = normalImage;
            this.selectedImage = selectedImage;
            if (normalImage > 0 || selectedImage > 0) {
                LinearLayout.LayoutParams layoutParams = new LinearLayoutCompat.LayoutParams(dipToPx(imageWidth), dipToPx(imageHeigh));
                if (imageBottom > 0) {
                    layoutParams.setMargins(0, 0, 0, dipToPx(imageBottom));
                }
                imageView = new ImageView(getContext());
                imageView.setLayoutParams(layoutParams);
                imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                imageView.setImageResource(normalImage > 0 ? normalImage : selectedImage);
                addView(imageView);
            }
            if (name != null && !name.isEmpty()) {
                LinearLayout.LayoutParams layoutParams = new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                if (imageView == null) {
                    layoutParams.setMargins(dipToPx(5), 0, dipToPx(5), 0);
                }
                else {
                    layoutParams.setMargins(dipToPx(5), dipToPx(6), dipToPx(5), 0);
                }
                textView = new TextView(getContext());
                textView.setLayoutParams(layoutParams);
                textView.setGravity(Gravity.CENTER);
                textView.setMaxLines(1);
                textView.setEllipsize(TextUtils.TruncateAt.END);
                textView.setTextColor(tabBarNormalColor);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
                textView.setText(name);
                addView(textView);
            }
        }

        @Override
        protected void selectedChange(boolean selected) {
            if (selected) {
                if (selectedImage > 0 && imageView != null) {
                    imageView.setImageResource(selectedImage);
                }
                if (textView != null) {
                    textView.setTextColor(tabBarSelectedColor);
                }
            }
            else {
                if (normalImage > 0 && imageView != null) {
                    imageView.setImageResource(normalImage);
                }
                if (textView != null) {
                    textView.setTextColor(tabBarNormalColor);
                }
            }
        }
    }
}
