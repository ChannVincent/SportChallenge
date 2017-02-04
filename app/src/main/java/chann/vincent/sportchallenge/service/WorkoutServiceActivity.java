package chann.vincent.sportchallenge.service;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import chann.vincent.sportchallenge.R;
import chann.vincent.sportchallenge.intent_service.WorkoutIntentService;
import chann.vincent.sportchallenge.service.WorkoutService;

public class WorkoutServiceActivity extends AppCompatActivity {

    private String TAG = "WorkoutIntentActivity";
    protected WorkoutService workoutService = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_service);
    }

    /*
    Workout Service
     */
    public void onStart4(View view) {
        Toast.makeText(this, "Start service", Toast.LENGTH_SHORT).show();
        Intent intentWorkoutService = new Intent(this, WorkoutService.class);
        startService(intentWorkoutService);
        bindService(intentWorkoutService, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                WorkoutService.WorkoutBinder workoutBinder = (WorkoutService.WorkoutBinder) iBinder;
                workoutService = workoutBinder.getService();
            }
            @Override
            public void onServiceDisconnected(ComponentName componentName) {

            }
        }, Context.BIND_AUTO_CREATE);
    }

    public void onStart5(View view) {
        if (workoutService == null) {
            return;
        }
        workoutService.startAction1();
    }

    public void onStart6(View view) {
        if (workoutService == null) {
            return;
        }
        workoutService.startAction2();
    }
}
