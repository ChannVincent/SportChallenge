package chann.vincent.sportchallenge.service;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by vincentchann on 12/02/2017.
 */

public class NotificationConstants {

    public interface ACTION {
        public static String PREVIOUS = "chann.vincent.sportchallenge.action.prev";
        public static String NEXT = "chann.vincent.sportchallenge.action.next";
        public static String PLAY = "chann.vincent.sportchallenge.action.play";
        public static String UPDATE = "chann.vincent.sportchallenge.action.update";
        public static String PAUSE = "chann.vincent.sportchallenge.action.pause";

        public static String START_FOREGROUND = "chann.vincent.sportchallenge.action.startforeground";
        public static String STOP_FOREGROUND = "chann.vincent.sportchallenge.action.stopforeground";
    }

    public interface NOTIFICATION_ID {
        public static int FOREGROUND_SERVICE = 102;
    }

    static int newId = 0;
    static public PendingIntent getCustomPendingIntent(Context context, String action) {
        Intent intent = new Intent(context, NotificationReceiver.class);
        intent.putExtra("action", action);
        return PendingIntent.getBroadcast(context, newId++, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public static class NotificationReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            String action = bundle.getString("action");
            if (action != null) {
                Intent intentWorkoutService = new Intent(context, WorkoutService.class);
                intentWorkoutService.setAction(action);
                context.startService(intentWorkoutService);
            }
        }
    }
}
