package com.wondereight.airsensio.UtilClass;

import com.wondereight.airsensio.R;

import java.util.ArrayList;

/**
 * Created by Miguel on 02/17/2016.
 */

public class Global {
    private static Global mInstance = null;

//    Signup | Signin page
    String CityName = "";
    String Geolocation = "";    //not use now

//    Select Symptom page
    int[] StateSymptomList = new int[] {0,0,0,0,0,0};

//    Statistics Page
    ArrayList<Boolean> StateStatistics = new ArrayList<Boolean>();
    int StyleGraph = 0; //0:day, 1:week, 2:month, 3: year

    Global(){
        for( int i = 0; i<6; i++)
            StateStatistics.add(false);
    }

    public static Global GetInstance() {
        if (mInstance == null)
            mInstance = new Global();
        return mInstance;
    }

    public String GetCityName(){
        return CityName;
    }
    public void SetCityName(String cityName){
        CityName = cityName;
    }

    public String GetGeolocation(){
        return Geolocation;
    }
    public void SetGeolocation(String geolocation){
        Geolocation = geolocation;
    }

    public int[] GetStateSymptomList(){
        int[] result = new int[6];
        for ( int i=0; i<6; i++)
            result[i] = StateSymptomList[i];

        return result;
    }
    public void SetStateSymptomList(int[] list) {
        for ( int i=0; i<6; i++)
            StateSymptomList[i] = list[i];
    }

    public ArrayList<Boolean> GetStateStatistics(){
        return new ArrayList<>(StateStatistics);
    }
    public void SetStateStatistics(ArrayList<Boolean> arr){
        StateStatistics = arr;
    }

    public int GetStyleOfGraph(){ return StyleGraph;}
    public void SetStyleOfGraph(int styleGraph) { StyleGraph = styleGraph;}

}
