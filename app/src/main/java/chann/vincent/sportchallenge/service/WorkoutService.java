package chann.vincent.sportchallenge.service;

import android.app.*;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
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
    static protected int currentPageSelected = 0;
    static protected String currentTitle = "Custom title";
    static protected boolean musicEnabled = false;
    static protected boolean cheerEnabled = false;
    static protected boolean isTimerPlaying = false;

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
                showForegroundNotification(0, currentTitle);
            }
            else if (action.equals(NotificationConstants.ACTION.PREVIOUS)) {
                startActionPreviousPage();
            }
            else if (action.equals(NotificationConstants.ACTION.NEXT)) {
                startActionNextPage();
            }
            else if (action.equals(NotificationConstants.ACTION.PLAY)) {
                startActionPlay();
            }
            else if (action.equals(NotificationConstants.ACTION.UPDATE)) {
                startActionUpdate();
            }
            else if (action.equals(NotificationConstants.ACTION.PAUSE)) {
                startActionPause();
            }
            else if (action.equals(NotificationConstants.ACTION.STOP_FOREGROUND)) {
                startActionFinish();
            }
            else if (action.equals(NotificationConstants.ACTION.MUSIC)) {
                if (isMusicEnabled()) {
                    startActionMusic(false);
                }
                else {
                    startActionMusic(true);
                }
            }
            else if (action.equals(NotificationConstants.ACTION.CHEER)) {
                if (isCheerEnabled()) {
                    startActionCheer(false);
                }
                else {
                    startActionCheer(true);
                }
            }

        }
        return super.onStartCommand(intent, flags, startId);
    }

    /*
    Command actions
     */
    protected void startActionMusic(boolean enabled) {
        setMusicEnabled(enabled);
        startActionUpdate();
        if (listener != null) {
            listener.music(enabled);
        }
    }

    protected void startActionCheer(boolean enabled) {
        setCheerEnabled(enabled);
        startActionUpdate();
        if (listener != null) {
            listener.cheer(enabled);
        }
    }

    protected void startActionPlay() {
        // play music if music enabled
        if (musicEnabled) {
            playMusic();
        }
        else {
            pauseMusic();
        }

        // start timer
        startTimer();
        if (listener != null) {
            listener.play();
        }
    }

    protected void startActionPause() {
        pauseMusic();
        endCheer();
        pauseTimer();
        if (timerManager != null) {
            updateForegroundNotification(timerManager.getTime(), currentTitle);
        }
        if (listener != null) {
            listener.pause();
        }
    }

    protected void startActionUpdate() {
        if (isMusicEnabled() && isTimerPlaying()) {
            playMusic();
        }
        else {
            pauseMusic();
        }

        if (isTimerPlaying()) {
            startTimer();
        }
        else {
            pauseTimer();
        }

        if (!isCheerEnabled()) {
            endCheer();
        }

        if (listener != null) {
            listener.update();
        }
    }

    protected void startActionNextPage() {
        currentPageSelected++;
        if (listener != null) {
            if (isTimerPlaying()) {
                restartTimer();
            }
            else {
                resetTimer();
            }
            listener.next();
        }
    }

    protected void startActionPreviousPage() {
        currentPageSelected--;
        if (listener != null) {
            if (isTimerPlaying()) {
                restartTimer();
            }
            else {
                resetTimer();
            }
            listener.previous();
        }
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

    /*
    Service parameters
     */
    static public void setMusicEnabled(boolean enabled) {
        musicEnabled = enabled;
    }

    static public void setCheerEnabled(boolean enabled) {
        cheerEnabled = enabled;
    }

    static public boolean isMusicEnabled() {
        return musicEnabled;
    }

    static public boolean isCheerEnabled() {
        return cheerEnabled;
    }

    static public boolean isTimerPlaying() {
        return isTimerPlaying;
    }

    static public int getCurrentPageSelected() {
        return currentPageSelected;
    }

    static public int getPageCount() {
        return 2;
    }

    /*
    Foreground notification
     */
    protected void updateForegroundNotification(int timer, String title) {
        if (listener != null) {
            listener.timer(timer, timerManager.getMaxTimer());
        }
        showForegroundNotification(timer, title);
    }

    protected void showForegroundNotification(int timer, String title) {
        // remote views
        RemoteViews notificationView = new RemoteViews(this.getPackageName(), R.layout.notification);
        RemoteViews notificationBigView = new RemoteViews(this.getPackageName(), R.layout.notification_big);
        notificationBigView.setTextViewText(R.id.text_timer, "Timer : " + timer + " / " + timerManager.getMaxTimer());
        notificationBigView.setTextViewText(R.id.title, title);

        // actions
        notificationBigView.setOnClickPendingIntent(R.id.action_previous, NotificationConstants.getCustomPendingIntent(this, NotificationConstants.ACTION.PREVIOUS));
        notificationBigView.setOnClickPendingIntent(R.id.action_next, NotificationConstants.getCustomPendingIntent(this, NotificationConstants.ACTION.NEXT));
        notificationBigView.setOnClickPendingIntent(R.id.action_play, NotificationConstants.getCustomPendingIntent(this, NotificationConstants.ACTION.PLAY));
        notificationBigView.setOnClickPendingIntent(R.id.action_pause, NotificationConstants.getCustomPendingIntent(this, NotificationConstants.ACTION.PAUSE));
        notificationBigView.setOnClickPendingIntent(R.id.action_finish, NotificationConstants.getCustomPendingIntent(this, NotificationConstants.ACTION.STOP_FOREGROUND));
        notificationBigView.setOnClickPendingIntent(R.id.action_music_enabled, NotificationConstants.getCustomPendingIntent(this, NotificationConstants.ACTION.MUSIC));
        notificationBigView.setOnClickPendingIntent(R.id.action_music_disabled, NotificationConstants.getCustomPendingIntent(this, NotificationConstants.ACTION.MUSIC));
        notificationBigView.setOnClickPendingIntent(R.id.action_cheer_enabled, NotificationConstants.getCustomPendingIntent(this, NotificationConstants.ACTION.CHEER));
        notificationBigView.setOnClickPendingIntent(R.id.action_cheer_disabled, NotificationConstants.getCustomPendingIntent(this, NotificationConstants.ACTION.CHEER));

        // switch play / pause
        if (isTimerPlaying()) {
            notificationBigView.setViewVisibility(R.id.action_play, View.GONE);
            notificationBigView.setViewVisibility(R.id.action_pause, View.VISIBLE);
        }
        else {
            notificationBigView.setViewVisibility(R.id.action_play, View.VISIBLE);
            notificationBigView.setViewVisibility(R.id.action_pause, View.GONE);
        }

        // visibility next / previous
        if (getCurrentPageSelected() <= 0) {
            notificationBigView.setViewVisibility(R.id.action_previous, View.INVISIBLE);
            notificationBigView.setViewVisibility(R.id.action_next, View.VISIBLE);
        }
        else if (getCurrentPageSelected() >= getPageCount() - 1) {
            notificationBigView.setViewVisibility(R.id.action_previous, View.VISIBLE);
            notificationBigView.setViewVisibility(R.id.action_next, View.INVISIBLE);
        }
        else {
            notificationBigView.setViewVisibility(R.id.action_previous, View.VISIBLE);
            notificationBigView.setViewVisibility(R.id.action_next, View.VISIBLE);
        }

        // switch music enabled
        if (isMusicEnabled()) {
            notificationBigView.setViewVisibility(R.id.action_music_enabled, View.VISIBLE);
            notificationBigView.setViewVisibility(R.id.action_music_disabled, View.GONE);
        }
        else {
            notificationBigView.setViewVisibility(R.id.action_music_enabled, View.GONE);
            notificationBigView.setViewVisibility(R.id.action_music_disabled, View.VISIBLE);
        }

        // switch cheer enabled
        if (isCheerEnabled()) {
            notificationBigView.setViewVisibility(R.id.action_cheer_enabled, View.VISIBLE);
            notificationBigView.setViewVisibility(R.id.action_cheer_disabled, View.GONE);
        }
        else {
            notificationBigView.setViewVisibility(R.id.action_cheer_enabled, View.GONE);
            notificationBigView.setViewVisibility(R.id.action_cheer_disabled, View.VISIBLE);
        }

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

    protected void playMusic() {
        audioPlayerMusic.start();
    }

    protected void pauseMusic() {
        audioPlayerMusic.pause();
    }

    protected void playCheer(String filename, float volume) {
        endCheer();
        audioPlayerCheers = assetManager.getAudioPlayer("cheer/" + filename, this, null);
        audioPlayerCheers.start();
        audioPlayerCheers.setVolume(volume);
    }

    protected void playCheer(String filename) {
        playCheer(filename, 1f);
    }

    protected void endCheer() {
        if (audioPlayerCheers != null) {
            audioPlayerCheers.pause();
            audioPlayerCheers.stop();
            audioPlayerCheers.release();
            audioPlayerCheers = null;
        }
    }

    protected void nextCheer() {
        if (currentCheerPosition < (audioFileList.size() - 1)) {
            currentCheerPosition++;
        }
        playCheer(audioFileList.get(currentCheerPosition));
    }

    protected void previousCheer() {
        if (currentCheerPosition > 0) {
            currentCheerPosition--;
        }
        playCheer(audioFileList.get(currentCheerPosition));
    }

    /*
    Timer
     */
    protected void initTimer() {
        TimerListener timerListener = new TimerListener() {
            @Override
            public void progress(int timer) {
                updateForegroundNotification(timer, currentTitle);
                if (cheerEnabled && (timer % 25 == 5)) {
                    nextCheer();
                }
            }

            @Override
            public void onFinish() {
                startActionNextPage();
            }
        };
        timerManager = new TimerManager(45, this, timerListener);
        timerListener.progress(0);
    }

    protected void startTimer() {
        if (timerManager != null) {
            isTimerPlaying = true;
            timerManager.start();
        }
    }

    protected void pauseTimer() {
        if (timerManager != null) {
            isTimerPlaying = false;
            timerManager.pause();
        }
    }

    protected void resetTimer() {
        pauseTimer();
        initTimer();
    }

    protected void restartTimer() {
        pauseTimer();
        initTimer();
        startTimer();
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
