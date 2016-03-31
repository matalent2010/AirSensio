package com.wondereight.sensioair.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.Bind;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.wondereight.sensioair.Helper._Debug;
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

public class LoginAcitivity extends AppCompatActivity {
    private static final String LOG_TAG = "LoginAcitivity";
    private static _Debug _debug = new _Debug(true);

    UtilityClass utilityClass;

    @Bind(R.id.etEmail)
    EditText etEmail;
    @Bind(R.id.etPassword)
    EditText etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        ButterKnife.bind(this);

        utilityClass = new UtilityClass(this);

        if(isSavedUserData())
            runAutoLogin();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login_acitivity, menu);
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

    @OnClick(R.id.btnLogin)
    void onClickBtnLogin() {
        if( checkValidation() ){
            if (!utilityClass.isInternetConnection()) {
                utilityClass.toast(getResources().getString(R.string.check_internet));
            } else {
                restCallLoginApi();
            }
        }
    }

    @OnClick(R.id.btnForgotPass)
    void onClickBtnForgotPassword() {
        String str_email = etEmail.getText().toString();
        if (!Validation.isEmailAddress(etEmail)){
            utilityClass.toast(getString(R.string.input_email));
            return;
        }
        AlertDialog alertDialog;
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(getResources().getString(R.string.title_alert))
                .setMessage(String.format(getResources().getString(R.string.forgotpass_msg_format),str_email))
                .setCancelable(false)
                .setPositiveButton("Send", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        restCallForgotApi();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void restCallLoginApi() {

        RequestParams params = new RequestParams();
        String str_email = etEmail.getText().toString();
        String str_md5_pass = utilityClass.MD5(Constant.LOGIN_SECTRET + utilityClass.MD5(etPassword.getText().toString()));
        String str_deviceid = utilityClass.GetDeviceID();
        String str_hash = utilityClass.MD5(str_deviceid + str_email + Constant.LOGIN_SECTRET);

        params.put(Constant.STR_EMAIL, str_email);
        params.put(Constant.STR_PASSWORD, str_md5_pass);
        params.put(Constant.STR_DEVICEID, str_deviceid);
        params.put(Constant.STR_HASH, str_hash);

        // AirSensioRestClient.post(AirSensioRestClient.LOGIN, params, new AsyncHttpResponseHandler() {
        AirSensioRestClient.post(AirSensioRestClient.LOGIN, params, new JsonHttpResponseHandler() {   //new JsonHttpResponseHandler(false) : onSuccess(int statusCode, Header[] headers, String responseString) must be overrided.
            @Override
            public void onStart() {
                // called before request is started
                utilityClass.processDialogStart(false);
                _debug.d(LOG_TAG, "AirSensioRestClient.onStart");
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
                _debug.d(LOG_TAG, "Login Info: " + response.toString());
                UserModal userModal = ParsingResponse.parsingUserModal(response);
                if (userModal != null);
                {
                    userModal.setPassword(etPassword.getText().toString());   //add password in UserModal
                    SaveSharedPreferences.setLoginUserData(LoginAcitivity.this, userModal);
                    Intent intent = new Intent(LoginAcitivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

                utilityClass.processDialogStop();
                if(responseString == null){

                    _debug.e(LOG_TAG, "None response string");
                } else if(responseString.equals("1")){

                    utilityClass.showAlertMessage("Alert", "Incorrect Hash");
                    _debug.e(LOG_TAG, "Incorrect Hash");
                } else if(responseString.equals("2")){

                    _debug.d(LOG_TAG, "Account Does Not Exist");
                    Intent SignupActivity = new Intent(LoginAcitivity.this, SignupActivity.class);
                    SignupActivity.putExtra("email", etEmail.getText().toString());
                    SignupActivity.putExtra("password", etPassword.getText().toString());
                    startActivity(SignupActivity);
                } else if(responseString.equals("3")){

                    utilityClass.showAlertMessage(getString(R.string.title_alert), getString(R.string.user_suspended));
                    _debug.d(LOG_TAG, "Account Suspended");
                } else if(responseString.equals("4")){

                    utilityClass.showAlertMessage(getString(R.string.title_notice),  getString(R.string.reset_password));
                    _debug.e(LOG_TAG, "You need to reset your password");
                } else if(responseString.equals("5")){

                    utilityClass.showAlertMessage(getString(R.string.title_alert), getString(R.string.device_not));
                    _debug.e(LOG_TAG, "Device ID not provided");
                } else if(responseString.equals("6")){

                    utilityClass.showAlertMessage(getString(R.string.title_alert), getString(R.string.http_error));
                            _debug.e(LOG_TAG, "HTTP Agent Provided or Not HTTPS");
                } else if(responseString.equals("9")){

                    utilityClass.toast( getString(R.string.incorrect_pass) );
                    _debug.d(LOG_TAG, "Login Failed / Incorrect Username or Password");
                } else{
                    _debug.e(LOG_TAG, "Login Exception Error:"+responseString);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                utilityClass.processDialogStop();
                utilityClass.toast("Please check your internet and try again");
                if (errorResponse==null) _debug.d(LOG_TAG, "errorJSONObject: null"); else _debug.d(LOG_TAG, "errorJSONObject:" + errorResponse.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                utilityClass.processDialogStop();
                utilityClass.toast("Please check your internet and try again");
                if (errorResponse==null) _debug.d(LOG_TAG, "errorJSONArray: null"); else _debug.d(LOG_TAG, "errorJSONArray:" + errorResponse.toString());
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried{
                utilityClass.processDialogStop();
                utilityClass.toast("Please try again after a moment");
                _debug.d(LOG_TAG, "AirSensioRestClient.onRetry");
            }

            @Override
            public void onFinish(){
                utilityClass.processDialogStop();
            }

        });
    }
//    @OnClick(R.id.btnSignup)
//    void onClickBtnSignup(){
//        Intent SignupActivity = new Intent(LoginAcitivity.this, SignupActivity.class);
//        startActivity(SignupActivity);
//    }

    private boolean checkValidation() {
        boolean ret = true;

        if (!Validation.isEmailAddress(etEmail)) ret = false;
        else if (!Validation.hasText(etPassword, "Password")) ret = false;

        return ret;
    }

    private void restCallForgotApi(){
        RequestParams params = new RequestParams();
        String str_email = etEmail.getText().toString();
        String str_deviceid = utilityClass.GetDeviceID();
        String str_hash = utilityClass.MD5(str_deviceid + str_email + Constant.LOGIN_SECTRET);

        params.put(Constant.STR_EMAIL, str_email);
        params.put(Constant.STR_DEVICEID, str_deviceid);
        params.put(Constant.STR_HASH, str_hash);

        // AirSensioRestClient.post(AirSensioRestClient.LOGIN, params, new AsyncHttpResponseHandler() {
        AirSensioRestClient.post(AirSensioRestClient.FORGOT_PASSWORD, params, new TextHttpResponseHandler() {   //new JsonHttpResponseHandler(false) : onSuccess(int statusCode, Header[] headers, String responseString) must be overrided.
            @Override
            public void onStart() {
                // called before request is started
                utilityClass.processDialogStart(false);
                _debug.d(LOG_TAG, "AirSensioRestClient.FORGOT_PASSWORD.onStart");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                utilityClass.processDialogStop();
                if (responseString == null) {
                    _debug.e(LOG_TAG, "None response string");
                } else if (responseString.equals("0")) {
                    utilityClass.showAlertMessage(getResources().getString(R.string.title_notice), getResources().getString(R.string.forgot_success));
                    _debug.e(LOG_TAG, "Send Forgot message to success");
                } else if (responseString.equals("1")) {
                    utilityClass.toast(getResources().getString(R.string.incorrect_hash));
                    _debug.e(LOG_TAG, "Incorrect Hash");
                } else if (responseString.equals("2")) {
                    utilityClass.toast(getResources().getString(R.string.email_notprovided));
                    _debug.e(LOG_TAG, "Email Not Provided");
                } else if (responseString.equals("3")) {
                    utilityClass.toast(getResources().getString(R.string.device_not));
                    _debug.e(LOG_TAG, "Device ID not provided");
                } else if (responseString.equals("4")) {
                    utilityClass.toast(getResources().getString(R.string.email_unable));
                    _debug.e(LOG_TAG, "Unable to Send Email");
                } else if (responseString.equals("5")) {
                    utilityClass.toast(getResources().getString(R.string.email_noexist));
                    _debug.e(LOG_TAG, "Email Does not exist");
                } else {
                    utilityClass.showAlertMessage(getResources().getString(R.string.title_alert), responseString);
                    _debug.e(LOG_TAG, "Send forgot password message Exception Error:"+responseString);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                utilityClass.processDialogStop();
                utilityClass.toast(getResources().getString(R.string.check_internet));
                if (responseString==null) _debug.d(LOG_TAG, "errorString: null"); else _debug.d(LOG_TAG, "errorString:" + responseString.toString());
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried{
                utilityClass.processDialogStop();
                utilityClass.toast(getResources().getString(R.string.try_again));
                _debug.d(LOG_TAG, "AirSensioRestClient.FORGOT_PASSWORD.onRetry");
            }

            @Override
            public void onFinish() {
                utilityClass.processDialogStop();
            }
        });
    }

    public void runAutoLogin()
    {
        etEmail.setText(SaveSharedPreferences.getLoginUserData(this).getEmail());
        etPassword.setText(SaveSharedPreferences.getLoginUserData(this).getPassword());
        onClickBtnLogin();
    }

    public Boolean isSavedUserData()
    {
        if(!SaveSharedPreferences.getLoginUserData(this).getId().isEmpty())
            return true;

        return false;
    }
}
