package com.wondereight.sensioair.UtilClass;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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
        percentValue = calcPercent(label);
    }
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
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int h = cal.get(Calendar.HOUR); int m = cal.get(Calendar.MINUTE); int s = cal.get(Calendar.SECOND);
        result = (cal.get(Calendar.HOUR)*60*60 + cal.get(Calendar.MINUTE)*60 + cal.get(Calendar.SECOND))/(24*60*60.0f)*100;
        return result;
    }
}
