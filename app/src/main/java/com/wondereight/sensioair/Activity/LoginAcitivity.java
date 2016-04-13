package com.wondereight.sensioair.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

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

import butterknife.ButterKnife;
import butterknife.Bind;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;

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
import java.util.Arrays;

/**
 * Created by Miguel on 02/2/2016.
 */

public class LoginAcitivity extends AppCompatActivity {
    private static final String LOG_TAG = "LoginAcitivity";
    private static _Debug _debug = new _Debug(true);
    UtilityClass utilityClass;

    private static final String host = "api.linkedin.com";
    private static final String topCardUrl = "https://" + host + "/v1/people/~:" + "(id,email-address)";
                                            //reference :  https://developer.linkedin.com/docs/fields/basic-profile
    String fbEmail="", twEmail="", liEmail="";
    String fbID="", twID="", liID="";
    String fbToken="", twToken="", liToken="", twSecret="";

    //facebook
    CallbackManager callbackManager = null;
    AccessToken mFacebookToken = null;

    //linkedin
    LISessionManager liSessionManager = null;

    //twitter
    private TwitterSession session;
    TwitterAuthToken authToken;
    TwitterAuthClient authClient;

    @Bind(R.id.etEmail)
    EditText etEmail;
    @Bind(R.id.etPassword)
    EditText etPassword;

//    @Bind(R.id.fb_login_button)
//    LoginButton fb_login_button;
    @Bind(R.id.btnTwitter_login_button)
    TwitterLoginButton btnTwitter_login_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        ButterKnife.bind(this);

        utilityClass = new UtilityClass(this);

        facebookLoginInit();
        //linkedinLogininit();
        twitterLoginInit();

        if( SaveSharedPreferences.isLogedinUser(this) )
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
                restCallLocalLoginApi();
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

    @OnClick(R.id.btn_facebook)
    void onClickBtnFacebook(){
        if (utilityClass.isInternetConnection()) {
            LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "email"));
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

    private void restCallLocalLoginApi() {

        RequestParams params = new RequestParams();
        String str_email = etEmail.getText().toString();
        String str_md5_pass = UtilityClass.MD5(Constant.LOGIN_SECTRET + UtilityClass.MD5(etPassword.getText().toString()));
        String str_deviceid = utilityClass.GetDeviceID();
        String str_hash = UtilityClass.MD5(str_deviceid + str_email + Constant.LOGIN_SECTRET);

        params.put(Constant.STR_EMAIL, str_email);
        params.put(Constant.STR_PASSWORD, str_md5_pass);
        params.put(Constant.STR_DEVICEID, str_deviceid);
        params.put(Constant.STR_HASH, str_hash);

        restCallLoginApi( Constant.LM_LOCAL, params );
    }

    private void restCallFacebookLoginApi( String email, String social_id, String token ) {

        RequestParams params = new RequestParams();
        String str_email = email;
        String str_deviceid = utilityClass.GetDeviceID();
        String str_hash = UtilityClass.MD5(str_deviceid + str_email + Constant.LOGIN_SECTRET);

        params.put(Constant.STR_EMAIL, str_email);
        params.put(Constant.STR_PASSWORD, "");
        params.put(Constant.STR_DEVICEID, str_deviceid);
        params.put(Constant.STR_HASH, str_hash);
        params.put(Constant.STR_SOCIAL_ID, social_id);
        params.put(Constant.STR_SOCIAL_TOKEN, token);

        restCallLoginApi(Constant.LM_FACEBOOK, params);
    }

    private void restCallLinkedinLoginApi( String email, String social_id, String token ) {

        RequestParams params = new RequestParams();
        String str_email = email;
        String str_deviceid = utilityClass.GetDeviceID();
        String str_hash = UtilityClass.MD5(str_deviceid + str_email + Constant.LOGIN_SECTRET);

        params.put(Constant.STR_EMAIL, str_email);
        params.put(Constant.STR_PASSWORD, "");
        params.put(Constant.STR_DEVICEID, str_deviceid);
        params.put(Constant.STR_HASH, str_hash);
        params.put(Constant.STR_SOCIAL_ID, social_id);
        params.put(Constant.STR_SOCIAL_TOKEN, token);

        restCallLoginApi(Constant.LM_LINKEDIN, params);
    }

    private void restCallTwitterLoginApi(String email, String social_id, String token, String secret) {

        RequestParams params = new RequestParams();
        String str_deviceid = utilityClass.GetDeviceID();
        String str_hash = UtilityClass.MD5(str_deviceid + email + Constant.LOGIN_SECTRET);

        params.put(Constant.STR_EMAIL, email);
        params.put(Constant.STR_PASSWORD, "");
        params.put(Constant.STR_DEVICEID, str_deviceid);
        params.put(Constant.STR_HASH, str_hash);
        params.put(Constant.STR_SOCIAL_ID, social_id);
        params.put(Constant.STR_SOCIAL_TOKEN, token);
        params.put(Constant.STR_SECRET_TOKEN, secret);

        restCallLoginApi(Constant.LM_TWITTER, params);
    }

    private void restCallLoginApi(String loginMode, RequestParams params) {

        params.put(Constant.STR_LOGIN_MODE, loginMode);
        // AirSensioRestClient.post(AirSensioRestClient.LOGIN, params, new AsyncHttpResponseHandler() {
        JsonHttpResponseHandler handler = new JsonHttpResponseHandler() {   //new JsonHttpResponseHandler(false) : onSuccess(int statusCode, Header[] headers, String responseString) must be overrided.
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
                _debug.e(LOG_TAG, "Recieved JSONObject result");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                // Pull out the first event on the public response
                utilityClass.processDialogStop();
                _debug.d(LOG_TAG, "Login Info: " + response.toString());
                UserModal userModal = ParsingResponse.parsingUserModal(response);
                if (userModal != null);
                {
                    String loginMode = (String)getTag();
                    if( Constant.LM_LOCAL.equalsIgnoreCase(loginMode) ) {
                        userModal.setPassword(etPassword.getText().toString());   //add password in UserModal
                    } else {
                        userModal.setPassword("");   //add "" password in UserModal
                    }

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
                    String loginMode = (String)getTag();

                    if( Constant.LM_LOCAL.equalsIgnoreCase(loginMode) ) {
                        SignupActivity.putExtra("email", etEmail.getText().toString());
                        SignupActivity.putExtra("password", etPassword.getText().toString());

                    } else if ( Constant.LM_FACEBOOK.equalsIgnoreCase(loginMode) ){
                        SignupActivity.putExtra("email", fbEmail);
                        SignupActivity.putExtra("password", Constant.DEFAULT_PASSWORD);

                    } else if ( Constant.LM_TWITTER.equalsIgnoreCase(loginMode) ){
                        SignupActivity.putExtra("email", twEmail);
                        SignupActivity.putExtra("password", Constant.DEFAULT_PASSWORD);

                    } else if ( Constant.LM_LINKEDIN.equalsIgnoreCase(loginMode) ){
                        SignupActivity.putExtra("email", liEmail);
                        SignupActivity.putExtra("password", Constant.DEFAULT_PASSWORD);
                    }
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

        };

        handler.setTag(loginMode);
        AirSensioRestClient.post(AirSensioRestClient.LOGIN, params, handler);
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
        String str_hash = UtilityClass.MD5(str_deviceid + str_email + Constant.LOGIN_SECTRET);

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
                    _debug.e(LOG_TAG, "Send forgot password message Exception Error:" + responseString);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                utilityClass.processDialogStop();
                utilityClass.toast(getResources().getString(R.string.check_internet));
                if (responseString == null) _debug.d(LOG_TAG, "errorString: null");
                else _debug.d(LOG_TAG, "errorString:" + responseString);
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
//        etEmail.setText(SaveSharedPreferences.getLoginUserData(this).getEmail());
//        etPassword.setText(SaveSharedPreferences.getLoginUserData(this).getPassword());
//        onClickBtnLogin();
        Intent intent = new Intent(LoginAcitivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
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

            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                mFacebookToken = loginResult.getAccessToken();
                _debug.d(LOG_TAG, "FB Login Access Token: " + mFacebookToken.toString());

                Profile profile = Profile.getCurrentProfile();
                if (profile != null) {
                    getFacebookProfileInfo(profile, mFacebookToken);
                } else {
                    mProfileTracker = new ProfileTracker() {
                        @Override
                        protected void onCurrentProfileChanged(Profile oldProfile, Profile newProfile) {
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
                _debug.d(LOG_TAG, "Facebook Login Canceled ");
                utilityClass.toast("FaceBook Login Canceled");
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
                if (exception instanceof FacebookAuthorizationException) {
                    if (AccessToken.getCurrentAccessToken() != null) {
                        LoginManager.getInstance().logOut();
                    }
                    utilityClass.toast("User logged in as different Facebook user");
                    return;
                }
                _debug.d(LOG_TAG, "Facebook Login Error:" + exception.toString());
                utilityClass.toast("Facebook Login Failed");
            }
        });
        return true;
    }

    private void getFacebookProfileInfo(Profile profile, AccessToken token){
        _debug.d(LOG_TAG, "FB Login Profile: ID:" + profile.getId() + "Firstname:" + profile.getFirstName() + ",    LastName: " + profile.getLastName());
        fbID = profile.getId();
        fbToken = token.getToken();
        GraphRequest request = GraphRequest.newMeRequest(
                token,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        if (response.getError() != null) {
                            utilityClass.processDialogStop();
                            utilityClass.toast(getString(R.string.msg_facebook_error));
                            _debug.e(LOG_TAG, "Facebook GraphRequest Error:" + response.getError().getErrorMessage());
                            return;
                        }
                        _debug.d(LOG_TAG, "Graph JsonObj: " + response.toString());
                        _debug.d(LOG_TAG, "Graph Response: " + object.toString());

                        try {
                            fbEmail = object.getString("email");
                            _debug.d(LOG_TAG, "FB Login Profile: ID:" + fbID + ", Token: " + fbToken);
                            restCallFacebookLoginApi(fbEmail, fbID, fbToken);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            utilityClass.processDialogStop();
                            fbEmail = "";
                            utilityClass.toast(getString(R.string.msg_facebook_error));
                            _debug.e(LOG_TAG, "Didn't get Email");
                        }

                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,email");
        request.setParameters(parameters);
        utilityClass.processDialogStart(true);
        request.executeAsync();
    }

    public Boolean twitterLoginInit(){
        btnTwitter_login_button.setCallback(new com.twitter.sdk.android.core.Callback<TwitterSession>() {

            @Override
            public void success(Result<TwitterSession> result) {
                // Do something with result, which provides a TwitterSession for making API calls

                session = result.data;
                authToken = session.getAuthToken();
                _debug.d(LOG_TAG, "Twitter Success: " + result.toString());

                twToken = authToken.token;
                twSecret = authToken.secret;
                String userName = session.getUserName();

                twID = String.valueOf(session.getUserId());

                _debug.d(LOG_TAG, "Twitter: " + "" + "Token :: " + twToken + " || Secret :: " + twSecret + " || " + userName + " || " + twID);

                authClient = new TwitterAuthClient();
                authClient.requestEmail(session, new com.twitter.sdk.android.core.Callback<String>() {
                    @Override
                    public void success(Result<String> result) {
                        // Do something with the result, which provides the email address
                        _debug.d(LOG_TAG, "Email Data Twitter : " + result.data);
                        twEmail = (String) result.data;
                        restCallTwitterLoginApi(twEmail, twID, twToken, twSecret);
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
        if( callbackManager != null )
            callbackManager.onActivityResult(requestCode, resultCode, data);

        btnTwitter_login_button.onActivityResult(requestCode, resultCode, data);

        if( liSessionManager != null )
            liSessionManager.onActivityResult(this, requestCode, resultCode, data);
    }

    public void login_Linkedin(){

        liSessionManager = LISessionManager.getInstance(getApplicationContext());
        liSessionManager.init(this, buildScope(),
                new AuthListener() {
                    @Override
                    public void onAuthSuccess() {

                        liToken = liSessionManager.getSession().getAccessToken().getValue();
                        _debug.d(LOG_TAG, "LinkedinLogin Success Token : " + liToken );

                        utilityClass.processDialogStart(false);
                        APIHelper apiHelper = APIHelper.getInstance(getApplicationContext());
                        apiHelper.getRequest(LoginAcitivity.this, topCardUrl, new ApiListener() {
                            @Override
                            public void onApiSuccess(ApiResponse result) {
                                try {
                                    JSONObject response = result.getResponseDataAsJson();
                                    liID = response.get("id").toString();
                                    liEmail = response.get("emailAddress").toString();
                                    _debug.d(LOG_TAG, "Linkedin ID : " + liID + ", Email : " + liEmail);

                                    restCallLinkedinLoginApi(liEmail, liID, liToken);

                                } catch (Exception e) {
                                    _debug.e(LOG_TAG, e.getMessage());
                                    utilityClass.processDialogStop();
                                    utilityClass.toast("Loinkedin Email not found");
                                }
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
                        utilityClass.toast("Login Failed with Linkedin");
                    }
                }, true);
    }

    private static Scope buildScope() {
        return Scope.build(Scope.R_BASICPROFILE, Scope.R_EMAILADDRESS);
    }
}
