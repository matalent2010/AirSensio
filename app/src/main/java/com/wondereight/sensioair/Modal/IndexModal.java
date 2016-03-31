package com.wondereight.sensioair.Modal;

/**
 * Created by VENUSPC on 3/17/2016.
 */
public class IndexModal {
    private String str_index;
    private String log_intensity;

    public IndexModal(){
        str_index = "0";
        log_intensity = "";
    }
    public IndexModal(String indexValue, String log_intensity)
    {
        this.str_index = indexValue;
        this.log_intensity = log_intensity;
    }

    public String getIndexValue()
    {
        return str_index;
    }

    public void setIndexValue(String indexValue) {
        this.str_index = indexValue;
    }

    public String getLogIntensity() {
        return log_intensity;
    }
    public void setLogIntensity(String log_intensity) {
        this.log_intensity = log_intensity;
    }

    public boolean isSet(){
        return !log_intensity.isEmpty();
    }
}
