package com.wondereight.airsensio.UtilClass;

import android.support.annotation.Nullable;

import com.wondereight.airsensio.Helper._Debug;
import com.wondereight.airsensio.Modal.DeviceDataModal;
import com.wondereight.airsensio.Modal.UserModal;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Migeul on 3/2/2016.
 */
public class ParsingResponse {
    private static final String LOG_TAG = "ParsingResponse";
    private static final _Debug _debug = new _Debug(true);

    public static UserModal parsingUserModal(JSONArray arr){
        try {
            UserModal userModal = new UserModal();

            JSONObject firstEvent = arr.getJSONObject(0);

            userModal.setId(firstEvent.optString(Constant.STR_USERID));
            userModal.setFirstname(firstEvent.optString(Constant.STR_FIRSTNAME));
            userModal.setLastname(firstEvent.optString(Constant.STR_LASTNAME));
            userModal.setEmail(firstEvent.optString(Constant.STR_EMAIL));
            userModal.setGender(firstEvent.optString(Constant.STR_GENDER));
            userModal.setPhone(firstEvent.optString(Constant.STR_PHONE));
            userModal.setNotification(firstEvent.optString(Constant.STR_NOTIFICATION));
            userModal.setBirthday(firstEvent.optString(Constant.STR_BIRTHDAY));
            userModal.setNewsletter(firstEvent.optString(Constant.STR_NEWSLETTER));
            userModal.setSession_nbr(firstEvent.optString(Constant.STR_NBR));

            return userModal;
        } catch (JSONException e) {
            e.printStackTrace();
            _debug.e(LOG_TAG, "JSONArray Fail: " + e.getMessage());
        }
        return null;
    }


    public static DeviceDataModal parsingDeviceData(JSONArray arr){
        try {
            DeviceDataModal deviceModal = new DeviceDataModal();

            JSONObject firstEvent = arr.getJSONObject(0);

            deviceModal.setEnvId(firstEvent.optString(Constant.STR_ENVID)); //Constant.STR_USERID
            deviceModal.setEnvValue(firstEvent.optString(Constant.STR_ENVVALUE));
            deviceModal.setDatetime(firstEvent.optString(Constant.STR_LOGDATETIME));
            deviceModal.setCityId(firstEvent.optString(Constant.STR_CITYID));

            return deviceModal;
        } catch (JSONException e) {
            e.printStackTrace();
            _debug.e(LOG_TAG, "JSONArray Fail: " + e.getMessage());
        }
        return null;
    }
}
