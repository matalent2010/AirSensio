package com.wondereight.airsensio.Activity;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.Bind;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;

import com.google.android.gms.analytics.Tracker;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.wondereight.airsensio.Helper._Debug;
import com.wondereight.airsensio.Interface.ThreadCallback;
import com.wondereight.airsensio.Modal.UserModal;
import com.wondereight.airsensio.R;
import com.wondereight.airsensio.UtilClass.AirSensioRestClient;
import com.wondereight.airsensio.UtilClass.Constant;
import com.wondereight.airsensio.UtilClass.GPSTracker;
import com.wondereight.airsensio.UtilClass.GetCityNameThread;
import com.wondereight.airsensio.UtilClass.Global;
import com.wondereight.airsensio.UtilClass.ParsingResponse;
import com.wondereight.airsensio.UtilClass.PhoneNumberInput;
import com.wondereight.airsensio.UtilClass.SaveSharedPreferences;
import com.wondereight.airsensio.UtilClass.UtilityClass;
import com.wondereight.airsensio.UtilClass.Validation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by Miguel on 02/2/2016.
 */

public class SignupActivity extends AppCompatActivity {
    private final String LOG_TAG = "SignupAcitivity";
    private static _Debug _debug = new _Debug(true);

    UtilityClass utilityClass;
    Calendar myCalendar = Calendar.getInstance();
    UserModal userModal = null;

    // Declaring a Location Manager
    private LocationManager locationManager;
    Tracker mTracker;
    GPSTracker gpsTracker;
    String geolocation = "";
    String cityName = "";


    @Bind(R.id.etFirstName)
    EditText etFirstName;
    @Bind(R.id.etLastName)
    EditText etLastName;
    @Bind(R.id.etPhonenum)
    EditText etPhonenum;
    @Bind(R.id.tvBirthday)
    TextView tvBirthday;
    @Bind(R.id.chbMale)
    CheckBox chbMale;
    @Bind(R.id.chbFemale)
    CheckBox chbFemale;
//    @Bind(R.id.chbGelocation)
//    CheckBox chbGelocation;
    @Bind(R.id.chbNewsletter)
    CheckBox chbNewsletter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_activity);
        ButterKnife.bind(this);

        etPhonenum.addTextChangedListener(new PhoneNumberInput(etPhonenum));

        utilityClass = new UtilityClass(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_signup, menu);
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

    @OnClick(R.id.chbMale)
    void onClickChbMale(){
        chbMale.setChecked(true);
        chbFemale.setChecked(false);
    }

    @OnClick(R.id.chbFemale)
    void onClickChbFemale(){
        chbFemale.setChecked(true);
        chbMale.setChecked(false);
    }

    @OnClick(R.id.btnRegister)
    void onClickBtnRegister(){
        if( checkValidation() ){
            if (!utilityClass.isInternetConnection()) {
                utilityClass.toast(getResources().getString(R.string.check_internet));
            } else {
                utilityClass.processDialogStart(false);
//                if (chbGelocation.isChecked()) {
                    GetCityNameThread getCityNameThread = new GetCityNameThread(SignupActivity.this, new ThreadCallback() {

                        @Override
                        public void runSuccessCallback() {
                            SignupActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //utilityClass.processDialogStop();
                                    restCallSignupApi();
                                }
                            });
                        }

                        @Override
                        public void runFailCallback(final String err) {
                            SignupActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    utilityClass.processDialogStop();
                                    utilityClass.showAlertMessage(getString(R.string.title_alert), err);
                                }
                            });
                        }
                    });
                    new Thread(getCityNameThread).start();
//                } else {
//                    Global.GetInstance().SetGoeCityName("");
//                    restCallSignupApi();
//                }

            }
        }

    }


    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateBirthday();
        }

    };

    @OnClick(R.id.tvBirthday)
    void onClickBirthday(){
        // TODO Auto-generated method stub
        new DatePickerDialog(SignupActivity.this, date, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void updateBirthday() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
        tvBirthday.setText(dateFormat.format(myCalendar.getTime()));
    }

    private boolean checkValidation() {
        boolean ret = true;

        if (!Validation.hasText(etFirstName, "First Name")) ret = false;
        else if (!Validation.hasText(etLastName, "Last Name")) ret = false;
        else if (!Validation.hasText(tvBirthday, "Birthday")) ret = false;
        else if (!Validation.isPhoneNumber(etPhonenum)) ret = false;

        return ret;
    }

    private void restCallSignupApi() {

        RequestParams params = new RequestParams();
        String str_firstname = etFirstName.getText().toString();
        String str_lastname = etLastName.getText().toString();
        String str_email = getIntent().getExtras().getString("email");
        String str_gender = chbMale.isChecked() ? "Male" : "Female";
        String str_phone = etPhonenum.getText().toString();
        String str_birthday = tvBirthday.getText().toString();
        final String str_deviceid = utilityClass.GetDeviceID();
        String str_newsletter = chbNewsletter.isChecked() ? "1" : "0";
        String str_pass = getIntent().getExtras().getString("password");
        String str_md5_pass = utilityClass.MD5(str_pass);
        String str_hash = utilityClass.MD5(str_deviceid + str_email + Constant.LOGIN_SECTRET);
        String str_geolocation = Global.GetInstance().GetGeolocation();
        String str_cityname = Global.GetInstance().GetGeoCityName().isEmpty() ? Constant.DEFAULT_CITYNAME : Global.GetInstance().GetGeoCityName();
        if( str_cityname.equalsIgnoreCase("丹东市") ) str_cityname = "Lisbon";     //MUST Remove

        params.put(Constant.STR_FIRSTNAME, str_firstname);
        params.put(Constant.STR_LASTNAME, str_lastname);
        params.put(Constant.STR_EMAIL, str_email);
        params.put(Constant.STR_GENDER, str_gender);
        params.put(Constant.STR_PHONE, str_phone);
        params.put(Constant.STR_BIRTHDAY, str_birthday);
        params.put(Constant.STR_DEVICEID, str_deviceid);
        params.put(Constant.STR_GEOLOCATION, str_cityname); //"London" //str_cityname // str_geolocation
        params.put(Constant.STR_NEWSLETTER, str_newsletter);
        params.put(Constant.STR_PASSWORD, str_md5_pass);
        params.put(Constant.STR_HASH, str_hash);
        //params.put(Constant.STR_CITYFLAG, str_hash);

        AirSensioRestClient.post(AirSensioRestClient.SIGNUP, params, new JsonHttpResponseHandler(false) {
            @Override
            public void onStart() {
                // called before request is started
                utilityClass.processDialogStart(false);
                _debug.d(LOG_TAG, "AirSensioRestClient.onStart");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString){
                utilityClass.processDialogStop();
                if(responseString == null){
                    _debug.e(LOG_TAG, "None response string");
                } else if(responseString.equals("1")){

                    _debug.e(LOG_TAG, "Incorrect Hash");
                } else if(responseString.equals("2")){
                    utilityClass.showAlertMessage(getResources().getString(R.string.title_notice), getResources().getString(R.string.email_used));
                    _debug.d(LOG_TAG, "Email Used by another account");
                } else if(responseString.equals("3")){
                    utilityClass.toast(getResources().getString(R.string.device_not));
                    _debug.e(LOG_TAG, "Device ID not provided");
                } else if(responseString.equals("4")){
                    utilityClass.showAlertMessage(getResources().getString(R.string.title_notice), getResources().getString(R.string.http_error));
                    _debug.e(LOG_TAG, "HTTP Agent Provided or Not HTTPS");
                } else if(responseString.equals("9")){
                    utilityClass.toast(getResources().getString(R.string.try_again));
                    _debug.d(LOG_TAG, "Registration Error, Please Try Again");
                } else{
                    utilityClass.toast(getResources().getString(R.string.register_fail));
                    _debug.e(LOG_TAG, "Unknown Case: " + responseString);
                }
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
                String city_not_exit = "0";
                        utilityClass.processDialogStop();
                try {
                    JSONObject firstEvent = response.getJSONObject(0);
                    city_not_exit = firstEvent.optString(Constant.STR_CITYFLAG);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                userModal = ParsingResponse.parsingUserModal(response);
                if (userModal != null)
                {
                    userModal.setPassword(getIntent().getExtras().getString("password"));   //add password in UserModal
                    _debug.d(LOG_TAG, "city_not_exit : " + city_not_exit);
                    if( city_not_exit.equalsIgnoreCase("1") ){
                        AlertDialog alertDialog;
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SignupActivity.this);
                        alertDialogBuilder.setTitle(getString(R.string.app_name))
                                .setMessage(getString(R.string.msg_not_city))
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                        goHealthActivity();
                                    }
                                });
                        alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                    } else {
                        goHealthActivity();
                    }
                } else {
                    utilityClass.showAlertMessage(getString(R.string.title_alert), getString(R.string.not_parsing));
                    _debug.e(LOG_TAG, "RESPONSE:" + response.toString());
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
                // called when request is retried
                utilityClass.toast(getResources().getString(R.string.try_again));
                _debug.d(LOG_TAG, "AirSensioRestClient.onRetry");
            }

            @Override
            public void onFinish(){
                utilityClass.processDialogStop();
            }
        });
    }

    private void goHealthActivity(){
        SaveSharedPreferences.setLoginUserData(SignupActivity.this, userModal);
        Intent HealthActivity = new Intent(SignupActivity.this, HealthActivity.class);
        startActivity(HealthActivity);
        finish();
    }

    private boolean getGeolocation() {
        geolocation = "";
        if (!utilityClass.isInternetConnection()) {
            utilityClass.toast(getResources().getString(R.string.check_internet));
        } else {
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            _debug.d(LOG_TAG, "Check GPS Enable : " + "True Or False");
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                gpsTracker = new GPSTracker(SignupActivity.this);
                if(gpsTracker != null && gpsTracker.canGetLocation()) {
                    geolocation = String.valueOf(gpsTracker.getLatitude()) + "," + String.valueOf(gpsTracker.getLongitude());
                } else {
                    geolocation = "";
                }
                return true;
            } else {
                utilityClass.showGPSDisabledAlertToUser();
            }
        }
        return false;
    }
}
