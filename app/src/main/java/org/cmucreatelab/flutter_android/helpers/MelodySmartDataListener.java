package org.cmucreatelab.flutter_android.helpers;

import android.util.Log;

import com.bluecreation.melodysmart.DataService;

import org.cmucreatelab.flutter_android.classes.Session;
import org.cmucreatelab.flutter_android.helpers.static_classes.Constants;

/**
 * Created by mike on 12/28/16.
 */

public class MelodySmartDataListener implements DataService.Listener {

    private Session mSession;
    private MelodySmartDeviceHandler parent;
    public boolean serviceConnected;


    public MelodySmartDataListener(Session session, MelodySmartDeviceHandler parent) {
        this.mSession = session;
        this.parent = parent;
        serviceConnected = false;
    }


    @Override
    public void onConnected(final boolean isFound) {
        Log.v(Constants.LOG_TAG,"MelodySmartDataListener.onConnected isFound="+isFound);
        serviceConnected = isFound;

        if (isFound) {
            parent.getDataService().enableNotifications(true);
        }
        mSession.flutterConnectListener.onFlutterConnected();
    }


    @Override
    public void onReceived(final byte[] bytes) {
        String response = new String(bytes);
        Log.v(Constants.LOG_TAG,"MelodySmartDataListener.onReceived="+response);
        mSession.flutterMessageListener.onFlutterMessageReceived(response);
    }

}
