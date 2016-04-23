package com.wondereight.sensioair.UtilClass;

import android.content.Context;
import android.content.SharedPreferences;

import com.wondereight.sensioair.Modal.CityModal;
import com.wondereight.sensioair.Modal.UserModal;
import com.wondereight.sensioair.R;

import java.util.ArrayList;

/**
 * Created by Miguel on 02/17/2016.
 */

public class Global {
    private static Global mInstance = null;

    public static int runningActivities = 0;

    private UserModal UserModal = null;

    // Signup | Signin page
    private String GeoCityName = "";
    private String Geolocation = "";    //not use now

    private ArrayList<CityModal> CitiesList = new ArrayList<>();
    private String CityName = Constant.DEFAULT_CITYNAME;
    private String CityID = Constant.DEFAULT_CITYID;

    private boolean isOnlineMode;

    private boolean isLoadedProfile = false;
    private ArrayList<String> strProfile = new ArrayList<>();

    // Select Symptom page
    private int[] StateSymptomList = new int[] {0,0,0,0,0,0};

    Global(){
        isOnlineMode = false;
    }

    public void init(){
        UserModal = null;
        GeoCityName = "";
        Geolocation = "";    //not use now

        CitiesList.clear();
        CityName = Constant.DEFAULT_CITYNAME;
        CityID = Constant.DEFAULT_CITYID;

        isLoadedProfile = false;
        strProfile.clear();

        for( int i = 0; i<6; i++) {
            StateSymptomList[i] =0;
        }
    }

    public static Global GetInstance() {
        if (mInstance == null)
            mInstance = new Global();
        return mInstance;
    }

    public Boolean SetUserModalFromShared(Context context){
        if ( SaveSharedPreferences.isLogedinUser(context) ) {
            UserModal = SaveSharedPreferences.getLoginUserData(context);
            return true;
        } else {
            UserModal = null;
            return false;
        }
    }

    public UserModal GetUserModal() {
        return UserModal;
    }

    public void SaveUserModal(Context context, UserModal userModal) {
        SaveSharedPreferences.setLoginUserData(context, userModal);
        this.UserModal = userModal;
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

    public boolean IsOnlineMode(){
        return isOnlineMode;
    }
    public void SetOnlineMode(boolean state){
        isOnlineMode = state;
    }


    public boolean GetProfileLoadState(){
        return isLoadedProfile;
    }
    public void SetProfileLoadState(boolean state){
        isLoadedProfile = state;
    }

    public ArrayList<String> GetProfileInfo(){
        return strProfile;
    }
    public void SetProfileInfo(ArrayList<String> info){
        strProfile = info;
    }
}
