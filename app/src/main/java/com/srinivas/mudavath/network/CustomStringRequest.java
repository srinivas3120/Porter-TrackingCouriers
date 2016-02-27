package com.srinivas.mudavath.network;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

/**
 * Created by Mudavath Srinivas on 25-11-2015.
 */
public class CustomStringRequest extends StringRequest {
    public CustomStringRequest(int method, String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);
    }

    public CustomStringRequest(String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(url, listener, errorListener);
    }

}
