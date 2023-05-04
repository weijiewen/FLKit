package com.wjw.flkit.unit;

import android.os.Handler;
import java.util.Timer;
import java.util.TimerTask;

public class FLTimer {
    private Timer timer;
    public interface FLTimerListencener {
        void run();
    }
    public final void startTimer(long delay, long period, FLTimerListencener listencener) {
        stopTimer();
        timer = new Timer();
        timer.schedule(new FLTimerTask(listencener), delay, period);
    }
    public final void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }
    private class FLTimerTask extends TimerTask {
        private Handler handler = new Handler();
        private FLTimerListencener listencener;
        public FLTimerTask(FLTimerListencener listencener) {
            super();
            this.listencener = listencener;
        }
        @Override
        public void run() {
            if (listencener != null) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        listencener.run();
                    }
                });
            }
        }
    }
}
