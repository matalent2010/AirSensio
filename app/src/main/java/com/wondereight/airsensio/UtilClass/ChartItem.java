package com.wondereight.airsensio.UtilClass;


import com.wondereight.airsensio.Helper._Debug;

/**
 * Created by Miguel on 02/21/2016.
 */

public class ChartItem {
    private static final String LOG_TAG = "LineChartItem";
    private static _Debug _debug = new _Debug(true);

    private int nCount;
    private String[] strLabel;
    private float[] fValue_Particles;
    private float[] fValue_Allergen;
    private float[] fValue_Humidity;
    private float[] fValue_Temperature;
    private float[] fValue_Sunlight;
    private float[] fValue_Gas;
    private float[] fValue_outbreak;


    public ChartItem(){
        nCount = 0;
        strLabel = null;
        fValue_Particles = null;
        fValue_Allergen = null;
        fValue_Humidity = null;
        fValue_Temperature = null;
        fValue_Sunlight = null;
        fValue_Gas = null;
    }

    public ChartItem(String[] label, float[] particles, float[] allergen, float[] humidity, float[] temperature, float[] sunlight, float[] gas){
        nCount = label.length;
        strLabel = new String[nCount];

        for( int i=0; i<nCount; i++){
            strLabel[i] = label[i];
        }
        setValueParticles(particles);
        setValueAllergen(allergen);
        setValueHumidity(humidity);
        setValueTemperature(temperature);
        setValueSunlight(sunlight);
        setValueGas(gas);
    }

    public void setLabel(String[] label){
        nCount = label.length;
        strLabel = new String[nCount];

        for( int i=0; i<nCount; i++){
            strLabel[i] = label[i];
        }
    }

    public void setValueParticles(float[] particles){

        fValue_Particles = new float[nCount];
        if ( nCount > particles.length )
        {
            for (int i = 0; i < particles.length; i++) {
                fValue_Particles[i] = particles[i];
            }
            for (int i=particles.length; i<nCount; i++){
                fValue_Particles[i] = 0;
            }
            _debug.e(LOG_TAG, "The length of particles Values list is less than X-Axis values");

        } else {
            for (int i = 0; i < nCount; i++) {
                fValue_Particles[i] = particles[i];
            }
        }
    }

    public void setValueAllergen(float[] allergen){

        fValue_Allergen = new float[nCount];
        if ( nCount > allergen.length )
        {
            for (int i = 0; i < allergen.length; i++) {
                fValue_Allergen[i] = allergen[i];
            }
            for (int i=allergen.length; i<nCount; i++){
                fValue_Allergen[i] = 0;
            }
            _debug.e(LOG_TAG, "The length of allergen Values list is less than X-Axis values");

        } else {
            for (int i = 0; i < nCount; i++) {
                fValue_Allergen[i] = allergen[i];
            }
        }
    }

    public void setValueHumidity(float[] humidity){

        fValue_Humidity = new float[nCount];
        if ( nCount > humidity.length )
        {
            for (int i = 0; i < humidity.length; i++) {
                fValue_Humidity[i] = humidity[i];
            }
            for (int i=humidity.length; i<nCount; i++){
                fValue_Humidity[i] = 0;
            }
            _debug.e(LOG_TAG, "The length of humidity Values list is less than X-Axis values");

        } else {
            for (int i = 0; i < nCount; i++) {
                fValue_Humidity[i] = humidity[i];
            }
        }
    }

    public void setValueTemperature(float[] temperature){

        fValue_Temperature = new float[nCount];
        if ( nCount > temperature.length )
        {
            for (int i = 0; i < temperature.length; i++) {
                fValue_Temperature[i] = temperature[i];
            }
            for (int i=temperature.length; i<nCount; i++){
                fValue_Temperature[i] = 0;
            }
            _debug.e(LOG_TAG, "The length of temperature Values list is less than X-Axis values");

        } else {
            for (int i = 0; i < nCount; i++) {
                fValue_Temperature[i] = temperature[i];
            }
        }
    }

    public void setValueSunlight(float[] sunlight){

        fValue_Sunlight = new float[nCount];
        if ( nCount > sunlight.length )
        {
            for (int i = 0; i < sunlight.length; i++) {
                fValue_Sunlight[i] = sunlight[i];
            }
            for (int i=sunlight.length; i<nCount; i++){
                fValue_Sunlight[i] = 0;
            }
            _debug.e(LOG_TAG, "The length of sunlight Values list is less than X-Axis values");

        } else {
            for (int i = 0; i < nCount; i++) {
                fValue_Sunlight[i] = sunlight[i];
            }
        }
    }

    public void setValueGas(float[] gas){

        fValue_Gas = new float[nCount];
        if ( nCount > gas.length )
        {
            for (int i = 0; i < gas.length; i++) {
                fValue_Gas[i] = gas[i];
            }
            for (int i=gas.length; i<nCount; i++){
                fValue_Gas[i] = 0;
            }
            _debug.e(LOG_TAG, "The length of gas Values list is less than X-Axis values");

        } else {
            for (int i = 0; i < nCount; i++) {
                fValue_Gas[i] = gas[i];
            }
        }
    }

    public void setValueOutbreak(float[] outbreak){

        fValue_outbreak = new float[nCount];
        if ( nCount > outbreak.length )
        {
            for (int i = 0; i < outbreak.length; i++) {
                fValue_outbreak[i] = outbreak[i];
            }
            for (int i=outbreak.length; i<nCount; i++){
                fValue_outbreak[i] = 0;
            }
            _debug.e(LOG_TAG, "The length of gas Values list is less than X-Axis values");

        } else {
            for (int i = 0; i < nCount; i++) {
                fValue_outbreak[i] = outbreak[i];
            }
        }
    }

    public String[] getLabel(){
        return strLabel;
    }
    public float[] getValueParticles(){
        return fValue_Particles;
    }
    public float[] getValueAllergen(){
        return fValue_Allergen;
    }
    public float[] getValueHumidity(){
        return fValue_Humidity;
    }
    public float[] getValueTemperature(){
        return fValue_Temperature;
    }
    public float[] getValueSunlight(){
        return fValue_Sunlight;
    }
    public float[] getValueGas(){
        return fValue_Gas;
    }
    public float[] getValueOutbreak(){
        return fValue_outbreak;
    }

}
