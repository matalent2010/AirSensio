package com.wondereight.sensioair.Modal;

public class CityModal extends Object
{

    private String city_id;
    private String city_name;

    public CityModal(String id, String name)
    {
        city_id = id;
        city_name = name;
    }

    public String getCityId()
    {
        return city_id;
    }

    public String getCityName()
    {
        return city_name;
    }

    public void setCityId(String id)
    {
        this.city_id = id;
    }

    public void setCityName(String name)
    {
        this.city_name = name;
    }

    @Override
    public String toString() {
        return "{ID: " + city_id + ", City:" + city_name + "}";
    }
}
