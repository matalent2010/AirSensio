package com.wondereight.airsensio.Fragment;

import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.wondereight.airsensio.Activity.LoginAcitivity;
import com.wondereight.airsensio.Helper._Debug;
import com.wondereight.airsensio.Modal.UserModal;
import com.wondereight.airsensio.R;
import com.wondereight.airsensio.UtilClass.AirSensioRestClient;
import com.wondereight.airsensio.UtilClass.Constant;
import com.wondereight.airsensio.UtilClass.Global;
import com.wondereight.airsensio.UtilClass.ParsingResponse;
import com.wondereight.airsensio.UtilClass.SaveSharedPreferences;
import com.wondereight.airsensio.UtilClass.UtilityClass;

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
        return view;
    }

    @OnClick(R.id.btnLogout)
    public void onClickLogout(){
        SaveSharedPreferences.clearUserdata(getContext());
        Global.GetInstance().init();
        Intent intent = new Intent(getActivity(), LoginAcitivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
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

    private void restCallGetSavedInfoApi() {

        RequestParams params = new RequestParams();
        UserModal userModal = SaveSharedPreferences.getLoginUserData(getActivity());
        String str_userid = userModal.getId();
        String str_email = userModal.getEmail();
        String str_deviceid = utilityClass.GetDeviceID();
        String str_hash = utilityClass.MD5(str_deviceid + str_email + Constant.LOGIN_SECTRET);

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
                    _debug.e(LOG_TAG, "Incorrect Hash");
                } else if (responseString.equals("1")) {
                    _debug.e(LOG_TAG, "Incorrect Hash");
                } else if (responseString.equals("2")) {
                    //utilityClass.showAlertMessage(getResources().getString(R.string.title_notice), getResources().getString(R.string.device_not));
                    _debug.d(LOG_TAG, "Device ID not provided");
                } else if (responseString.equals("3")) {
                    //utilityClass.showAlertMessage(getResources().getString(R.string.title_notice), getResources().getString(R.string.userid_not));
                    _debug.d(LOG_TAG, "User ID not provided");
                } else if (responseString.equals("9")) {
                    //utilityClass.showAlertMessage(getResources().getString(R.string.title_notice), getResources().getString(R.string.no_exist));
                    _debug.d(LOG_TAG, "Nothing Found");
                } else {
                    try {
                        ParsingResponse.parsingSavedInfo(new JSONArray(responseString));
//                        if (indexModals.get(0).isSet()) {
//                            tvAllergyIndex.setText(indexModals.get(0).getIndexValue());
//                            tvAlergyIntensity.setText(indexModals.get(0).getLogIntensity());
//                        }
//                        if (indexModals.get(1).isSet()) {
//                            tvPollutionIndex.setText(indexModals.get(1).getIndexValue());
//                            tvPollutionIntensity.setText(indexModals.get(1).getLogIntensity());
//                        }

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
}
