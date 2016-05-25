package com.wondereight.sensioair.Activity;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.telephony.PhoneNumberUtils;
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

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookAuthorizationException;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.analytics.Tracker;
import com.linkedin.platform.APIHelper;
import com.linkedin.platform.LISessionManager;
import com.linkedin.platform.errors.LIApiError;
import com.linkedin.platform.errors.LIAuthError;
import com.linkedin.platform.listeners.ApiListener;
import com.linkedin.platform.listeners.ApiResponse;
import com.linkedin.platform.listeners.AuthListener;
import com.linkedin.platform.utils.Scope;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import com.twitter.sdk.android.core.models.User;
import com.wondereight.sensioair.Helper._Debug;
import com.wondereight.sensioair.Interface.ThreadCallback;
import com.wondereight.sensioair.Modal.UserModal;
import com.wondereight.sensioair.R;
import com.wondereight.sensioair.UtilClass.AirSensioRestClient;
import com.wondereight.sensioair.UtilClass.Constant;
import com.wondereight.sensioair.UtilClass.GPSTracker;
import com.wondereight.sensioair.UtilClass.GetCityNameThread;
import com.wondereight.sensioair.UtilClass.Global;
import com.wondereight.sensioair.UtilClass.ParsingResponse;
import com.wondereight.sensioair.UtilClass.PhoneNumberInput;
import com.wondereight.sensioair.UtilClass.SaveSharedPreferences;
import com.wondereight.sensioair.UtilClass.UtilityClass;
import com.wondereight.sensioair.UtilClass.Validation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Miguel on 02/2/2016.
 */

public class SignupActivity extends AppCompatActivity {
    private final String LOG_TAG = "SignupAcitivity";
    private static _Debug _debug = new _Debug(true);

    private static final String host = "api.linkedin.com";
    private static final String topCardUrl = "https://" + host + "/v1/people/~:" +
            "(id,email-address,first-name,last-name,phone-numbers)";

    private static final String[] LOCATION_PERMS={
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };
    private static final int LOCATION_REQUEST=188;

    UtilityClass utilityClass;
    Calendar myCalendar = Calendar.getInstance();
    UserModal userModal = null;


    //facebook
    CallbackManager callbackManager = null;

    //linkedin
    LISessionManager liSessionManager = null;

    //twitter
    private TwitterSession session;
    TwitterAuthToken authToken;
    TwitterAuthClient authClient;
    // Declaring a Location Manager
    private LocationManager locationManager;
    Tracker mTracker;
    GPSTracker gpsTracker;
    String geolocation = "";
    String cityName = "";
    String mEmail = "";
    String mPassword = "";


    @Bind(R.id.etFirstName)
    EditText etFirstName;
    @Bind(R.id.etLastName)
    EditText etLastName;
    @Bind(R.id.etPhonenum)
    EditText etPhonenum;
    @Bind(R.id.etCountryCode)
    EditText etCountryCode;
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

//    @Bind(R.id.fb_signup_button)
//    LoginButton fb_signup_button;
    @Bind(R.id.btnTwitter_login_button)
    TwitterLoginButton btnTwitter_login_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_activity);
        ButterKnife.bind(this);

        etPhonenum.addTextChangedListener(new PhoneNumberInput(etPhonenum));
        etCountryCode.addTextChangedListener(new PhoneNumberInput(etCountryCode));

        utilityClass = new UtilityClass(this);

        mEmail = getIntent().getExtras().getString("email");
        mPassword = getIntent().getExtras().getString("password");

        facebookLoginInit();
        //linkedinLogininit();
        twitterLoginInit();
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
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, LOCATION_PERMS, LOCATION_REQUEST);
                    _debug.e(LOG_TAG, "Needed permission request of Location.");
                } else {
                    utilityClass.processDialogStart(false);
                    SignupWithCityName();
                }
            }
        }
    }

    @OnClick(R.id.btn_facebook)
    void onClickBtnFacebook(){
        if (utilityClass.isInternetConnection()) {
            LoginManager.getInstance().logOut();
            LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "email", "user_birthday"));
        } else {
            utilityClass.toast(getString(R.string.check_internet));
        }
    }

    @OnClick(R.id.btn_linkedin)
    void onClickLinkedin(){
        login_Linkedin();
    }

    @OnClick(R.id.btn_twitter)
    void onClickTwitter(){
        if (utilityClass.isInternetConnection()) {
            utilityClass.processDialogStart(false);
            btnTwitter_login_button.performClick();
        } else {
            utilityClass.toast(getString(R.string.check_internet));
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

    @Override
    public void onBackPressed() {
        Intent loginActivity = new Intent(SignupActivity.this, LoginAcitivity.class);
        startActivity(loginActivity);
        finish();
        super.onBackPressed();
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
        String str_email = mEmail;
        String str_gender = chbMale.isChecked() ? "Male" : "Female";
        String str_phone = "+" + etCountryCode.getText().toString() + etPhonenum.getText().toString();
        String str_birthday = tvBirthday.getText().toString();
        final String str_deviceid = utilityClass.GetDeviceID();
        String str_newsletter = chbNewsletter.isChecked() ? "1" : "0";
        String str_pass = mPassword;
        String str_md5_pass = UtilityClass.MD5(str_pass);
        String str_hash = UtilityClass.MD5(str_deviceid + str_email + Constant.LOGIN_SECTRET);
        String str_geolocation = Global.GetInstance().GetGeolocation();
        String str_cityname = Global.GetInstance().GetGeoCityName().isEmpty() ? Constant.DEFAULT_CITYNAME : Global.GetInstance().GetGeoCityName();
        //if( str_cityname.equalsIgnoreCase("丹东市") ) str_cityname = "Lisbon";     //MUST Remove

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

        _debug.d(LOG_TAG, params.toString());
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
        Global.GetInstance().SaveUserModal(SignupActivity.this, userModal);
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


    public Boolean facebookLoginInit(){
        // Initialize the SDK before executing any other operations,
        // especially, if you're using Facebook UI elements.
        FacebookSdk.sdkInitialize(getApplicationContext());
        // Other app specific specialization
        callbackManager = CallbackManager.Factory.create();
        // Callback registration
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            private ProfileTracker mProfileTracker;
            private AccessToken mFacebookToken;

            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                mFacebookToken = loginResult.getAccessToken();
                _debug.d(LOG_TAG, "FB LogIn Token: " + mFacebookToken.toString());
                Profile profile = Profile.getCurrentProfile();

                if ( profile != null ) {
                    _debug.d(LOG_TAG, "FB Profile: " + profile.toString());
                    getFacebookProfileInfo(profile, mFacebookToken);
                }else {
                    mProfileTracker = new ProfileTracker() {
                        @Override
                        protected void onCurrentProfileChanged(Profile oldProfile, Profile newProfile) {
                            _debug.d(LOG_TAG, "FB stopTracking()");
                            getFacebookProfileInfo(newProfile, mFacebookToken);
                            mProfileTracker.stopTracking();
                        }
                    };
                    mProfileTracker.startTracking();
                }

            }

            @Override
            public void onCancel() {
                // App code
                _debug.d(LOG_TAG, "Facebook Log In Canceled ");
                utilityClass.toast("FaceBook Cancel");
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
                if (exception instanceof FacebookAuthorizationException) {
                    if (AccessToken.getCurrentAccessToken() != null) {
                        LoginManager.getInstance().logOut();
                    }
                }
                _debug.d(LOG_TAG, "Facebook LogIn Error:" + exception.toString());
                utilityClass.toast("User logged in as different Facebook user.");
            }
        });
        return true;
    }

    public Boolean twitterLoginInit(){
        btnTwitter_login_button.setCallback(new com.twitter.sdk.android.core.Callback<TwitterSession>() {

            @Override
            public void success(Result<TwitterSession> result) {
                // Do something with result, which provides a TwitterSession for making API calls

                session = result.data;
                authToken = session.getAuthToken();
                _debug.d(LOG_TAG, "Twitter Success: " + result.toString());

                String token = authToken.token;
                String secret = authToken.secret;
                String userName = session.getUserName();

                String twID = String.valueOf(session.getUserId());

                _debug.d(LOG_TAG, "Twitter: " + "" + "Token :: " + token + " || Secret :: " + secret + " || " + userName + " || " + twID);

                authClient = new TwitterAuthClient();
                authClient.requestEmail(session, new com.twitter.sdk.android.core.Callback<String>() {
                    @Override
                    public void success(Result<String> result) {
                        // Do something with the result, which provides the email address
                        utilityClass.processDialogStop();
                        _debug.d(LOG_TAG, "Email Data Twitter : " + result.data);
                        String email = (String) result.data;
                        if (email != null && !email.isEmpty())
                            mEmail = email;

                        etFirstName.setText("");
                        etLastName.setText("");
                        tvBirthday.setText("");
                        chbMale.setChecked(true);
                        chbFemale.setChecked(false);
                    }

                    @Override
                    public void failure(TwitterException exception) {
                        utilityClass.processDialogStop();
                        utilityClass.toast("Don't get your Email with Twitter");
                        _debug.d(LOG_TAG, "Fail Email Twitter : " + exception.toString());
                        Twitter.getSessionManager().clearActiveSession();
                        Twitter.logOut();
                    }
                });
            }

            @Override
            public void failure(TwitterException exception) {
                // Do something on failure
                utilityClass.processDialogStop();
                utilityClass.toast("Login failure with Twitter");
                _debug.d(LOG_TAG, "Twitter failure : " + exception.toString());
                Twitter.getSessionManager().clearActiveSession();
                Twitter.logOut();
            }
        });
        return true;
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        _debug.d(LOG_TAG, "Request Code : " + requestCode + ", Activity Result : " + resultCode + ", Intent Extra : " + data.getExtras().toString());
        if( callbackManager!= null )
            callbackManager.onActivityResult(requestCode, resultCode, data);

        btnTwitter_login_button.onActivityResult(requestCode, resultCode, data);

        if( liSessionManager == null) {
            liSessionManager = LISessionManager.getInstance(getApplicationContext());
            _debug.e(LOG_TAG, "after liSessionManager.init() liSessionManager is null!");
        }
        if( liSessionManager != null )
            liSessionManager.onActivityResult(this, requestCode, resultCode, data);
    }

    public void login_Linkedin(){

        liSessionManager = LISessionManager.getInstance(getApplicationContext());
        liSessionManager.init(this, buildScope(),
                new AuthListener() {
                    @Override
                    public void onAuthSuccess() {

                        _debug.d(LOG_TAG, "LinkedinLogin Success : " +
                                liSessionManager.getSession().getAccessToken().toString());

                        utilityClass.processDialogStart(false);
                        APIHelper apiHelper = APIHelper.getInstance(getApplicationContext());
                        apiHelper.getRequest(SignupActivity.this, topCardUrl, new ApiListener() {
                            @Override
                            public void onApiSuccess(ApiResponse result) {
                                utilityClass.processDialogStop();
                                String user_id, first_name, last_name, user_email, user_phone, user_birthday;
                                JSONObject response = result.getResponseDataAsJson();
                                if( response == null ) {
                                    utilityClass.toast("Loinkedin Email not found");
                                    user_id = first_name = last_name = user_email = user_phone = user_birthday = "";
                                } else {
                                    try { user_id = response.get("id").toString(); }
                                    catch ( JSONException e ) { user_id = ""; _debug.e(LOG_TAG, e.getMessage());}
                                    try { first_name = response.get("firstName").toString(); }
                                    catch ( JSONException e ) { first_name = "";  _debug.e(LOG_TAG, e.getMessage());}
                                    try { last_name = response.get("lastName").toString(); }
                                    catch ( JSONException e ) { last_name = "";  _debug.e(LOG_TAG, e.getMessage());}
                                    try { user_email = response.get("emailAddress").toString(); }
                                    catch ( JSONException e ) { user_email = "";  _debug.e(LOG_TAG, e.getMessage());}
                                    try { user_phone = response.get("phoneNumber").toString(); }
                                    catch ( JSONException e ) { user_phone = "";  _debug.e(LOG_TAG, e.getMessage());}
                                    try { user_birthday = response.get("birthday").toString(); }
                                    catch ( JSONException e ) { user_birthday = "";  _debug.e(LOG_TAG, e.getMessage());}
                                }
                                etFirstName.setText(first_name);
                                etLastName.setText(last_name);
                                etPhonenum.setText(user_phone);
                                tvBirthday.setText(user_birthday);
                                if( !user_email.isEmpty() )
                                    mEmail = user_email;
                                _debug.d(LOG_TAG, "Linkedin id : " + user_id + ", Email : " + user_email + ", firstName : " + first_name
                                        + ", lastName : " + last_name + " :: " + user_phone + "  :: " + user_birthday);
                            }

                            @Override
                            public void onApiError(LIApiError error) {
                                _debug.e(LOG_TAG, error.toString());
                                utilityClass.processDialogStop();
                                utilityClass.toast("Your information not found");
                            }
                        });

                    }

                    @Override
                    public void onAuthError(LIAuthError error) {
                        _debug.d(LOG_TAG, "LinkedinLogin failed " + error.toString());
                        utilityClass.processDialogStop();
                        utilityClass.toast("Login Failed with Linkedin");
                    }
                }, true);
    }

    private static Scope buildScope() {
        return Scope.build(Scope.R_BASICPROFILE, Scope.R_EMAILADDRESS);
    }

    private Boolean getFacebookProfileInfo(Profile profile, AccessToken token){
        _debug.d(LOG_TAG, "FB LogIn Profile: ID:" + profile.getId() + "Firstname:" + profile.getFirstName() + ",    LastName: " + profile.getLastName());
        //String fbID = profile.getId();
        etFirstName.setText(profile.getFirstName());
        etLastName.setText(profile.getLastName());
        GraphRequest request = GraphRequest.newMeRequest(
                token,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        utilityClass.processDialogStop();
                        if (response.getError() != null) {
                            utilityClass.toast(getString(R.string.msg_facebook_error));
                            _debug.d(LOG_TAG, "Facebook GraphRequest Error:" + response.getError().getErrorMessage());
                            return;
                        }
                        _debug.d(LOG_TAG, "Graph JsonObj: " + response.toString());
                        _debug.d(LOG_TAG, "Graph Response: " + object.toString());

                        String email, birthday, gender;
                        try {
                            email = object.getString("email");

                        } catch (JSONException e) {
                            email = "";
                            _debug.e(LOG_TAG, e.getMessage());
                        }
                        try {
                            birthday = object.getString("birthday"); // 01/31/1980 format
                        } catch (JSONException e) {
                            birthday = "";
                            _debug.e(LOG_TAG, e.getMessage());
                        }
                        try {
                            gender = object.getString("gender");
                        } catch (JSONException e) {
                            gender = "";
                            _debug.e(LOG_TAG, e.getMessage());
                        }
                        SimpleDateFormat fbDateFormat = new SimpleDateFormat("MM/dd/yyyy");
                        SimpleDateFormat newDateFormat = new SimpleDateFormat("yyyy/MM/dd");
                        try {
                            Date newDate = fbDateFormat.parse(birthday);
                            tvBirthday.setText(newDateFormat.format(newDate));

                        } catch (ParseException e) {
                            _debug.w(LOG_TAG, "Birthday Parse Error: " + birthday);
                            tvBirthday.setText("");
                        }

                        if (gender.equalsIgnoreCase("Female")) {
                            chbMale.setChecked(false);
                            chbFemale.setChecked(true);
                        } else {
                            chbMale.setChecked(true);
                            chbFemale.setChecked(false);
                        }
                        if (!email.isEmpty())
                            mEmail = email;
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email,gender,birthday");
        request.setParameters(parameters);
        utilityClass.processDialogStart(true);
        request.executeAsync();
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch(requestCode) {
            case LOCATION_REQUEST:
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    SignupWithNoCityName();
                    _debug.e(LOG_TAG, "The permission of Location is not granted.");
                } else {
                    utilityClass.processDialogStart(false);
                    SignupWithCityName();
                }
                break;
        }
    }

    private void SignupWithCityName(){
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
    }

    private void SignupWithNoCityName(){
        Global.GetInstance().SetGeolocation("0, 0");
        Global.GetInstance().SetGoeCityName("");
        utilityClass.processDialogStart(false);
        restCallSignupApi();
    }


}
