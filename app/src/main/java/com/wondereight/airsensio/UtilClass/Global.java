package com.wondereight.airsensio.UtilClass;


import android.view.View;

import com.wondereight.airsensio.R;

import java.util.ArrayList;

/**
 * Created by Miguel on 02-17-2016.
 */

public class Global {
    private static Global mInstance = null;

    public static Global GetInstance() {
        if (mInstance == null)
            mInstance = new Global();
        return mInstance;
    }

    int[] StateSymptomList = new int[] {0,0,0,0,0,0};

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

}
