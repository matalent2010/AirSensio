package com.wondereight.sensioair.UtilClass;

/**
 * Created by Miguel on 02/23/2016.
 */

import android.content.Context;

import com.loopj.android.http.*;

public class AirSensioRestClient {
    public static final String BASE_URL = "https://wonderthirteen.com/whitelabair/";

    public static final String LOGIN = "get_login.php";
    public static final String SIGNUP = "do_register.php";
    public static final String FORGOT_PASSWORD = "forgot_password.php";
    public static final String GET_DEVICE_DATA = "get_device_data.php";
    public static final String SAVE_INFO = "do_saveinfo.php";   //about your health page
    public static final String SAVE_SETTINGS = "do_savesettings.php";
    public static final String SEND_USERINFO = "send_user_info.php";
    public static final String GET_GRAPH_DATA = "get_graph_data.php";
    public static final String LOG_OUTBREAK = "do_logoutbreak.php";
    public static final String GET_CITYLIST = "get_cities.php";
    public static final String GET_SAVED_INFO = "get_saved_info.php";
    public static final String GET_PROFILE_INFO = "get_profile_info.php";
    public static final String GET_ALLERGY_INDEX = "get_indexes.php";
    public static final String GET_DATA_DETAILS = "get_data_details.php";
    public static final String GET_DATA_DETAILS2 = "get_data_details2.php";
    public static final String GET_ADVICE = "get_advice.php";
    public static final String SEND_FEEDBACK = "do_send_feedback.php";
    public static final String SEND_PUSHID = "get_push_id.php";

    private static AsyncHttpClient client = new AsyncHttpClient();

    AirSensioRestClient(){
        client.setConnectTimeout(20 * 1000);
    }
    public static void get(String url, RequestParams params, AsyncHttpResponseHandler  responseHandler) {
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler  responseHandler) {
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void cancelRequest(Context context){
        client.cancelRequests(context, true);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }
}