package chann.vincent.sportchallenge.service;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import chann.vincent.sportchallenge.R;

public class WorkoutServiceActivity extends AppCompatActivity {

    private String TAG = "WorkoutIntentActivity";
    protected WorkoutService workoutService = null;
    protected Intent intentWorkoutService = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_service);
    }

    /*
    Start and bind Workout Service
     */
    public void onStart4(View view) {
        Toast.makeText(this, "Start service", Toast.LENGTH_SHORT).show();
        intentWorkoutService = new Intent(this, WorkoutService.class);
        startService(intentWorkoutService);
        bindService(intentWorkoutService, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                WorkoutService.WorkoutBinder workoutBinder = (WorkoutService.WorkoutBinder) iBinder;
                workoutService = workoutBinder.getService();

                if (workoutService != null) {
                    final TextView triggerAction1 = (TextView) findViewById(R.id.trigger_action_1);
                    final TextView triggerAction2 = (TextView) findViewById(R.id.trigger_action_2);
                    workoutService.setListener(new WorkoutServiceListener() {
                        @Override
                        public void triggerAction1(String message) {
                            triggerAction1.setText(message);
                        }

                        @Override
                        public void triggerAction2(String message) {
                            triggerAction2.setText(message);
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
    Start action 1 of Workout Service
     */
    public void onStart5(View view) {
        if (workoutService == null) {
            return;
        }
        workoutService.startAction1();
    }

    /*
    Start action 2 of Workout Service
     */
    public void onStart6(View view) {
        if (workoutService == null) {
            return;
        }
        workoutService.startAction2();
    }

    /*
    Stop Workout Service
     */
    public void onStart7(View view) {
        if (workoutService == null) {
            return;
        }
        workoutService.stopService();
    }
}
