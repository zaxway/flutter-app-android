package org.cmucreatelab.flutter_android.helpers;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by mike on 5/2/17.
 */

public class HttpRequestHandler {

    private GlobalHandler parent;
    private static final String VOLLEY_TAG = "email_server";
    public RequestQueue volleyRequestQueue;


    public HttpRequestHandler(GlobalHandler parent) {
        this.parent = parent;
        this.volleyRequestQueue = Volley.newRequestQueue(parent.appContext);
    }


    public void addRequestToRequestQueue(Request request) {
        request.setTag(VOLLEY_TAG);
        volleyRequestQueue.add(request);
    }


    public void clearRequestsFromRequestQueue() {
        volleyRequestQueue.cancelAll(VOLLEY_TAG);
    }

}
