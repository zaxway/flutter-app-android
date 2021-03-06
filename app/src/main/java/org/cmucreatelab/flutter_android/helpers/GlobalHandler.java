package org.cmucreatelab.flutter_android.helpers;

import android.content.Context;

import org.cmucreatelab.flutter_android.helpers.datalogging.DataLoggingHandler;
import org.cmucreatelab.flutter_android.helpers.melodysmart.FlutterDeviceHandler;
import org.cmucreatelab.flutter_android.helpers.static_classes.EmailHandler;

/**
 * Created by Steve on 5/26/2016.
 *
 * GlobalHandler
 *
 * A class that gives access to needed elements throughout various activities.
 *
 */
public class GlobalHandler {

    public Context appContext;
    public SessionHandler sessionHandler;
    public FlutterDeviceHandler melodySmartDeviceHandler;
    public EmailHandler emailHandler;
    public DataLoggingHandler dataLoggingHandler;
    public HttpRequestHandler httpRequestHandler;

    // Singleton Implementation


    private static GlobalHandler classInstance;


    public static synchronized GlobalHandler getInstance(Context context) {
        if (classInstance == null) {
            classInstance = new GlobalHandler(context);
        }
        return classInstance;
    }


    private GlobalHandler(Context context) {
        this.appContext = context;
        this.sessionHandler = new SessionHandler(this);
        this.melodySmartDeviceHandler = new FlutterDeviceHandler(this);
        this.emailHandler = new EmailHandler();
        this.dataLoggingHandler = new DataLoggingHandler(appContext);
        this.httpRequestHandler = new HttpRequestHandler(this);
    }

}
