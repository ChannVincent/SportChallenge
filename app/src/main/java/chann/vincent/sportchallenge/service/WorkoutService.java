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
import chann.vincent.sportchallenge.manager.timer.TimerListener;
import chann.vincent.sportchallenge.manager.timer.TimerManager;
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
    protected TimerManager timerManager;
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
        initMusic("music01.mp3");
        initTimer();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        Log.e(TAG, "onStartCommand() - action : " + action);
        if (action != null) {
            if (action.equals(NotificationConstants.ACTION.START_FOREGROUND)) {
                startActionForegroundNotification(0);
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
        startMusic();
        startTimer();
    }

    protected void startActionPause() {
        if (listener != null) {
            listener.pause();
        }
        pauseMusic();
        pauseTimer();
    }

    protected void startActionNext() {
        if (listener != null) {
            listener.next();
        }
        if (currentCheerPosition < (audioFileList.size() - 1)) {
            currentCheerPosition++;
        }
        startCheer(audioFileList.get(currentCheerPosition));
    }

    protected void startActionPrevious() {
        if (listener != null) {
            listener.previous();
        }
        if (currentCheerPosition > 0) {
            currentCheerPosition--;
        }
        startCheer(audioFileList.get(currentCheerPosition));
    }

    protected void startActionFinish() {
        if (audioPlayerMusic != null) {
            audioPlayerMusic.pause();
        }
        if (audioPlayerCheers != null) {
            audioPlayerCheers.pause();
        }
        pauseTimer();
        stopForeground(true);
        stopSelf();
    }

    protected void startActionUpdateTimer(int timer) {
        if (listener != null) {
            listener.timer(timer, timerManager.getMaxTimer());
        }
        startActionForegroundNotification(timer);
    }

    protected void startActionForegroundNotification(int timer) {
        // remote views
        RemoteViews notificationView = new RemoteViews(this.getPackageName(), R.layout.notification);
        RemoteViews notificationBigView = new RemoteViews(this.getPackageName(), R.layout.notification_big);
        notificationBigView.setTextViewText(R.id.text, "timer : " + timer);

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

        /*
        // glide image loading into notification
        // TODO https://futurestud.io/tutorials/glide-loading-images-into-notifications-and-appwidgets
        NotificationTarget notificationTarget = new NotificationTarget(this,
                notificationBigView,
                R.id.image,
                notification,
                NotificationConstants.NOTIFICATION_ID.FOREGROUND_SERVICE);
        Glide.with(this.getApplicationContext()) // safer!
                .load(new SMAFile("gif/power_jump.gif", assetManager))
                .asBitmap()
                .into(notificationTarget);
                */
    }

    /*
    Audio
     */
    protected void initMusic(String filename, float volume) {
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
        audioPlayerMusic.setVolume(volume);
    }

    protected void initMusic(String filename) {
        initMusic(filename, 0.15f);
    }

    protected void startMusic() {
        audioPlayerMusic.start();
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
    Timer
     */
    protected void initTimer() {
        timerManager = new TimerManager(45, this, new TimerListener() {
            @Override
            public void progress(int timer) {
                startActionUpdateTimer(timer);
            }

            @Override
            public void onFinish() {

            }
        });
    }

    protected void startTimer() {
        if (timerManager != null) {
            timerManager.start();
        }
    }

    protected void pauseTimer() {
        if (timerManager != null) {
            timerManager.pause();
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
