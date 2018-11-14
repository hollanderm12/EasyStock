package com.mtlcollege.quickstocks.util;

import com.mtlcollege.quickstocks.model.Results;
import com.mtlcollege.quickstocks.model.StockInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

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

                //TODO: Check if JSON Object actually exists (e.g. for RY.NO.U between 10-01-12 and 10-31-12
                Iterator<String> keys = dataArray.getJSONObject(i).keys();
                while(keys.hasNext()) {
                    String key = keys.next();
                    switch(key) {
                        case("date"): r.setDate(dataArray.getJSONObject(i).getString("date")); break;
                        case("open"): r.setOpen(dataArray.getJSONObject(i).getDouble("open")); break;
                        case("high"): r.setHigh(dataArray.getJSONObject(i).getDouble("high")); break;
                        case("low"): r.setLow(dataArray.getJSONObject(i).getDouble("low")); break;
                        case("close"): r.setClose(dataArray.getJSONObject(i).getDouble("close")); break;
                        case("volume"): r.setVolume(dataArray.getJSONObject(i).getLong("volume")); break;
                        case("ex_dividend"): r.setExDividend(dataArray.getJSONObject(i).getDouble("ex_dividend")); break;
                        case("split_ratio"): r.setSplitRatio(dataArray.getJSONObject(i).getDouble("split_ratio")); break;
                        case("adj_factor"): r.setAdjFactor(dataArray.getJSONObject(i).getDouble("adj_factor")); break;
                        case("adj_open"): r.setAdjOpen(dataArray.getJSONObject(i).getDouble("adj_open")); break;
                        case("adj_high"): r.setAdjHigh(dataArray.getJSONObject(i).getDouble("adj_high")); break;
                        case("adj_low"): r.setAdjLow(dataArray.getJSONObject(i).getDouble("adj_low")); break;
                        case("adj_close"): r.setAdjClose(dataArray.getJSONObject(i).getDouble("adj_close")); break;
                        case("adj_volume"): r.setAdjVolume(dataArray.getJSONObject(i).getLong("adj_volume")); break;
                    }
                }
                resultList.add(r);
            }
        }

        catch(JSONException e) {
            e.printStackTrace();
            resultList.clear();
        }

        return resultList;
    }

    public static ArrayList<StockInfo> getSearchResults(String toParse) {
        ArrayList<StockInfo> resultList = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(toParse);
            JSONArray dataArray = jsonObject.getJSONArray("data");

            for(int i = 0 ; i < dataArray.length() ; i++) {
                StockInfo si = new StockInfo();
                si.setSymbol(dataArray.getJSONObject(i).getString("ticker"));
                si.setSecurityName(dataArray.getJSONObject(i).getString("security_name"));
                si.setSecurityType(dataArray.getJSONObject(i).getString("security_type"));
                si.setCurrencyType(dataArray.getJSONObject(i).getString("currency"));
                resultList.add(si);
            }
        }

        catch(JSONException e) {
            e.printStackTrace();
            resultList.clear();
        }

        return resultList;
    }
}
