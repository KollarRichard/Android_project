package com.example.appka.mtaaaplikacia;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class CustomJSONObjectRequest extends JsonObjectRequest {

    public CustomJSONObjectRequest(int method, String url, JSONObject jsonRequest,Response.Listener listener, Response.ErrorListener errorListener)
    {
        super(method, url, jsonRequest, listener, errorListener);
    }

    @Override
    public Map getHeaders() throws AuthFailureError {
        Map headers = new HashMap();
        headers.put("application-id", "39E3DA48-2F5E-4EC5-FF1C-15EC0E508400");
        headers.put("secret-key", "7A5F072E-9D48-4C90-978F-DD29516E5562");
        headers.put("Content-Type", "application/json");
        headers.put("application-type", "REST");
        return headers;
    }
}