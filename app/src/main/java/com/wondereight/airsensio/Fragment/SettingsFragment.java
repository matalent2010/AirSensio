package com.wondereight.airsensio.Fragment;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.wondereight.airsensio.Activity.HomeActivity;
import com.wondereight.airsensio.Activity.MainActivity;
import com.wondereight.airsensio.CustomView.SnapBar;
import com.wondereight.airsensio.Helper._Debug;
import com.wondereight.airsensio.Modal.UserModal;
import com.wondereight.airsensio.R;
import com.wondereight.airsensio.UtilClass.AirSensioRestClient;
import com.wondereight.airsensio.UtilClass.Constant;
import com.wondereight.airsensio.UtilClass.SaveSharedPreferences;
import com.wondereight.airsensio.UtilClass.UtilityClass;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;

/**
 * Created by Miguel on 02/2/2016.
 */
public class SettingsFragment extends Fragment {
    private static Context _context;
    private static final String LOG_TAG = "SettingsFragment";
    private static _Debug _debug = new _Debug(true);
    UtilityClass utilityClass;

    @Bind(R.id.snapbar_allergens)
    SnapBar sbAllergens;
    @Bind(R.id.snapbar_pollution)
    SnapBar sbPollution;

    public SettingsFragment() {
    }

    public static Fragment newInstance(Context context) {
        _context = context;
        SettingsFragment f = new SettingsFragment();
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        ButterKnife.bind(this, view);

        utilityClass = ((HomeActivity)getActivity()).utilityClass;
        return view;
    }

    @OnClick(R.id.btnSendEmail)
    public void onclickSendEmail(){
        restCallSaveSettingsApi();
    }

    @OnClick(R.id.btnViewTerms)
    public void onclickViewTerms(){
        Intent intentTerms = new Intent(SettingsFragment._context, MainActivity.class);
        intentTerms.putExtra("Settings", true);
        startActivity(intentTerms);
    }

    private boolean checkValidation() {
        boolean ret = true;

//        if (!Validation.hasText(etFirstName, "First Name")) ret = false;

        return ret;
    }

    private void restCallSaveSettingsApi() {

        RequestParams params = new RequestParams();
        UserModal userModal = SaveSharedPreferences.getLoginUserData(getActivity());
        String str_noti_allergens = String.valueOf(sbAllergens.getSteps());
        String str_noti_pollution = String.valueOf(sbPollution.getSteps());
        String str_userid = userModal.getId();
        String str_email = userModal.getEmail();
        String str_deviceid = utilityClass.GetDeviceID();
        String str_hash = utilityClass.MD5(str_deviceid + str_email + Constant.LOGIN_SECTRET);

        params.put(Constant.STR_NOTI_ALLERGENS, str_noti_allergens);
        params.put(Constant.STR_NOTI_POLLUTION, str_noti_pollution);
        params.put(Constant.STR_USERID, str_userid);
        params.put(Constant.STR_EMAIL, str_email);
        params.put(Constant.STR_DEVICEID, str_deviceid);
        params.put(Constant.STR_HASH, str_hash);


        // AirSensioRestClient.post(AirSensioRestClient.LOGIN, params, new AsyncHttpResponseHandler() {
        AirSensioRestClient.post(AirSensioRestClient.SAVE_SETTINGS, params, new TextHttpResponseHandler() {   //new JsonHttpResponseHandler(false) : onSuccess(int statusCode, Header[] headers, String responseString) must be overrided.
            @Override
            public void onStart() {
                // called before request is started
                utilityClass.processDialogStart(false);
                _debug.d(LOG_TAG, "AirSensioRestClient.SAVE_INFO.onStart");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                utilityClass.processDialogStop();
                if (responseString == null) {
                    _debug.e(LOG_TAG, "None response string");
                } else if (responseString.equals("0")) {
                    utilityClass.showAlertMessage(getResources().getString(R.string.title_notice), getResources().getString(R.string.savingsetting_success));
                    _debug.e(LOG_TAG, "Saving Settings Success");
                } else if (responseString.equals("1")) {
                    _debug.e(LOG_TAG, "Incorrect Hash");
                } else if (responseString.equals("2")) {
                    utilityClass.toast(getResources().getString(R.string.email_used));
                    _debug.e(LOG_TAG, "Email Used by another account");
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
                _debug.d(LOG_TAG, "errorString: "+responseString);
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

}
