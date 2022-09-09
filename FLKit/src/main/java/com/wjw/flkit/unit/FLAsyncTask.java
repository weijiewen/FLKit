package com.wjw.flkit.unit;

import android.os.AsyncTask;

public class FLAsyncTask extends AsyncTask<Integer, Integer, Integer> {
    public interface FLAsyncCallback {
        void doInBack();
        void doInMain();
    }
    private FLAsyncCallback callback;
    public final static void start(FLAsyncCallback callback) {
        FLAsyncTask task = new FLAsyncTask();
        task.callback = callback;
        task.execute(1, 1, 1);
    }
    @Override
    protected final Integer doInBackground(Integer... integers) {
        callback.doInBack();
        return 1;
    }

    @Override
    protected final void onPostExecute(Integer integer) {
        super.onPostExecute(integer);
        callback.doInMain();
    }
}
