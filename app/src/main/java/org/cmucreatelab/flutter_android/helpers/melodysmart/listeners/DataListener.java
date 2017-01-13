package org.cmucreatelab.flutter_android.helpers.melodysmart.listeners;

import android.util.Log;

import com.bluecreation.melodysmart.DataService;

import org.cmucreatelab.flutter_android.classes.FlutterMessage;
import org.cmucreatelab.flutter_android.classes.Session;
import org.cmucreatelab.flutter_android.classes.outputs.Output;
import org.cmucreatelab.flutter_android.classes.sensors.Sensor;
import org.cmucreatelab.flutter_android.helpers.melodysmart.DeviceHandler;
import org.cmucreatelab.flutter_android.helpers.melodysmart.MessageQueue;
import org.cmucreatelab.flutter_android.helpers.static_classes.Constants;
import org.cmucreatelab.flutter_android.helpers.static_classes.MessageConstructor;

import java.util.ArrayList;

/**
 * Created by mike on 12/28/16.
 *
 * Creates a DataService.Listener instance using information from a Session.
 *
 */
public class DataListener implements DataService.Listener {

    final private Session mSession;
    final private DeviceHandler parent;
    final private MessageQueue messageQueue;
    private boolean serviceConnected;


    public boolean isServiceConnected() {
        return serviceConnected;
    }


    public DataListener(Session session, DeviceHandler parent, MessageQueue messageQueue) {
        this.mSession = session;
        this.parent = parent;
        this.messageQueue = messageQueue;
        serviceConnected = false;
    }


    @Override
    public void onConnected(final boolean isFound) {
        Log.v(Constants.LOG_TAG,"DataListener.onConnected isFound="+isFound);
        serviceConnected = isFound;

        if (isFound) {
            parent.getDataService().enableNotifications(true);
        }

        // read sensor types
        Sensor[] sensors = mSession.getFlutter().getSensors();
        for (Sensor sensor : sensors) {
            parent.addMessage(MessageConstructor.constructReadInputType(sensor));
        }
        // read outputs
        ArrayList<Output> outputs = mSession.getFlutter().getOutputs();
        for (Output output : outputs) {
            parent.addMessage(MessageConstructor.constructReadOutputState(output));
        }

        mSession.getFlutterConnectListener().onFlutterConnected();
    }


    @Override
    public void onReceived(final byte[] bytes) {
        FlutterMessage currentMessage = messageQueue.notifyMessageReceived();
        if (currentMessage == null) {
            Log.v(Constants.LOG_TAG,"DataListener.onReceived ignoring with null currentMessage");
        } else {
            String response = new String(bytes);
            Log.v(Constants.LOG_TAG,"DataListener.onReceived="+response);
            // handle parse
            mSession.onFlutterMessageReceived(currentMessage.getRequest(), response);
            // update views
            if (mSession.getFlutterMessageListener() != null) {
                mSession.getFlutterMessageListener().onFlutterMessageReceived(currentMessage.getRequest(), response);
            } else {
                Log.w(Constants.LOG_TAG,"Tried to call callback onFlutterMessageReceived but session FlutterMessageListener is null.");
            }
        }
    }

}