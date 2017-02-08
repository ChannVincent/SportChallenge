package chann.vincent.sportchallenge.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;
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
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand() - action : " + intent.getAction());
        if (intent.getAction() != null) {
            startForegroundNotification(intent.getAction());
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy()");
    }

    /*
    Raw action
     */
    protected void startAction1() {
        if (listener != null) {
            listener.triggerAction1("action1");
        }
    }

    protected void startAction2() {
        if (listener != null) {
            listener.triggerAction2("action2");
        }
    }

    /*
    Service discuss with notification
     */
    public void startForegroundNotification(String action) {
        if (action.equals(Constants.ACTION.START_FOREGROUND_ACTION)) {
            Log.e(TAG, "Received Start Foreground Intent");

            Intent notificationIntent = new Intent(this, WorkoutServiceActivity.class);
            notificationIntent.setAction(Constants.ACTION.MAIN_ACTION);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

            Intent previousIntent = new Intent(this, WorkoutService.class);
            previousIntent.setAction(Constants.ACTION.PREV_ACTION);
            PendingIntent ppreviousIntent = PendingIntent.getService(this, 0, previousIntent, 0);

            Intent playIntent = new Intent(this, WorkoutService.class);
            playIntent.setAction(Constants.ACTION.PLAY_ACTION);
            PendingIntent pplayIntent = PendingIntent.getService(this, 0, playIntent, 0);

            Intent nextIntent = new Intent(this, WorkoutService.class);
            nextIntent.setAction(Constants.ACTION.NEXT_ACTION);
            PendingIntent pnextIntent = PendingIntent.getService(this, 0, nextIntent, 0);

            Bitmap icon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);

            Notification notification = new NotificationCompat.Builder(this)
                    .setContentTitle("Truiton Music Player")
                    .setTicker("Truiton Music Player")
                    .setContentText("My Music")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setLargeIcon(Bitmap.createScaledBitmap(icon, 128, 128, false))
                    .setContentIntent(pendingIntent)
                    .setOngoing(true)
                    .addAction(android.R.drawable.ic_media_previous, "Previous", ppreviousIntent)
                    .addAction(android.R.drawable.ic_media_play, "Play", pplayIntent)
                    .addAction(android.R.drawable.ic_media_next, "Next", pnextIntent)
                    .build();

            startForeground(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE, notification);
        }
        else if (action.equals(Constants.ACTION.PREV_ACTION)) {
            Log.e(TAG, "Clicked Previous");
            startAction1();
        }
        else if (action.equals(Constants.ACTION.PLAY_ACTION)) {
            Log.e(TAG, "Clicked Play");
            startAction2();
        }
        else if (action.equals(Constants.ACTION.NEXT_ACTION)) {
            Log.e(TAG, "Clicked Next");
        }
        else if (action.equals(Constants.ACTION.STOP_FOREGROUND_ACTION)) {
            Log.e(TAG, "Received Stop Foreground Intent");
            stopForeground(true);
            stopSelf();
        }
    }

    /*
    Service discuss with activity
     */
    @Override
    public IBinder onBind(Intent intent) {
        Log.e(TAG, "onBind()");
        return workoutBinder;
    }

    public class WorkoutBinder extends Binder {
        public WorkoutService getService() {
            return WorkoutService.this;
        }
    }

    public void setListener(WorkoutServiceListener listener) {
        this.listener = listener;
    }
}
