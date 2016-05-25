package com.wondereight.sensioair.Activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import butterknife.ButterKnife;
import butterknife.Bind;
import butterknife.OnClick;
import co.mobiwise.materialintro.animation.MaterialIntroListener;
import co.mobiwise.materialintro.prefs.PreferencesManager;
import co.mobiwise.materialintro.shape.ArrowType;
import co.mobiwise.materialintro.shape.Focus;
import co.mobiwise.materialintro.shape.FocusGravity;
import co.mobiwise.materialintro.view.MaterialIntroView;
import cz.msebera.android.httpclient.Header;

import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.wondereight.sensioair.Helper._Debug;
import com.wondereight.sensioair.Interface.ThreadCallback;
import com.wondereight.sensioair.Modal.RequestParamsModal;
import com.wondereight.sensioair.Modal.UserModal;
import com.wondereight.sensioair.R;
import com.wondereight.sensioair.UtilClass.AirSensioRestClient;
import com.wondereight.sensioair.UtilClass.Constant;
import com.wondereight.sensioair.UtilClass.Global;
import com.wondereight.sensioair.UtilClass.SaveSharedPreferences;
import com.wondereight.sensioair.UtilClass.UtilityClass;

import java.util.List;

/**
 * Created by Miguel on 02/2/2016.
 */

public class SymptomActivity extends AppCompatActivity implements MaterialIntroListener {

    private static final String LOG_TAG = "SymptomActivity";
    private static _Debug _debug = new _Debug(true);
    UtilityClass utilityClass;

    int[] symptomList;
    Drawable sDrawable;
    int[] btnCircle;

    @Bind({R.id.wateryeyes, R.id.itchyeyes, R.id.runnynose, R.id.itchynose, R.id.sneezing, R.id.nasal})
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

        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                drawSymptomTutorial();
            }
        }, 100);
    }

    private void declaration() {
        utilityClass = new UtilityClass(SymptomActivity.this);
        //symptomList = Global.GetInstance().GetStateSymptomList();
        symptomList = new int[6];
        for ( int i=0; i<6; i++)
            symptomList[i] = 0;
    }

    private void initialization() {
        sDrawable = null;
        btnCircle = new int[] {R.drawable.circle_btn_0, R.drawable.circle_btn_1, R.drawable.circle_btn_2, R.drawable.circle_btn_3};

        for (int i=0; i<ivSymptoms.size(); i++)
        {
            sDrawable = ContextCompat.getDrawable(getBaseContext(), btnCircle[symptomList[i]]);
            if(Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                ivSymptoms.get(i).setBackgroundDrawable( sDrawable );
            } else {
                ivSymptoms.get(i).setBackground( sDrawable );
            }
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
        Intent intent = new Intent();
        setResult(RESULT_CANCELED, intent);
        finish();
        _debug.e(LOG_TAG, "Log outbreak closed");
    }

    @OnClick(R.id.btnSubmit)
    void onClickSubmit(){
        //Global.GetInstance().SetStateSymptomList(symptomList);
        if(isSelectedSymptom(symptomList)) {
            SaveSharedPreferences.addSymptomData(SymptomActivity.this, getParam());

            if( Global.GetInstance().IsOnlineMode() ){
                utilityClass.processDialogStart(false);
                utilityClass.sendSymptomList(new Runnable() {
                    @Override
                    public void run() {
                        utilityClass.processDialogStop();
                        goBackHomeActivity();
                    }
                });
            } else {
                utilityClass.sendSymptomList();
                goBackHomeActivity();
            }
        }
        else
            utilityClass.toast(getResources().getString(R.string.select_symptom));
    }

    @OnClick(R.id.sneezing)
    void onClickSneezing(){
        symptomList[4]++;
        if(symptomList[4]>3)
            symptomList[4] = 0;

        sDrawable = ContextCompat.getDrawable(getBaseContext(), btnCircle[symptomList[4]]);
        if(Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            ivSymptoms.get(4).setBackgroundDrawable( sDrawable );
        } else {
            ivSymptoms.get(4).setBackground(sDrawable);
        }

    }
    @OnClick(R.id.itchyeyes)
    void onClickItchyEyes(){
        symptomList[1]++;
        if(symptomList[1]>3)
            symptomList[1] = 0;

        sDrawable = ContextCompat.getDrawable(getBaseContext(), btnCircle[symptomList[1]]);
        if(Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            ivSymptoms.get(1).setBackgroundDrawable( sDrawable );
        } else {
            ivSymptoms.get(1).setBackground(sDrawable);
        }
    }
    @OnClick(R.id.runnynose)
    void onClickRunnyNose(){
        symptomList[2]++;
        if(symptomList[2]>3)
            symptomList[2] = 0;

        sDrawable = ContextCompat.getDrawable(getBaseContext(), btnCircle[symptomList[2]]);
        if(Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            ivSymptoms.get(2).setBackgroundDrawable( sDrawable );
        } else {
            ivSymptoms.get(2).setBackground(sDrawable);
        }
    }
    @OnClick(R.id.nasal)
    void onClickNasal(){
        symptomList[5]++;
        if(symptomList[5]>3)
            symptomList[5] = 0;

        sDrawable = ContextCompat.getDrawable(getBaseContext(), btnCircle[symptomList[5]]);
        if(Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            ivSymptoms.get(5).setBackgroundDrawable( sDrawable );
        } else {
            ivSymptoms.get(5).setBackground(sDrawable);
        }
    }
    @OnClick(R.id.wateryeyes)
    void onClickWateryEyes(){
        symptomList[0]++;
        if(symptomList[0]>3)
            symptomList[0] = 0;

        sDrawable = ContextCompat.getDrawable(getBaseContext(), btnCircle[symptomList[0]]);
        if(Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            ivSymptoms.get(0).setBackgroundDrawable( sDrawable );
        } else {
            ivSymptoms.get(0).setBackground(sDrawable);
        }
    }
    @OnClick(R.id.itchynose)
    void onClickItchyNose(){
        symptomList[3]++;
        if(symptomList[3]>3)
            symptomList[3] = 0;

        sDrawable = ContextCompat.getDrawable(getBaseContext(), btnCircle[symptomList[3]]);
        if(Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            ivSymptoms.get(3).setBackgroundDrawable( sDrawable );
        } else {
            ivSymptoms.get(3).setBackground(sDrawable);
        }
    }

    private RequestParamsModal getParam(){
        RequestParamsModal params = new RequestParamsModal();

        UserModal userModal = Global.GetInstance().GetUserModal();

        String str_email = userModal.getEmail();
        String str_userid = userModal.getId();
        String str_location = Global.GetInstance().GetCityName();
        String str_cityid = Global.GetInstance().GetCityID();
        String str_deviceid = utilityClass.GetDeviceID();
        String str_hash = UtilityClass.MD5(str_deviceid + str_email + Constant.LOGIN_SECTRET);

        params.add(Constant.STR_EMAIL, str_email);
        params.add(Constant.STR_DEVICEID, str_deviceid);
        params.add(Constant.STR_HASH, str_hash);
        params.add(Constant.STR_USERID, str_userid);
        params.add(Constant.STR_CITYID, str_cityid);
        params.add(Constant.STR_LOCATION, str_location);
        params.add(Constant.STR_DATETIME, UtilityClass.getDateTime());
        params.add(Constant.STR_SYMPTOM_1, String.valueOf(symptomList[0]));
        params.add(Constant.STR_SYMPTOM_2, String.valueOf(symptomList[1]));
        params.add(Constant.STR_SYMPTOM_3, String.valueOf(symptomList[2]));
        params.add(Constant.STR_SYMPTOM_4, String.valueOf(symptomList[3]));
        params.add(Constant.STR_SYMPTOM_5, String.valueOf(symptomList[4]));
        params.add(Constant.STR_SYMPTOM_6, String.valueOf(symptomList[5]));
        return params;
    }

    private void restCallLogOutbreakApi() {

        RequestParamsModal modal = getParam();
        RequestParams params = new RequestParams();
        for(int i=0; i<modal.getCount(); i++)
            params.put(modal.getKey(i), modal.getValue(i));

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

    public void drawSymptomTutorial(){
        //Show intro
        View viewEnvir = ivSymptoms.get(3);
        PreferencesManager pm = new PreferencesManager(this);

        if( viewEnvir != null && !pm.isDisplayed(Constant.INTRO_ID_3)){
            //new PreferencesManager(this).reset(Constant.INTRO_ID_3);
            showIntro(viewEnvir, Constant.INTRO_ID_3, getString(R.string.tutorial_sympotom), ArrowType.AT_RED);
        }
    }

    private void showIntro(View view, String usageId, String text, ArrowType type){

        new MaterialIntroView.Builder(this)
                .setMaskColor(0x70000000)
                .setTextColor(0xFF3F9CFF)
                .enableDotAnimation(false)
                .setFocusGravity(FocusGravity.CENTER)
                .setFocusType(Focus.MINIMUM)
                .setDelayMillis(200)
                .enableFadeAnimation(true)
                .performClick(true)
                .setTouchCount(5)
                .setListener(this)
                .setArrowType(type)
                .setInfoText(text)
                .enableInfoIDText(true)     //Info ID text appear
                .enableIcon(false)
                .setTarget(view)
                .setUsageId(usageId) //THIS SHOULD BE UNIQUE ID
                .show();
    }

    @Override
    public void onUserClicked(String materialIntroViewId) {
        _debug.d(LOG_TAG, "Tutor pressed: " + materialIntroViewId);

        if(materialIntroViewId == Constant.INTRO_ID_3){
            new android.os.Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent();
                    intent.putExtra(Constant.TUTORIAL, true);
                    setResult(RESULT_CANCELED, intent);
                    finish();
                }
            }, 200);

            _debug.e(LOG_TAG, "Log outbreak closed by tutorial");;
        }
    }

    private void goBackHomeActivity(){
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
    }
}
