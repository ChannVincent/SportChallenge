package chann.vincent.sportchallenge.activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import chann.vincent.sportchallenge.R;
import chann.vincent.sportchallenge.fragment.WorkoutFragment;
import chann.vincent.sportchallenge.service.NotificationConstants;
import chann.vincent.sportchallenge.service.WorkoutService;
import chann.vincent.sportchallenge.service.WorkoutServiceListener;
import fr.smartapps.smasupportv1.pager.SMAViewPager;

public class WorkoutActivity extends AppCompatActivity {

    private String TAG = "WorkoutActivity";
    protected Intent intentWorkoutService = null;
    protected WorkoutActivity getActivity() { return this; }
    protected TextView timerTextView;
    protected Button buttonPlay;
    protected CircularProgressBar timerProgressView;
    protected SMAViewPager pager;
    protected ImageButton buttonPrevious;
    protected ImageButton buttonNext;
    protected int currentPageSelected;

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
                if (WorkoutService.isMusicEnabled()) {
                    WorkoutService.setMusicEnabled(false);
                }
                else {
                    WorkoutService.setMusicEnabled(true);
                }
                startActionUpdate(null);
                invalidateOptionsMenu();
                return true;

            case R.id.action_cheer:
                if (WorkoutService.isCheerEnabled()) {
                    WorkoutService.setCheerEnabled(false);
                }
                else {
                    WorkoutService.setCheerEnabled(true);
                }
                startActionUpdate(null);
                invalidateOptionsMenu();
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
        pager = (SMAViewPager) findViewById(R.id.workout_pager);
        pager.fragmentManager(getFragmentManager())
                .setFragments(WorkoutFragment.newInstance("TITLE 1", "power_jump.gif"), WorkoutFragment.newInstance("TITLE 2", "power_jacks.gif"))
                .pageBoundaries(20, 20)
                .swipeable(false)
                .create();
        ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setNavigationBarTitles("title " + (position + 1), "subtitle " + (position + 1));
                currentPageSelected = position;
                updateState();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        };
        pager.setOnPageChangeListener(onPageChangeListener);

        // select first page at start
        onPageChangeListener.onPageSelected(0);
    }

    public void nextPage() {
        if (pager != null && pager.getCurrentItem() < pager.getChildCount()) {
            pager.setCurrentItem(pager.getCurrentItem() + 1);
        }
    }

    public void previousPage() {
        if (pager != null && pager.getCurrentItem() > 0) {
            pager.setCurrentItem(pager.getCurrentItem() - 1);
        }
    }

    public void updateState() {
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

            if (currentPageSelected == 0) {
                buttonPrevious.setVisibility(View.INVISIBLE);
                buttonNext.setVisibility(View.VISIBLE);
            }

            if (currentPageSelected == (pager.getChildCount() - 1)) {
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
                            updateState();
                        }

                        @Override
                        public void pause() {
                            updateState();
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

    public void startActionFinish(View view) {
        intentWorkoutService = new Intent(this, WorkoutService.class);
        intentWorkoutService.setAction(NotificationConstants.ACTION.STOP_FOREGROUND);
        startService(intentWorkoutService);
    }
}
