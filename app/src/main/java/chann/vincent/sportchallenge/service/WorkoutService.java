package chann.vincent.sportchallenge.service;

import android.app.*;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import chann.vincent.sportchallenge.R;

/**
 * Created by vincentchann on 12/02/2017.
 */

public class WorkoutService extends Service {

    private String TAG = "WorkoutService";
    protected WorkoutBinder workoutBinder = new WorkoutService.WorkoutBinder();
    protected WorkoutServiceListener listener = null;

    /*
    Life cycle
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        Log.e(TAG, "onStartCommand() - action : " + action);
        if (action != null) {
            if (action.equals(NotificationConstants.ACTION.START_FOREGROUND)) {
                startActionForegroundNotification();
            }
            else if (action.equals(NotificationConstants.ACTION.PREVIOUS)) {
                startActionPrevious();
            }
            else if (action.equals(NotificationConstants.ACTION.NEXT)) {
                startActionNext();
            }
            else if (action.equals(NotificationConstants.ACTION.PLAY)) {
                startActionPlay();
            }
            else if (action.equals(NotificationConstants.ACTION.PAUSE)) {
                startActionPause();
            }
            else if (action.equals(NotificationConstants.ACTION.STOP_FOREGROUND)) {
                startActionFinish();
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    /*
    Actions
     */
    protected void startActionPlay() {
        if (listener != null) {
            listener.play();
        }
    }

    protected void startActionPause() {
        if (listener != null) {
            listener.pause();
        }
    }

    protected void startActionNext() {
        if (listener != null) {
            listener.next();
        }
    }

    protected void startActionPrevious() {
        if (listener != null) {
            listener.previous();
        }
    }

    protected void startActionFinish() {
        stopForeground(true);
        stopSelf();
    }

    protected void startActionForegroundNotification() {
        // remote views
        RemoteViews notificationView = new RemoteViews(this.getPackageName(), R.layout.notification);
        RemoteViews notificationBigView = new RemoteViews(this.getPackageName(), R.layout.notification_big);

        // actions
        notificationBigView.setOnClickPendingIntent(R.id.action_previous, NotificationConstants.getCustomPendingIntent(this, NotificationConstants.ACTION.PREVIOUS));
        notificationBigView.setOnClickPendingIntent(R.id.action_next, NotificationConstants.getCustomPendingIntent(this, NotificationConstants.ACTION.NEXT));
        notificationBigView.setOnClickPendingIntent(R.id.action_play, NotificationConstants.getCustomPendingIntent(this, NotificationConstants.ACTION.PLAY));
        notificationBigView.setOnClickPendingIntent(R.id.action_pause, NotificationConstants.getCustomPendingIntent(this, NotificationConstants.ACTION.PAUSE));
        notificationBigView.setOnClickPendingIntent(R.id.action_finish, NotificationConstants.getCustomPendingIntent(this, NotificationConstants.ACTION.STOP_FOREGROUND));

        // create notification
        Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setCustomContentView(notificationView)
                .setCustomBigContentView(notificationBigView)
                .build();

        // show notification
        startForeground(NotificationConstants.NOTIFICATION_ID.FOREGROUND_SERVICE, notification);
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
