package com.wondereight.airsensio.UtilClass;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.wondereight.airsensio.Modal.UserModal;

/**
 * Created by Miguel on 02-26-2016.
 */
public class SaveSharedPreferences {

    static final String LoginUserData = "LoginUserData";
    static final String InstallInfo = "InstallInfo";
    //

    static SharedPreferences getSharedPreferences(Context ctx) {

        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    public static void setLoginUserData(Context ctx, UserModal userModal) {
        Gson gson = new Gson();
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(LoginUserData, gson.toJson(userModal));
        editor.commit();
    }

    public static UserModal getLoginUserData(Context ctx) {
        Gson gson = new Gson();
        String json = getSharedPreferences(ctx).getString(LoginUserData, new Gson().toJson(new UserModal()));
        return gson.fromJson(json, UserModal.class);
    }

    public static void setInstallInfo(Context ctx, Boolean installed) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putBoolean(InstallInfo, installed);
        editor.commit();
    }

    public static Boolean getInstallInfo(Context ctx) {
        return getSharedPreferences(ctx).getBoolean(InstallInfo, false);
    }

    public static void clearUserdata(Context ctx) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.remove(LoginUserData);
        editor.commit();
    }
}
