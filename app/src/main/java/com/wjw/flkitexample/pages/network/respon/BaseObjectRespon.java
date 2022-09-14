package com.wjw.flkitexample.pages.network.respon;

import com.wjw.flkitexample.pages.network.ApiRespon;

public class BaseObjectRespon<Obj> extends ApiRespon {
    Obj data;

    public void setData(Obj data) {
        this.data = data;
    }

    public Obj getData() {
        return data;
    }
}
