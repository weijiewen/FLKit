package com.wjw.flkitexample.network;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public abstract class ApiLoader {

    protected final <T> Observable<T> observe(Observable<T> observable) {
        return observable
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
