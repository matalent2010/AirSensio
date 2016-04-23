package com.wondereight.sensioair.Modal;


public class DataDetailsModal
{
    private String str_category;
    private String str_category_color;
    private String str_parameter;
    private String log_value;
    private String log_unit;
    private String log_intensity;

    public DataDetailsModal(){
        str_category = "";
        str_category_color = "";
        str_parameter = "";
        log_value = "";
        log_unit = "";
        log_intensity = "";
    }
    public DataDetailsModal(String str_category, String str_category_color, String str_parameter, String log_value, String log_unit, String log_intensity)
    {
        this.str_category = str_category;
        this.str_category_color = str_category_color;
        this.str_parameter = str_parameter;
        this.log_value = log_value;
        this.log_unit = log_unit;
        this.log_intensity = log_intensity;
    }
    public String getCategory()
    {
        return str_category;
    }
    public void setCategory(String str_category) {
        this.str_category = str_category;
    }

    public String getCategoryColor()
    {
        return str_category_color;
    }
    public void setCategoryColor(String str_category_color) {
        this.str_category_color = str_category_color;
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

    public String getLogUnit() {
        return log_unit;
    }
    public void setLogUnit(String log_unit) {
        this.log_unit = log_unit;
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
