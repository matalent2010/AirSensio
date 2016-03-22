package com.wondereight.airsensio.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;

import com.wondereight.airsensio.R;
import com.wondereight.airsensio.UtilClass.Global;
import com.wondereight.airsensio.UtilClass.UtilityClass;

public class SplashScreen extends AppCompatActivity {
    /** Duration of wait **/
    private final int SPLASH_DISPLAY_LENGTH = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splashscreen_activity);

        /* New Handler to start the Menu-Activity
         * and close this Splash-Screen after some seconds.*/
        Global.GetInstance().SetNetworkState(UtilityClass.isInternetConnection(SplashScreen.this));

        UtilityClass utilityClass = new UtilityClass(SplashScreen.this);
        if( utilityClass.isHavingSymptomList() ){
            utilityClass.sendSymptomList(new Runnable() {
                @Override
                public void run() {
                    goMainActivity();
                }
            });
        } else {
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
}
