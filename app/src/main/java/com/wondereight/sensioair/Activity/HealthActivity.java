package com.wondereight.sensioair.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import butterknife.ButterKnife;
import butterknife.Bind;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;

import com.google.gson.Gson;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.wondereight.sensioair.Helper._Debug;
import com.wondereight.sensioair.Modal.HealthInfoModal;
import com.wondereight.sensioair.Modal.UserModal;
import com.wondereight.sensioair.R;
import com.wondereight.sensioair.UtilClass.AirSensioRestClient;
import com.wondereight.sensioair.UtilClass.Constant;
import com.wondereight.sensioair.UtilClass.Global;
import com.wondereight.sensioair.UtilClass.ParsingResponse;
import com.wondereight.sensioair.UtilClass.SaveSharedPreferences;
import com.wondereight.sensioair.UtilClass.UtilityClass;
import com.wondereight.sensioair.UtilClass.Validation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Miguel on 02/2/2016.
 */

public class HealthActivity extends AppCompatActivity {
    private static final String LOG_TAG = "HealthActivity";
    private static _Debug _debug = new _Debug(true);
    UtilityClass utilityClass;

    HealthInfoModal infoModal;
    boolean havingModal = false;
    private static int nCountCallingProfile = 0;

    @Bind(R.id.chbconscious)
    CheckBox chbConscious;
    @Bind(R.id.chballergies)
    CheckBox chbAllergies;
//    @Bind(R.id.chbeyes)       //removed by Habib 4/19
//    CheckBox chbEyes;
//    @Bind(R.id.chbnose)
//    CheckBox chbNose;
//    @Bind(R.id.chblungs)
//    CheckBox chbLungs;
    @Bind(R.id.etspecify)
    EditText etSpecify;
    @Bind(R.id.chbrespiratory)
    CheckBox chbRespiratory;
    @Bind(R.id.etanotherspecify)
    EditText etAnotherApecify;
    @Bind(R.id.chbpet)
    CheckBox chbPet;
    @Bind(R.id.btn_close)
    View btnClose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.health_activity);
        ButterKnife.bind(this);

        utilityClass = new UtilityClass(this);

        if(getIntent().getExtras() != null) {

            try {
                String json = getIntent().getExtras().getString("HealthInfoModal");
                infoModal = new Gson().fromJson(json, HealthInfoModal.class);
                havingModal = true;

                setViewsWithModal(infoModal);
            } catch (Exception ignored){ }
        } else {
            btnClose.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_health, menu);
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

    @OnClick(R.id.btnHealthSave)
    void onClickBtnHealthSave(){
        if( checkValidation() ) {
            if (!utilityClass.isInternetConnection()) {
                utilityClass.toast(getResources().getString(R.string.check_internet));
            } else {
                utilityClass.processDialogStart(false);
                restCallSaveInfoApi();
            }
        }
    }

    @OnClick(R.id.btn_close)
    void onClickCloseBtn(){
        goBack(RESULT_CANCELED);
    }

    private void setViewsWithModal(HealthInfoModal modal){
        chbConscious.setChecked(modal.getConscious());
        chbAllergies.setChecked(modal.getAllergies());
//        chbEyes.setChecked(modal.getEyes());
//        chbNose.setChecked(modal.getNose());
//        chbLungs.setChecked(modal.getLungs());
        etSpecify.setText(modal.getSpecify());
        chbRespiratory.setChecked(modal.getRespiratory());
        etAnotherApecify.setText(modal.getAnotherspecify());
        chbPet.setChecked(modal.getPet());
    }

    private boolean checkValidation() {
        boolean ret = true;

//        if (!Validation.hasText(etFirstName, "First Name")) ret = false;

        return ret;
    }

    private void restCallSaveInfoApi() {

        RequestParams params = new RequestParams();


        String str_conscious = chbConscious.isChecked() ? "1" : "0";
        String str_allergies = chbAllergies.isChecked() ? "1" : "0";
//        String str_eyes = chbEyes.isChecked() ? "1" : "0";
//        String str_nose = chbNose.isChecked() ? "1" : "0";
//        String str_lungs = chbLungs.isChecked() ? "1" : "0";
        String str_specify = etSpecify.getText().toString();
        String str_respiratory = chbRespiratory.isChecked() ? "1" : "0";
        String str_anotherspecify = etAnotherApecify.getText().toString();
        String str_pet = chbPet.isChecked() ? "1" : "0";



        String str_email = Global.GetInstance().GetUserModal().getEmail();
        String str_deviceid = utilityClass.GetDeviceID();
        String str_hash = UtilityClass.MD5(str_deviceid + str_email + Constant.LOGIN_SECTRET);

        params.put(Constant.STR_CONSCIOUS, str_conscious);
        params.put(Constant.STR_HAVEALLERGIES, str_allergies);
//        params.put(Constant.STR_EYES, str_eyes);
//        params.put(Constant.STR_NOSE, str_nose);
//        params.put(Constant.STR_LUNGS, str_lungs);
        params.put(Constant.STR_ALLERGIESOTHER, str_specify);
        params.put(Constant.STR_RES_DISTRESS, str_respiratory);
        params.put(Constant.STR_RES_OTHER, str_anotherspecify);
        params.put(Constant.STR_PET, str_pet);
        params.put(Constant.STR_EMAIL, str_email);
        params.put(Constant.STR_DEVICEID, str_deviceid);
        params.put(Constant.STR_HASH, str_hash);


        // AirSensioRestClient.post(AirSensioRestClient.LOGIN, params, new AsyncHttpResponseHandler() {
        AirSensioRestClient.post(AirSensioRestClient.SAVE_INFO, params, new TextHttpResponseHandler() {   //new JsonHttpResponseHandler(false) : onSuccess(int statusCode, Header[] headers, String responseString) must be overrided.
            @Override
            public void onStart() {
                // called before request is started
                utilityClass.processDialogStart(false);
                _debug.d(LOG_TAG, "AirSensioRestClient.SAVE_INFO.onStart");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString){
                utilityClass.processDialogStop();
                if (responseString == null) {
                    _debug.e(LOG_TAG, "None response string");
                } else if (responseString.equals("0")) {
                    if( havingModal ){
                        restCallGetProfileInfoApi();
                    } else {
                        Intent HomeIntent = new Intent(HealthActivity.this, HomeActivity.class);
                        HomeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(HomeIntent);
                    }
                    _debug.d(LOG_TAG, "Save Info Success");
                } else if (responseString.equals("1")) {
                    _debug.e(LOG_TAG, "Incorrect Hash");
                } else if (responseString.equals("3")) {
                    utilityClass.toast(getResources().getString(R.string.device_not));
                    _debug.e(LOG_TAG, "Device ID not provided");
                } else if (responseString.equals("4")) {
                    utilityClass.toast(getResources().getString(R.string.no_exist));
                    _debug.e(LOG_TAG, "Account Does Not Exist");
                } else {
                    _debug.e(LOG_TAG, "Save Info Exception Error:"+responseString);
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
                _debug.d(LOG_TAG, "AirSensioRestClient.SAVE_INFO.onRetry");
            }

            @Override
            public void onFinish() {
                utilityClass.processDialogStop();
            }

        });
    }

    public void goBack(int result){
        Intent intent = new Intent();
        setResult(result, intent);
        finish();
    }

    private void restCallGetProfileInfoApi() {
        if( !Global.GetInstance().isLogedinUser() ) return;

        RequestParams params = new RequestParams();
        UserModal userModal = Global.GetInstance().GetUserModal();
        String str_userid = userModal.getId();
        String str_email = userModal.getEmail();
        String str_deviceid = utilityClass.GetDeviceID();
        String str_hash = UtilityClass.MD5(str_deviceid + str_email + Constant.LOGIN_SECTRET);

        params.put(Constant.STR_USERID, str_userid);
        params.put(Constant.STR_EMAIL, str_email);
        params.put(Constant.STR_DEVICEID, str_deviceid);
        params.put(Constant.STR_HASH, str_hash);

        AirSensioRestClient.post(AirSensioRestClient.GET_PROFILE_INFO, params, new TextHttpResponseHandler() {
            @Override
            public void onStart() {
                // called before request is started
                utilityClass.processDialogStart(false);
                _debug.d(LOG_TAG, "AirSensioRestClient.GET_PROFILE_INFO.onStart");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                utilityClass.processDialogStop();
                if( !Global.GetInstance().isLogedinUser() ) return;

                if (responseString == null) {
                    _debug.e(LOG_TAG, "None response string");
                } else if (responseString.equals("0")) {
                    utilityClass.showAlertMessage(getResources().getString(R.string.title_notice), getResources().getString(R.string.no_saved_info));
                    _debug.e(LOG_TAG, "No Saved Info");
                } else if (responseString.equals("1")) {

                    utilityClass.showAlertMessage(getResources().getString(R.string.title_notice), getResources().getString(R.string.incorrect_hash));
                    _debug.e(LOG_TAG, "Incorrect Hash");
                } else if (responseString.equals("2")) {
                    utilityClass.showAlertMessage(getResources().getString(R.string.title_notice), getResources().getString(R.string.device_not));
                    _debug.d(LOG_TAG, "Device ID not provided");
                } else if (responseString.equals("3")) {
                    utilityClass.showAlertMessage(getResources().getString(R.string.title_notice), getResources().getString(R.string.userid_not));
                    _debug.d(LOG_TAG, "User ID not provided");
                } else if (responseString.equals("9")) {
                    utilityClass.showAlertMessage(getResources().getString(R.string.title_notice), getResources().getString(R.string.no_exist));
                    _debug.d(LOG_TAG, "Nothing Found");
                } else {
                    try {
                        ArrayList<String> profile = ParsingResponse.parsingProfileInfo(new JSONArray(responseString));
                        Global.GetInstance().SetProfileInfo(profile);
                        _debug.d(LOG_TAG, "AirSensioRestClient.GET_PROFILE_INFO.Success");
                    } catch (JSONException e) {
                        e.printStackTrace();
                        _debug.e(LOG_TAG, "GET_PROFILE_INFO Exception Error:" + responseString);

                        Global.GetInstance().SetProfileInfo(new ArrayList<String>());
                    }

                    goBack(RESULT_OK);
                }
                Global.GetInstance().SetProfileLoadState(true);
                nCountCallingProfile = 0;
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                //utilityClass.processDialogStop();
                //utilityClass.toast(getResources().getString(R.string.check_internet));
                if( !Global.GetInstance().isLogedinUser() ) return;

                _debug.e(LOG_TAG, "errorString: " + responseString);
                nCountCallingProfile++;
                if (nCountCallingProfile < 3) {
                    restCallGetProfileInfoApi();
                } else {
                    nCountCallingProfile = 0;
                }
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried{
                //utilityClass.toast(getResources().getString(R.string.try_again));
                if( !Global.GetInstance().isLogedinUser() ) return;

                _debug.d(LOG_TAG, "AirSensioRestClient.GET_PROFILE_INFO.onRetry");
                nCountCallingProfile++;
                if (nCountCallingProfile < 3) {
                    restCallGetProfileInfoApi();
                } else {
                    nCountCallingProfile = 0;
                }
            }

            @Override
            public void onFinish() {
                utilityClass.processDialogStop();
                _debug.d(LOG_TAG, "AirSensioRestClient.GET_PROFILE_INFO.onFinish");
            }

        });
    }
}
