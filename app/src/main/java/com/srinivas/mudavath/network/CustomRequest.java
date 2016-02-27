package com.srinivas.mudavath.network;

import com.android.volley.Request;
import com.android.volley.Response;

/**
 * Created by Mudavath Srinivas on 24-11-2015.
 */
public abstract class CustomRequest extends Request {
    public CustomRequest(int method, String url, Response.ErrorListener listener) {
        super(method, url, listener);
    }
    /*Map<String, String> mParams=super.getParams();

    public String setCacheKey() {
        String temp = super.getCacheKey();
        for (Map.Entry<String, String> entry : mParams.entrySet())
            temp += entry.getKey() + "=" + entry.getValue();
        return temp;
    }*/
}
