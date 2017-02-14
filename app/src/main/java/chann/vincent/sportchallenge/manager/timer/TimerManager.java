package chann.vincent.sportchallenge.manager.timer;

import android.content.Context;
import android.os.Handler;

/**
 * Created by vincentchann on 12/02/2017.
 */

public class TimerManager {

    protected int timerCount;
    protected int timerCountMax;
    protected Context context;
    protected Handler handler;
    protected Runnable runnable;
    protected TimerListener timerListener;

    public TimerManager(final int timerCountMax, Context context, TimerListener listener) {
        this.timerCountMax = timerCountMax;
        this.context = context;
        this.timerListener = listener;

        handler = new Handler();
        handler.post(runnable = new Runnable() {
            @Override
            public void run() {
                timerListener.progress(timerCount);
                handler.postDelayed(runnable, 1000);
                if (timerCount >= timerCountMax) {
                    timerListener.onFinish();
                    handler.removeCallbacks(runnable);
                }
                else {
                    timerCount++;
                }
            }
        });
        handler.removeCallbacks(runnable);
    }

    public int getMaxTimer() {
        return timerCountMax;
    }

    public void start() {
        if(runnable != null) {
            runnable.run();
        }
    }

    public void pause() {
        if(handler != null) {
            handler.removeCallbacks(runnable);
        }
    }
}
