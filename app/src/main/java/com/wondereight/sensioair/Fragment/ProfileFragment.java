package com.wondereight.sensioair.Fragment;

import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.google.gson.Gson;
import com.linkedin.platform.LISessionManager;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.twitter.sdk.android.core.models.User;
import com.wondereight.sensioair.Activity.HealthActivity;
import com.wondereight.sensioair.Activity.LoginAcitivity;
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
import com.wondereight.sensioair.gcm.GcmMain;
import com.wondereight.sensioair.gcm.SAPreferences;

import org.json.JSONArray;
import org.json.JSONException;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;

/**
 * Created by Miguel on 02/2/2016.
 */
public class ProfileFragment extends Fragment {
    private static Context _context;
    private static final String LOG_TAG = "ProfileFragment";
    private static _Debug _debug = new _Debug(true);
    UtilityClass utilityClass;

    private static int nCountCallingGetInfo = 0;
    private static int nCountCallingProfile = 0;
    private static boolean isLoadedProfile = false;

    private static FeedbackFragment feedbackdlg;

    @Bind(R.id.feedback_container)
    View vContainer;

    public ProfileFragment() {
    }

    public static Fragment newInstance(Context context) {
        _context = context;
        ProfileFragment f = new ProfileFragment();
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, view);

        utilityClass = new UtilityClass(_context);
        feedbackdlg = (FeedbackFragment)FeedbackFragment.newInstance(_context);
        if( isLoadedProfile == false)
            restCallGetProfileInfoApi();
        return view;
    }

    @OnClick(R.id.btnLogout)
    public void onClickLogout(){
        UserModal userModal = SaveSharedPreferences.getLoginUserData(getContext());
        userModal.setLogouted(true);
        SaveSharedPreferences.setLoginUserData(getContext(), userModal);
        Global.GetInstance().init();
        restCallLogoutApi();

        //GCM UnregisterReciever
        PreferenceManager.getDefaultSharedPreferences(getContext())
                .edit().putBoolean(SAPreferences.REGISTER_TO_SERVER, false)
                .apply();
        GcmMain.GetInstance().unregisterReceiver(getContext());

        try {
            //Facebook Token logout
            if (AccessToken.getCurrentAccessToken() != null) {
                LoginManager.getInstance().logOut();
            }
        }catch (Exception e){ }

        //Linkedin clear session
        LISessionManager.getInstance(getActivity().getApplicationContext()).clearSession();

        gotoLoginActivity();
    }

    @OnClick(R.id.btnFeedback)
    public void onClickFeedback(){
        vContainer.setVisibility(View.VISIBLE);
        android.support.v4.app.FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.slide_top_in, R.anim.slide_top_out);
//        ft.add(SensioFragment.newInstance(_context), "feedbackFragment");
        ft.replace(R.id.feedback_container, feedbackdlg, "feedbackFragment");
        ft.commit();
    }

    @OnClick(R.id.btnEditInfo)
    public void onClickEditInfo(){
        restCallGetSavedInfoApi();
    }

    private void restCallLogoutApi(){
        RequestParams params = new RequestParams();
        UserModal userModal = SaveSharedPreferences.getLoginUserData(getActivity());
        String str_userid = userModal.getId();
        String str_email = userModal.getEmail();
        String str_deviceid = utilityClass.GetDeviceID();
        String str_hash = UtilityClass.MD5(str_deviceid + str_email + Constant.LOGIN_SECTRET);

        params.put(Constant.STR_USERID, str_userid);
        params.put(Constant.STR_EMAIL, str_email);
        params.put(Constant.STR_DEVICEID, str_deviceid);
        params.put(Constant.STR_HASH, str_hash);

        /*AirSensioRestClient.post(AirSensioRestClient.DO_LOGOUT, params, new TextHttpResponseHandler() {
            @Override
            public void onStart() {
                // called before request is started
                utilityClass.processDialogStart(false);
                _debug.d(LOG_TAG, "AirSensioRestClient.DO_LOGOUT.onStart");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                utilityClass.processDialogStop();
                if (responseString == null) {
                    _debug.e(LOG_TAG, "None response string");
                } else if (responseString.equals("0")) {
                    SaveSharedPreferences.clearUserdata(getContext());
                    _debug.e(LOG_TAG, "Logout Success");
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                utilityClass.processDialogStop();
                //utilityClass.toast(getResources().getString(R.string.check_internet));
                _debug.e(LOG_TAG, "errorString: " + responseString);
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried{
                utilityClass.processDialogStop();
                _debug.d(LOG_TAG, "AirSensioRestClient.DO_LOGOUT.onRetry");
            }

            @Override
            public void onFinish() {
                utilityClass.processDialogStop();
                _debug.d(LOG_TAG, "AirSensioRestClient.DO_LOGOUT.onFinish");
            }

        });*/
    }
    private void restCallGetSavedInfoApi() {

        RequestParams params = new RequestParams();
        UserModal userModal = SaveSharedPreferences.getLoginUserData(getActivity());
        String str_userid = userModal.getId();
        String str_email = userModal.getEmail();
        String str_deviceid = utilityClass.GetDeviceID();
        String str_hash = UtilityClass.MD5(str_deviceid + str_email + Constant.LOGIN_SECTRET);

        params.put(Constant.STR_USERID, str_userid);
        params.put(Constant.STR_EMAIL, str_email);
        params.put(Constant.STR_DEVICEID, str_deviceid);
        params.put(Constant.STR_HASH, str_hash);

        AirSensioRestClient.post(AirSensioRestClient.GET_SAVED_INFO, params, new TextHttpResponseHandler() {
            @Override
            public void onStart() {
                // called before request is started
                utilityClass.processDialogStart(false);
                _debug.d(LOG_TAG, "AirSensioRestClient.GET_SAVED_INFO.onStart");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                utilityClass.processDialogStop();
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
                    utilityClass.showAlertMessage(getResources().getString(R.string.title_notice), getResources().getString(R.string.nothing_found));
                    _debug.d(LOG_TAG, "Nothing Found");
                } else {
                    try {
                        HealthInfoModal info = ParsingResponse.parsingSavedInfo(new JSONArray(responseString));
                        Gson gson = new Gson();
                        Intent intentTerms = new Intent(_context, HealthActivity.class);
                        intentTerms.putExtra("HealthInfoModal", gson.toJson(info));
                        startActivity(intentTerms);
                        //utilityClass.showAlertMessage(getResources().getString(R.string.title_alert), "Saved Info : " + responseString);
                        _debug.d(LOG_TAG, "AirSensioRestClient.GET_SAVED_INFO.Success");
                    } catch (JSONException e) {
                        e.printStackTrace();
                        _debug.e(LOG_TAG, "GET_SAVED_INFO Exception Error:" + responseString);
                    }
                }
                nCountCallingGetInfo = 0;
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                utilityClass.processDialogStop();
                //utilityClass.toast(getResources().getString(R.string.check_internet));
                _debug.e(LOG_TAG, "errorString: " + responseString);
                nCountCallingGetInfo++;
                if (nCountCallingGetInfo < 3) {
                    restCallGetSavedInfoApi();
                } else {
                    nCountCallingGetInfo = 0;
                }
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried{
                utilityClass.processDialogStop();
                //utilityClass.toast(getResources().getString(R.string.try_again));
                _debug.d(LOG_TAG, "AirSensioRestClient.GET_SAVED_INFO.onRetry");
                nCountCallingGetInfo++;
                if (nCountCallingGetInfo < 3) {
                    restCallGetSavedInfoApi();
                } else {
                    nCountCallingGetInfo = 0;
                }
            }

            @Override
            public void onFinish() {
                utilityClass.processDialogStop();
                _debug.d(LOG_TAG, "AirSensioRestClient.GET_SAVED_INFO.onFinish");
            }

        });
    }

    private void restCallGetProfileInfoApi() {

        RequestParams params = new RequestParams();
        UserModal userModal = SaveSharedPreferences.getLoginUserData(getActivity());
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
                //utilityClass.processDialogStart(false);
                _debug.d(LOG_TAG, "AirSensioRestClient.GET_PROFILE_INFO.onStart");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                //utilityClass.processDialogStop();
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
                        ParsingResponse.parsingSavedInfo(new JSONArray(responseString));    // must be changed
                        _debug.d(LOG_TAG, "AirSensioRestClient.GET_PROFILE_INFO.Success");
                    } catch (JSONException e) {
                        e.printStackTrace();
                        _debug.e(LOG_TAG, "GET_PROFILE_INFO Exception Error:" + responseString);
                    }
                }
                nCountCallingProfile = 0;
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                //utilityClass.processDialogStop();
                //utilityClass.toast(getResources().getString(R.string.check_internet));
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
                //utilityClass.processDialogStop();
                //utilityClass.toast(getResources().getString(R.string.try_again));
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
                //utilityClass.processDialogStop();
                _debug.d(LOG_TAG, "AirSensioRestClient.GET_PROFILE_INFO.onFinish");
            }

        });
    }

    public void gotoLoginActivity(){
        Intent intent = new Intent(getActivity(), LoginAcitivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
