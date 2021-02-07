package com.example.appka.mtaaaplikacia;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JSONParser {

    public String[] getStringFromJson(JSONObject obj, String tag) {
        String[] s;
        JSONArray jArray = null;
        JSONObject tmpObj = null;
        try {
            jArray = obj.getJSONArray("data");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (jArray != null) {
            s = new String[jArray.length()];
            for (int i = 0; i < jArray.length(); i++) {
                try {
                    tmpObj = jArray.getJSONObject(i);
                    s[i] = tmpObj.getString(tag);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return s;
        }
        return null;
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


    public int getTotalObjects(JSONObject obj) {
        String tmp = "-1";
        try {
            tmp = obj.getString("totalObjects");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return Integer.parseInt(tmp);
    }

    public String getNextPageUrl(JSONObject obj) {
        String s = null;
        try {
            s = obj.getString("nextPage");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return s;
    }
}
