package com.wondereight.airsensio.Modal;


public class DataDetailsModal
{
    private String str_parameter;
    private String log_value;
    private String log_intensity;

    public DataDetailsModal(){
        str_parameter = "";
        log_value = "";
        log_intensity = "";
    }
    public DataDetailsModal(String str_parameter, String log_value, String log_intensity)
    {
        this.str_parameter = str_parameter;
        this.log_value = log_value;
        this.log_intensity = log_intensity;
    }

    public String getParameter()
    {
        return str_parameter;
    }
    public void setParameter(String str_parameter) {
        this.str_parameter = str_parameter;
    }

    public String getLogValue()
    {
        return log_value;
    }
    public void setLogValue(String log_value) {
        this.log_value = log_value;
    }

    public String getLogIntensity() {
        return log_intensity;
    }
    public void setLogIntensity(String log_intensity) {
        this.log_intensity = log_intensity;
    }

    public boolean isSet(){
        return !str_parameter.isEmpty();
    }
}
