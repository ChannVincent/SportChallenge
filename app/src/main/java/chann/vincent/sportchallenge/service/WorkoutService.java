package chann.vincent.sportchallenge.service;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import chann.vincent.sportchallenge.R;

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
        startForegroundNotification();
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

    public void stopService() {
        Toast.makeText(this, "stop WorkoutService", Toast.LENGTH_SHORT).show();
        stopForeground(true);
        stopSelf();
    }

    public void setListener(WorkoutServiceListener listener) {
        this.listener = listener;
    }

    /*
    TODO http://www.truiton.com/2014/10/android-foreground-service-example/
     */
    public void startForegroundNotification() {
        Notification notification = new Notification.Builder(this)
                .setContentTitle("content title")
                .setContentText("content text")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setTicker("thicker text")
                .build();

        startForeground(1, notification);
    }

    public class WorkoutBinder extends Binder {
        public WorkoutService getService() {
            return WorkoutService.this;
        }
    }
}
