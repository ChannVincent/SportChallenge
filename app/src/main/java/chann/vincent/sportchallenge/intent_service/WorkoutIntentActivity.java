package chann.vincent.sportchallenge.intent_service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import chann.vincent.sportchallenge.R;
import chann.vincent.sportchallenge.intent_service.WorkoutIntentService;

public class WorkoutIntentActivity extends AppCompatActivity {

    private String TAG = "WorkoutIntentActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_intent);
        registerIntentReceiver();
    }

    /*
    Workout Intent Service
     */
    protected void registerIntentReceiver() {
        IntentFilter workoutIntent = new IntentFilter(WorkoutIntentService.ACTION_RESP);
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.e(TAG, "WorkoutIntentReceiver - onReceive() " + intent.getAction());
            }
        }, workoutIntent);
    }

    public void onStart1(View view) {
        Toast.makeText(this, "Start intent service", Toast.LENGTH_SHORT).show();
        Intent msgIntent = new Intent(this, WorkoutIntentService.class);
        startService(msgIntent);
    }
}
