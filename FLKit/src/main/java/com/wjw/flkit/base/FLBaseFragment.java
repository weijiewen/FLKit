package com.wjw.flkit.base;

import androidx.fragment.app.Fragment;
import androidx.viewbinding.ViewBinding;

public abstract class FLBaseFragment<T extends ViewBinding> extends Fragment {
    T binding;

}
