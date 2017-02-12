package chann.vincent.sportchallenge.activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import chann.vincent.sportchallenge.R;
import chann.vincent.sportchallenge.service.NotificationConstants;
import chann.vincent.sportchallenge.service.WorkoutService;
import chann.vincent.sportchallenge.service.WorkoutServiceListener;

public class MainActivity extends AppCompatActivity {

    private String TAG = "MainActivity";
    protected Intent intentWorkoutService = null;
    protected MainActivity getActivity() { return this; }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout);
        startAndConnectToWorkoutService();
    }

    /*
    Start and bind to Workout Service
     */
    public void startAndConnectToWorkoutService() {
        Toast.makeText(this, "Start service", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(getActivity(), "play", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void pause() {
                            Toast.makeText(getActivity(), "pause", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void next() {
                            Toast.makeText(getActivity(), "next", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void previous() {
                            Toast.makeText(getActivity(), "previous", Toast.LENGTH_SHORT).show();
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
    Actions
     */
    public void startActionPlay(View view) {
        intentWorkoutService = new Intent(this, WorkoutService.class);
        intentWorkoutService.setAction(NotificationConstants.ACTION.PLAY);
        startService(intentWorkoutService);
    }

    public void startActionPause(View view) {
        intentWorkoutService = new Intent(this, WorkoutService.class);
        intentWorkoutService.setAction(NotificationConstants.ACTION.PAUSE);
        startService(intentWorkoutService);
    }

    public void startActionNext(View view) {
        intentWorkoutService = new Intent(this, WorkoutService.class);
        intentWorkoutService.setAction(NotificationConstants.ACTION.NEXT);
        startService(intentWorkoutService);
    }

    public void startActionPrevious(View view) {
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
