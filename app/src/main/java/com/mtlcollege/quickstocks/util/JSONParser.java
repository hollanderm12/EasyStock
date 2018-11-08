package com.mtlcollege.quickstocks.util;

import com.mtlcollege.quickstocks.model.Results;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class JSONParser {

    public static int getResultCount(String toParse) {

        int resultCount;

        try {
            JSONObject jsonObject = new JSONObject(toParse);
            resultCount = jsonObject.getInt("result_count");
        }

        catch(JSONException e) {
            e.printStackTrace();
            resultCount = 0;
        }

        return resultCount;
    }

    public static ArrayList<Results> getLookupResults(String toParse) {

        ArrayList<Results> resultList = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(toParse);
            JSONArray dataArray = jsonObject.getJSONArray("data");

            for(int i = 0 ; i < dataArray.length() ; i++) {
                Results r = new Results();
                r.setDate(dataArray.getJSONObject(i).getString("date"));
                r.setOpen(dataArray.getJSONObject(i).getDouble("open"));
                r.setHigh(dataArray.getJSONObject(i).getDouble("high"));
                r.setLow(dataArray.getJSONObject(i).getDouble("low"));
                r.setClose(dataArray.getJSONObject(i).getDouble("close"));
                r.setVolume(dataArray.getJSONObject(i).getLong("volume"));
                r.setExDividend(dataArray.getJSONObject(i).getDouble("ex_dividend"));
                r.setSplitRatio(dataArray.getJSONObject(i).getDouble("split_ratio"));
                r.setAdjFactor(dataArray.getJSONObject(i).getDouble("adj_factor"));
                r.setAdjOpen(dataArray.getJSONObject(i).getDouble("adj_open"));
                r.setAdjHigh(dataArray.getJSONObject(i).getDouble("adj_high"));
                r.setAdjLow(dataArray.getJSONObject(i).getDouble("adj_low"));
                r.setAdjClose(dataArray.getJSONObject(i).getDouble("adj_close"));
                r.setAdjVolume(dataArray.getJSONObject(i).getLong("adj_volume"));
                resultList.add(r);
            }
        }

        catch(JSONException e) {
            e.printStackTrace();
            resultList.clear();
        }

        return resultList;
    }
}
