package com.wondereight.airsensio.Modal;

/**
 * Created by Miguel on 2/29/2016.
 */
public class GraphDataModal {
    String parameter = "";
    String log_value  = "";
    String log_date = "";
    String log_time = "";
    String label = "";
    String num_week = "";

    public String getParameter() {
        return parameter;
    }

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }

    public String getLogValue() {
        return log_value;
    }

    public void setLogValue(String log_value) {
        this.log_value = log_value;
    }

    public String getLogDate() {
        return log_date;
    }

    public void setLogDate(String log_date) {
        this.log_date = log_date;
    }

    public String getLogTime() {
        return log_time;
    }

    public void setLogTime(String log_time) {
        this.log_time = log_time;
    }

    public String getNumWeek()
    {
        return num_week;
    }

    public void setNumWeek(String numWeek)
    {
        this.num_week = numWeek;
    }

    public String getLabel()
    {
        return label;
    }

    public void setLabel(String label)
    {
        this.label = label;
    }
}
