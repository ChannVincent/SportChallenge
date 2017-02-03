package chann.vincent.sportchallenge;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import chann.vincent.sportchallenge.service.WorkoutIntentService;
import chann.vincent.sportchallenge.service.WorkoutService;

public class MainActivity extends AppCompatActivity {

    private String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        registerIntentReceiver();
        registerReceiver();
    }

    /*
    Workout Intent Service
     */
    protected void registerIntentReceiver() {
        IntentFilter filter = new IntentFilter(WorkoutIntentService.ACTION_RESP);
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.e(TAG, "WorkoutIntentReceiver - onReceive() " + intent.getAction());
            }
        }, filter);
    }

    public void onStart1(View view) {
        Toast.makeText(this, "Start intent service", Toast.LENGTH_SHORT).show();
        Intent msgIntent = new Intent(this, WorkoutIntentService.class);
        startService(msgIntent);
    }

    public void onStart2(View view) {
        Toast.makeText(this, "Bind intent service", Toast.LENGTH_SHORT).show();
    }

    public void onStart3(View view) {
        Toast.makeText(this, "Destroy intent service", Toast.LENGTH_SHORT).show();
    }

    /*
    Workout Service
     */
    protected void registerReceiver() {
        IntentFilter filter = new IntentFilter(WorkoutService.ACTION_RESP);
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.e(TAG, "WorkoutService - onReceive() " + intent.getAction());
            }
        }, filter);

    }

    public void onStart4(View view) {
        Toast.makeText(this, "Start service", Toast.LENGTH_SHORT).show();
        Intent msgIntent = new Intent(this, WorkoutService.class);
        startService(msgIntent);
    }

    public void onStart5(View view) {
        Toast.makeText(this, "Bind service", Toast.LENGTH_SHORT).show();
    }

    public void onStart6(View view) {
        Toast.makeText(this, "Destroy service", Toast.LENGTH_SHORT).show();
    }
}
