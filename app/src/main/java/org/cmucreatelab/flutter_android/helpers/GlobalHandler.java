package org.cmucreatelab.flutter_android.helpers;

import android.content.Context;

/**
 * Created by Steve on 5/26/2016.
 */
public class GlobalHandler {

    public Context appContext;
    public NamingHandler namingHandler;
    public SessionHandler sessionHandler;
    public AppState appState;


    // Singleton Implementation


    private static GlobalHandler classInstance;


    public static synchronized GlobalHandler newInstance(Context context) {
        if (classInstance == null) {
            classInstance = new GlobalHandler(context);
        }
        return classInstance;
    }


    private GlobalHandler(Context context) {
        this.appContext = context;
        this.sessionHandler = new SessionHandler();
        this.namingHandler = new NamingHandler(appContext);
        this.appState = new AppState(GuidedInputStates.MAIN_PROMPT, null);
    }

}
