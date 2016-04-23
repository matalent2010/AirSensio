package com.wondereight.sensioair;


import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.multidex.MultiDexApplication;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import io.fabric.sdk.android.Fabric;

import com.crashlytics.android.Crashlytics;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.wondereight.sensioair.Activity.HomeActivity;
import com.wondereight.sensioair.CustomView.FontsOverride;
import com.wondereight.sensioair.UtilClass.Global;
import com.wondereight.sensioair.UtilClass.UtilityClass;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.concurrent.ExecutionException;


/**
 * Created by VENUSPC on 4/2/2016.
 */
public class SensioAirApplication extends MultiDexApplication {

    private static final String LOG_TAG = "Application";

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_CONSUMER_KEY = "9BTgHWtXNZLWkECtTlMjcdN41"; //m  //"LklqPP0PO24ILtWK59I2m8PYb"; //client //
    private static final String TWITTER_CONSUMER_SECRET = "ESGOagVAh0UFEKaCBwIczPVHFTBevuWt2rdOiGadoSrXglkTM0"; //m //"vObhW72i8rieeZZmarPtPz9RRsnVDJ9QQMpSerjed3TqXgWNXL"; //client //

    public static BroadcastReceiver brNetStatus = null;

    @Override
    public void onCreate() {
        super.onCreate();

        FontsOverride.setDefaultFonts(this);
        FontsOverride.setDefaultFontForTypeFaceMonospace(this);

        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_CONSUMER_KEY, TWITTER_CONSUMER_SECRET);
        Fabric.with(this, new Crashlytics(), new Twitter(authConfig));
        printHashKey();

        registerActivityLifecycleCallbacks(new activityLife());
        //registerBRNetStatus();
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

    private void registerBRNetStatus(){
        if( brNetStatus == null) {
            brNetStatus = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    boolean foreground;
                    try {
                        foreground = new ForegroundCheckTask().execute(context).get();
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                        foreground = false;
                    }

                    if( UtilityClass.isInternetConnection(context) ){
                        Log.d(LOG_TAG, "NetStatus: online");
                        if( !Global.GetInstance().IsOnlineMode() ) {
                            Global.GetInstance().SetOnlineMode(true);
                            UtilityClass utilityClass = new UtilityClass(context);
                            utilityClass.sendSymptomList( );

                            if( foreground )
                                Toast.makeText(getApplicationContext(), "You are working on online mode", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Log.d(LOG_TAG, "NetStatus: offline");
                        if( Global.GetInstance().IsOnlineMode() ){
                            Global.GetInstance().SetOnlineMode(false);

                            if( foreground )
                                Toast.makeText(getApplicationContext(), "You are working on offline mode", Toast.LENGTH_LONG).show();
                        }
                    }
                }
            };
        }

        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(brNetStatus, intentFilter);
    }

    private void unRegisterBRNetStatus(){
        unregisterReceiver(brNetStatus);
    }

    class activityLife implements ActivityLifecycleCallbacks{

        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            if( activity.getClass().getSimpleName().equalsIgnoreCase(HomeActivity.class.getSimpleName()) ){
                registerBRNetStatus();
            }
        }

        @Override
        public void onActivityStarted(Activity activity) {

        }

        @Override
        public void onActivityResumed(Activity activity) {

        }

        @Override
        public void onActivityPaused(Activity activity) {

        }

        @Override
        public void onActivityStopped(Activity activity) {

        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            if( activity.getClass().getSimpleName().equalsIgnoreCase(HomeActivity.class.getSimpleName()) ){
                unRegisterBRNetStatus();
            }
        }
    }

    class ForegroundCheckTask extends AsyncTask<Context, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Context... params) {
            final Context context = params[0].getApplicationContext();
            return isAppOnForeground(context);
        }

        private boolean isAppOnForeground(Context context) {
            ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
            if (appProcesses == null) {
                return false;
            }
            final String packageName = context.getPackageName();
            for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
                if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND && appProcess.processName.equals(packageName)) {
                    return true;
                }
            }
            return false;
        }
    }
}
