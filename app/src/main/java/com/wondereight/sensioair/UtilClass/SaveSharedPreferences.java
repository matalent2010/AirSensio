package com.wondereight.sensioair.UtilClass;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.RequestParams;
import com.wondereight.sensioair.Activity.MainActivity;
import com.wondereight.sensioair.Modal.RequestParamsModal;
import com.wondereight.sensioair.Modal.SettingsModal;
import com.wondereight.sensioair.Modal.UserModal;
import com.wondereight.sensioair.R;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by Miguel on 02-26-2016.
 */
public class SaveSharedPreferences {

    static final String LoginUserData = "LoginUserData";
    static final String InstallInfo = "InstallInfo";
    static final String SendSymptomList = "SymptomList";

    static Context _context;
    static RequestParamsModal _modal;
    static final int DELAYED_LENGTH = 1000;

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

    public static void saveSettings(Context ctx, String user, SettingsModal settingsValue) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        Gson gson = new Gson();
        editor.putString(user, gson.toJson(settingsValue));
        editor.commit();
    }

    public static SettingsModal getSettings(Context ctx, String user) {
        Gson gson = new Gson();
        String json = getSharedPreferences(ctx).getString(user, new Gson().toJson(new SettingsModal()));
        return gson.fromJson(json, SettingsModal.class);
    }

    public static ArrayList<RequestParamsModal> getSendSymptomList(Context ctx) {
        Gson gson = new Gson();
        String json = getSharedPreferences(ctx).getString(SendSymptomList, new Gson().toJson(new ArrayList<>()));
        Type type = new TypeToken<ArrayList<RequestParamsModal>>() {}.getType();
        return gson.fromJson(json, type);
    }

    public static void saveSendSymptomList(Context ctx, ArrayList<RequestParamsModal> paramsList) {

        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        Gson gson = new Gson();
        editor.putString(SendSymptomList, gson.toJson(paramsList));
        editor.commit();
    }

    public static boolean addSymptomData(Context ctx, RequestParamsModal params) {
        _context = ctx;
        _modal = params;
        if( UtilityClass.isSendingNow() ){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    addSymptomData(_context, _modal);
                }
            }, DELAYED_LENGTH);
        } else {
            ArrayList<RequestParamsModal> list = getSendSymptomList(ctx);
            list.add(params);
            saveSendSymptomList(ctx, list);
        }
        return true;
    }

    public static void clearUserdata(Context ctx) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.remove(LoginUserData);
        editor.commit();
    }
}
