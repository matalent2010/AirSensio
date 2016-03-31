package com.wondereight.sensioair.Fragment;


import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

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

import org.json.JSONArray;
import org.json.JSONException;

import java.util.logging.Handler;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;

/**
 * A simple {@link Fragment} subclass.
 */
public class FeedbackFragment extends Fragment {
    private static Context _context;
    private static final String LOG_TAG = "FeedbackFragment";
    private static _Debug _debug = new _Debug(true);
    private UtilityClass utilityClass;


    private ProfileFragment parentFg;
    public int nCallFeedback;

    @Bind(R.id.feedback)
    EditText etFeedback;

    public FeedbackFragment() {
        // Required empty public constructor
    }

    public static Fragment newInstance(Context context) {
        _context = context;
        FeedbackFragment f = new FeedbackFragment();
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View _view = inflater.inflate(R.layout.fragment_feedback, container, false);
        ButterKnife.bind(this, _view);
        utilityClass = new UtilityClass(getActivity());

        parentFg = (ProfileFragment)getParentFragment();
        return _view;
    }

    @OnClick(R.id.btnSubmit)
    public void onClickSubmit(){
        if( !etFeedback.getText().toString().isEmpty() )
            restCallSendFeedbackApi();
        else {

        }

    }

    @OnClick(R.id.btn_close)
    public void onClickClose(){

        etFeedback.setText("");
        android.support.v4.app.FragmentTransaction ft = parentFg.getChildFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.slide_top_in, R.anim.slide_bottom_out);
        ft.remove(this);
        ft.commit();
        clearText();
    }

    private void goBack(){

        etFeedback.setText("");
        android.support.v4.app.FragmentTransaction ft = parentFg.getChildFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.slide_top_in, R.anim.slide_top_out);
//        ft.add(SensioFragment.newInstance(_context), "feedbackFragment");
        ft.remove(this);
        ft.commit();

//        clearText();
    }

    private void clearText(){
        new android.os.Handler().postDelayed(new Runnable() {
            public void run() {
                etFeedback.setText("");
            }
        }, 800);
    }

    private void restCallSendFeedbackApi(){

        RequestParams params = new RequestParams();
        UserModal userModal = SaveSharedPreferences.getLoginUserData(getActivity());
        String str_userid = userModal.getId();
        String str_email = userModal.getEmail();
        String str_deviceid = utilityClass.GetDeviceID();
        String str_hash = utilityClass.MD5(str_deviceid + str_email + Constant.LOGIN_SECTRET);
        String str_feedback = etFeedback.getText().toString();

        params.put(Constant.STR_USERID, str_userid);
        params.put(Constant.STR_EMAIL, str_email);
        params.put(Constant.STR_DEVICEID, str_deviceid);
        params.put(Constant.STR_HASH, str_hash);
        params.put(Constant.STR_FEEDBACK, str_feedback);

        AirSensioRestClient.post(AirSensioRestClient.SEND_FEEDBACK, params, new TextHttpResponseHandler() {
            @Override
            public void onStart() {
                // called before request is started
                utilityClass.processDialogStart(false);
                _debug.d(LOG_TAG, "AirSensioRestClient.SEND_FEEDBACK.onStart");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                utilityClass.processDialogStop();
                if (responseString == null) {
                    _debug.e(LOG_TAG, "None response string");
                } else if (responseString.equals("0")) {

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(_context);
                    alertDialogBuilder.setTitle(getString(R.string.title_notice))
                            .setMessage(getString(R.string.feedback_success))
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();

                                    new android.os.Handler().postDelayed(new Runnable() {
                                        public void run() {
                                            goBack();
                                        }
                                    }, 500);
                                }
                            });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                    _debug.d(LOG_TAG, "Send feedback to success");
                } else if (responseString.equals("1")) {
                    utilityClass.showAlertMessage(getString(R.string.title_alert), getString(R.string.incorrect_hash));
                    _debug.e(LOG_TAG, "Incorrect Hash");
                } else if (responseString.equals("2")) {
                    utilityClass.showAlertMessage(getResources().getString(R.string.title_notice), getResources().getString(R.string.userid_not));
                    _debug.d(LOG_TAG, "User ID not provided");
                } else if (responseString.equals("3")) {
                    utilityClass.showAlertMessage(getResources().getString(R.string.title_notice), getResources().getString(R.string.device_not));
                    _debug.d(LOG_TAG, "Device ID not provided");
                } else {
                    _debug.e(LOG_TAG, "Send feedback Exception Error:" );
                }
                nCallFeedback = 0;
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                //utilityClass.toast(getResources().getString(R.string.check_internet));
                _debug.e(LOG_TAG, "errorString: " + responseString);
                nCallFeedback++;
                if (nCallFeedback < 3) {
                    restCallSendFeedbackApi();
                } else {
                    utilityClass.processDialogStop();
                    nCallFeedback = 0;
                }
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried{
                //utilityClass.toast(getResources().getString(R.string.try_again));
                _debug.d(LOG_TAG, "AirSensioRestClient.SEND_FEEDBACK.onRetry");
                nCallFeedback++;
                if (nCallFeedback < 3) {
                    restCallSendFeedbackApi();
                } else {
                    utilityClass.processDialogStop();
                    nCallFeedback = 0;
                }
            }

            @Override
            public void onFinish() {
                _debug.d(LOG_TAG, "AirSensioRestClient.SEND_FEEDBACK.onFinish");
            }

        });
    }

}
