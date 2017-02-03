package chann.vincent.sportchallenge.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by vincentchann on 03/02/2017.
 */

public class WorkoutService extends Service {

    private String TAG = "WorkoutService";
    public static final String ACTION_RESP = "chann.vincent.sportchallenge.service.WorkoutService";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "onCreate()");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand() " + intent.getAction());

        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(ACTION_RESP);
        sendBroadcast(broadcastIntent);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy()");
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.e(TAG, "onUnbind() " + intent.getAction());
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
        Log.e(TAG, "onRebind() " + intent.getAction());
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.e(TAG, "onBind() " + intent.getAction());
        return null;
    }
}
