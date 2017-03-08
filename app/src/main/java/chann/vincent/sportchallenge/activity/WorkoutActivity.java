package chann.vincent.sportchallenge.activity;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import chann.vincent.sportchallenge.R;
import chann.vincent.sportchallenge.fragment.WorkoutFragment;
import chann.vincent.sportchallenge.service.NotificationConstants;
import chann.vincent.sportchallenge.service.WorkoutService;
import chann.vincent.sportchallenge.service.WorkoutServiceListener;

public class WorkoutActivity extends AppCompatActivity {

    private String TAG = "WorkoutActivity";
    protected Intent intentWorkoutService = null;
    protected WorkoutActivity getActivity() { return this; }
    protected TextView timerTextView;
    protected Button buttonPlay;
    protected CircularProgressBar timerProgressView;
    protected ImageButton buttonPrevious;
    protected ImageButton buttonNext;

    /*
    Life cycle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout);
        startAndConnectToWorkoutService();
        initNavigationBar();
        initViewPager();
    }

    /*
    Navigation bar : titles & menus
     */
    protected void initNavigationBar() {
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Custom title");
        getSupportActionBar().setSubtitle("Custom subtitle");
    }

    protected void setNavigationBarTitles(String title, String subtitle) {
        getSupportActionBar().setTitle(title);
        getSupportActionBar().setSubtitle(subtitle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.workout_menu, menu);
        invalidateOptionsMenu();
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                onBackPressed();
                finish();
                return true;

            case R.id.action_music:
                startActionMusic(null);
                return true;

            case R.id.action_cheer:
                startActionCheer(null);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // music
        if (WorkoutService.isMusicEnabled()) {
            menu.findItem(R.id.action_music).setIcon(getResources().getDrawable(R.drawable.ic_volume_up_white_24dp));
        }
        else {
            menu.findItem(R.id.action_music).setIcon(getResources().getDrawable(R.drawable.ic_volume_off_white_24dp));
        }

        // cheer
        if (WorkoutService.isCheerEnabled()) {
            menu.findItem(R.id.action_cheer).setIcon(getResources().getDrawable(R.drawable.ic_sentiment_very_satisfied_white_24dp));
        }
        else {
            menu.findItem(R.id.action_cheer).setIcon(getResources().getDrawable(R.drawable.ic_mood_bad_white_24dp));
        }

        // return updated menu after invalidateOptionsMenu();
        return super.onPrepareOptionsMenu(menu);
    }

    /*
    ViewPager
     */
    protected void initViewPager() {
        buttonPrevious = (ImageButton) findViewById(R.id.button_previous);
        buttonNext = (ImageButton) findViewById(R.id.button_next);
        // TODO set from service
        nextPage();
    }

    public void nextPage() {
        // TODO add object with title, subtitle, gif, baseUrl as parameter
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.animator.slide_in_right, R.animator.slide_out_left);
        fragmentTransaction.replace(R.id.workout_layout, WorkoutFragment.newInstance("TITLE 1", "power_jump.gif", !WorkoutService.isTimerPlaying()));
        fragmentTransaction.commitAllowingStateLoss(); // keep this fragment after rotation

        setNavigationBarTitles("title " + (WorkoutService.getCurrentPageSelected() + 1), "subtitle " + (WorkoutService.getCurrentPageSelected() + 1));
        updateButtonState();
    }

    public void previousPage() {
        // TODO add object with title, subtitle, gif, baseUrl as parameter
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_right);
        fragmentTransaction.replace(R.id.workout_layout, WorkoutFragment.newInstance("TITLE 1", "power_jump.gif", !WorkoutService.isTimerPlaying()));
        fragmentTransaction.commitAllowingStateLoss(); // keep this fragment after rotation

        setNavigationBarTitles("title " + (WorkoutService.getCurrentPageSelected() + 1), "subtitle " + (WorkoutService.getCurrentPageSelected() + 1));
        updateButtonState();
    }

    public void updatePageState() {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(0, 0);
        fragmentTransaction.replace(R.id.workout_layout, WorkoutFragment.newInstance("TITLE 1", "power_jump.gif", !WorkoutService.isTimerPlaying()));
        fragmentTransaction.commitAllowingStateLoss(); // keep this fragment after rotation
    }

    public void updateButtonState() {
        buttonPlay = (Button) findViewById(R.id.button_play);
        timerTextView = (TextView) findViewById(R.id.text_timer);

        // state play
        if (WorkoutService.isTimerPlaying()) {
            buttonPlay.setBackground(null);
            buttonPlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActionPause(null);
                }
            });
            timerTextView.setVisibility(View.VISIBLE);

            buttonPrevious.setVisibility(View.INVISIBLE);
            buttonNext.setVisibility(View.INVISIBLE);
        }

        // state pause
        else {
            buttonPlay.setBackground(getResources().getDrawable(R.drawable.ic_play_arrow_white_24dp));
            buttonPlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActionPlay(null);
                }
            });
            timerTextView.setVisibility(View.GONE);

            if (WorkoutService.getCurrentPageSelected() == 0) {
                buttonPrevious.setVisibility(View.INVISIBLE);
                buttonNext.setVisibility(View.VISIBLE);
            }

            if (WorkoutService.getCurrentPageSelected() == WorkoutService.getPageCount() - 1) {
                buttonPrevious.setVisibility(View.VISIBLE);
                buttonNext.setVisibility(View.INVISIBLE);
            }
        }
    }

    /*
    Start and bind to Workout Service
     */
    public void startAndConnectToWorkoutService() {
        timerTextView = (TextView) findViewById(R.id.text_timer);
        timerProgressView = (CircularProgressBar) findViewById(R.id.progress_bar_timer);
        intentWorkoutService = new Intent(this, WorkoutService.class);

        intentWorkoutService.setAction(NotificationConstants.ACTION.START_FOREGROUND);
        startService(intentWorkoutService);
        bindService(intentWorkoutService, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                WorkoutService.WorkoutBinder workoutBinder = (WorkoutService.WorkoutBinder) iBinder;
                WorkoutService workoutService = workoutBinder.getService();

                if (workoutService != null) {
                    workoutService.setListener(new WorkoutServiceListener() {
                        @Override
                        public void play() {
                            updateButtonState();
                            updatePageState();
                        }

                        @Override
                        public void pause() {
                            updateButtonState();
                            updatePageState();
                        }

                        @Override
                        public void next() {
                            nextPage();
                        }

                        @Override
                        public void previous() {
                            previousPage();
                        }

                        @Override
                        public void music(boolean enabled) {
                            invalidateOptionsMenu();
                        }

                        @Override
                        public void cheer(boolean enabled) {
                            invalidateOptionsMenu();
                        }

                        @Override
                        public void update() {

                        }

                        @Override
                        public void timer(int timer, int maxTimer) {
                            if (timerTextView != null) {
                                int remainingTime = maxTimer - timer;
                                int percentRemainingTime = timer * 100 / maxTimer;
                                timerTextView.setText("" + remainingTime);
                                timerProgressView.setProgress(percentRemainingTime);
                            }
                        }
                    });
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {

            }
        }, Context.BIND_AUTO_CREATE);
    }

    /*
    Actions sent to workout service
     */
    public void startActionPlay(View view) {
        intentWorkoutService = new Intent(this, WorkoutService.class);
        intentWorkoutService.setAction(NotificationConstants.ACTION.PLAY);
        startService(intentWorkoutService);
    }

    public void startActionUpdate(View view) {
        intentWorkoutService = new Intent(this, WorkoutService.class);
        intentWorkoutService.setAction(NotificationConstants.ACTION.UPDATE);
        startService(intentWorkoutService);
    }

    public void startActionPause(View view) {
        intentWorkoutService = new Intent(this, WorkoutService.class);
        intentWorkoutService.setAction(NotificationConstants.ACTION.PAUSE);
        startService(intentWorkoutService);
    }

    public void startActionNextPage(View view) {
        intentWorkoutService = new Intent(this, WorkoutService.class);
        intentWorkoutService.setAction(NotificationConstants.ACTION.NEXT);
        startService(intentWorkoutService);
    }

    public void startActionPreviousPage(View view) {
        intentWorkoutService = new Intent(this, WorkoutService.class);
        intentWorkoutService.setAction(NotificationConstants.ACTION.PREVIOUS);
        startService(intentWorkoutService);
    }

    public void startActionMusic(View view) {
        intentWorkoutService = new Intent(this, WorkoutService.class);
        intentWorkoutService.setAction(NotificationConstants.ACTION.MUSIC);
        startService(intentWorkoutService);
    }

    public void startActionCheer(View view) {
        intentWorkoutService = new Intent(this, WorkoutService.class);
        intentWorkoutService.setAction(NotificationConstants.ACTION.CHEER);
        startService(intentWorkoutService);
    }

    public void startActionFinish(View view) {
        intentWorkoutService = new Intent(this, WorkoutService.class);
        intentWorkoutService.setAction(NotificationConstants.ACTION.STOP_FOREGROUND);
        startService(intentWorkoutService);
    }
}
