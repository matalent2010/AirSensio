package com.wondereight.airsensio.UtilClass;

import android.support.annotation.Nullable;
import android.util.Log;

import com.wondereight.airsensio.Helper._Debug;
import com.wondereight.airsensio.Modal.CityModal;
import com.wondereight.airsensio.Modal.DataDetailsModal;
import com.wondereight.airsensio.Modal.DeviceDataModal;
import com.wondereight.airsensio.Modal.GraphDataModal;
import com.wondereight.airsensio.Modal.IndexModal;
import com.wondereight.airsensio.Modal.UserModal;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
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
                item.setNumWeek(obj_item.optString(Constant.STR_NUM_WEEK));
                item.setLabel(obj_item.optString(Constant.STR_LABEL));
                graphDataArray.add(item);
                _debug.d(LOG_TAG, "GraphStyle(" + String.valueOf(state) + ") Data " + i + " : " + obj_item.toString());
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

    private static class ValuesData {
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


    private static ChartItem getDayGraphData(ArrayList<GraphDataModal> ArrOfGraph){
        ChartItem result = new ChartItem();
//        ArrayList<String> labels = new ArrayList<>();
        ArrayList<String> short_labels = new ArrayList(Arrays.asList(new String[]{"00:00", "03:00", "06:00", "09:00", "12:00", "15:00", "18:00", "21:00", "24:00"}));

        ArrayList<ValuesData> arrDayGraphData = new ArrayList<>();
        arrDayGraphData.add(new ValuesData("Particle Count"));
        arrDayGraphData.add(new ValuesData("Allergen Count"));
        arrDayGraphData.add(new ValuesData("Humidity"));
        arrDayGraphData.add(new ValuesData("Temperature"));
        arrDayGraphData.add(new ValuesData("UV Index"));
        arrDayGraphData.add(new ValuesData("Harmful Gas"));
        arrDayGraphData.add(new ValuesData("Outbreak"));

        ValuesDataForDayOutbreak dayOutbreakData = new ValuesDataForDayOutbreak();
//        for( GraphDataModal item : ArrOfGraph ){
//            if(item.getParameter().equalsIgnoreCase(arrDayGraphData.get(6).str)){  // "Outbreak"
//                break;
//            }
//            Boolean isInclude = false;
//            for(String label : labels){
//                if(item.getLogTime().equalsIgnoreCase(label)) {
//                    isInclude = true;
//                    break;
//                }
//            }
//            if(!isInclude)
//                labels.add(item.getLogTime());
//        }
//        Collections.sort(labels, new Comparator<String>() {
//            @Override
//            public int compare(String lhs, String rhs) {
//                return lhs.compareToIgnoreCase(rhs);
//            }
//        });
//        for (String label : labels){
//            short_labels.add(label.substring(0,label.length()-3));
//        }
        for ( ValuesData dayGraph : arrDayGraphData ){
            dayGraph.setCount(short_labels.size());
        }

        String[] mStringArray = new String[short_labels.size()];
        mStringArray = short_labels.toArray(mStringArray);
        result.setLabel(mStringArray);
        for( GraphDataModal item : ArrOfGraph ){
            if(item.getParameter().equalsIgnoreCase(arrDayGraphData.get(6).str)){  // "Outbreak"
                dayOutbreakData.addData(item.getLogTime(), Float.parseFloat(item.getLogValue()));
            }
            for(int j=0; j<6/*arrDayGraphData.size()-1*/; j++){
                if(item.getParameter().equalsIgnoreCase(arrDayGraphData.get(j).str)){
                    for(int k=0; k<short_labels.size(); k++){
                        String logTime = item.getLogTime();
                        if( logTime.substring(0, logTime.length()-3).equalsIgnoreCase(short_labels.get(k))){
                            arrDayGraphData.get(j).setfValueWithIndex(Float.parseFloat(item.getLogValue()),k);
                            break;
                        }
                    }
                    break;
                }
            }
        }
        dayOutbreakData.sortValue();

        result.setValueParticles(arrDayGraphData.get(0).getfValue());
        result.setValueAllergen(arrDayGraphData.get(1).getfValue());
        result.setValueHumidity(arrDayGraphData.get(2).getfValue());
        result.setValueTemperature(arrDayGraphData.get(3).getfValue());
        result.setValueSunlight(arrDayGraphData.get(4).getfValue());
        result.setValueGas(arrDayGraphData.get(5).getfValue());
        result.setValueOutbreak(arrDayGraphData.get(6).getfValue());

        result.setValueDayOutbreak(dayOutbreakData);

        return result;
    }

    private static ChartItem getWeekGraphData(ArrayList<GraphDataModal> ArrOfGraph){
        ChartItem result = new ChartItem();
        ArrayList<String> labels = new ArrayList<>();
        ArrayList<String> week_labels = new ArrayList<>();

        ArrayList<ValuesData> arrWeekGraphData = new ArrayList<>();
        arrWeekGraphData.add(new ValuesData("Particle Count"));
        arrWeekGraphData.add(new ValuesData("Allergen Count"));
        arrWeekGraphData.add(new ValuesData("Humidity"));
        arrWeekGraphData.add(new ValuesData("Temperature"));
        arrWeekGraphData.add(new ValuesData("UV Index"));
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

    private static ChartItem getMonthGraphData(ArrayList<GraphDataModal> ArrOfGraph){
        ChartItem result = new ChartItem();
        ArrayList<String> labels = new ArrayList<>();
        ArrayList<String> month_labels = new ArrayList<>();

        ArrayList<ValuesData> arrMonthGraphData = new ArrayList<>();
        arrMonthGraphData.add(new ValuesData("Particle Count"));
        arrMonthGraphData.add(new ValuesData("Allergen Count"));
        arrMonthGraphData.add(new ValuesData("Humidity"));
        arrMonthGraphData.add(new ValuesData("Temperature"));
        arrMonthGraphData.add(new ValuesData("UV Index"));
        arrMonthGraphData.add(new ValuesData("Harmful Gas"));
        arrMonthGraphData.add(new ValuesData("Outbreak"));

        for( GraphDataModal item : ArrOfGraph ){
            Boolean isInclude = false;
            for( String label : labels){
                if(item.getNumWeek().equalsIgnoreCase(label))
                {
                    isInclude = true;
                    break;
                }
            }
            if(!isInclude)
                labels.add(item.getNumWeek());
        }
        Collections.sort(labels, new Comparator<String>() {
            @Override
            public int compare(String lhs, String rhs) {
                return lhs.compareToIgnoreCase(rhs);
            }
        });

        for( String label : labels ) {
            for (GraphDataModal item : ArrOfGraph) {
                if (item.getNumWeek().equalsIgnoreCase(label)) {
                    month_labels.add(item.getLabel());
                    break;
                }
            }
        }

        for( ValuesData monthGraph : arrMonthGraphData){
            monthGraph.setCount(month_labels.size());
        }
        String[] mStringArray = new String[month_labels.size()];
        mStringArray = month_labels.toArray(mStringArray);
        result.setLabel(mStringArray);

        for( GraphDataModal item : ArrOfGraph ){
            for( ValuesData monthGraph : arrMonthGraphData ){
                if(item.getParameter().equalsIgnoreCase(monthGraph.str)){
                    for(int k=0; k<labels.size(); k++){
                        if( item.getNumWeek().equalsIgnoreCase(labels.get(k))){
                            monthGraph.setfValueWithIndex(Float.parseFloat(item.getLogValue()), k);
                            break;
                        }
                    }
                    break;
                }
            }
        }
        result.setValueParticles(arrMonthGraphData.get(0).getfValue());
        result.setValueAllergen(arrMonthGraphData.get(1).getfValue());
        result.setValueHumidity(arrMonthGraphData.get(2).getfValue());
        result.setValueTemperature(arrMonthGraphData.get(3).getfValue());
        result.setValueSunlight(arrMonthGraphData.get(4).getfValue());
        result.setValueGas(arrMonthGraphData.get(5).getfValue());
        result.setValueOutbreak(arrMonthGraphData.get(6).getfValue());

        return result;
    }
    private static ChartItem getYearGraphData(ArrayList<GraphDataModal> ArrOfGraph){
        ChartItem result = new ChartItem();
        ArrayList<String> year_labels = new ArrayList(Arrays.asList(new String[]{"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct","Nev", "Dec"}));

        ArrayList<ValuesData> arrYearGraphData = new ArrayList<>();
        arrYearGraphData.add(new ValuesData("Particle Count"));
        arrYearGraphData.add(new ValuesData("Allergen Count"));
        arrYearGraphData.add(new ValuesData("Humidity"));
        arrYearGraphData.add(new ValuesData("Temperature"));
        arrYearGraphData.add(new ValuesData("UV Index"));
        arrYearGraphData.add(new ValuesData("Harmful Gas"));
        arrYearGraphData.add(new ValuesData("Outbreak"));

        for( ValuesData yearGraph : arrYearGraphData){
            yearGraph.setCount(year_labels.size());
        }
        String[] mStringArray = new String[year_labels.size()];
        mStringArray = year_labels.toArray(mStringArray);
        result.setLabel(mStringArray);

        for( GraphDataModal item : ArrOfGraph ){
            for( ValuesData yearGraph : arrYearGraphData ){
                if(item.getParameter().equalsIgnoreCase(yearGraph.str)){
                    for(int k=0; k<year_labels.size(); k++){
                        if( item.getLabel().equalsIgnoreCase(year_labels.get(k))){
                            yearGraph.setfValueWithIndex(Float.parseFloat(item.getLogValue()), k);
                            break;
                        }
                    }
                    break;
                }
            }
        }
        result.setValueParticles(arrYearGraphData.get(0).getfValue());
        result.setValueAllergen(arrYearGraphData.get(1).getfValue());
        result.setValueHumidity(arrYearGraphData.get(2).getfValue());
        result.setValueTemperature(arrYearGraphData.get(3).getfValue());
        result.setValueSunlight(arrYearGraphData.get(4).getfValue());
        result.setValueGas(arrYearGraphData.get(5).getfValue());
        result.setValueOutbreak(arrYearGraphData.get(6).getfValue());

        return result;
    }

    public static ArrayList parsingCitiesList(JSONArray arr)
    {
        ArrayList<CityModal> cityList = new ArrayList();
        for( int i=0; i<arr.length(); i++){
            try {
                cityList.add( new CityModal(arr.getJSONObject(i).optString(Constant.STR_CITY), arr.getJSONObject(i).optString(Constant.STR_CITYNAME)));
            } catch (JSONException e) {
                _debug.e(LOG_TAG, "City List JSONException: " + e.getMessage());
                e.printStackTrace();
            }
        }
        return cityList;
    }

    public static ArrayList<IndexModal> parsingIndexData(JSONArray arr){
        ArrayList<IndexModal> result = new ArrayList<>();
        try {
            IndexModal allergyModal = new IndexModal();
            IndexModal pollutionModal = new IndexModal();

            JSONObject obj = arr.getJSONObject(0);
            allergyModal.setIndexValue(String.valueOf(obj.optInt(Constant.STR_ALLERGYINDEX)));
            allergyModal.setLogIntensity(obj.optString(Constant.STR_LOGINTENSITY));
            obj = arr.getJSONObject(1);
            pollutionModal.setIndexValue(String.valueOf(obj.optInt(Constant.STR_POLLUTIONINDEX)));
            pollutionModal.setLogIntensity(obj.optString(Constant.STR_LOGINTENSITY));

            result.add(allergyModal);
            result.add(pollutionModal);

        } catch (JSONException e) {
            e.printStackTrace();
            _debug.e(LOG_TAG, "IndexModal JSONArray Fail: " + e.getMessage());
            result.add( new IndexModal() );
            result.add( new IndexModal() );
        }
        return result;
    }

    public static ArrayList<DataDetailsModal> parsingDataDetails(JSONArray arr){
        ArrayList<DataDetailsModal> result = new ArrayList<>();
        for ( int i=0; i<arr.length(); i++ ) {
            DataDetailsModal modal = new DataDetailsModal();
            try {
                JSONObject obj = arr.getJSONObject(i);
                modal.setParameter(obj.optString(Constant.STR_PARAMETER));
                modal.setLogValue(obj.optString(Constant.STR_VALUE));
                modal.setLogIntensity(obj.optString(Constant.STR_LOGINTENSITY));

                result.add(modal);
            } catch (JSONException e) {
                e.printStackTrace();
                _debug.e(LOG_TAG, "DataDetailsModal JSONArray Fail: " + e.getMessage());
            }
        }
        return result;
    }

    public static ArrayList<IndexModal> parsingSavedInfo(JSONArray arr){
        ArrayList<IndexModal> result = new ArrayList<>();
//        try {
//            IndexModal allergyModal = new IndexModal();
//            IndexModal pollutionModal = new IndexModal();
//
//            JSONObject obj = arr.getJSONObject(0);
//            allergyModal.setIndexValue(String.valueOf(obj.optInt(Constant.STR_ALLERGYINDEX)));
//            allergyModal.setLogIntensity(obj.optString(Constant.STR_LOGINTENSITY));
//            obj = arr.getJSONObject(1);
//            pollutionModal.setIndexValue(String.valueOf(obj.optInt(Constant.STR_POLLUTIONINDEX)));
//            pollutionModal.setLogIntensity(obj.optString(Constant.STR_LOGINTENSITY));
//
//            result.add(allergyModal);
//            result.add(pollutionModal);
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//            _debug.e(LOG_TAG, "IndexModal JSONArray Fail: " + e.getMessage());
//            result.add( new IndexModal() );
//            result.add( new IndexModal() );
//        }
        return result;
    }
}
