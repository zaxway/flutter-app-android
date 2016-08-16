package org.cmucreatelab.flutter_android.classes.sensors;

/**
 * Created by Steve on 8/11/2016.
 *
 * Sound
 *
 * A class representing a sound sensor.
 *
 */
public class Sound extends A_Sensor implements Sensor {

    private static final Sensor.Type sensorType = Type.SOUND;


    @Override
    public Type getSensorType() {
        return sensorType;
    }


    @Override
    public int getSensorReading() {
        return 0;
    }

}