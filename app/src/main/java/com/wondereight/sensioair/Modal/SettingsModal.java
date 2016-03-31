package com.wondereight.sensioair.Modal;

/**
 * Created by VENUSPC on 3/17/2016.
 */
public class SettingsModal {
    private String allergens_value;
    private String pollution_value;

    public SettingsModal(){
        allergens_value = "5";
        pollution_value = "5";
    }
    public SettingsModal(String allergens_value, String pollution_value)
    {
        this.allergens_value = allergens_value;
        this.pollution_value = pollution_value;
    }

    public SettingsModal(int allergens_value, int pollution_value)
    {
        this.allergens_value = String.valueOf(allergens_value);
        this.pollution_value = String.valueOf(pollution_value);
    }

    public void setValues(String allergens_value, String pollution_value)
    {
        this.allergens_value = allergens_value;
        this.pollution_value = pollution_value;
    }

    public void setValues(int allergens_value, int pollution_value)
    {
        this.allergens_value = String.valueOf(allergens_value);
        this.pollution_value = String.valueOf(pollution_value);
    }

    public int getAllergensValue(){
        return Integer.valueOf(allergens_value);
    }

    public int getPollutionValue(){
        return Integer.valueOf(pollution_value);
    }
}
