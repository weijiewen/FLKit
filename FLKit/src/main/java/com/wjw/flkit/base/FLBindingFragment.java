package com.wjw.flkit.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewbinding.ViewBinding;

public abstract class FLBindingFragment<Binding extends ViewBinding> extends Fragment {
    protected Binding fragmentBinding;
    protected abstract Binding getBinding(LayoutInflater inflater, ViewGroup viewGroup);
    protected abstract void didLoad();
    @Nullable
    public final FLBaseActivity getBaseActivity() {
        return (FLBaseActivity) getActivity();
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentBinding = getBinding(inflater, container);
        didLoad();
        return fragmentBinding.getRoot();
    }
    public final int dipToPx(float pxValue) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (pxValue * scale + 0.5f);
    }
}
