package com.example.appka.mtaaaplikacia;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JSONParser {

    public String[] getStringFromJson(JSONObject obj, String tag) {
        String[] s = {"dat"};

        try {
            s[0]  = obj.getString(tag);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return s;
    }

    public String getSingleString(JSONObject obj, String tag) {
        String s = null;
        try {
            s = obj.getString(tag);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return s;
    }


}
