package chann.vincent.sportchallenge.intent_service;

import android.app.IntentService;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by vincentchann on 03/02/2017.
 */

public class WorkoutIntentService extends IntentService {

    static private String TAG = "WorkoutIntentService";
    public static final String ACTION_RESP = "chann.vincent.sportchallenge.intent_service.WorkoutIntentService";

    public WorkoutIntentService() {
        super(TAG);
        Log.e(TAG, "constructor");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.e(TAG, "onHandleIntent() " + intent.getAction());
        Log.e(TAG, "send broadcast() " + ACTION_RESP);

        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(ACTION_RESP);
        sendBroadcast(broadcastIntent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "onCreate()");
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        Log.e(TAG, "onStart() " + intent.getAction());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand() " + intent.getAction());
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy()");
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.e(TAG, "onBind() " + intent.getAction());
        return super.onBind(intent);
    }
}
