package com.example.appka.mtaaaplikacia;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class DeleteJSONObjectRequest extends JsonObjectRequest{
    public DeleteJSONObjectRequest(int method, String url, JSONObject jsonRequest,Response.Listener listener, Response.ErrorListener errorListener)
    {
        super(method, url, jsonRequest, listener, errorListener);
    }

    @Override
    public Map getHeaders() throws AuthFailureError {
        Map headers = new HashMap();
        headers.put("application-id", "AF062A0C-71E5-3B32-FF5F-117AA0B3F400");
        headers.put("secret-key", "AC2EB8C8-3AE1-75BC-FF59-2CE223352300");
        return headers;
    }
}
