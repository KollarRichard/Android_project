package com.example.appka.mtaaaplikacia;

import android.app.Application;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;


public class MTAAApplication extends Application {

    private static MTAAApplication mInstance;
    private static RequestQueue mRequestQueue;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    public static synchronized MTAAApplication getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if(mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(this.getApplicationContext());
        }

        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> request, String tag) {
        request.setTag(TextUtils.isEmpty(tag) ? "default" : tag);
        getRequestQueue().add(request);
    }

    public void cancelRequests(Object tag) {
        if(mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }


}
