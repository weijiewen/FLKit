package com.wjw.flkit.ui;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;

import java.util.ArrayList;
import java.util.List;

public class FLTableView extends RecyclerView {
    /**
     * 控件已经创建, 子类重写进行全局配置
     * @param context
     */
    public void didLoad(@NonNull Context context) {
        setLayoutManager(new LinearLayoutManager(context));
    }

    /**
     * 需要展示错误提示，子类重写可自定义提示
     * @param message 错误信息
     */
    public void tableNeedShowToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT);
    }

    /**
     * 创建cell (viewHolder)
     * @param creatCell new CreatCell对象
     */
    public final void setCreatCell(CreatCell creatCell) {
        setCreatCell("暂无数据", creatCell);
    }

    /**
     * 创建cell (viewHolder)
     * @param empty 没有数据文字，配置 configEmpty 会覆盖此值
     * @param creatCell new CreatCell对象
     */
    public final void setCreatCell(String empty, CreatCell creatCell) {
        this.empty = empty;
        this.creatCell = creatCell;
    }

    /**
     * 创建section (分组头部)
     * @param creatSection new CreatSection对象
     */
    public final void setCreatSection(CreatSection creatSection) {
        this.creatSection = creatSection;
    }

    /**
     * 请求失败刷新方法，会重载列表，显示error和重试按钮，有数据时自行提示toast
     * @param error 要显示的错误信息
     * @param retry 点击重试回调
     */
    public final void reloadData(String error, Retry retry) {
        this.retry = retry;
        reloadData(error, true, null);
    }

    /**
     * 请求成功刷新方法，会重载列表，addFooterRefresh后 hasMore传true 上拉加载可触发，传入false 上拉加载不可触发并显示setFooterNoMoreString文字
     * @param hasMore
     */
    public final void reloadData(boolean hasMore) {
        reloadData(null, hasMore, null);
    }

    /**
     * 重载列表
     */
    public final void reloadData() {
        reloadData(null, true, true);
    }

    /**
     * 更新列表，不会重新加载，删除数据时用 reloadData
     */
    public final void updateData() {
        reloadData(null, true, false);
    }

    /**
     * 添加下拉刷新
     * @param action 下拉刷新触发回调
     */
    public final void addHeaderRefresh(RefreshInterface action) {
        addHeaderRefresh(new HeaderRefreshView(getContext()), action);
    }

    /**
     * 添加下拉刷新
     * @param header 刷新控件
     * @param action 下拉刷新触发回调
     */
    public final void addHeaderRefresh(FLHeaderRefreshView header, RefreshInterface action) {
        headerRefresh = header;
        headerAction = action;
    }

    /**
     * 添加上拉加载
     * @param action 上拉加载触发回调
     */
    public final void addFooterRefresh(RefreshInterface action) {
        addFooterRefresh(new FooterRefreshView(getContext()), action);
    }

    /**
     * 添加上拉加载
     * @param footer 刷新控件
     * @param action 上拉加载触发回调
     */
    public final void addFooterRefresh(FLFooterRefreshView footer, RefreshInterface action) {
        footerRefresh = footer;
        footerAction = action;
    }

    /**
     * 显示loading，调用reloadData取消loading
     */
    public final void startLoading() {
        if (!isRefreshing()) {
            if (configLoading != null) {
                loadingView = configLoading.getLoadingView(getContext());
            }
            if (loadingView == null) {
                loadingView = new LoadingView(getContext());
            }
            mainCount = 0;
            reloadAdapter();
        }
    }

    /**
     * 设置header
     * @param headerView header视图
     */
    public final void setHeaderView(View headerView) {
        this.headerView = headerView;
    }

    /**
     * 配置自定义loading
     * @param configLoading 配置回调
     */
    public final void setConfigLoading(ConfigLoading configLoading) {
        this.configLoading = configLoading;
    }

    /**
     * 配置自定义empty空数据
     * @param configEmpty 配置回调
     */
    public final void setConfigEmpty(ConfigEmpty configEmpty) {
        this.configEmpty = configEmpty;
    }
    /**
     * 配置自定义error错误
     * @param configErrorView 配置回调
     */
    public final void setConfigErrorView(ConfigError configErrorView) {
        this.configError = configErrorView;
    }

    /**
     * 设置上拉加载没有数据文字
     * @param footerNoMoreString 没有数据文字
     */
    public final void setFooterNoMoreString(String footerNoMoreString) {
        this.footerNoMoreString = footerNoMoreString;
    }

    /**
     * 设置所有loading颜色
     * @param color 颜色
     */
    public final void setLoadingTintColor(int color) {
        this.tintColor = color;
    }

    /**
     * 设置所有文字颜色
     * @param textColor 颜色
     */
    public final void setTextColor(int textColor) {
        this.textColor = textColor;
    }


    private View headerView;

    public interface ConfigLoading {
        View getLoadingView(Context context);
    }
    private ConfigLoading configLoading;

    public interface ConfigEmpty {
        View getEmptyView(Context context);
    }
    private ConfigEmpty configEmpty;

    public interface ConfigError {
        FLBindingErrorView getErrorView(Context context, String error);
    }
    private ConfigError configError;
    public static abstract class FLBindingErrorView<Binding extends ViewBinding> extends LinearLayout {
        protected Binding errorBinding;
        public FLBindingErrorView(Context context, String error) {
            super(context);
            setLayoutParams(new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            errorBinding = getBinding();
            getReloadView().setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (retry != null) {
                        retry.retryRequest();
                        retry = null;
                    }
                }
            });
        }
        protected abstract Binding getBinding();
        protected abstract View getReloadView();
        private Retry retry;
    }

    public interface CreatSection<Head extends FLTableViewBaseSection, Foot extends FLTableViewBaseSection> {
        int sectionCount();
        @Nullable
        Head getHeader(@NonNull ViewGroup parent);
        @Nullable
        Foot getFooter(@NonNull ViewGroup parent);
    }
    private CreatSection creatSection;

    public interface CreatCell<T extends FLCell> {
        int itemCount(int section);
        T getCell(@NonNull ViewGroup parent);
    }
    public static class FLTableViewBaseSection extends ViewHolder {
        protected int section;
        public static FLTableViewBaseSection placeholderView(ViewGroup parent, int height) {
            View view = new LinearLayout(parent.getContext());
            view.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height));
            return new FLTableViewBaseSection(view);
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
    private CreatCell creatCell;
    //baseViewHolder
    public abstract static class FLCell extends ViewHolder {
        protected int section;
        protected int index;
        private View cellView;
        private Context context;
        public FLCell(@NonNull View cellView) {
            super(cellView);
            cellView.setBackgroundColor(Color.TRANSPARENT);
            this.cellView = cellView;
            context = cellView.getContext();
        }
        public FLCell(@NonNull ViewGroup viewGroup, int layoutResId) {
            super(LayoutInflater.from(viewGroup.getContext())
                    .inflate(layoutResId, viewGroup, false));
            context = viewGroup.getContext();
        }
        public final Context getContext() {
            return context;
        }
        protected void addChildClickViewIds(View.OnClickListener listener, int... args) {
            for (int id : args) {
                cellView.findViewById(id).setOnClickListener(listener);
            }
        };
        protected abstract void bindData(int section, int index);
    }
    public abstract static class FLBindingCell<Binding extends ViewBinding> extends FLCell {
        public final Binding cellBinding;
        public FLBindingCell(@NonNull Binding cellBinding) {
            super(cellBinding.getRoot());
            this.cellBinding = cellBinding;
        }
    }
    private int tintColor = Color.parseColor("#247BEF");
    private int textColor = Color.parseColor("#BBBBBB");
    public interface Retry {
        void retryRequest();
    }
    private Retry retry;
    public interface RefreshInterface {
        void enterRefreshing();
    }
    private FLHeaderRefreshView headerRefresh;
    private RefreshInterface headerAction;
    private FLFooterRefreshView footerRefresh;
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
    private View loadingView;
    private View errorView;
    private View emptyView;
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
                    if (creatSection != null) {
                        count += 2;
                    }
                }
                itemCounts.add(itemCount);
            }
        }

        boolean reload = isReload == null ? mainCount >= count : isReload.booleanValue();
        if (!reload && (loadingView != null || errorView != null || emptyView != null)) {
            reload = true;
        }
        loadingView = null;
        errorView = null;
        emptyView = null;
        mainCount = count;
        if (count == 0) {
            if (error != null && !error.isEmpty()) {
                if (configError != null) {
                    FLBindingErrorView tableErrorView = configError.getErrorView(getContext(), error);
                    tableErrorView.retry = retry;
                    errorView = tableErrorView;
                }
                if (errorView == null) {
                    errorView = new ErrorView(getContext(), error);
                }
            }
            else {
                if (configEmpty != null) {
                    emptyView = configEmpty.getEmptyView(getContext());
                }
                if (emptyView == null) {
                    emptyView = new EmptyView(getContext(), empty);
                }
            }
            reloadAdapter();
            return;
        }
        else if (error != null && !error.isEmpty()) {
            tableNeedShowToast(error);
        }
        else {
            if (adapter == null || reload) {
                reloadAdapter();
            }
            else {
                adapter.notifyDataSetChanged();
            }
        }
    }
    private void reloadAdapter() {
        adapter = new Adapter() {
            @Override
            public int getItemCount() {
                int count = mainCount;
                startIndex = 0;
                if (headerView != null) {
                    count += 1;
                    startIndex += 1;
                }
                if (loadingView != null || errorView != null) {
                    count += 1;
                    startIndex += 1;
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
                    if (headerRefresh != null) {
                        if (position == 0) {
                            return ViewType.RefreshHeader.value;
                        } else if (headerView != null && position == 1) {
                            return ViewType.HeaderView.value;
                        }
                    }
                    else if (position == 0 && headerView != null) {
                        return ViewType.HeaderView.value;
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
                else if (viewType == ViewType.HeaderView.value) {
                    return new PlaceHolderViewHolder(headerView);
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
                    FLCell cell = (FLCell) holder;
                    cell.section = section;
                    cell.index = cellIndex;
                    cell.bindData(section, cellIndex);
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
        didLoad(context);
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

    int mLastX, mLastY;
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                getParent().requestDisallowInterceptTouchEvent(true);
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                int deltaX = x - mLastX;
                if (Math.abs(deltaX) > dipToPx(15)) {
                    getParent().requestDisallowInterceptTouchEvent(false);
                }
                break;
            }
            case MotionEvent.ACTION_UP: {
                getParent().requestDisallowInterceptTouchEvent(false);
                break;
            }
            default:
                break;
        }
        mLastX = x;
        mLastY = y;
        return super.dispatchTouchEvent(event);
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
        Cell(99992),
        HeaderView(99991);

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
                case 99991:
                    return HeaderView;
                default:
                    return null;
            }
        }
    }
    private class PlaceholdView extends LinearLayout {
        public PlaceholdView(Context context) {
            super(context);
            setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0));
        }
    }
    private class LoadingView extends LinearLayout {
        public LoadingView(Context context) {
            super(context);
            setGravity(Gravity.CENTER);
            setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            ProgressBar progressBar = new ProgressBar(context);
            progressBar.setLayoutParams(new LayoutParams(dipToPx(35), dipToPx(35)));
            progressBar.setIndeterminateTintList(ColorStateList.valueOf(tintColor));
            addView(progressBar);
        }
    }
    private class ErrorView extends LinearLayout {
        public ErrorView(Context context, String error) {
            super(context);
            setOrientation(VERTICAL);
            setGravity(Gravity.CENTER);
            setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            TextView textView = new TextView(getContext());
            textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
            textView.setTextColor(textColor);
            textView.setText(error);
            textView.setMaxLines(99);
            textView.setPadding(dipToPx(70), 0, dipToPx(70), 0);
            textView.setGravity(Gravity.CENTER);
            textView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            addView(textView);

            textView = new TextView(getContext());
            textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
            textView.setTextColor(Color.parseColor("#247BEF"));
            textView.setText("重试");
            textView.setMaxLines(1);
            textView.setPadding(dipToPx(10), dipToPx(15), dipToPx(10), dipToPx(15));
            textView.setGravity(Gravity.CENTER);
            textView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
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
            setOrientation(VERTICAL);
            setGravity(Gravity.CENTER);
            setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

            TextView textView = new TextView(getContext());
            textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
            textView.setTextColor(textColor);
            textView.setText(emptyString);
            textView.setGravity(Gravity.CENTER);

            LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(dipToPx(15), dipToPx(0), dipToPx(15), dipToPx(0));

            addView(textView, layoutParams);
            reloadHeight();
        }
        private void reloadHeight() {
            if (headerView != null) {
                View thisView = this;
                FLTableView.this.post(new Runnable() {
                    @Override
                    public void run() {
                        int height = FLTableView.this.getMeasuredHeight();
                        int headerHeight = headerView.getMeasuredHeight();
                        if (height == 0 || headerHeight == 0) {
                            reloadHeight();
                        }
                        else {
                            ViewGroup.LayoutParams layoutParams = thisView.getLayoutParams();
                            layoutParams.height = height - headerHeight;
                            thisView.setLayoutParams(layoutParams);
                        }
                    }
                });
            }
        }
    }
    public abstract class FLHeaderRefreshView extends LinearLayout {
        private int headerHeight;
        private boolean refreshing;
        private boolean readyRefresh;

        public FLHeaderRefreshView(Context context) {
            this(context, null);
        }

        public FLHeaderRefreshView(Context context, @Nullable AttributeSet attrs) {
            this(context, attrs, 0);
        }

        public FLHeaderRefreshView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
            this(context, attrs, defStyleAttr, 0);
        }

        public FLHeaderRefreshView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
            super(context, attrs, defStyleAttr, defStyleRes);
            headerHeight = headerHeight();
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
            setLayoutParams(layoutParams);
        }

        public abstract int headerHeight();
        public abstract void refreshReadyChange(boolean ready);
        public abstract void refreshStateChange(boolean isRefreshing);
        public void onMove(float offSet, float sumOffSet) {
            if (getVisibleHeight() > 0 || offSet > 0) {
                setVisibleHeight((int) offSet + getVisibleHeight());
                if (!refreshing) { // 未处于刷新状态，更新文字
                    if (getVisibleHeight() > headerHeight) {
                        if (!readyRefresh) {
                            readyRefresh = true;
                            refreshReadyChange(true);
                        }
                    } else {
                        if (readyRefresh) {
                            readyRefresh = false;
                            refreshReadyChange(false);
                        }
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
            smoothScrollTo(refreshing ? headerHeight : 0);
            this.refreshStateChange(refreshing);

            if (readyRefresh) {
                readyRefresh = false;
                refreshReadyChange(false);
            }
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
            ViewGroup.LayoutParams layoutParams = getLayoutParams();
            layoutParams.height = height;
            setLayoutParams(layoutParams);
        }
        private int getVisibleHeight() { return getLayoutParams().height; }
        private void enterRefresh() { changeRefreshing(true); }
        public void endRefresh() {
            changeRefreshing(false);
        }
    }
    //header
    private class HeaderRefreshView extends FLHeaderRefreshView {
        private ConstraintLayout contentLayout;
        private ProgressBar progressBar;
        private TextView textView;

        public HeaderRefreshView(Context context) {
            super(context);
            createView();
        }

        @Override
        public int headerHeight() {
            return dipToPx(getContext(), 50);
        }

        @Override
        public void refreshReadyChange(boolean ready) {
            textView.setText(ready ? "松开刷新" : "下拉刷新");
        }

        @Override
        public void refreshStateChange(boolean isRefreshing) {
            if (isRefreshing) {
                textView.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);
            }
            else {
                textView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
            }
        }

        @SuppressLint("ResourceType")
        private void createView() {
            //将Header高度设置为0
            contentLayout = new ConstraintLayout(getContext());
            contentLayout.setId(99);
            addView(contentLayout, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            ConstraintSet set = new ConstraintSet();
            set.constrainDefaultWidth(10, ConstraintSet.MATCH_CONSTRAINT);
            set.constrainDefaultHeight(10, ConstraintSet.MATCH_CONSTRAINT);

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
    }

    public abstract class FLFooterRefreshView extends LinearLayout {
        public abstract int footerHeight();
        public abstract void refreshHasDataChange(boolean hasData);
        public abstract void refreshStateChange(boolean isRefreshing);
        private int footerHeight;
        private boolean refreshing;
        private boolean enable = true;
        private boolean hasData = true;
        private void changeRefreshing(boolean refreshing) {
            if (this.refreshing == refreshing) {
                return;
            }
            this.refreshing = refreshing;
            refreshStateChange(refreshing);
        }

        private void setVisibleHeight(int height) {
            if (getLayoutParams().height == height) {
                return;
            }
            if (height < 0) height = 0;
            LayoutParams layoutParams = (LayoutParams) getLayoutParams();
            layoutParams.height = height;
            setLayoutParams(layoutParams);
        }
        private void enterRefresh() { changeRefreshing(true); }

        public void endRefresh(boolean hasData) {
            changeRefreshing(false);
            setHasData(hasData);
        }

        public void setEnable(boolean enable) {
            this.enable = enable;
            setVisibleHeight(enable ? footerHeight : 0);
        }
        private void setHasData(boolean hasData) {
            if (this.hasData != hasData) {
                this.hasData = hasData;
                this.refreshHasDataChange(hasData);
            }
        }
        public boolean getHasData() { return hasData; }
        public FLFooterRefreshView(Context context) {this(context, null);}

        public FLFooterRefreshView(Context context, @Nullable AttributeSet attrs) {this(context, attrs, 0);}

        public FLFooterRefreshView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {this(context, attrs, defStyleAttr, 0);}

        public FLFooterRefreshView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
            super(context, attrs, defStyleAttr, defStyleRes);
            footerHeight = footerHeight();
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, footerHeight);
            setLayoutParams(layoutParams);
        }
    }
    private String footerNoMoreString = "我已经到底了";
    //footer
    private class FooterRefreshView extends FLFooterRefreshView {
        private ConstraintLayout contentLayout;
        private ProgressBar progressBar;
        private TextView textView;

        @Override
        public int footerHeight() {
            return dipToPx(getContext(), 50);
        }

        @Override
        public void refreshHasDataChange(boolean hasData) {
            textView.setText(hasData ? "上拉加载更多" : footerNoMoreString);
        }

        @Override
        public void refreshStateChange(boolean isRefreshing) {
            if (isRefreshing) {
                textView.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);
            }
            else {
                textView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
            }
        }

        public FooterRefreshView(Context context) {
            super(context);
            creatView();
        }

        @SuppressLint("ResourceType")
        private void creatView() {
            contentLayout = new ConstraintLayout(getContext());
            contentLayout.setId(99);
            addView(contentLayout, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            ConstraintSet set = new ConstraintSet();
            set.constrainDefaultWidth(10, ConstraintSet.MATCH_CONSTRAINT);
            set.constrainDefaultHeight(10, ConstraintSet.MATCH_CONSTRAINT);

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
    }
}
