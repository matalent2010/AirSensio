package com.wondereight.airsensio.UtilClass;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.widget.Toast;

/**
 * Created by Miguel on 3/22/2016.
 */
public class NetworkChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if( UtilityClass.isInternetConnection(context) && !Global.GetInstance().GetNetworkState() ){
            Global.GetInstance().SetNetworkState(true);
            UtilityClass utilityClass = new UtilityClass(context);
            utilityClass.sendSymptomList( );
            //Toast.makeText(context, "Internet connection enabled", Toast.LENGTH_LONG).show();
        } else if( !UtilityClass.isInternetConnection(context) && Global.GetInstance().GetNetworkState() ){
            Global.GetInstance().SetNetworkState(false);
            //Toast.makeText(context, "Not connected to Internet", Toast.LENGTH_LONG).show();
        }
    }
}
