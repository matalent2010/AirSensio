package com.wondereight.airsensio.UtilClass;

import android.support.annotation.Nullable;
import android.util.Log;

import com.wondereight.airsensio.Helper._Debug;
import com.wondereight.airsensio.Modal.DeviceDataModal;
import com.wondereight.airsensio.Modal.GraphDataModal;
import com.wondereight.airsensio.Modal.UserModal;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by Migeul on 3/2/2016.
 */
public class ParsingResponse {
    private static final String LOG_TAG = "ParsingResponse";
    private static final _Debug _debug = new _Debug(true);

    public static UserModal parsingUserModal(JSONArray arr){
        try {
            UserModal userModal = new UserModal();

            JSONObject firstEvent = arr.getJSONObject(0);

            userModal.setId(firstEvent.optString(Constant.STR_USERID));
            userModal.setFirstname(firstEvent.optString(Constant.STR_FIRSTNAME));
            userModal.setLastname(firstEvent.optString(Constant.STR_LASTNAME));
            userModal.setEmail(firstEvent.optString(Constant.STR_EMAIL));
            userModal.setGender(firstEvent.optString(Constant.STR_GENDER));
            userModal.setPhone(firstEvent.optString(Constant.STR_PHONE));
            userModal.setNotification(firstEvent.optString(Constant.STR_NOTIFICATION));
            userModal.setBirthday(firstEvent.optString(Constant.STR_BIRTHDAY));
            userModal.setNewsletter(firstEvent.optString(Constant.STR_NEWSLETTER));
            userModal.setSession_nbr(firstEvent.optString(Constant.STR_NBR));

            return userModal;
        } catch (JSONException e) {
            e.printStackTrace();
            _debug.e(LOG_TAG, "JSONArray Fail: " + e.getMessage());
        }
        return null;
    }


    public static DeviceDataModal parsingDeviceData(JSONArray arr){
        try {
            DeviceDataModal deviceModal = new DeviceDataModal();

            JSONObject firstEvent = arr.getJSONObject(0);

            deviceModal.setEnvId(firstEvent.optString(Constant.STR_ENVID)); //Constant.STR_USERID
            deviceModal.setEnvValue(firstEvent.optString(Constant.STR_ENVVALUE));
            deviceModal.setDatetime(firstEvent.optString(Constant.STR_LOGDATETIME));
            deviceModal.setCityId(firstEvent.optString(Constant.STR_CITYID));

            return deviceModal;
        } catch (JSONException e) {
            e.printStackTrace();
            _debug.e(LOG_TAG, "JSONArray Fail: " + e.getMessage());
        }
        return null;
    }

    public static ChartItem parsingGraphData(JSONArray arr, int state) {
        _debug.d(LOG_TAG, "GraphStyle(" + String.valueOf(state) + ") Data Length : " + String.valueOf(arr.length()));
        ChartItem result = new ChartItem();
        ArrayList<GraphDataModal> graphDataArray = new ArrayList<>();
        for (int i = 0; i < arr.length(); i++) {
            try {
                GraphDataModal item = new GraphDataModal();
                JSONObject obj_item = arr.getJSONObject(i);
                item.setParameter(obj_item.optString(Constant.STR_PARAMETER));
                item.setLogValue(obj_item.optString(Constant.STR_VALUE));
                item.setLogDate(obj_item.optString(Constant.STR_DATE));
                item.setLogTime(obj_item.optString(Constant.STR_TIME));
                graphDataArray.add(item);
                _debug.d(LOG_TAG, "GraphStyle(" + String.valueOf(state) + ") Data "+i+" : " + obj_item.toString());
            } catch (Exception e) {
                _debug.e(LOG_TAG, "(" + String.valueOf(i) + ")th item of Response JsonArray : " + e.toString());
            }
        }

        switch (state){
            case 0: //DAY
                result = getDayGraphData(graphDataArray);
                break;
            case 1: //WEEK
                result = getWeekGraphData(graphDataArray);
                break;
            case 2: //MONTH
                result = getMonthGraphData(graphDataArray);
                break;
            case 3: //YEAR
                result = getYearGraphData(graphDataArray);
                break;
        }
        return result;
    }

    static class ValuesData {
        String str;
        int nCount;
        float[] fValue;
        ValuesData(String str){
            this.str = str;
        }
        public void setCount(int count){
            this.nCount = count;
            fValue = new float[count];
            for(int i=0; i<count; i++){
                fValue[i] = 0.0f;
            }
        }

        public void setfValueWithIndex(float value, int index){
            this.fValue[index] = value;
        }
        public float[] getfValue(){
            return fValue;
        }
    }

    static ChartItem getDayGraphData(ArrayList<GraphDataModal> ArrOfGraph){
        ChartItem result = new ChartItem();
        ArrayList<String> labels = new ArrayList<>();
        ArrayList<String> short_labels = new ArrayList<>();

        ArrayList<ValuesData> arrDayGraphData = new ArrayList<>();
        arrDayGraphData.add(new ValuesData("Particles Count"));
        arrDayGraphData.add(new ValuesData("Allergen Count"));
        arrDayGraphData.add(new ValuesData("Humidity"));
        arrDayGraphData.add(new ValuesData("Temperature"));
        arrDayGraphData.add(new ValuesData("Sunlight"));
        arrDayGraphData.add(new ValuesData("Harmful Gas"));
        arrDayGraphData.add(new ValuesData("Outbreak"));

        for( GraphDataModal item : ArrOfGraph ){
            if(item.getParameter().equalsIgnoreCase(arrDayGraphData.get(6).str)){  // "Outbreak"
                break;
            }
            Boolean isInclude = false;
            for(String label : labels){
                if(item.getLogTime().equalsIgnoreCase(label)) {
                    isInclude = true;
                    break;
                }
            }
            if(!isInclude)
                labels.add(item.getLogTime());
        }
        Collections.sort(labels, new Comparator<String>() {
            @Override
            public int compare(String lhs, String rhs) {
                return lhs.compareToIgnoreCase(rhs);
            }
        });
        for (String label : labels){
            short_labels.add(label.substring(0,label.length()-3));
        }
        for ( ValuesData dayGraph : arrDayGraphData ){
            dayGraph.setCount(labels.size());
        }

        String[] mStringArray = new String[short_labels.size()];
        mStringArray = short_labels.toArray(mStringArray);
        result.setLabel(mStringArray);
        for( GraphDataModal item : ArrOfGraph ){
            if(item.getParameter().equalsIgnoreCase(arrDayGraphData.get(6).str)){  // "Outbreak"
                for(int k=0; k<labels.size(); k++){
                    arrDayGraphData.get(6).setfValueWithIndex(Float.parseFloat(item.getLogValue()),k);
                }
                break;
            }
            for(int j=0; j<6/*arrDayGraphData.size()-1*/; j++){
                if(item.getParameter().equalsIgnoreCase(arrDayGraphData.get(j).str)){
                    for(int k=0; k<labels.size(); k++){
                        if( item.getLogTime().equalsIgnoreCase(labels.get(k))){
                            arrDayGraphData.get(j).setfValueWithIndex(Float.parseFloat(item.getLogValue()),k);
                            break;
                        }
                    }
                    break;
                }
            }

        }
        result.setValueParticles(arrDayGraphData.get(0).getfValue());
        result.setValueAllergen(arrDayGraphData.get(1).getfValue());
        result.setValueHumidity(arrDayGraphData.get(2).getfValue());
        result.setValueTemperature(arrDayGraphData.get(3).getfValue());
        result.setValueSunlight(arrDayGraphData.get(4).getfValue());
        result.setValueGas(arrDayGraphData.get(5).getfValue());
        result.setValueOutbreak(arrDayGraphData.get(6).getfValue());

        return result;
    }

    static ChartItem getWeekGraphData(ArrayList<GraphDataModal> ArrOfGraph){
        ChartItem result = new ChartItem();
        ArrayList<String> labels = new ArrayList<>();
        ArrayList<String> week_labels = new ArrayList<>();

        ArrayList<ValuesData> arrWeekGraphData = new ArrayList<>();
        arrWeekGraphData.add(new ValuesData("Particles Count"));
        arrWeekGraphData.add(new ValuesData("Allergen Count"));
        arrWeekGraphData.add(new ValuesData("Humidity"));
        arrWeekGraphData.add(new ValuesData("Temperature"));
        arrWeekGraphData.add(new ValuesData("Sunlight"));
        arrWeekGraphData.add(new ValuesData("Harmful Gas"));
        arrWeekGraphData.add(new ValuesData("Outbreak"));

        for( GraphDataModal item : ArrOfGraph ){
            if(item.getParameter().equalsIgnoreCase(arrWeekGraphData.get(6).str)){  // "Outbreak"
                break;
            }
            Boolean isInclude = false;
            for(String label : labels){
                if(item.getLogDate().equalsIgnoreCase(label)) {
                    isInclude = true;
                    break;
                }
            }
            if(!isInclude)
                labels.add(item.getLogDate());
        }
        Collections.sort(labels, new Comparator<String>() {
            @Override
            public int compare(String lhs, String rhs) {
                return lhs.compareToIgnoreCase(rhs);
            }
        });

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        for (String label : labels){
            Calendar c = Calendar.getInstance();
            try {
                c.setTime(format.parse(label)); // yourdate is a object of type Date
            } catch (ParseException e) {
                e.printStackTrace();
            }

            int dayOfWeek = c.get(Calendar.DAY_OF_WEEK); // this will for example return 2 for tuesday
            switch (dayOfWeek){
                case Calendar.MONDAY:
                    week_labels.add("MON");
                    break;
                case Calendar.TUESDAY:
                    week_labels.add("TUE");
                    break;
                case Calendar.WEDNESDAY:
                    week_labels.add("WED");
                    break;
                case Calendar.THURSDAY:
                    week_labels.add("THU");
                    break;
                case Calendar.FRIDAY:
                    week_labels.add("FRI");
                    break;
                case Calendar.SATURDAY:
                    week_labels.add("SAT");
                    break;
                case Calendar.SUNDAY:
                    week_labels.add("SUN");
                    break;
            }
        }
        for ( ValuesData weekGraph : arrWeekGraphData ){
            weekGraph.setCount(labels.size());
        }

        String[] mStringArray = new String[week_labels.size()];
        mStringArray = week_labels.toArray(mStringArray);
        result.setLabel(mStringArray);
        for( GraphDataModal item : ArrOfGraph ){
            for( ValuesData weekGraph : arrWeekGraphData ){
                if(item.getParameter().equalsIgnoreCase(weekGraph.str)){
                    for(int k=0; k<labels.size(); k++){
                        if( item.getLogDate().equalsIgnoreCase(labels.get(k))){
                            weekGraph.setfValueWithIndex(Float.parseFloat(item.getLogValue()), k);
                            break;
                        }
                    }
                    break;
                }
            }

        }
        result.setValueParticles(arrWeekGraphData.get(0).getfValue());
        result.setValueAllergen(arrWeekGraphData.get(1).getfValue());
        result.setValueHumidity(arrWeekGraphData.get(2).getfValue());
        result.setValueTemperature(arrWeekGraphData.get(3).getfValue());
        result.setValueSunlight(arrWeekGraphData.get(4).getfValue());
        result.setValueGas(arrWeekGraphData.get(5).getfValue());
        result.setValueOutbreak(arrWeekGraphData.get(6).getfValue());

        return result;
    }
    static ChartItem getMonthGraphData(ArrayList<GraphDataModal> ArrOfGraph){
        ChartItem result = new ChartItem();

        return result;
    }
    static ChartItem getYearGraphData(ArrayList<GraphDataModal> ArrOfGraph){
        ChartItem result = new ChartItem();

        return result;
    }
}
