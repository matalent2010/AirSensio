package com.wondereight.airsensio.UtilClass;

import com.wondereight.airsensio.Modal.CityModal;
import com.wondereight.airsensio.R;

import java.util.ArrayList;

/**
 * Created by Miguel on 02/17/2016.
 */

public class Global {
    private static Global mInstance = null;

    // Signup | Signin page
    private String GeoCityName = "";
    private String Geolocation = "";    //not use now

    private ArrayList<CityModal> CitiesList = new ArrayList();
    private String CityName = Constant.DEFAULT_CITYNAME;
    private String CityID = Constant.DEFAULT_CITYID;

    // Select Symptom page
    private int[] StateSymptomList = new int[] {0,0,0,0,0,0};

    Global(){
    }

    public void init(){
        GeoCityName = "";
        Geolocation = "";    //not use now

        CitiesList.clear();
        CityName = Constant.DEFAULT_CITYNAME;
        CityID = Constant.DEFAULT_CITYID;

        for( int i = 0; i<6; i++) {
            StateSymptomList[i] =0;
        }
    }

    public static Global GetInstance() {
        if (mInstance == null)
            mInstance = new Global();
        return mInstance;
    }

    public String GetGeoCityName(){
        return GeoCityName;
    }
    public void SetGoeCityName(String cityName){
        GeoCityName = cityName;
    }
    public String GetCityName(){
        return CityName;
    }
    public void SetCityName(String cityName){
        this.CityName = cityName;
    }

    public String GetCityID(){
        return CityID;
    }
    public void SetCityID(String cityID){
        this.CityID = cityID;
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

    public Boolean IsSetCitiesList()
    {
        return !CitiesList.isEmpty();
    }

    public void SetCityList(ArrayList<CityModal> cityList)
    {
        CitiesList.clear();
        CitiesList.addAll(cityList);
    }

    public ArrayList<CityModal> GetCityList()
    {
        return CitiesList;
    }
}
