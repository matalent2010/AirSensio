package com.wondereight.airsensio.Modal;

import java.util.ArrayList;

/**
 * Created by Administrator on 3/22/2016.
 */
public class RequestParamsModal {
    ArrayList<String> str_keys;
    ArrayList<String> str_values;

    public RequestParamsModal(){
        str_keys = new ArrayList<>();
        str_values = new ArrayList<>();
    }
    public void add(String key, String value){
        str_keys.add(key);
        str_values.add(value);
    }

    public int getCount(){
        return str_keys.size();
    }

    public String getKey(int i){
        return str_keys.get(i);
    }

    public String getValue(int i){
        return str_values.get(i);
    }
}
