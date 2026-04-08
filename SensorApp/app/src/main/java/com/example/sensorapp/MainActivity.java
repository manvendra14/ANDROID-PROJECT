package com.example.sensorapp;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager manager;
    private Sensor accelerometer, light, proximity;

    private TextView accelView, lightView, proximityView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        accelView = findViewById(R.id.accel);
        lightView = findViewById(R.id.light);
        proximityView = findViewById(R.id.proximity);

        manager = (SensorManager) getSystemService(SENSOR_SERVICE);

        accelerometer = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        light = manager.getDefaultSensor(Sensor.TYPE_LIGHT);
        proximity = manager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
    }

    @Override
    protected void onStart() {
        super.onStart();
        manager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        manager.registerListener(this, light, SensorManager.SENSOR_DELAY_UI);
        manager.registerListener(this, proximity, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onStop() {
        super.onStop();
        manager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        int type = event.sensor.getType();

        if (type == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            accelView.setText("Accelerometer Values:\nX: " + x + "\nY: " + y + "\nZ: " + z);
        }

        else if (type == Sensor.TYPE_LIGHT) {
            lightView.setText("Light Intensity: " + event.values[0]);
        }

        else if (type == Sensor.TYPE_PROXIMITY) {
            proximityView.setText("Proximity Level: " + event.values[0]);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // No changes needed here
    }
}