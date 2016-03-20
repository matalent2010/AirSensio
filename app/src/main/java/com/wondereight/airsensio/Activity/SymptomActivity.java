package com.wondereight.airsensio.Activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import butterknife.ButterKnife;
import butterknife.Bind;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;

import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.wondereight.airsensio.Helper._Debug;
import com.wondereight.airsensio.Interface.ThreadCallback;
import com.wondereight.airsensio.Modal.UserModal;
import com.wondereight.airsensio.R;
import com.wondereight.airsensio.UtilClass.AirSensioRestClient;
import com.wondereight.airsensio.UtilClass.Constant;
import com.wondereight.airsensio.UtilClass.GetCityNameThread;
import com.wondereight.airsensio.UtilClass.Global;
import com.wondereight.airsensio.UtilClass.SaveSharedPreferences;
import com.wondereight.airsensio.UtilClass.UtilityClass;

import java.util.List;

/**
 * Created by Miguel on 02/2/2016.
 */

public class SymptomActivity extends AppCompatActivity {

    private static final String LOG_TAG = "SymptomActivity";
    private static _Debug _debug = new _Debug(true);
    UtilityClass utilityClass;

    int[] symptomList;
    Drawable sDrawable;
    int[][] idDrawable;

    @Bind({R.id.sneezing, R.id.itchyeyes, R.id.runnynose, R.id.nasal, R.id.wateryeyes, R.id.itchynose})
    List<ImageView> ivSymptoms;

    @Bind(R.id.btn_close)
    View btn_close;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.symptom_activity);

        ButterKnife.bind(this);
        btn_close.bringToFront();
        declaration();
        initialization();
    }

    private void declaration() {
        utilityClass = new UtilityClass(SymptomActivity.this);
        symptomList = Global.GetInstance().GetStateSymptomList();
    }

    private void initialization() {
        sDrawable = null;
        idDrawable = new int[][]{
                {R.drawable.symptom_1_0, R.drawable.symptom_1_1, R.drawable.symptom_1_2, R.drawable.symptom_1_3},
                {R.drawable.symptom_2_0, R.drawable.symptom_2_1, R.drawable.symptom_2_2, R.drawable.symptom_2_3},
                {R.drawable.symptom_3_0, R.drawable.symptom_3_1, R.drawable.symptom_3_2, R.drawable.symptom_3_3},
                {R.drawable.symptom_4_0, R.drawable.symptom_4_1, R.drawable.symptom_4_2, R.drawable.symptom_4_3},
                {R.drawable.symptom_5_0, R.drawable.symptom_5_1, R.drawable.symptom_5_2, R.drawable.symptom_5_3},
                {R.drawable.symptom_6_0, R.drawable.symptom_6_1, R.drawable.symptom_6_2, R.drawable.symptom_6_3}
        };

        for (int i=0; i<ivSymptoms.size(); i++)
        {
            switch (symptomList[i]){
                case 0:
                    sDrawable = getResources().getDrawable(idDrawable[i][0]);
                    break;
                case 1:
                    sDrawable = getResources().getDrawable(idDrawable[i][1]);
                    break;
                case 2:
                    sDrawable = getResources().getDrawable(idDrawable[i][2]);
                    break;
                case 3:
                    sDrawable = getResources().getDrawable(idDrawable[i][3]);
                    break;
                default:
                    sDrawable = getResources().getDrawable(idDrawable[i][0]);
                    break;
            }
            ivSymptoms.get(i).setImageDrawable(sDrawable);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sympton, menu);
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

    @OnClick(R.id.btn_close)
    void onClickClose(){
        finish();
        _debug.e(LOG_TAG, "Log outbreak closed");
    }

    @OnClick(R.id.btnSubmit)
    void onClickSubmit(){
        Global.GetInstance().SetStateSymptomList(symptomList);
        if(isSelectedSymptom(symptomList)) {
            if( Global.GetInstance().GetGeolocation().isEmpty() ) {
                GetCityNameThread getCityNameThread = new GetCityNameThread(SymptomActivity.this, new ThreadCallback() {

                    @Override
                    public void runSuccessCallback() {
                        SymptomActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //utilityClass.processDialogStop();
                                restCallLogOutbreakApi();
                            }
                        });
                    }

                    @Override
                    public void runFailCallback(final String err) {
                        SymptomActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                utilityClass.processDialogStop();
                                utilityClass.showAlertMessage("Alert", err);
                            }
                        });
                    }
                });
                utilityClass.processDialogStart(false);
                new Thread(getCityNameThread).start();
            } else {
                restCallLogOutbreakApi();
            }

        }
        else
            utilityClass.toast(getResources().getString(R.string.select_symptom));
    }

    @OnClick(R.id.sneezing)
    void onClickSneezing(){
        symptomList[0]++;
        if(symptomList[0]>3)
            symptomList[0] = 0;

        sDrawable = getResources().getDrawable(idDrawable[0][symptomList[0]]);
        ivSymptoms.get(0).setImageDrawable(sDrawable);

    }
    @OnClick(R.id.itchyeyes)
    void onClickItchyEyes(){
        symptomList[1]++;
        if(symptomList[1]>3)
            symptomList[1] = 0;

        sDrawable = getResources().getDrawable(idDrawable[1][symptomList[1]]);
        ivSymptoms.get(1).setImageDrawable(sDrawable);
    }
    @OnClick(R.id.runnynose)
    void onClickRunnyNose(){
        symptomList[2]++;
        if(symptomList[2]>3)
            symptomList[2] = 0;

        sDrawable = getResources().getDrawable(idDrawable[2][symptomList[2]]);
        ivSymptoms.get(2).setImageDrawable(sDrawable);
    }
    @OnClick(R.id.nasal)
    void onClickNasal(){
        symptomList[3]++;
        if(symptomList[3]>3)
            symptomList[3] = 0;

        sDrawable = getResources().getDrawable(idDrawable[3][symptomList[3]]);
        ivSymptoms.get(3).setImageDrawable(sDrawable);
    }
    @OnClick(R.id.wateryeyes)
    void onClickWateryEyes(){
        symptomList[4]++;
        if(symptomList[4]>3)
            symptomList[4] = 0;

        sDrawable = getResources().getDrawable(idDrawable[4][symptomList[4]]);
        ivSymptoms.get(4).setImageDrawable(sDrawable);
    }
    @OnClick(R.id.itchynose)
    void onClickItchyNose(){
        symptomList[5]++;
        if(symptomList[5]>3)
            symptomList[5] = 0;

        sDrawable = getResources().getDrawable(idDrawable[5][symptomList[5]]);
        ivSymptoms.get(5).setImageDrawable(sDrawable);
    }

    private void restCallLogOutbreakApi() {

        RequestParams params = new RequestParams();

        UserModal userModal = SaveSharedPreferences.getLoginUserData(SymptomActivity.this);

        String str_email = userModal.getEmail();
        String str_userid = userModal.getId();
        String str_location = Global.GetInstance().GetGeolocation();
        String str_temp = Global.GetInstance().GetGeoCityName();
        String str_cityid = Global.GetInstance().GetCityID();
        String str_deviceid = utilityClass.GetDeviceID();
        String str_hash = utilityClass.MD5(str_deviceid + str_email + Constant.LOGIN_SECTRET);

        params.put(Constant.STR_EMAIL, str_email);
        params.put(Constant.STR_DEVICEID, str_deviceid);
        params.put(Constant.STR_HASH, str_hash);
        params.put(Constant.STR_USERID, str_userid);
        params.put(Constant.STR_CITYID, str_cityid);
        params.put(Constant.STR_LOCATION, str_location);
        params.put(Constant.STR_DATETIME, utilityClass.getDateTime());
        params.put(Constant.STR_SYMPTOM_1, String.valueOf(symptomList[0]));
        params.put(Constant.STR_SYMPTOM_2, String.valueOf(symptomList[1]));
        params.put(Constant.STR_SYMPTOM_3, String.valueOf(symptomList[2]));
        params.put(Constant.STR_SYMPTOM_4, String.valueOf(symptomList[3]));
        params.put(Constant.STR_SYMPTOM_5, String.valueOf(symptomList[4]));
        params.put(Constant.STR_SYMPTOM_6, String.valueOf(symptomList[5]));

        _debug.i(LOG_TAG, params.toString());
        // AirSensioRestClient.post(AirSensioRestClient.LOGIN, params, new AsyncHttpResponseHandler() {
        AirSensioRestClient.post(AirSensioRestClient.LOG_OUTBREAK, params, new TextHttpResponseHandler() {   //new JsonHttpResponseHandler(false) : onSuccess(int statusCode, Header[] headers, String responseString) must be overrided.
            @Override
            public void onStart() {
                // called before request is started
                utilityClass.processDialogStart(false);
                _debug.d(LOG_TAG, "AirSensioRestClient.LOG_OUTBREAK.onStart");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                utilityClass.processDialogStop();
                if (responseString == null) {
                    _debug.e(LOG_TAG, "None response string");
                } else if (responseString.equals("0")) {
                    finish();
                    _debug.e(LOG_TAG, "Log outbreak Success");
                } else if (responseString.equals("1")) {
                    _debug.e(LOG_TAG, "Incorrect Hash");
                } else if (responseString.equals("2")) {
                    utilityClass.toast(getResources().getString(R.string.userid_not));
                    _debug.e(LOG_TAG, "User ID Not Provided");
                } else if (responseString.equals("3")) {
                    utilityClass.toast(getResources().getString(R.string.device_not));
                    _debug.e(LOG_TAG, "Device ID not provided");
                } else if (responseString.equals("8")) {
                    utilityClass.toast(getResources().getString(R.string.select_symptom));
                    _debug.e(LOG_TAG, "You have to select at least 1 symptom");
                } else if (responseString.equals("9")) {
                    utilityClass.toast(getResources().getString(R.string.try_again));
                    _debug.d(LOG_TAG, "Error, Please Try Again Later");
                } else {
                    _debug.e(LOG_TAG, "Log your outbreak Exception Error:" + responseString);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                utilityClass.processDialogStop();
                utilityClass.toast(getResources().getString(R.string.check_internet));
                _debug.e(LOG_TAG, "errorString: " + responseString);
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried{
                utilityClass.processDialogStop();
                utilityClass.toast(getResources().getString(R.string.try_again));
                _debug.d(LOG_TAG, "AirSensioRestClient.LOG_OUTBREAK.onRetry");
            }

            @Override
            public void onFinish() {
                utilityClass.processDialogStop();
            }

        });
    }

//    public String GetCityID(String CityName){
//        return "1";
//    }

    public Boolean isSelectedSymptom(int[] SymptomList){
        for ( int i= 0; i<6; i++){
            if( SymptomList[i]!=0 )
                return true;
        }
        return false;
    }

}
