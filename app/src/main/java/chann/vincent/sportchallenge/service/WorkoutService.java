package chann.vincent.sportchallenge.service;

import android.app.*;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import java.util.ArrayList;
import java.util.List;

import chann.vincent.sportchallenge.R;
import fr.smartapps.lib.SMAAssetManager;
import fr.smartapps.lib.audio.SMAAudioPlayer;
import fr.smartapps.lib.audio.SMAAudioPlayerListener;

/**
 * Created by vincentchann on 12/02/2017.
 */

public class WorkoutService extends Service {

    private String TAG = "WorkoutService";
    protected WorkoutBinder workoutBinder = new WorkoutService.WorkoutBinder();
    protected WorkoutServiceListener listener = null;
    protected SMAAssetManager assetManager;
    protected SMAAudioPlayer audioPlayerMusic;
    protected SMAAudioPlayer audioPlayerCheers;
    protected List<String> audioFileList = new ArrayList<>();
    protected int currentCheerPosition = 0;

    /*
    Life cycle
     */
    @Override
    public void onCreate() {
        super.onCreate();
        assetManager = new SMAAssetManager(this);
        assetManager.setDefaultStorageType(SMAAssetManager.STORAGE_TYPE_ASSETS);
        assetManager.setExtensionDirectory("shaun_t/");
        audioFileList.add("media01.mp3");
        audioFileList.add("media02.mp3");
        audioFileList.add("media03.mp3");
        audioFileList.add("media04.mp3");
        audioFileList.add("media05.mp3");
        audioFileList.add("media06.mp3");
        audioFileList.add("media07.mp3");
    }

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
            startMusic("music01.mp3");
        }
    }

    protected void startActionPause() {
        if (listener != null) {
            listener.pause();
            pauseMusic();
        }
    }

    protected void startActionNext() {
        if (listener != null) {
            listener.next();
            if (currentCheerPosition < (audioFileList.size() - 1)) {
                currentCheerPosition++;
            }
            startCheer(audioFileList.get(currentCheerPosition));
        }
    }

    protected void startActionPrevious() {
        if (listener != null) {
            listener.previous();
            if (currentCheerPosition > 0) {
                currentCheerPosition--;
            }
            startCheer(audioFileList.get(currentCheerPosition));
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
    Audio
     */
    protected void startMusic(String filename, float volume) {
        audioPlayerMusic = assetManager.getAudioPlayer("music/" + filename, this, new SMAAudioPlayerListener() {
            @Override
            public void onSongProgress(int progress, int totalProgress) {
                // callback as long as the song is playing
            }

            @Override
            public void onSongFinish(int totalProgress) {
                // callback when the song is finished
            }
        });
        audioPlayerMusic.start();
        audioPlayerMusic.setVolume(volume);
    }

    protected void startMusic(String filename) {
        startMusic(filename, 0.15f);
    }

    protected void pauseMusic() {
        audioPlayerMusic.pause();
    }

    protected void startCheer(String filename, float volume) {
        if (audioPlayerCheers != null) {
            audioPlayerCheers.pause();
            audioPlayerCheers.stop();
            audioPlayerCheers.release();
            audioPlayerCheers = null;
        }

        audioPlayerCheers = assetManager.getAudioPlayer("cheer/" + filename, this, null);
        audioPlayerCheers.start();
        audioPlayerCheers.setVolume(volume);
    }

    protected void startCheer(String filename) {
        startCheer(filename, 1f);
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