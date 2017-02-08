package chann.vincent.sportchallenge.service;

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
}