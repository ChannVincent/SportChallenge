package chann.vincent.sportchallenge.service;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class Constants {
    public interface ACTION {
        public static String MAIN_ACTION = "chann.vincent.sportchallenge.action.main";
        public static String PREV_ACTION = "chann.vincent.sportchallenge.action.prev";
        public static String PLAY_ACTION = "chann.vincent.sportchallenge.action.play";
        public static String NEXT_ACTION = "chann.vincent.sportchallenge.action.next";
        public static String START_FOREGROUND_ACTION = "chann.vincent.sportchallenge.action.startforeground";
        public static String STOP_FOREGROUND_ACTION = "chann.vincent.sportchallenge.action.stopforeground";
    }

    public interface NOTIFICATION_ID {
        public static int FOREGROUND_SERVICE = 101;
    }

    static public PendingIntent getMainPendingIntent(Context context, Class activityStarted, String action) {
        Intent notificationIntent = new Intent(context, activityStarted);
        notificationIntent.setAction(action);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        return PendingIntent.getActivity(context, 0, notificationIntent, 0);
    }
}