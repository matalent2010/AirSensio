package com.wondereight.airsensio.UtilClass;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ValuesDataForDayOutbreak {
    String str = "DayOutbreak";
    ArrayList<PairEntry> logTime = new ArrayList<>();

    public ValuesDataForDayOutbreak(){}
    public void addData(String label, float value){
        addData(new PairEntry(label, value));
    }

    public void addData(PairEntry entry){
        logTime.add(entry);
    }

    public PairEntry getData(int index){
        return logTime.get(index);
    }

    public float[] getfValue(){
        float[] result = new float[logTime.size()];
        for( int i=0; i<logTime.size(); i++) {
            result[i] = logTime.get(i).getValue();
        }
        return result;
    }

    public float[] getPercentValue(){
        float[] result = new float[logTime.size()];
        for( int i=0; i<logTime.size(); i++) {
            result[i] = logTime.get(i).getPercentValue();
        }
        return result;
    }

    public void sortValue(){
        Collections.sort(logTime, new Comparator<PairEntry>() {
            @Override
            public int compare(PairEntry lhs, PairEntry rhs) {
                return lhs.getLabel().compareToIgnoreCase(rhs.getLabel());
            }
        });
    }
}
