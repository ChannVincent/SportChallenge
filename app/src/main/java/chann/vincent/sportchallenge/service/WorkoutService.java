package chann.vincent.sportchallenge.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by vincentchann on 03/02/2017.
 */

public class WorkoutService extends Service {

    private String TAG = "WorkoutService";
    protected WorkoutBinder workoutBinder = new WorkoutBinder();
    protected WorkoutServiceListener listener = null;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "onCreate()");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand()");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy()");
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.e(TAG, "onUnbind()");
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
        Log.e(TAG, "onRebind()");
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.e(TAG, "onBind()");
        return workoutBinder;
    }

    protected int stack1 = 0;
    public void startAction1() {
        Toast.makeText(this, "WorkoutService - action1", Toast.LENGTH_SHORT).show();
        if (listener != null) {
            stack1++;
            listener.triggerAction1("stack action 1 : " + stack1);
        }
    }

    protected int stack2 = 0;
    public void startAction2() {
        Toast.makeText(this, "WorkoutService - action2", Toast.LENGTH_SHORT).show();
        if (listener != null) {
            stack2++;
            listener.triggerAction2("stack action 2 : " + stack2);
        }
    }

    public void setListener(WorkoutServiceListener listener) {
        this.listener = listener;
    }

    public class WorkoutBinder extends Binder {
        public WorkoutService getService() {
            return WorkoutService.this;
        }
    }
}
