package com.mtlcollege.quickstocks.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class JSONParser {

    public static ArrayList<String> getValues(String toParse) {

        ArrayList<String> values = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(toParse);
            JSONArray dataArray = jsonObject.getJSONArray("data");

            if(dataArray.length() > 0) {
                values.add("High: " + dataArray.getJSONObject(0).get("high").toString());
                values.add("Low: " + dataArray.getJSONObject(0).get("low").toString());
                values.add("Volume: " + dataArray.getJSONObject(0).get("volume").toString());
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
            values.clear();
        }

        return values;
    }
}
