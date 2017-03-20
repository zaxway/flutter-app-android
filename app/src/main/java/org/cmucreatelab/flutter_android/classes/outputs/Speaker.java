package org.cmucreatelab.flutter_android.classes.outputs;

import org.cmucreatelab.flutter_android.classes.flutters.Flutter;
import org.cmucreatelab.flutter_android.classes.settings.SettingsConstant;
import org.cmucreatelab.flutter_android.classes.settings.SettingsProportional;

/**
 * Created by Steve on 6/20/2016.
 *
 * Speaker
 *
 * A class that represents the speaker on a flutter.
 *
 */
public class Speaker implements FlutterOutput {

    public static final String SPEAKER_KEY = "speaker_key";
    private static final int NUMBER_OF_OUTPUTS = 2;

    private int portNumber;
    private Output[] outputs;
    private Flutter flutter;

    // getters
    public int getPortNumber() { return this.portNumber; }
    public Pitch getPitch() { return (Pitch)this.outputs[0]; }
    public Volume getVolume() { return (Volume)this.outputs[1]; }


    public Speaker(int portNumber, Flutter flutter) {
        this.portNumber = portNumber;
        this.outputs = new Output[NUMBER_OF_OUTPUTS];
        outputs[0] = new Pitch(portNumber, flutter);
        outputs[1] = new Volume(portNumber, flutter);
        this.flutter = flutter;
    }


    public static Speaker newInstance(Speaker oldInstance) {
        Speaker newInstance = new Speaker(oldInstance.portNumber,oldInstance.flutter);

        // settings (pitch)
        if (oldInstance.getPitch().getSettings().getClass() == SettingsConstant.class) {
            newInstance.getPitch().setSettings(SettingsConstant.newInstance(oldInstance.getPitch().getSettings()));
        } else {
            newInstance.getPitch().setSettings(SettingsProportional.newInstance(oldInstance.getPitch().getSettings()));
        }

        // settings (volume)
        if (oldInstance.getVolume().getSettings().getClass() == SettingsConstant.class) {
            newInstance.getVolume().setSettings(SettingsConstant.newInstance(oldInstance.getVolume().getSettings()));
        } else {
            newInstance.getVolume().setSettings(SettingsProportional.newInstance(oldInstance.getVolume().getSettings()));
        }

        return newInstance;
    }


    @Override
    public Output[] getOutputs() {
        return this.outputs;
    }

}
