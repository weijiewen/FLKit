package com.wjw.flkitexample.network.respon;


public class ObjectRespon<Obj> extends ApiRespon {
    Obj data;

    public void setData(Obj data) {
        this.data = data;
    }

    public Obj getData() {
        return data;
    }
}
