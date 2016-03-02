package com.wondereight.airsensio.Modal;

/**
 * Created by Miguel on 2/29/2016.
 */
public class DeviceDataModal {
    String env_parameter_id = "";
    String env_value = "";
    String log_datetime = "";
    String city_id = "";

    public String getEnvId() {
        return env_parameter_id;
    }

    public void setEnvId(String envId) {
        this.env_parameter_id = envId;
    }

    public String getEnvValue() {
        return env_value;
    }

    public void setEnvValue(String envValue) {
        this.env_value = envValue;
    }

    public String getDatetime() {
        return log_datetime;
    }

    public void setDatetime(String log_datetime) {
        this.log_datetime = log_datetime;
    }

    public String getCityId() {
        return city_id;
    }

    public void setCityId(String city_id) {
        this.city_id = city_id;
    }


}
