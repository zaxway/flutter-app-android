package org.cmucreatelab.flutter_android.activities.abstract_activities;

import org.cmucreatelab.flutter_android.helpers.GlobalHandler;
import org.cmucreatelab.flutter_android.helpers.static_classes.MessageConstructor;

import java.io.Serializable;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Steve on 11/23/2016.
 */
public abstract class BaseSensorReadingActivity extends BaseNavigationActivity implements Serializable {


    private Timer timer;


    protected void startSensorReading() {
        stopSensorReading();

        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                GlobalHandler globalHandler = GlobalHandler.getInstance(getApplicationContext());
                globalHandler.melodySmartDeviceHandler.addMessage(MessageConstructor.constructReadSensorValues());
            }
        };
        timer = new Timer();
        timer.schedule(timerTask, 0, 1000);
    }


    protected void stopSensorReading() {
        if (timer != null) {
            timer.cancel();
        }
    }


    public abstract void updateSensorViews();


    @Override
    protected void onPause() {
        super.onPause();
        stopSensorReading();
    }

}
