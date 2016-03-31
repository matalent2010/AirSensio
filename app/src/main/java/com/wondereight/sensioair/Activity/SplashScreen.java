package com.wondereight.sensioair.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.wondereight.sensioair.R;
import com.wondereight.sensioair.UtilClass.Global;
import com.wondereight.sensioair.UtilClass.UtilityClass;
import com.wondereight.sensioair.gcm.SAPreferences;
import com.wondereight.sensioair.gcm.RegistrationIntentService;

public class SplashScreen extends AppCompatActivity {
    /** Duration of wait **/
    private final int SPLASH_DISPLAY_LENGTH = 2000;
    UtilityClass utilityClass;
    private static final String LOG_TAG = "SplashScreen";
    private boolean mNothingPreWork = true;

    /* for GCM Register and tocken*/
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private BroadcastReceiver mRegistrationBroadcastReceiver;
//    private ProgressBar mRegistrationProgressBar;
//    private TextView mInformationTextView;
    private boolean isReceiverRegistered;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splashscreen_activity);

        /* New Handler to start the Menu-Activity
         * and close this Splash-Screen after some seconds.*/
        Global.GetInstance().SetNetworkState(UtilityClass.isInternetConnection(SplashScreen.this));

        utilityClass = new UtilityClass(SplashScreen.this);
        if( utilityClass.isHavingSymptomList() ){
            utilityClass.sendSymptomList(new Runnable() {
                @Override
                public void run() {
                    goMainActivity();
                }
            });
            mNothingPreWork = false;
        }

//        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                //mRegistrationProgressBar.setVisibility(ProgressBar.GONE);
//                SharedPreferences sharedPreferences =
//                        PreferenceManager.getDefaultSharedPreferences(context);
//                boolean sentToken = sharedPreferences
//                        .getBoolean(SAPreferences.SENT_TOKEN_TO_SERVER, false);
//                if (sentToken) {
//                    Log.i(LOG_TAG, getString(R.string.gcm_send_message)); //mInformationTextView.setText(getString(R.string.gcm_send_message));
//                } else {
//                    Log.i(LOG_TAG, getString(R.string.token_error_message)); //mInformationTextView.setText(getString(R.string.token_error_message));
//                }
//            }
//        };
        // mInformationTextView = (TextView) findViewById(R.id.informationTextView);

//        if( true ) {
//            // Registering BroadcastReceiver
//            registerReceiver();
//
//            if (checkPlayServices()) {
//                // Start IntentService to register this application with GCM.
//                Intent intent = new Intent(this, RegistrationIntentService.class);
//                startService(intent);
//            }
//            mNothingPreWork = false;
//        }
        if ( mNothingPreWork ) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    goMainActivity();
                }
            }, SPLASH_DISPLAY_LENGTH);
        }

    }

    private void goMainActivity(){
        /* Create an Intent that will start the Menu-Activity. */
        Intent mainIntent = new Intent(SplashScreen.this, MainActivity.class);
        SplashScreen.this.startActivity(mainIntent);
        overridePendingTransition(R.anim.slide_fade_in, R.anim.slide_fade_out);
        SplashScreen.this.finish();
    }


    private void registerReceiver(){
        if(!isReceiverRegistered) {
            LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                    new IntentFilter(SAPreferences.REGISTRATION_COMPLETE));
            isReceiverRegistered = true;
        }
    }
    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {

                Log.i(LOG_TAG, "This device is not supported.");
                //finish();
            }
            return false;
        }
        return true;
    }

}
