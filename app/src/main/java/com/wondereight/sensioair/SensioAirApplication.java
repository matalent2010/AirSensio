package com.wondereight.sensioair;


import android.support.multidex.MultiDexApplication;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Base64;
import android.util.Log;

import io.fabric.sdk.android.Fabric;

import com.crashlytics.android.Crashlytics;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.wondereight.sensioair.CustomView.FontsOverride;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import io.fabric.sdk.android.Fabric;


/**
 * Created by VENUSPC on 4/2/2016.
 */
public class SensioAirApplication extends MultiDexApplication {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_CONSUMER_KEY = "9BTgHWtXNZLWkECtTlMjcdN41"; //m
    private static final String TWITTER_CONSUMER_SECRET = "ESGOagVAh0UFEKaCBwIczPVHFTBevuWt2rdOiGadoSrXglkTM0"; //m

    @Override
    public void onCreate() {
        super.onCreate();

        FontsOverride.setDefaultFonts(this);
        FontsOverride.setDefaultFontForTypeFaceMonospace(this);

        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_CONSUMER_KEY, TWITTER_CONSUMER_SECRET);
        Fabric.with(this, new Twitter(authConfig));
        Fabric.with(this, new Crashlytics());
        printHashKey();
    }

    public void printHashKey() {

        try{
            Log.d("PackageName", getApplicationContext().getPackageName());
            PackageInfo info = getPackageManager().getPackageInfo(
                    getApplicationContext().getPackageName(),
                    PackageManager.GET_SIGNATURES);
            for(Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }

        } catch (PackageManager.NameNotFoundException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

}
