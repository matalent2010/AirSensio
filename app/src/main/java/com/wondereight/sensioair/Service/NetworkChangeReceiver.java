package com.wondereight.sensioair.Service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.wondereight.sensioair.UtilClass.Global;
import com.wondereight.sensioair.UtilClass.UtilityClass;

/**
 * Created by Miguel on 3/22/2016.
 */
public class NetworkChangeReceiver extends BroadcastReceiver {
    private static final String LOG_TAG = "NetworkChangeReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        if( UtilityClass.isInternetConnection(context) ) {
            Log.i(LOG_TAG, "Internet : online");
        } else {
            Log.i(LOG_TAG, "Internet : offline");
        }

        if( UtilityClass.isInternetConnection(context) && !Global.GetInstance().IsOnlineMode() ){
            Global.GetInstance().SetOnlineMode(true);
            UtilityClass utilityClass = new UtilityClass(context);
            utilityClass.sendSymptomList( );
            Toast.makeText(context, "You are working on online mode", Toast.LENGTH_LONG).show();
        } else if( !UtilityClass.isInternetConnection(context) && Global.GetInstance().IsOnlineMode() ){
            Global.GetInstance().SetOnlineMode(false);
            Toast.makeText(context, "You are working on offline mode", Toast.LENGTH_LONG).show();
        }
    }
}
