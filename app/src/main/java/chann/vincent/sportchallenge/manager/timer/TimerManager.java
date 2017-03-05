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
    protected boolean isRunning = false;

    public TimerManager(final int timerCountMax, Context context, TimerListener listener) {
        this.timerCountMax = timerCountMax;
        this.context = context;
        this.timerListener = listener;

        handler = new Handler();
        handler.post(runnable = new Runnable() {
            @Override
            public void run() {
                isRunning = true;
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

    public int getTime() {
        return timerCount;
    }

    public int getMaxTimer() {
        return timerCountMax;
    }

    public void start() {
        if(runnable != null && !isRunning) {
            runnable.run();
        }
    }

    public void pause() {
        if(handler != null) {
            handler.removeCallbacks(runnable);
            isRunning = false;
        }
    }
}
