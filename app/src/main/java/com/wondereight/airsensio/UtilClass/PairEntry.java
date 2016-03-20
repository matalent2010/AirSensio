package com.wondereight.airsensio.UtilClass;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.SimpleTimeZone;

/**
 * Created by Miguel on 02/21/2016.
 */
public class PairEntry{
    String label = "";
    float percentValue = 0;
    float value = 0;

    public PairEntry( String label, float value){
        this.label = label;
        this.value = value;
        percentValue = calcPercent(label);}
    String getLabel(){ return label;}
    void setLabel(String label){this.label = label;}
    float getValue(){ return value;}
    void setValue(float value){ this.value = value;}

    float getPercentValue(){ return percentValue;}
    void setPercentValue(float value){ this.percentValue = value;}
    float calcPercent(String strTime){
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date();
        float result=-1;
        try {
            date = format.parse(strTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int h = date.getHours(); int m = date.getMinutes(); int s = date.getSeconds();
        result = (date.getHours()*60*60 + date.getMinutes()*60 + date.getSeconds())/(24*60*60.0f)*100;
        return result;
    }
}
