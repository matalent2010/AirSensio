package com.wondereight.sensioair.UtilClass;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Toast;


import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.wondereight.sensioair.Modal.RequestParamsModal;
import com.wondereight.sensioair.R;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Miguel on 02/17/2016.
 */
public class UtilityClass {

    //Class
    Context _context;
    private ProgressDialog pdialog;
    private AlertDialog alertDialog;
    private ArrayList<Boolean> _symFlagList;
    private int _symRequestCount;
    private static boolean _sendingNow;
    private static Runnable _r;

//Variable

    public UtilityClass(Context context) {
        this._context = context;
        _symFlagList = new ArrayList<>();
        _symRequestCount = 0;
        _sendingNow = false;
    }

    public void toast(String str) {
        Toast.makeText(_context, str, Toast.LENGTH_LONG).show();
    }

    public boolean isInternetConnection() {
        return isInternetConnection(_context);
    }

    public static boolean isInternetConnection(Context context) {

        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();

            if( activeNetwork != null) {
                if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                    // connected to wifi
                    //Toast.makeText(context, activeNetwork.getTypeName(), Toast.LENGTH_SHORT).show();
                    Log.i("Network State:", "wifi enabled");
                    return true;
                } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                    // connected to the mobile provider's data plan
                    //Toast.makeText(context, activeNetwork.getTypeName(), Toast.LENGTH_SHORT).show();
                    Log.i("Network State:", "mobile enabled");
                    return true;
                }
            }
            Log.i("Network State:", "no enabled");
            return false;

        } catch (Exception e) {
            Log.i("Network Check Error:", e.toString());
            return false;
        }
    }

    public void processDialogStart(boolean cancelable) {
        if(pdialog == null) {
            pdialog = new ProgressDialog(_context);
            pdialog.setMessage("Please Wait");
            pdialog.setIndeterminate(false);
        }
        pdialog.setCancelable(cancelable);
        if( !pdialog.isShowing() )
            pdialog.show();

    }

    public void processDialogStop() {
        if (pdialog != null) {
            if (pdialog.isShowing()) {
                pdialog.dismiss();
            }
        }
    }

    public int GetHeight() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) _context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }

    public int GetWidth() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) _context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }

    public String GetDeviceID() {
        return Settings.Secure.getString(_context.getContentResolver(),Settings.Secure.ANDROID_ID);
    }

    static public String GetDeviceID(Context context) {
        return Settings.Secure.getString(context.getContentResolver(),Settings.Secure.ANDROID_ID);
    }

    public void showGPSDisabledAlertToUser() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(_context);
        alertDialogBuilder.setMessage("GPS is disabled in your device. Would you like to enable it?")
                .setCancelable(false)
                .setPositiveButton("Setting", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent callGPSSettingIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        _context.startActivity(callGPSSettingIntent);
                    }
                });
        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //  utilityClass.toast("Must GPS enable for map.");
                dialog.cancel();
            }
        });
        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void showAlertMessage(String title, String msg) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(_context);
        alertDialogBuilder.setTitle(title)
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
//Convert to MD5
    public static String MD5(String string) {
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest
                    .getInstance(MD5);
            digest.update(string.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static float[] toPrimitive(Float[] array, float valueForNull) {
        if (array == null) {
            return null;
        } else if (array.length == 0) {
            return new float[0];
        }
        final float[] result = new float[array.length];
        for (int i = 0; i < array.length; i++) {
            Float b = array[i];
            result[i] = (b == null ? valueForNull : b.floatValue());
        }
        return result;
    }

    public static String getDateTime(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String datetime = dateFormat.format(Calendar.getInstance().getTime());//String datetime = dateFormat.format(Calendar.getInstance(TimeZone.getTimeZone("GMT")).getTime());
        return datetime;
    }

    public boolean isHavingSymptomList(){
        ArrayList<RequestParamsModal> params =  SaveSharedPreferences.getSendSymptomList(_context);
        return params.size()>0;
    }

    public boolean sendSymptomList(){
        return sendSymptomList(null);
    }

    public boolean sendSymptomList(Runnable r){
        String LOG_TAG = "sendSymptomList";
        Log.d(LOG_TAG, "send list function");
        if( isSendingNow() ) return false;
        _r = r;
        ArrayList<RequestParamsModal> params =  SaveSharedPreferences.getSendSymptomList(_context);
        RequestParamsModal item;
        _symFlagList.clear();
        _symRequestCount = 0;
        Log.d(LOG_TAG, "Size : " + params.size() + ", Connect State : " + isInternetConnection(_context));
        if( params.size() > 0 && isInternetConnection(_context) ) {
            for (int i = 0; i < params.size(); i++) {
                _sendingNow = true;
                _symFlagList.add(true);
                item = params.get(i);
                restCallLogOutbreakApi(item, i);
            }
        } else {

        }
        return true;
    }

    public static boolean isSendingNow(){
        return _sendingNow;
    }

    private void restCallLogOutbreakApi(RequestParamsModal modal, int tag) {
        String LOG_TAG = "restCallLogOutbreakApi";
        Log.d(LOG_TAG, modal.toString());

        RequestParams param = new RequestParams();
        for(int i=0; i<modal.getCount(); i++)
            param.put(modal.getKey(i), modal.getValue(i));

        TextHttpResponseHandler handler = new TextHttpResponseHandler() {
            String LOG_TAG = "UtilityClass";

            @Override
            public void onStart() {
                Log.d(LOG_TAG, "AirSensioRestClient.LOG_OUTBREAK.onStart");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                if (responseString == null) {
                    Log.e(LOG_TAG, "None response string");
                } else if (responseString.equals("0")) {
                    int i = (int)getTag();
                    _symFlagList.set(i,false);
                    //finish();
                    Log.e(LOG_TAG, "Log outbreak Success");
                } else if (responseString.equals("1")) {
                    int i = (int)getTag();
                    _symFlagList.set(i,false);
                    Log.e(LOG_TAG, "Incorrect Hash");
                } else if (responseString.equals("2")) {
                    int i = (int)getTag();
                    _symFlagList.set(i,false);
                    Log.e(LOG_TAG, "User ID Not Provided");
                } else if (responseString.equals("3")) {
                    Log.e(LOG_TAG, "Device ID not provided");
                    int i = (int)getTag();
                    _symFlagList.set(i, false);
                } else if (responseString.equals("8")) {
                    int i = (int)getTag();
                    _symFlagList.set(i,false);
                    Log.e(LOG_TAG, "You have to select at least 1 symptom");
                } else if (responseString.equals("9")) {
                    Log.d(LOG_TAG, "Error, Please Try Again Later");
                } else {
                    Log.e(LOG_TAG, "Log your outbreak Exception Error:" + responseString);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e(LOG_TAG, "errorString: " + responseString);
            }

            @Override
            public void onRetry(int retryNo) {
                Log.d(LOG_TAG, "AirSensioRestClient.LOG_OUTBREAK.onRetry");
            }

            @Override
            public void onFinish() {
                _symRequestCount++;
                if(_symRequestCount >= _symFlagList.size() )
                    reSaveSymptomList();
            }
        };
        handler.setTag(tag);

        AirSensioRestClient.post(AirSensioRestClient.LOG_OUTBREAK, param, handler);
    }

    private boolean reSaveSymptomList(){
        String LOG_TAG = "reSaveSymptomList";
        ArrayList<RequestParamsModal> params =  SaveSharedPreferences.getSendSymptomList(_context);
        ArrayList<RequestParamsModal> new_prams = new ArrayList<>();
        Log.d(LOG_TAG, "Params Size:" + String.valueOf(params.size()) + ", symFlagList Size:"+ String.valueOf(_symFlagList.size()));
        if( params.size() > 0 ) {
            for (int i = 0; i < _symFlagList.size(); i++) {
                if (_symFlagList.get(i)) {
                    new_prams.add(params.get(i));
                }
            }
        }
        Log.d(LOG_TAG, "ReSaved Params Size:" + String.valueOf(new_prams.size()));
        _sendingNow = false;
        SaveSharedPreferences.saveSendSymptomList(_context, new_prams);
        if( _r != null)
            _r.run();
        return true;
    }


    public static String getExtraString(Intent data){
        String result = "";
        if( data == null )
            return result;
        Bundle bundle = data.getExtras();

        if( bundle != null ){
            for (String key : bundle.keySet()) {
                Object value = bundle.get(key);
                try {
                    assert value != null;
                    result += String.format("%s %s (%s)", key,
                            value.toString(), value.getClass().getName());
                } catch ( Exception ignored) {}
            }
        }
        return result;
    }
}