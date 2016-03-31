package com.wondereight.sensioair.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.Bind;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.wondereight.sensioair.Adapter.ViewPagerAdapter;
import com.wondereight.sensioair.CustomView.CustomViewPager;
import com.wondereight.sensioair.Fragment.HomeFragment;
import com.wondereight.sensioair.Fragment.SensioFragment;
import com.wondereight.sensioair.Fragment.ProfileFragment;
import com.wondereight.sensioair.Fragment.SettingsFragment;
import com.wondereight.sensioair.Fragment.StatisticsFragment;
import com.wondereight.sensioair.Helper._Debug;
import com.wondereight.sensioair.Modal.DeviceDataModal;
import com.wondereight.sensioair.R;
import com.wondereight.sensioair.UtilClass.AirSensioRestClient;
import com.wondereight.sensioair.UtilClass.Constant;
import com.wondereight.sensioair.UtilClass.Global;
import com.wondereight.sensioair.UtilClass.ParsingResponse;
import com.wondereight.sensioair.UtilClass.SaveSharedPreferences;
import com.wondereight.sensioair.UtilClass.UtilityClass;
import com.wondereight.sensioair.gcm.GcmMain;
import com.wondereight.sensioair.gcm.RegistrationIntentService;
import com.wondereight.sensioair.gcm.SAPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Miguel on 02/2/2016.
 */

public class HomeActivity extends FragmentActivity {

    private int curTabIndex;

    public UtilityClass utilityClass;
    private static final String LOG_TAG = "HomeActivity";
    private static _Debug _debug = new _Debug(true);
    private static boolean preorderNow = false;

    private BroadcastReceiver mRegistrationBroadcastReceiver;

    @Bind(R.id.viewpager) public CustomViewPager mTabPager;

    @Bind(R.id.ivitem_home) public ImageView mBtnHomeImage;
    @Bind(R.id.ivitemtext_home) public TextView mBtnHomeText;
    @Bind(R.id.ivitem_statistics) public ImageView mBtnStatisticsImage;
    @Bind(R.id.ivitemtext_statistics) public TextView mBtnStatisticsText;
    @Bind(R.id.ivitem_sensio) public ImageView mBtnSensioImage;
    @Bind(R.id.ivitemtext_sensio) public TextView mBtnSensioText;
    @Bind(R.id.ivitem_profile) public ImageView mBtnProfileImage;
    @Bind(R.id.ivitemtext_profile) public TextView mBtnProfileText;
    @Bind(R.id.ivitem_settings) public ImageView mBtnSettingsImage;
    @Bind(R.id.ivitemtext_settings) public TextView mBtnSettingsText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);
        ButterKnife.bind(HomeActivity.this);

        utilityClass = new UtilityClass(HomeActivity.this);
        //restCallDeviceDataApi();
        mTabPager.setPagingEnabled(true);
        try {
            mTabPager.addOnPageChangeListener( new TabPageChangeListener());
        } catch ( Exception e){
            e.printStackTrace();
        }

        ViewPagerAdapter mPagerAdapter = new ViewPagerAdapter(HomeActivity.this, getSupportFragmentManager());
        mPagerAdapter.addFragment(HomeFragment.newInstance(HomeActivity.this));
        mPagerAdapter.addFragment(StatisticsFragment.newInstance(HomeActivity.this));
        mPagerAdapter.addFragment(SensioFragment.newInstance(HomeActivity.this));
        mPagerAdapter.addFragment(ProfileFragment.newInstance(HomeActivity.this));
        mPagerAdapter.addFragment(SettingsFragment.newInstance(HomeActivity.this));
        mTabPager.setAdapter(mPagerAdapter);
        mTabPager.setCurrentItem(0);

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                SharedPreferences sharedPreferences =
                        PreferenceManager.getDefaultSharedPreferences(context);
                boolean sentToken = sharedPreferences
                        .getBoolean(SAPreferences.SENT_TOKEN_TO_SERVER, false);
                if (sentToken) {
                    _debug.i(LOG_TAG, getString(R.string.gcm_send_message)); //mInformationTextView.setText(getString(R.string.gcm_send_message));
                } else {
                    _debug.i(LOG_TAG, getString(R.string.token_error_message)); //mInformationTextView.setText(getString(R.string.token_error_message));
                }
            }
        };

        GcmMain.GetInstance().registerReceiver(HomeActivity.this, mRegistrationBroadcastReceiver);

        if (GcmMain.GetInstance().checkPlayServices(this)) {
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        GcmMain.GetInstance().registerReceiver(this);
    }

    @Override
    protected void onPause() {
        GcmMain.GetInstance().unregisterReceiver(this);
        super.onPause();
    }

    @OnClick(R.id.btn_home)
    void onClickHome(){
        mTabPager.setCurrentItem(0, false);
    }
    @OnClick(R.id.btn_statistics)
    void onClickStatistics(){
        mTabPager.setCurrentItem(1, false);
    }
    @OnClick(R.id.btn_sensio)
    void onClickSensio(){
        mTabPager.setCurrentItem(2, false);
    }
    @OnClick(R.id.btn_profile)
    void onClickProfile(){
        mTabPager.setCurrentItem(3, false);
    }
    @OnClick(R.id.btn_settings)
    void onClickSettings(){ mTabPager.setCurrentItem(4, false); }

    public class TabPageChangeListener implements OnPageChangeListener {

        @Override
        public void onPageScrollStateChanged(int arg0) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onPageSelected(int arg0) {
            Drawable sDrawableNormal = null;
            switch (curTabIndex) {
                case 0:
                    sDrawableNormal = ContextCompat.getDrawable(getBaseContext(), R.drawable.btn_home_n);
                    mBtnHomeImage.setImageDrawable(sDrawableNormal);
                    mBtnHomeText.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.BottomMenuTextColor_n));
                    break;
                case 1:
                    sDrawableNormal = ContextCompat.getDrawable(getBaseContext(), R.drawable.btn_statistics_n);
                    mBtnStatisticsImage.setImageDrawable(sDrawableNormal);
                    mBtnStatisticsText.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.BottomMenuTextColor_n));
                    break;
                case 2:
                    sDrawableNormal = ContextCompat.getDrawable(getBaseContext(), R.drawable.btn_sensio_n);
                    mBtnSensioImage.setImageDrawable(sDrawableNormal);
                    mBtnSensioText.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.BottomMenuTextColor_n));
                    break;
                case 3:
                    sDrawableNormal = ContextCompat.getDrawable(getBaseContext(), R.drawable.btn_profile_n);
                    mBtnProfileImage.setImageDrawable(sDrawableNormal);
                    mBtnProfileText.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.BottomMenuTextColor_n));
                    break;
                case 4:
                    sDrawableNormal = ContextCompat.getDrawable(getBaseContext(), R.drawable.btn_settings_n);
                    mBtnSettingsImage.setImageDrawable(sDrawableNormal);
                    mBtnSettingsText.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.BottomMenuTextColor_n));
                    break;
            }

            sDrawableNormal.setCallback(null);

            Drawable sDrawablePressed = null;

            switch (arg0) {
                case 0:
                    sDrawablePressed = ContextCompat.getDrawable(getBaseContext(), R.drawable.btn_home_s);
                    mBtnHomeImage.setImageDrawable(sDrawablePressed);
                    mBtnHomeText.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.BottomMenuTextColor_s));
                    break;
                case 1:
                    sDrawablePressed = ContextCompat.getDrawable(getBaseContext(), R.drawable.btn_statistics_s);
                    mBtnStatisticsImage.setImageDrawable(sDrawablePressed);
                    mBtnStatisticsText.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.BottomMenuTextColor_s));
                    break;
                case 2:
                    sDrawablePressed = ContextCompat.getDrawable(getBaseContext(), R.drawable.btn_sensio_s);
                    mBtnSensioImage.setImageDrawable(sDrawablePressed);
                    mBtnSensioText.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.BottomMenuTextColor_s));
                    break;
                case 3:
                    sDrawablePressed = ContextCompat.getDrawable(getBaseContext(), R.drawable.btn_profile_s);
                    mBtnProfileImage.setImageDrawable(sDrawablePressed);
                    mBtnProfileText.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.BottomMenuTextColor_s));
                    break;
                case 4:
                    sDrawablePressed = ContextCompat.getDrawable(getBaseContext(), R.drawable.btn_settings_s);
                    mBtnSettingsImage.setImageDrawable(sDrawablePressed);
                    mBtnSettingsText.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.BottomMenuTextColor_s));
                    break;
            }

            sDrawablePressed.setCallback(null);
            curTabIndex = arg0;
        }
    }

//    @Override
//    public void onStop(){
//        if( !isPreorderNow() ) {
//            Intent intent = new Intent(HomeActivity.this, CloseActivity.class);
//            //Clear all activities and start new task
//            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(intent);
//        }
//        super.onStop();
//    }
//
//    @Override
//    public void onStart(){
//        super.onStart();
//        setPreorderNow(false);
//    }

    public void setPreorderNow(boolean flag){
        preorderNow = flag;
    }
    public boolean isPreorderNow(){
        return preorderNow;
    }

    private void restCallDeviceDataApi() {

        RequestParams params = new RequestParams();
        String str_userid = SaveSharedPreferences.getLoginUserData(HomeActivity.this).getId();
        String str_deviceid = utilityClass.GetDeviceID();
        String str_email = SaveSharedPreferences.getLoginUserData(HomeActivity.this).getEmail();
        String str_hash = utilityClass.MD5(str_deviceid + str_email + Constant.LOGIN_SECTRET);
        String str_cityid =  Global.GetInstance().GetCityID();

        params.put(Constant.STR_USERID, str_userid);
        params.put(Constant.STR_DEVICEID, str_deviceid);
        params.put(Constant.STR_CITYID, str_cityid);
        params.put(Constant.STR_HASH, str_hash);

        // AirSensioRestClient.post(AirSensioRestClient.LOGIN, params, new AsyncHttpResponseHandler() {
        AirSensioRestClient.post(AirSensioRestClient.GET_DEVICE_DATA, params, new JsonHttpResponseHandler() {   //new JsonHttpResponseHandler(false) : onSuccess(int statusCode, Header[] headers, String responseString) must be overrided.
            @Override
            public void onStart() {
                // called before request is started
                utilityClass.processDialogStart(false);
                _debug.d(LOG_TAG, "AirSensioRestClient.onStart(GET_DEVICE_DATA)");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // If the response is JSONObject instead of expected JSONArray
                utilityClass.processDialogStop();
                _debug.d(LOG_TAG, "Recieved JSONObject result");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                // Pull out the first event on the public response
                utilityClass.processDialogStop();
                DeviceDataModal deviceData = ParsingResponse.parsingDeviceData(response);
                if (deviceData != null) ;
                {
//                    userModal.setPassword(etPassword.getText().toString());   //add password in UserModal
//                    SaveSharedPreferences.setLoginUserData(LoginAcitivity.this, userModal);
//                    Intent HealthActivity = new Intent(LoginAcitivity.this, HealthActivity.class);
//                    startActivity(HealthActivity);
                    ;
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

                utilityClass.processDialogStop();
                if (responseString == null) {

                    _debug.e(LOG_TAG, "None response string");
                } else if (responseString.equals("1")) {

                    utilityClass.showAlertMessage("Alert", "Incorrect Hash");
                    _debug.e(LOG_TAG, "Incorrect Hash");
                } else if (responseString.equals("2")) {

                    _debug.d(LOG_TAG, "Device ID not provided");
                } else if (responseString.equals("3")) {

                    _debug.d(LOG_TAG, "User ID not provided");
                } else if (responseString.equals("9")) {

                    utilityClass.toast(getResources().getString(R.string.not_found));
                    _debug.d(LOG_TAG, "Device not found");
                } else {
                    _debug.e(LOG_TAG, "Get Device Data Error:"+responseString);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                utilityClass.processDialogStop();
                utilityClass.toast(getResources().getString(R.string.check_internet));
                if (errorResponse==null) _debug.d(LOG_TAG, "errorJSONObject: null"); else _debug.d(LOG_TAG, "errorJSONObject:" + errorResponse.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                utilityClass.processDialogStop();
                utilityClass.toast(getResources().getString(R.string.check_internet));
                if (errorResponse==null) _debug.d(LOG_TAG, "errorJSONArray: null"); else _debug.d(LOG_TAG, "errorJSONArray:" + errorResponse.toString());
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried{
                utilityClass.processDialogStop();
                utilityClass.toast(getResources().getString(R.string.try_again));
                _debug.d(LOG_TAG, "AirSensioRestClient.GET_DEVICE_DATA.onRetry");
            }

            @Override
            public void onFinish() {
                utilityClass.processDialogStop();
            }

        });
    }
}