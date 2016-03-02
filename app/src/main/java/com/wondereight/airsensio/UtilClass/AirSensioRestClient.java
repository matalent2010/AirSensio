package com.wondereight.airsensio.UtilClass;

/**
 * Created by Miguel on 02/23/2016.
 */

import com.loopj.android.http.*;

public class AirSensioRestClient {
    private static final String BASE_URL = "https://wonderthirteen.com/whitelabair/";

    public static final String LOGIN = "get_login.php";
    public static final String SIGNUP = "do_register.php";
    public static final String FORGOT_PASSWORD = "forgot_password.php";
    public static final String GET_DEVICE_DATA = "get_device_data.php";
    public static final String SAVE_INFO = "do_saveinfo.php";   //about your health page
    public static final String SAVE_SETTINGS = "do_savesettings.php";
    public static final String GET_GRAPH_DATA = "get_graph_data.php";
    public static final String LOG_OUTBREAK = "do_logoutbreak.php";

    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler  responseHandler) {
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler  responseHandler) {
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }
}