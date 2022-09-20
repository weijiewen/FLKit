package com.wjw.flkit.ui;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;

import java.util.ArrayList;
import java.util.List;

public class FLTableView extends RecyclerView {
    public interface CreatSection<Head extends FLTableViewBaseSection, Foot extends FLTableViewBaseSection> {
        int sectionCount();
        @Nullable
        Head getHeader(@NonNull ViewGroup parent);
        @Nullable
        Foot getFooter(@NonNull ViewGroup parent);
    }
    private CreatSection creatSection;

    public void setCreatSection(CreatSection creatSection) {
        this.creatSection = creatSection;
    }
    public interface CreatCell<T extends FLTableViewCell> {
        int itemCount(int section);
        T getCell(@NonNull ViewGroup parent);
    }
    private CreatCell creatCell;
    public void setCreatCell(CreatCell creatCell) {
        setCreatCell("暂无数据", creatCell);
    }
    public void setCreatCell(String empty, CreatCell creatCell) {
        this.empty = empty;
        this.creatCell = creatCell;
    }
    private int tintColor = Color.parseColor("#247BEF");
    public void setTintColor(int color) {
        this.tintColor = color;
    }
    private int textColor = Color.BLACK;
    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }
    public final void startLoading() {
        if (!isRefreshing()) {
            loadingView = new LoadingView(getContext());
            reloadAdapter();
        }
    }
    public final void reloadData() {
        reloadData(null, true, true);
    }
    public interface Retry {
        void retryRequest();
    }
    private Retry retry;
    public final void reloadData(String error, Retry retry) {
        this.retry = retry;
        reloadData(error, true, null);
    }
    public final void reloadData(boolean hasMore) {
        reloadData(null, hasMore, null);
    }
    public final void getCell(int section, int index) {

    }
    public interface RefreshInterface {
        void enterRefreshing();
    }
    public void addHeaderRefresh(RefreshInterface action) {
        headerRefresh = new HeaderRefresh(getContext());
        headerAction = action;
    }
    public void addFooterRefresh(RefreshInterface action) {
        footerRefresh = new FooterRefresh(getContext());
        footerAction = action;
    }
    private HeaderRefresh headerRefresh;
    private RefreshInterface headerAction;
    private FooterRefresh footerRefresh;
    private RefreshInterface footerAction;
    private boolean isRefreshing() {
        if (headerRefresh != null && headerRefresh.refreshing) {
            return true;
        }
        if (footerRefresh != null && footerRefresh.refreshing) {
            return true;
        }
        return false;
    }

    private Adapter adapter;
    private int mainCount = 0;
    private int startIndex = 0;
    private int endIndex = 0;
    private int sectionCount;
    private List<Integer> itemCounts;
    private String empty;
    private LoadingView loadingView;
    private ErrorView errorView;
    private EmptyView emptyView;
    private void reloadData(String error, boolean hasMore, Boolean isReload) {
        if (headerRefresh != null) {
            headerRefresh.endRefresh();
        }
        if (footerRefresh != null) {
            footerRefresh.endRefresh(hasMore);
        }
        sectionCount = 1;
        int count = 0;
        if (creatSection != null) {
            sectionCount = creatSection.sectionCount();
        }
        itemCounts = new ArrayList<>();
        if (creatCell != null) {
            for (int i = 0; i < sectionCount; i++) {
                int itemCount = creatCell.itemCount(i);
                if (itemCount > 0) {
                    count += itemCount;
                }
                if (creatSection != null) {
                    count += 2;
                }
                itemCounts.add(itemCount);
            }
        }

        boolean reload = isReload == null ? mainCount > count : isReload.booleanValue();
        if (!reload && (loadingView != null || errorView != null || emptyView != null)) {
            reload = true;
        }
        loadingView = null;
        errorView = null;
        emptyView = null;
        mainCount = count;
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
    private void reloadAdapter() {
        adapter = new Adapter() {
            @Override
            public int getItemCount() {
                int count = mainCount;
                startIndex = 0;
                if (loadingView != null || errorView != null) {
                    count = 1;
                    startIndex = 1;
                }
                else {
                    if (headerRefresh != null) {
                        count += 1;
                        startIndex += 1;
                    }
                    if (emptyView != null) {
                        count += 1;
                        startIndex += 1;
                    }
                    else if (footerRefresh != null) {
                        count += 1;
                    }
                }
                endIndex = startIndex + mainCount;
                return count;
            }

            @Override
            public int getItemViewType(int position) {
                if (position < startIndex) {
                    if (loadingView != null) {
                        return ViewType.Loading.value;
                    }
                    if (errorView != null) {
                        return ViewType.Error.value;
                    }
                    if (position == 0 && headerRefresh != null) {
                        return ViewType.RefreshHeader.value;
                    }
                    return ViewType.Empty.value;
                }
                if (endIndex == position) {
                    return ViewType.RefreshFooter.value;
                }
                int index = position - startIndex;
                if (creatSection != null) {
                    for (int i = 0; i < sectionCount; i++) {
                        int itemCount = itemCounts.get(i) + 2;
                        index -= itemCount;
                        if (index < 0) {
                            if (index == -itemCount) {
                                return ViewType.Header.value;
                            }
                            if (index == -1) {
                                return ViewType.Footer.value;
                            }
                            break;
                        }
                    }
                }
                return ViewType.Cell.value;
            }

            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                if (viewType == ViewType.Loading.value) {
                    return new PlaceHolderViewHolder(loadingView);
                }
                else if (viewType == ViewType.Error.value) {
                    return new PlaceHolderViewHolder(errorView);
                }
                else if (viewType == ViewType.Empty.value) {
                    return new PlaceHolderViewHolder(emptyView);
                }
                else if (viewType == ViewType.RefreshHeader.value) {
                    return new PlaceHolderViewHolder(headerRefresh);
                }
                else if (viewType == ViewType.RefreshFooter.value) {
                    return new PlaceHolderViewHolder(footerRefresh);
                }
                else if (viewType == ViewType.Header.value) {
                    ViewHolder viewHolder = creatSection.getHeader(parent);
                    if (viewHolder == null) {
                        viewHolder = new FLTableViewBaseSection(new PlaceholdView(getContext()));
                    }
                    return viewHolder;
                }
                else if (viewType == ViewType.Footer.value) {
                    ViewHolder viewHolder = creatSection.getFooter(parent);
                    if (viewHolder == null) {
                        viewHolder = new FLTableViewBaseSection(new PlaceholdView(getContext()));
                    }
                    return viewHolder;
                }
                return creatCell.getCell(parent);
            }

            @Override
            public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
                if (startIndex <= position && position < endIndex) {
                    int index = position - startIndex;
                    int section = 0;
                    int cellIndex = index;
                    if (creatSection != null) {
                        for (int i = 0; i < sectionCount; i++) {
                            section = i;
                            int itemCount = itemCounts.get(i) + 2;
                            index -= itemCount;
                            if (index < 0) {
                                if (index == -itemCount) {
                                    FLTableViewBaseSection baseHeader = (FLTableViewBaseSection) holder;
                                    baseHeader.section = section;
                                    try {
                                        FLTableViewSection header = (FLTableViewSection) holder;
                                        if (header != null) {
                                            header.bindData(header.sectionBinding, section);
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    return;
                                }
                                if (index == -1) {
                                    FLTableViewBaseSection baseFooter = (FLTableViewBaseSection) holder;
                                    baseFooter.section = section;
                                    try {
                                        FLTableViewSection footer = (FLTableViewSection) holder;
                                        if (footer != null) {
                                            footer.bindData(footer.sectionBinding, section);
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    return;
                                }
                                cellIndex = index + itemCount;
                                break;
                            }
                        }
                    }
                    if (creatSection != null) {
                        cellIndex -= 1;
                    }
                    FLTableViewCell cell = (FLTableViewCell) holder;
                    cell.section = section;
                    cell.index = cellIndex;
                    cell.bindData(cell.cellBinding, section, cellIndex);
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
                if (headerRefresh != null && headerRefresh.getParent() != null && deltaY > 0) {
                    if ((footerRefresh == null || !footerRefresh.refreshing) && !headerRefresh.refreshing) {
                        headerRefresh.onMove(deltaY, sumOffSet);
                        if (headerRefresh.getVisibleHeight() > 0) {
                            return false;
                        }
                    }
                }
                else if (footerRefresh != null && footerRefresh.enable && footerRefresh.hasData && footerRefresh.getParent() != null && deltaY < 0) {
                    if ((headerRefresh == null || !headerRefresh.refreshing) && !footerRefresh.refreshing) {
                        footerRefresh.enterRefresh();
                        footerAction.enterRefreshing();
                    }
                }
                break;
            default:
                lastY = -1; // reset
                if (headerRefresh != null && headerRefresh.getParent() != null) {
                    if (!headerRefresh.refreshing) {
                        if (headerRefresh.readyRefresh) {
                            headerRefresh.enterRefresh();
                            headerAction.enterRefreshing();
                        }
                        else  {
                            headerRefresh.changeRefreshing(false);
                        }
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

    private enum ViewType {
        Loading(99999),
        Error(99998),
        Empty(99997),
        RefreshHeader(99996),
        RefreshFooter(99995),
        Header(99994),
        Footer(99993),
        Cell(99992);

        private int value = 0;
        private ViewType(int value) {
            this.value = value;
        }
        private static ViewType valueOf(int value) {
            switch (value) {
                case 99999:
                    return Loading;
                case 99998:
                    return Error;
                case 99997:
                    return Empty;
                case 99996:
                    return RefreshHeader;
                case 99995:
                    return RefreshFooter;
                case 99994:
                    return Header;
                case 99993:
                    return Footer;
                case 99992:
                    return Cell;
                default:
                    return null;
            }
        }
    }
    private class PlaceholdView extends LinearLayout {
        public PlaceholdView(Context context) {
            super(context);
            setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0));
        }
    }
    private class LoadingView extends LinearLayout {
        public LoadingView(Context context) {
            super(context);
            setGravity(Gravity.CENTER);
            setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            ProgressBar progressBar = new ProgressBar(context);
            progressBar.setLayoutParams(new LinearLayout.LayoutParams(dipToPx(35), dipToPx(35)));
            progressBar.setIndeterminateTintList(ColorStateList.valueOf(tintColor));
            addView(progressBar);
        }
    }
    private class ErrorView extends LinearLayout {
        public ErrorView(Context context, String error) {
            super(context);
            setOrientation(VERTICAL);
            setGravity(Gravity.CENTER);
            setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            TextView textView = new TextView(getContext());
            textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
            textView.setTextColor(textColor);
            textView.setText(error);
            textView.setMaxLines(99);
            textView.setPadding(dipToPx(70), 0, dipToPx(70), 0);
            textView.setGravity(Gravity.CENTER);
            textView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            addView(textView);

            textView = new TextView(getContext());
            textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
            textView.setTextColor(Color.parseColor("#247BEF"));
            textView.setText("重试");
            textView.setMaxLines(1);
            textView.setPadding(dipToPx(10), dipToPx(15), dipToPx(10), dipToPx(15));
            textView.setGravity(Gravity.CENTER);
            textView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            addView(textView);
            textView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (retry != null) {
                        retry.retryRequest();
                        retry = null;
                    }
                }
            });
        }
    }
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
            setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            TextView textView = new TextView(getContext());
            textView.setTextSize(15);
            textView.setTextColor(textColor);
            textView.setText(emptyString);
            textView.setGravity(Gravity.CENTER);
            textView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            addView(textView);
        }
    }
    //header
    private class HeaderRefresh extends LinearLayout {
        private ConstraintLayout contentLayout;
        private ProgressBar progressBar;
        private TextView textView;

        private int headerHeight;
        private boolean refreshing;
        private boolean readyRefresh;
        public HeaderRefresh(Context context) {
            this(context, null);
        }

        public HeaderRefresh(Context context, @Nullable AttributeSet attrs) {
            this(context, attrs, 0);
        }

        public HeaderRefresh(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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
            headerHeight = dipToPx(getContext(), 50);
            //将Header高度设置为0
            contentLayout = new ConstraintLayout(getContext());
            contentLayout.setId(99);
            addView(contentLayout, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0));
            ConstraintSet set = new ConstraintSet();
            set.constrainDefaultWidth(10, ConstraintSet.MATCH_CONSTRAINT);
            set.constrainDefaultHeight(10, headerHeight);

            textView = new TextView(getContext());
            textView.setTextColor(textColor);
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
            progressBar.setIndeterminateTintList(ColorStateList.valueOf(tintColor));
            contentLayout.addView(progressBar);
            set.connect(progressBar.getId(), ConstraintSet.LEFT, contentLayout.getId(), ConstraintSet.LEFT, 0);
            set.connect(progressBar.getId(), ConstraintSet.TOP, contentLayout.getId(), ConstraintSet.TOP, dipToPx(getContext(), 10));
            set.connect(progressBar.getId(), ConstraintSet.RIGHT, contentLayout.getId(), ConstraintSet.RIGHT, 0);
            set.connect(progressBar.getId(), ConstraintSet.BOTTOM, contentLayout.getId(), ConstraintSet.BOTTOM, dipToPx(getContext(), 10));
            set.constrainWidth(progressBar.getId(), dipToPx(getContext(), 25));
            set.constrainHeight(progressBar.getId(), dipToPx(getContext(), 25));

            set.applyTo(contentLayout);
            progressBar.setVisibility(View.INVISIBLE);
        }

        private int dipToPx(Context context, float pxValue) {
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
    private class FooterRefresh extends LinearLayout {
        private ConstraintLayout contentLayout;
        private ProgressBar progressBar;
        private TextView textView;

        private int footerHeight;
        private boolean refreshing;
        private boolean enable = true;
        private boolean hasData = true;
        public FooterRefresh(Context context) {
            this(context, null);
        }

        public FooterRefresh(Context context, @Nullable AttributeSet attrs) {
            this(context, attrs, 0);
        }

        public FooterRefresh(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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
            footerHeight = dipToPx(getContext(), 50);
            //将Header高度设置为0
            contentLayout = new ConstraintLayout(getContext());
            contentLayout.setId(99);
            addView(contentLayout, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, footerHeight));
            ConstraintSet set = new ConstraintSet();
            set.constrainDefaultWidth(10, ConstraintSet.MATCH_CONSTRAINT);
            set.constrainDefaultHeight(10, footerHeight);

            textView = new TextView(getContext());
            textView.setTextColor(textColor);
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
            progressBar.setIndeterminateTintList(ColorStateList.valueOf(tintColor));
            contentLayout.addView(progressBar);
            set.connect(progressBar.getId(), ConstraintSet.LEFT, contentLayout.getId(), ConstraintSet.LEFT, 0);
            set.connect(progressBar.getId(), ConstraintSet.TOP, contentLayout.getId(), ConstraintSet.TOP, dipToPx(getContext(), 10));
            set.connect(progressBar.getId(), ConstraintSet.RIGHT, contentLayout.getId(), ConstraintSet.RIGHT, 0);
            set.connect(progressBar.getId(), ConstraintSet.BOTTOM, contentLayout.getId(), ConstraintSet.BOTTOM, dipToPx(getContext(), 10));
            set.constrainWidth(progressBar.getId(), dipToPx(getContext(), 25));
            set.constrainHeight(progressBar.getId(), dipToPx(getContext(), 25));

            set.applyTo(contentLayout);
            progressBar.setVisibility(View.INVISIBLE);
        }

        private int dipToPx(Context context, float pxValue) {
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
    public static class FLTableViewBaseSection extends ViewHolder {
        protected int section;
        public static View PlaceholderView(ViewGroup parent, int height) {
            View view = new LinearLayout(parent.getContext());
            view.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height));
            return view;
        }
        public FLTableViewBaseSection(@NonNull View itemView) {
            super(itemView);
        }
    }
    public abstract static class FLTableViewSection<Binding extends ViewBinding> extends FLTableViewBaseSection {
        public final Binding sectionBinding;
        public FLTableViewSection(@NonNull Binding sectionBinding) {
            super(sectionBinding.getRoot());
            this.sectionBinding = sectionBinding;
        }
        public final Context getContext() {
            return sectionBinding.getRoot().getContext();
        }
        protected abstract void bindData(Binding sectionBinding, int section);
    }
    //baseViewHolder
    public abstract static class FLTableViewCell<Binding extends ViewBinding> extends ViewHolder {
        public final Binding cellBinding;
        protected int section;
        protected int index;
        private LinearLayout layout;
        public FLTableViewCell(@NonNull Binding cellBinding) {
            super(cellBinding.getRoot());
            this.cellBinding = cellBinding;
        }
        public final Context getContext() {
            return cellBinding.getRoot().getContext();
        }
        protected abstract void bindData(Binding cellBinding, int section, int index);
    }
}
