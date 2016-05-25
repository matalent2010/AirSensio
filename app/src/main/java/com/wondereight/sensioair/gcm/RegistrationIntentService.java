/**
 * Copyright 2015 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.wondereight.sensioair.gcm;

import android.app.IntentService;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.goebl.david.Response;
import com.goebl.david.Webb;
import com.goebl.david.WebbException;
import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.wondereight.sensioair.Modal.UserModal;
import com.wondereight.sensioair.R;
import com.wondereight.sensioair.UtilClass.AirSensioRestClient;
import com.wondereight.sensioair.UtilClass.Constant;
import com.wondereight.sensioair.UtilClass.SaveSharedPreferences;
import com.wondereight.sensioair.UtilClass.UtilityClass;

import java.io.IOException;

import cz.msebera.android.httpclient.Header;

public class RegistrationIntentService extends IntentService {

    private static final String LOG_TAG = "RegIntentService";
    private static final String[] TOPICS = {"global"};

    public RegistrationIntentService() {
        super(LOG_TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        try {
            // [START register_for_gcm]
            // Initially this call goes out to the network to retrieve the token, subsequent calls
            // are local.
            // R.string.gcm_defaultSenderId (the Sender ID) is typically derived from google-services.json.
            // See https://developers.google.com/cloud-messaging/android/start for details on this file.
            // [START get_token]
            InstanceID instanceID = InstanceID.getInstance(this);
            String authorizedEntity = getString(R.string.gcm_defaultSenderId);
            String token = instanceID.getToken(authorizedEntity, GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            // [END get_token]
            Log.i(LOG_TAG, "GCM Registration Token: " + token);

            // TODO: Implement this method to send any registration to your app's servers.
            sendRegistrationToServer(token);

            // Subscribe to topic channels
            subscribeTopics(token);

            // You should store a boolean that indicates whether the generated token has been
            // sent to your server. If the boolean is false, send the token to your server,
            // otherwise your server should have already received the token.
//            sharedPreferences.edit().putBoolean(SAPreferences.SENT_TOKEN_TO_SERVER, true).apply();
            // [END register_for_gcm]
        } catch (Exception e) {
            Log.e(LOG_TAG, "Failed to complete token refresh", e);
            // If an exception happens while fetching the new token or updating our registration data
            // on a third-party server, this ensures that we'll attempt the update at a later time.
            sharedPreferences.edit().putBoolean(SAPreferences.SENT_TOKEN_TO_SERVER, false).apply();
        }
        // Notify UI that registration has completed, so the progress indicator can be hidden.
        Intent registrationComplete = new Intent(SAPreferences.REGISTRATION_COMPLETE);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    /**
     * Persist registration to third-party servers.
     *
     * Modify this method to associate the user's GCM registration token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        // Add custom implementation, as needed.
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if( !sharedPreferences.getBoolean(SAPreferences.SENT_TOKEN_TO_SERVER, false)) {
            restCallSendPushIDApi(token);
        }
    }

    /**
     * Subscribe to any GCM topics of interest, as defined by the TOPICS constant.
     *
     * @param token GCM token
     * @throws IOException if unable to reach the GCM PubSub service
     */
    // [START subscribe_topics]
    private void subscribeTopics(String token) throws IOException {
        GcmPubSub pubSub = GcmPubSub.getInstance(this);
        for (String topic : TOPICS) {
            pubSub.subscribe(token, "/topics/" + topic, null);
        }
    }
    // [END subscribe_topics]

    private void restCallSendPushIDApi( String pushid ){

        UserModal userModal = SaveSharedPreferences.getLoginUserData(this);
        String str_userid = userModal.getId();
        String str_email = userModal.getEmail();
        String str_deviceid = UtilityClass.GetDeviceID(this);
        String str_hash = UtilityClass.MD5(str_deviceid + str_email + Constant.LOGIN_SECTRET);

        Webb webb = Webb.create();
        webb.setBaseUri(AirSensioRestClient.BASE_URL);
//        webb.setDefaultHeader(Webb.HDR_USER_AGENT, Const.UA);
        try {
            Response<String> response = webb
                    .post(AirSensioRestClient.SEND_PUSHID)
                    .param(Constant.STR_USERID, str_userid)
                    .param(Constant.STR_EMAIL, str_email)
                    .param(Constant.STR_DEVICEID, str_deviceid)
                    .param(Constant.STR_HASH, str_hash)
                    .param(Constant.STR_SENDPUSHID, pushid)
                    .ensureSuccess()
                    .asString();
            if (response.isSuccess()) {
                String responseString = response.getBody();
                if (responseString == null) {
                    Log.e(LOG_TAG, "None response string");
                } else if (responseString.equals("0")) {
                    PreferenceManager.getDefaultSharedPreferences(this)
                            .edit().putBoolean(SAPreferences.SENT_TOKEN_TO_SERVER, true)
                            .apply();
                    Log.d(LOG_TAG, "SEND_PUSHID to success");
                } else if (responseString.equals("1")) {
                    Log.e(LOG_TAG, "Incorrect Hash");
                } else if (responseString.equals("2")) {
                    Log.d(LOG_TAG, "User ID not provided");
                } else if (responseString.equals("3")) {
                    Log.d(LOG_TAG, "Device ID not provided");
                } else if (responseString.equals("9")) {
                    Log.d(LOG_TAG, "Nothing Found");
                } else {
                    Log.e(LOG_TAG, "SEND_PUSHID Exception Error:");
                }
            } else {
                String err = response.getErrorBody().toString();
                Log.e(LOG_TAG, "errorString: " + err);
            }
        } catch (WebbException ex) {
            String err = ex.getMessage();//response.getErrorBody().toString();
            Log.e(LOG_TAG, "errorString: " + err);
        }
    }

}

