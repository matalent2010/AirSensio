package com.wondereight.sensioair.Fragment;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.wondereight.sensioair.Activity.HomeActivity;
import com.wondereight.sensioair.Activity.MainActivity;
import com.wondereight.sensioair.CustomView.SnapBar;
import com.wondereight.sensioair.Helper._Debug;
import com.wondereight.sensioair.Modal.SettingsModal;
import com.wondereight.sensioair.Modal.UserModal;
import com.wondereight.sensioair.R;
import com.wondereight.sensioair.UtilClass.AirSensioRestClient;
import com.wondereight.sensioair.UtilClass.Constant;
import com.wondereight.sensioair.UtilClass.Global;
import com.wondereight.sensioair.UtilClass.SaveSharedPreferences;
import com.wondereight.sensioair.UtilClass.UtilityClass;

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
        AlertDialog sureDlg;
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(_context);
        alertDialogBuilder.setMessage("It will send the settings information via your email.")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        restCallSendUserinfoApi();
                    }
                });
        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        sureDlg = alertDialogBuilder.create();
        sureDlg.show();
    }

    @OnClick(R.id.btnSaveSettings)
    public void onclickSaveSettings(){
        restCallSaveSettingsApi();
    }

    @OnClick(R.id.btnViewTerms)
    public void onclickViewTerms(){
        Intent intentTerms = new Intent(SettingsFragment._context, MainActivity.class);
        intentTerms.putExtra("Settings", true);
        startActivity(intentTerms);
    }

    @Override
    public void onDestroy() {
        _debug.d(LOG_TAG, "StatisticsFragment destroyed.");
        AirSensioRestClient.cancelRequest(getContext());
        super.onDestroy();
    }

    private boolean checkValidation() {
        boolean ret = true;

//        if (!Validation.hasText(etFirstName, "First Name")) ret = false;

        return ret;
    }

    private void restCallSaveSettingsApi() {

        RequestParams params = new RequestParams();
        UserModal userModal = Global.GetInstance().GetUserModal();
        String str_noti_allergens = String.valueOf(sbAllergens.getSteps());
        String str_noti_pollution = String.valueOf(sbPollution.getSteps());
        String str_userid = userModal.getId();
        String str_email = userModal.getEmail();
        String str_deviceid = utilityClass.GetDeviceID();
        String str_hash = UtilityClass.MD5(str_deviceid + str_email + Constant.LOGIN_SECTRET);

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
                    SaveSettings();
                    utilityClass.showAlertMessage(getResources().getString(R.string.title_notice), getResources().getString(R.string.savingsetting_success));
                    _debug.d(LOG_TAG, "Saving Settings Success");
                } else if (responseString.equals("1")) {
                    _debug.e(LOG_TAG, "Incorrect Hash");
                } else if (responseString.equals("2")) {
                    utilityClass.toast(getResources().getString(R.string.email_used));
                    _debug.d(LOG_TAG, "Email Used by another account");
                } else if (responseString.equals("3")) {
                    utilityClass.showAlertMessage(getResources().getString(R.string.title_notice), getResources().getString(R.string.device_not));
                    _debug.e(LOG_TAG, "Device ID not provided");
                } else if (responseString.equals("4")) {
                    utilityClass.showAlertMessage(getResources().getString(R.string.title_notice), getResources().getString(R.string.no_exist));
                    _debug.e(LOG_TAG, "Account Does Not Exist");
                } else {
                    utilityClass.showAlertMessage(getResources().getString(R.string.title_notice), getResources().getString(R.string.no_exist));
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

    private void restCallSendUserinfoApi() {

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


        // AirSensioRestClient.post(AirSensioRestClient.LOGIN, params, new AsyncHttpResponseHandler() {
        AirSensioRestClient.post(AirSensioRestClient.SEND_USERINFO, params, new TextHttpResponseHandler() {   //new JsonHttpResponseHandler(false) : onSuccess(int statusCode, Header[] headers, String responseString) must be overrided.
            @Override
            public void onStart() {
                // called before request is started
                utilityClass.processDialogStart(false);
                _debug.d(LOG_TAG, "AirSensioRestClient.SEND_USERINFO.onStart");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                utilityClass.processDialogStop();
                if (responseString == null) {
                    _debug.e(LOG_TAG, "None response string");
                } else if (responseString.equals("0")) {
                    utilityClass.showAlertMessage(getResources().getString(R.string.title_notice), getResources().getString(R.string.senduserinfo_success));
                    _debug.d(LOG_TAG, "Sending of User info Success");
                } else if (responseString.equals("1")) {
                    _debug.e(LOG_TAG, "Incorrect Hash");
                } else if (responseString.equals("3")) {
                    utilityClass.showAlertMessage(getResources().getString(R.string.title_notice), getResources().getString(R.string.device_not));
                    _debug.d(LOG_TAG, "Device ID not provided");
                } else if (responseString.equals("4")) {
                    utilityClass.showAlertMessage(getResources().getString(R.string.title_notice), getResources().getString(R.string.no_exist));
                    _debug.d(LOG_TAG, "Account Does Not Exist");
                } else {
                    utilityClass.showAlertMessage(getResources().getString(R.string.title_notice), getResources().getString(R.string.not_parsing));
                    _debug.e(LOG_TAG, "Send user info Exception Error:"+responseString);
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
                _debug.d(LOG_TAG, "AirSensioRestClient.SEND_USERINFO.onRetry");
            }

            @Override
            public void onFinish() {
                utilityClass.processDialogStop();
            }

        });
    }

    private boolean SaveSettings(){
        UserModal user = Global.GetInstance().GetUserModal();
        user.setThresholdAllergens(String.valueOf(sbAllergens.getSteps()));
        user.setThresholdPollution(String.valueOf(sbPollution.getSteps()));
        Global.GetInstance().SaveUserModal(_context, user);
        return true;
    }

    private boolean setSettingsValue(){
        String allergens = Global.GetInstance().GetUserModal().getThresholdAllergens();
        String pollution = Global.GetInstance().GetUserModal().getThresholdPollution();
        int nAllergens = Integer.valueOf(allergens);
        int nPollution = Integer.valueOf(pollution);
        nAllergens = nAllergens<0 ? 5 : nAllergens>10 ? 5 : nAllergens;
        nPollution = nPollution<0 ? 5 : nPollution>10 ? 5 : nPollution;
        sbAllergens.setSteps(nAllergens);
        sbPollution.setSteps(nPollution);
        return true;
    }

    @Override
    public void onResume() {
        setSettingsValue();
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
