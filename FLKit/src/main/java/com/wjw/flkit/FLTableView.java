package com.wjw.flkit;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class FLTableView extends RecyclerView {
    public interface DataSource <T extends FLTableViewCell> {
        void errorRetryRequest();
        int itemCount();
        int itemType(int index);
        @LayoutRes
        int getItemLayout(int itemType);
        T createItem(View itemView, int viewType);
        void bindItem(T view, int index);
    }
    public void setDataSource(String empty, DataSource dataSource) {
        this.empty = empty;
        this.dataSource = dataSource;
    }

    public final void startLoading() {
        if (!isRefreshing()) {
            loadingView = new LoadingView(getContext());
            reloadAdapter();
        }
    }

    public final void reloadData(String error) {
        reloadData(error, true);
    }

    public final void reloadData(boolean hasMore) {
        reloadData(null, hasMore);
    }

    public final FLTableView removeItem(int index) {
        if (index >= 0 && index < itemCount) {
            adapter.notifyItemRemoved(startIndex + index);
            itemCount -= 1;
            endIndex -= 1;
        }
        return this;
    }

    public interface RefreshInterface {
        void enterRefreshing();
    }
    public void addHeader(RefreshInterface action) {
        header = new Header(getContext());
        headerAction = action;
    }

    public void addFooter(RefreshInterface action) {
        footer = new Footer(getContext());
        footerAction = action;
    }
    private Header header;
    private RefreshInterface headerAction;
    private Footer footer;
    private RefreshInterface footerAction;
    private boolean isRefreshing() {
        if (header != null && header.refreshing) {
            return true;
        }
        if (footer != null && footer.refreshing) {
            return true;
        }
        return false;
    }

    private void reloadData(String error, boolean hasMore) {
        if (header != null) {
            header.endRefresh();
        }
        if (footer != null) {
            footer.endRefresh(hasMore);
        }
        int count = dataSource.itemCount();
        boolean reload = itemCount > count;
        if (loadingView != null || errorView != null || emptyView != null) {
            reload = true;
        }
        loadingView = null;
        errorView = null;
        emptyView = null;
        itemCount = count;
        if (count == 0) {
            if (error != null && !error.isEmpty()) {
                errorView = new ErrorView(getContext(), error);
            }
            else {
                emptyView = new EmptyView(getContext(), empty);
            }
            reloadAdapter();
            return;
        }
        if (adapter == null || reload) {
            reloadAdapter();
        }
        else {
            adapter.notifyDataSetChanged();
        }
    }

    private Adapter adapter;
    private DataSource dataSource;
    private int itemCount = 0;
    private int startIndex = 0;
    private int endIndex = 0;
    private String empty;
    private LoadingView loadingView;
    private ErrorView errorView;
    private EmptyView emptyView;
    private void reloadAdapter() {
        adapter = new Adapter() {
            @Override
            public int getItemCount() {
                int count = itemCount;
                startIndex = 0;
                if (loadingView != null || errorView != null) {
                    count = 1;
                    startIndex = 1;
                }
                else {
                    if (header != null) {
                        count += 1;
                        startIndex += 1;
                    }
                    if (emptyView != null) {
                        count += 1;
                        startIndex += 1;
                    }
                    else if (footer != null) {
                        count += 1;
                    }
                }
                endIndex = startIndex + itemCount;
                return count;
            }

            @Override
            public int getItemViewType(int position) {
                if (position < startIndex) {
                    if (loadingView != null) {
                        return loadingViewType;
                    }
                    if (errorView != null) {
                        return errorViewType;
                    }
                    if (position == 0) {
                        return header != null ? headerViewType : emptyViewType;
                    }
                    return emptyViewType;
                }
                if (endIndex == position) {
                    return footerViewType;
                }
                return dataSource.itemType(position - startIndex);
            }

            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                if (viewType == loadingViewType) {
                    return new PlaceHolderViewHolder(loadingView);
                }
                else if (viewType == errorViewType) {
                    return new PlaceHolderViewHolder(errorView);
                }
                else if (viewType == emptyViewType) {
                    return new PlaceHolderViewHolder(emptyView);
                }
                else if (viewType == headerViewType) {
                    return new PlaceHolderViewHolder(header);
                }
                else if (viewType == footerViewType) {
                    return new PlaceHolderViewHolder(footer);
                }
                View itemView = LayoutInflater.from(parent.getContext()).inflate(dataSource.getItemLayout(viewType), parent, false);
                return dataSource.createItem(itemView, viewType);
            }

            @Override
            public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
                if (startIndex <= position && position < endIndex) {
                    dataSource.bindItem((FLTableViewCell) holder, position - startIndex);
                }
            }
        };
        setAdapter(adapter);
    }

    public FLTableView(@NonNull Context context) {
        this(context, null);
    }

    public FLTableView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FLTableView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setLayoutManager(new LinearLayoutManager(context));
    }

    private float lastY = -1;
    private float sumOffSet;
    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if (lastY == -1) {
            lastY = e.getRawY();
        }
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastY = e.getRawY();
                sumOffSet = 0;
                break;
            case MotionEvent.ACTION_MOVE:
                float deltaY = (e.getRawY() - lastY) / 2;//为了防止滑动幅度过大，将实际手指滑动的距离除以2
                lastY = e.getRawY();
                sumOffSet += deltaY;//计算总的滑动的距离
                if (header != null && header.getParent() != null && deltaY > 0) {
                    if ((footer == null || !footer.refreshing) && !header.refreshing) {
                        header.onMove(deltaY, sumOffSet);
                        if (header.getVisibleHeight() > 0) {
                            return false;
                        }
                    }
                }
                else if (footer != null && footer.enable && footer.hasData && footer.getParent() != null && deltaY < 0) {
                    if ((header == null || !header.refreshing) && !footer.refreshing) {
                        footer.enterRefresh();
                        footerAction.enterRefreshing();
                    }
                }
                break;
            default:
                lastY = -1; // reset
                if (header != null && header.getParent() != null) {
                    if (!header.refreshing && header.readyRefresh) {
                        header.enterRefresh();
                        headerAction.enterRefreshing();
                    }
                }
                break;
        }
        return super.onTouchEvent(e);
    }

    private int dipToPx(float dpValue) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    private class PlaceHolderViewHolder extends ViewHolder {
        public PlaceHolderViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    //loading
    private static int loadingViewType = 10086;
    private class LoadingView extends LinearLayout {
        public LoadingView(Context context) {
            super(context);
            setGravity(Gravity.CENTER);
            setLayoutParams(new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            ProgressBar progressBar = new ProgressBar(context);
            progressBar.setLayoutParams(new LinearLayoutCompat.LayoutParams(dipToPx(40), dipToPx(40)));
            addView(progressBar);
        }
    }

    private static int errorViewType = 10087;
    private class ErrorView extends LinearLayout {
        public ErrorView(Context context, String error) {
            super(context);
            setOrientation(VERTICAL);
            setGravity(Gravity.CENTER);
            setLayoutParams(new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            TextView textView = new TextView(getContext());
            textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
            textView.setTextColor(Color.BLACK);
            textView.setText(error);
            textView.setMaxLines(99);
            textView.setPadding(dipToPx(70), 0, dipToPx(70), 0);
            textView.setGravity(Gravity.CENTER);
            textView.setLayoutParams(new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            addView(textView);

            textView = new TextView(getContext());
            textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
            textView.setTextColor(Color.BLUE);
            textView.setText("重试");
            textView.setMaxLines(1);
            textView.setPadding(dipToPx(10), dipToPx(15), dipToPx(10), dipToPx(15));
            textView.setGravity(Gravity.CENTER);
            textView.setLayoutParams(new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            addView(textView);
            textView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    dataSource.errorRetryRequest();
                }
            });
        }
    }

    private static int emptyViewType = 10088;
    private class EmptyView extends LinearLayout {
        public EmptyView(Context context) {
            this(context, null);
        }
        public EmptyView(Context context, String emptyString) {
            super(context);
            if (emptyString == null || emptyString.isEmpty()) {
                emptyString = "暂无数据";
            }
            setGravity(Gravity.CENTER);
            setLayoutParams(new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            TextView textView = new TextView(getContext());
            textView.setTextSize(15);
            textView.setTextColor(Color.BLACK);
            textView.setText(emptyString);
            textView.setGravity(Gravity.CENTER);
            textView.setLayoutParams(new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            addView(textView);
        }
    }

    //header
    private static int headerViewType = 99998;
    private class Header extends LinearLayout {
        private ConstraintLayout contentLayout;
        private ProgressBar progressBar;
        private TextView textView;

        private int headerHeight;
        private boolean refreshing;
        private boolean readyRefresh;
        public Header(Context context) {
            this(context, null);
        }

        public Header(Context context, @Nullable AttributeSet attrs) {
            this(context, attrs, 0);
        }

        public Header(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            init();//初始化视图
        }

        private void init() {
            LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(0, 0, 0, 0);
            this.setLayoutParams(layoutParams);
            this.setPadding(0, 0, 0, 0);
            createView();
        }

        @SuppressLint("ResourceType")
        private void createView() {
            headerHeight = pxTodp(getContext(), 50);
            //将Header高度设置为0
            contentLayout = new ConstraintLayout(getContext());
            contentLayout.setId(99);
            addView(contentLayout, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0));
            ConstraintSet set = new ConstraintSet();
            set.constrainDefaultWidth(10, ConstraintSet.MATCH_CONSTRAINT);
            set.constrainDefaultHeight(10, headerHeight);

            textView = new TextView(getContext());
            textView.setTextColor(Color.BLACK);
            textView.setTextSize(14);
            textView.setGravity(Gravity.CENTER);
            textView.setId(100);
            contentLayout.addView(textView);
            set.connect(textView.getId(), ConstraintSet.LEFT, contentLayout.getId(), ConstraintSet.LEFT, 0);
            set.connect(textView.getId(), ConstraintSet.TOP, contentLayout.getId(), ConstraintSet.TOP, 0);
            set.connect(textView.getId(), ConstraintSet.RIGHT, contentLayout.getId(), ConstraintSet.RIGHT, 0);
            set.connect(textView.getId(), ConstraintSet.BOTTOM, contentLayout.getId(), ConstraintSet.BOTTOM, 0);

            progressBar = new ProgressBar(getContext());
            progressBar.setId(101);
            contentLayout.addView(progressBar);
            set.connect(progressBar.getId(), ConstraintSet.LEFT, contentLayout.getId(), ConstraintSet.LEFT, 0);
            set.connect(progressBar.getId(), ConstraintSet.TOP, contentLayout.getId(), ConstraintSet.TOP, pxTodp(getContext(), 10));
            set.connect(progressBar.getId(), ConstraintSet.RIGHT, contentLayout.getId(), ConstraintSet.RIGHT, 0);
            set.connect(progressBar.getId(), ConstraintSet.BOTTOM, contentLayout.getId(), ConstraintSet.BOTTOM, pxTodp(getContext(), 10));
            set.constrainWidth(progressBar.getId(), pxTodp(getContext(), 30));
            set.constrainHeight(progressBar.getId(), pxTodp(getContext(), 30));

            set.applyTo(contentLayout);
            progressBar.setVisibility(View.INVISIBLE);
        }

        private int pxTodp(Context context, float pxValue) {
            final float scale = context.getResources().getDisplayMetrics().density;
            return (int) (pxValue * scale + 0.5f);
        }

        public void onMove(float offSet, float sumOffSet) {
            if (getVisibleHeight() > 0 || offSet > 0) {
                setVisibleHeight((int) offSet + getVisibleHeight());
                if (!refreshing) { // 未处于刷新状态，更新文字
                    if (getVisibleHeight() > headerHeight) {
                        readyRefresh = true;
                        textView.setText("松开刷新");
                    } else {
                        readyRefresh = false;
                        textView.setText("下拉刷新");
                    }
                }
            }
        }

        private void changeRefreshing(boolean refreshing) {
            if (this.refreshing == refreshing && refreshing) {
                if (getVisibleHeight() > 0) {
                    smoothScrollTo(0);
                }
                return;
            }
            this.refreshing = refreshing;
            if (refreshing) {
                textView.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                smoothScrollTo(headerHeight);
            }
            else {
                textView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
                smoothScrollTo(0);
            }
            readyRefresh = false;
        }

        private void smoothScrollTo(int destHeight) {
            ValueAnimator animator = ValueAnimator.ofInt(getVisibleHeight(), destHeight);
            animator.setDuration(300).start();
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    setVisibleHeight((int) animation.getAnimatedValue());
                }
            });
            animator.start();
        }

        private void setVisibleHeight(int height) {
            if (height < 0) height = 0;
            LayoutParams layoutParams = (LayoutParams) contentLayout.getLayoutParams();
            layoutParams.height = height;
            contentLayout.setLayoutParams(layoutParams);
        }
        private int getVisibleHeight() { return contentLayout.getLayoutParams().height; }
        private void enterRefresh() { changeRefreshing(true); }

        public boolean refreshing() { return refreshing; }
        public void endRefresh() {
            changeRefreshing(false);
        }
    }

    //footer
    private static int footerViewType = 99999;
    private class Footer extends LinearLayout {
        private ConstraintLayout contentLayout;
        private ProgressBar progressBar;
        private TextView textView;

        private int footerHeight;
        private boolean refreshing;
        private boolean enable = true;
        private boolean hasData = true;
        public Footer(Context context) {
            this(context, null);
        }

        public Footer(Context context, @Nullable AttributeSet attrs) {
            this(context, attrs, 0);
        }

        public Footer(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            init();//初始化视图
        }

        private void init() {
            LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(0, 0, 0, 0);
            this.setLayoutParams(layoutParams);
            this.setPadding(0, 0, 0, 0);
            createView();
        }

        @SuppressLint("ResourceType")
        private void createView() {
            footerHeight = pxTodp(getContext(), 50);
            //将Header高度设置为0
            contentLayout = new ConstraintLayout(getContext());
            contentLayout.setId(99);
            addView(contentLayout, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, footerHeight));
            ConstraintSet set = new ConstraintSet();
            set.constrainDefaultWidth(10, ConstraintSet.MATCH_CONSTRAINT);
            set.constrainDefaultHeight(10, footerHeight);

            textView = new TextView(getContext());
            textView.setTextColor(Color.BLACK);
            textView.setTextSize(14);
            textView.setGravity(Gravity.CENTER);
            textView.setId(100);
            textView.setText("上拉加载更多");
            contentLayout.addView(textView);
            set.connect(textView.getId(), ConstraintSet.LEFT, contentLayout.getId(), ConstraintSet.LEFT, 0);
            set.connect(textView.getId(), ConstraintSet.TOP, contentLayout.getId(), ConstraintSet.TOP, 0);
            set.connect(textView.getId(), ConstraintSet.RIGHT, contentLayout.getId(), ConstraintSet.RIGHT, 0);
            set.connect(textView.getId(), ConstraintSet.BOTTOM, contentLayout.getId(), ConstraintSet.BOTTOM, 0);

            progressBar = new ProgressBar(getContext());
            progressBar.setId(101);
            contentLayout.addView(progressBar);
            set.connect(progressBar.getId(), ConstraintSet.LEFT, contentLayout.getId(), ConstraintSet.LEFT, 0);
            set.connect(progressBar.getId(), ConstraintSet.TOP, contentLayout.getId(), ConstraintSet.TOP, pxTodp(getContext(), 10));
            set.connect(progressBar.getId(), ConstraintSet.RIGHT, contentLayout.getId(), ConstraintSet.RIGHT, 0);
            set.connect(progressBar.getId(), ConstraintSet.BOTTOM, contentLayout.getId(), ConstraintSet.BOTTOM, pxTodp(getContext(), 10));
            set.constrainWidth(progressBar.getId(), pxTodp(getContext(), 30));
            set.constrainHeight(progressBar.getId(), pxTodp(getContext(), 30));

            set.applyTo(contentLayout);
            progressBar.setVisibility(View.INVISIBLE);
        }

        private int pxTodp(Context context, float pxValue) {
            final float scale = context.getResources().getDisplayMetrics().density;
            return (int) (pxValue * scale + 0.5f);
        }

        private void changeRefreshing(boolean refreshing) {
            if (this.refreshing == refreshing) {
                return;
            }
            this.refreshing = refreshing;
            if (refreshing) {
                textView.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);
            }
            else {
                textView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
            }
        }

        private void setVisibleHeight(int height) {
            if (contentLayout.getLayoutParams().height == height) {
                return;
            }
            if (height < 0) height = 0;
            LayoutParams layoutParams = (LayoutParams) contentLayout.getLayoutParams();
            layoutParams.height = height;
            contentLayout.setLayoutParams(layoutParams);
        }
        private void enterRefresh() { changeRefreshing(true); }

        public boolean refreshing() { return refreshing; }
        public void endRefresh(boolean hasData) {
            changeRefreshing(false);
            setHasData(hasData);
        }

        public void setEnable(boolean enable) {
            this.enable = enable;
            setVisibleHeight(enable ? footerHeight : 0);
        }
        private boolean getEnable() { return enable; }

        private void setHasData(boolean hasData) {
            this.hasData = hasData;
            textView.setText(hasData ? "上拉加载更多" : "我已经到底啦~");
        }
        public boolean getHasData() { return hasData; }
    }

    //baseViewHolder
    public abstract static class FLTableViewCell<DataType> extends ViewHolder {
        protected View itemView;
        protected DataType itemData;
        protected int itemIndex;
        public FLTableViewCell(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            configItem();
        }
        public final Context getContext() {
            return itemView.getContext();
        }
        public final void bindData(int index, DataType data) {
            itemIndex = index;
            DataType oldData = itemData;
            itemData = data;
            dataUpdated(oldData);
        }
        protected abstract void configItem();
        protected abstract void dataUpdated(DataType oldData);

        @Nullable
        public final <T extends View> T findViewById(@IdRes int id) {
            if (id == NO_ID) {
                return null;
            }
            return itemView.findViewById(id);
        }
        @Nullable
        public final ImageView findImageViewById(@IdRes int id) {
            if (id == NO_ID) {
                return null;
            }
            return itemView.findViewById(id);
        }
        public final void setText(@IdRes int id, String text) {
            if (id != NO_ID) {
                TextView view = findViewById(id);
                if (view != null) {
                    view.setText(text);
                }
            }
        }
        public final void setText(@IdRes int id, @StringRes int stringId) {
            if (id != NO_ID) {
                TextView view = findViewById(id);
                if (view != null) {
                    view.setText(stringId);
                }
            }
        }
        public final void setTextColor(@IdRes int id, @ColorInt int colorId) {
            if (id != NO_ID) {
                TextView view = findViewById(id);
                if (view != null) {
                    view.setTextColor(colorId);
                }
            }
        }
        public final void setTextColor(@IdRes int id, String hexString) {
            if (id != NO_ID) {
                TextView view = findViewById(id);
                if (view != null) {
                    view.setTextColor(Color.parseColor(hexString));
                }
            }
        }
        public final void setImage(@IdRes int id, @DrawableRes int imageResId) {
            if (id != NO_ID) {
                ImageView view = findViewById(id);
                if (view != null) {
                    view.setImageResource(imageResId);
                }
            }
        }
        public final void setImage(@IdRes int id, Drawable drawable) {
            if (id != NO_ID) {
                ImageView view = findViewById(id);
                if (view != null) {
                    view.setImageDrawable(drawable);
                }
            }
        }
        public final void setImage(@IdRes int id, Bitmap bitmap) {
            if (id != NO_ID) {
                ImageView view = findViewById(id);
                if (view != null) {
                    view.setImageBitmap(bitmap);
                }
            }
        }
        public final void setBackground(@IdRes int id, Drawable drawable) {
            if (id != NO_ID) {
                View view = findViewById(id);
                if (view != null) {
                    view.setBackground(drawable);
                }
            }
        }
        public final void setBackgroundColor(@IdRes int id, @ColorInt int colorId) {
            if (id != NO_ID) {
                View view = findViewById(id);
                if (view != null) {
                    view.setBackgroundColor(colorId);
                }
            }
        }
        public final void setBackgroundColor(@IdRes int id, String hexString) {
            if (id != NO_ID) {
                View view = findViewById(id);
                if (view != null) {
                    view.setBackgroundColor(Color.parseColor(hexString));
                }
            }
        }
        public final void setBackgroundResource(@IdRes int id, @DrawableRes int layoutId) {
            if (id != NO_ID) {
                View view = findViewById(id);
                if (view != null) {
                    view.setBackgroundResource(layoutId);
                }
            }
        }
        public final void setVisible(@IdRes int id, boolean visible) {
            if (id != NO_ID) {
                View view = findViewById(id);
                if (view != null) {
                    view.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
                }
            }
        }
        public final void setEnable(@IdRes int id, boolean enable) {
            if (id != NO_ID) {
                View view = findViewById(id);
                if (view != null) {
                    view.setEnabled(enable);
                }
            }
        }
    }
}
