package com.wondereight.airsensio.Interface;

/**
 * Created by VENUSPC on 2/26/2016.
 */
public interface ThreadCallback {
    void runSuccessCallback();
    void runFailCallback(String error);
}
