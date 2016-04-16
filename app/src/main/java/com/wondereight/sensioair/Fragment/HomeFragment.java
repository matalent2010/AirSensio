package com.wondereight.sensioair.Fragment;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.Bind;
import butterknife.OnClick;
import co.mobiwise.materialintro.animation.MaterialIntroListener;
import co.mobiwise.materialintro.prefs.PreferencesManager;
import co.mobiwise.materialintro.shape.ArrowType;
import co.mobiwise.materialintro.shape.Focus;
import co.mobiwise.materialintro.shape.FocusGravity;
import co.mobiwise.materialintro.view.MaterialIntroView;
import cz.msebera.android.httpclient.Header;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.shehabic.droppy.DroppyClickCallbackInterface;
import com.shehabic.droppy.DroppyMenuCustomItem;
import com.shehabic.droppy.DroppyMenuItem;
import com.shehabic.droppy.DroppyMenuPopup;
import com.shehabic.droppy.animations.DroppyFadeInAnimation;
import com.wondereight.sensioair.Activity.SymptomActivity;
import com.wondereight.sensioair.Adapter.ViewPagerAdapter;
import com.wondereight.sensioair.Helper._Debug;
import com.wondereight.sensioair.Modal.CityModal;
import com.wondereight.sensioair.Modal.DataDetailsModal;
import com.wondereight.sensioair.Modal.IndexModal;
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
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * Created by Miguel on 02/2/2016.
 */

public class HomeFragment extends Fragment implements MaterialIntroListener {
    private static Context _context;
    private static final String LOG_TAG = "HomeFragment";
    private static _Debug _debug = new _Debug(true);

    UtilityClass utilityClass;

    public static final int RESULT_SYMPTOM = 101;

    @Bind(R.id.city_name)
    TextView tvCityname;
    @Bind(R.id.field_cityname)
    LinearLayout rlCityname;
    @Bind(R.id.text_advice)
    TextView  tvAdvice;
    @Bind(R.id.allergy_index)
            TextView tvAllergyIndex;
    @Bind(R.id.allergy_intensity)
            TextView tvAlergyIntensity;
    @Bind(R.id.pollution_index)
            TextView tvPollutionIndex;
    @Bind(R.id.pollution_intensity)
            TextView tvPollutionIntensity;
    @Bind(R.id.logoutbreakCanvas)
            View _mainContainer;
    @Bind(R.id.sub_page)
            View mSubpage;
    @Bind(R.id.data_details_container)
            ViewGroup vgContainer;

    private ArrayList<DataDetailsModal> dataModals = new ArrayList<>();
    private int nCallDataDetails;

    DroppyMenuPopup cityMenu;
    Boolean isShownCityMenu = false;
    Boolean isLoading;
    private String strAdvice = "";
    int nCountCallingAdvice = 0;
    int nCountCallingIndex = 0;

    ArrayList<IndexModal> indexModals = new ArrayList<>();

    public HomeFragment() {
        indexModals.add(new IndexModal());
        indexModals.add(new IndexModal());
    }

    public static Fragment newInstance(Context context) {
        _context = context;
        HomeFragment f = new HomeFragment();
        return f;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind( this, view );

        utilityClass = new UtilityClass(getContext());

        setIndex();
        //tvAllergyIndex.setText();
        getAdvice();
        tvAdvice.setText(Html.fromHtml(strAdvice));

        getCityList();
        loadDataDetails();

        tvCityname.setText(Global.GetInstance().GetCityName());
        initCityMenu();

        _mainContainer.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    _mainContainer.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    _mainContainer.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
                int width = _mainContainer.getMeasuredWidth();
                int height = _mainContainer.getMeasuredHeight();
                mSubpage.setMinimumHeight(height);
            }
        });


        return view;
    }

    @OnClick(R.id.btnPress)
    public void onClickPress(){
        Intent SymptomActivity = new Intent(_context, SymptomActivity.class);
        startActivityForResult(SymptomActivity, RESULT_SYMPTOM);
    }


    @OnClick(R.id.field_cityname)
    public void onClickCityname(){
        getCityList();
        isShownCityMenu = true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case (RESULT_SYMPTOM) : {
                if (resultCode == Activity.RESULT_OK) {
                    //String newText = data.getStringExtra(PUBLIC_STATIC_STRING_IDENTIFIER);
                    utilityClass.sendSymptomList( );
                }
                break;
            }
        }
    }

    private void getCityList(){
        if ( !Global.GetInstance().IsSetCitiesList() ){
            restCallGetCitiesApi();
        }
    }

    private void getAdvice(){
        if( strAdvice.isEmpty() )
            restCallGetAdviceApi();
    }

    private void setIndex(){
        if( indexModals.get(0).isSet() ){
            tvAllergyIndex.setText(indexModals.get(0).getIndexValue());
            tvAlergyIntensity.setText(indexModals.get(0).getLogIntensity());
        }
        if( indexModals.get(1).isSet() ){
            tvPollutionIndex.setText(indexModals.get(1).getIndexValue());
            tvPollutionIntensity.setText(indexModals.get(1).getLogIntensity());
        }
        if( !(indexModals.get(0).isSet() && indexModals.get(1).isSet()) )
            restCallGetIndexApi();
    }

    private void restCallGetCitiesApi()
    {
        AirSensioRestClient.post(AirSensioRestClient.GET_CITYLIST, new RequestParams(), new JsonHttpResponseHandler() {

            public void onStart() {
                _debug.d(LOG_TAG, "AirSensioRestClient.GET_CITIES_LIST.onStart");
            }

            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                ArrayList arraylist = ParsingResponse.parsingCitiesList(response);
                if (!arraylist.isEmpty()) {
                    Global.GetInstance().SetCityList(arraylist);

                    if(isShownCityMenu)
                        cityMenu.dismiss(false);
                    initCityMenu();
                    if(isShownCityMenu)
                        cityMenu.show();

                    _debug.d(LOG_TAG, "AirSensioRestClient.GET_CITIES_LIST.onSuccess : " + arraylist.toString());
                } else {
                    _debug.d(LOG_TAG, "AirSensioRestClient.GET_CITIES_LIST.onSuccess : Empty");
                }
            }

            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray response) {
                if (response == null) _debug.d(LOG_TAG, "errorJSONArray: null");
                else _debug.d(LOG_TAG, "errorJSONArray:" + response.toString());
            }

            public void onFailure(int statusCode, Header aheader[], Throwable throwable, JSONObject response) {
                if (response == null) _debug.d(LOG_TAG, "errorJSONObject: null");
                else _debug.d(LOG_TAG, "errorJSONObject:" + response.toString());
            }

            public void onRetry(int statusCode) {
                _debug.d(LOG_TAG, "AirSensioRestClient.GET_CITIES_LIST.onRetry");
            }

            public void onFinish() {
            }
        });
    }

    private void initCityMenu( )
    {
        DroppyMenuPopup.Builder droppyBuilder = new DroppyMenuPopup.Builder(getContext(), rlCityname);

        CityMenuCallback cityMenuCallback = new CityMenuCallback();

        if( Global.GetInstance().IsSetCitiesList() ) {
            ArrayList<CityModal> cityList = Global.GetInstance().GetCityList();
            for (int i = 0; i < cityList.size(); i++) {
                droppyBuilder.addMenuItem(new DroppyMenuItem(cityList.get(i).getCityName()));
            }
        } else {
            droppyBuilder.addMenuItem(new DroppyMenuCustomItem(R.layout.loading_menu_item));
        }
        droppyBuilder.setOnDismissCallback(cityMenuCallback)
                .setOnClick(cityMenuCallback)
                .setXOffset(getResources().getDimensionPixelSize(R.dimen._10sdp))
                .setPopupAnimation(new DroppyFadeInAnimation())
                .triggerOnAnchorClick(true);


        cityMenu = droppyBuilder.build();
    }

    @Override
    public void onUserClicked(String materialIntroViewId) {
        _debug.d(LOG_TAG, "pressed " + materialIntroViewId);
        if(materialIntroViewId == Constant.INTRO_ID_1){
            View btnPress = getView().findViewById(R.id.btnPress);
            if( btnPress != null) {
                new PreferencesManager(getContext()).reset(Constant.INTRO_ID_2);
                showIntro(btnPress, Constant.INTRO_ID_2, getString(R.string.tutorial_home_press), ArrowType.AT_RED);
            }
        }
    }

    class CityMenuCallback implements DroppyMenuPopup.OnDismissCallback, DroppyClickCallbackInterface
    {

        @Override
        public void call(View v, int id) {
            String idText;

            switch (id) {
                //case R.id.droppy1:  idText = "Droppy Item 1"; break;
                default:
                    idText = String.valueOf(id);
            }
            ArrayList<CityModal> cityList = Global.GetInstance().GetCityList();
            Global.GetInstance().SetCityName(cityList.get(id).getCityName());
            Global.GetInstance().SetCityID(cityList.get(id).getCityId());
            initIndex();
            initAdvice();
            restCallGetAdviceApi();
            restCallGetIndexApi();
            tvCityname.setText(Global.GetInstance().GetCityName());
            isShownCityMenu = false;
        }

        @Override
        public void call() {
            isShownCityMenu = false;
            //utilityClass.toast("Menu dismissed");
        }
    }

    private void initIndex(){
        tvAllergyIndex.setText(Constant.DEFAULT_ALLERGY_INDEX);
        tvAlergyIntensity.setText(Constant.DEFAULT_ALLERGY_INTENSITY);
        tvPollutionIndex.setText(Constant.DEFAULT_POLLUTION_INDEX);
        tvPollutionIntensity.setText(Constant.DEFAULT_POLLUTION_INTENSITY);
    }

    private void initAdvice(){
        tvAdvice.setText(Constant.DEFAULT_ADVICE);
    }

    public void restCallGetAdviceApi(){
        RequestParams params = new RequestParams();
        UserModal userModal = SaveSharedPreferences.getLoginUserData(getActivity());
        String str_userid = userModal.getId();
        String str_email = userModal.getEmail();
        String str_deviceid = utilityClass.GetDeviceID();
        String str_hash = UtilityClass.MD5(str_deviceid + str_email + Constant.LOGIN_SECTRET);
        String str_cityid = Global.GetInstance().GetCityID();

        params.put(Constant.STR_USERID, str_userid);
        params.put(Constant.STR_EMAIL, str_email);
        params.put(Constant.STR_DEVICEID, str_deviceid);
        params.put(Constant.STR_HASH, str_hash);
        params.put(Constant.STR_CITYID, str_cityid);

        AirSensioRestClient.post(AirSensioRestClient.GET_ADVICE, params, new TextHttpResponseHandler() {   //new JsonHttpResponseHandler(false) : onSuccess(int statusCode, Header[] headers, String responseString) must be overrided.
            @Override
            public void onStart() {
                // called before request is started
                //utilityClass.processDialogStart(false);
                _debug.d(LOG_TAG, "AirSensioRestClient.GET_ADVICE.onStart");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                //utilityClass.processDialogStop();
                if (responseString == null) {
                    _debug.e(LOG_TAG, "None response string");
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
                        JSONArray response = new JSONArray(responseString);
                        JSONObject firstEvent = response.getJSONObject(0);
                        strAdvice = firstEvent.optString(Constant.STR_ADVICE);

                        _debug.d(LOG_TAG, "AirSensioRestClient.GET_ADVICE.Success");
                    } catch (JSONException e) {
                        strAdvice = "";
                        _debug.e(LOG_TAG, "Getting advice text Exception Error:" + responseString);
                    }

                    tvAdvice.setText(Html.fromHtml(strAdvice));
                }
                nCountCallingAdvice = 0;
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                //utilityClass.processDialogStop();
                //utilityClass.toast(getResources().getString(R.string.check_internet));
                _debug.e(LOG_TAG, "errorString: " + responseString);
                nCountCallingAdvice++;
                if( nCountCallingAdvice < 3 ){
                    restCallGetAdviceApi();
                } else {
                    nCountCallingAdvice = 0;
                }
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried{
                //utilityClass.processDialogStop();
                //utilityClass.toast(getResources().getString(R.string.try_again));
                _debug.d(LOG_TAG, "AirSensioRestClient.GET_ADVICE.onRetry");
                nCountCallingAdvice++;
                if( nCountCallingAdvice < 3 ){
                    restCallGetAdviceApi();
                } else {
                    nCountCallingAdvice = 0;
                }
            }

            @Override
            public void onFinish() {
                _debug.d(LOG_TAG, "AirSensioRestClient.GET_ADVICE.onFinish");
            }

        });
    }

    private void restCallGetIndexApi(){

        RequestParams params = new RequestParams();
        UserModal userModal = SaveSharedPreferences.getLoginUserData(getActivity());
        String str_userid = userModal.getId();
        String str_email = userModal.getEmail();
        String str_deviceid = utilityClass.GetDeviceID();
        String str_hash = UtilityClass.MD5(str_deviceid + str_email + Constant.LOGIN_SECTRET);
        String str_cityid = Global.GetInstance().GetCityID();

        params.put(Constant.STR_USERID, str_userid);
        params.put(Constant.STR_EMAIL, str_email);
        params.put(Constant.STR_DEVICEID, str_deviceid);
        params.put(Constant.STR_HASH, str_hash);
        params.put(Constant.STR_CITYID, str_cityid);

        AirSensioRestClient.post(AirSensioRestClient.GET_ALLERGY_INDEX, params, new TextHttpResponseHandler() {   //new JsonHttpResponseHandler(false) : onSuccess(int statusCode, Header[] headers, String responseString) must be overrided.
            @Override
            public void onStart() {
                // called before request is started
                //utilityClass.processDialogStart(false);
                _debug.d(LOG_TAG, "AirSensioRestClient.GET_ALLERGY_INDEX.onStart");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                //utilityClass.processDialogStop();
                if (responseString == null) {
                    _debug.e(LOG_TAG, "None response string");
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
                        indexModals = ParsingResponse.parsingIndexData(new JSONArray(responseString));
                        if( indexModals.get(0).isSet() ){
                            tvAllergyIndex.setText(indexModals.get(0).getIndexValue());
                            tvAlergyIntensity.setText(indexModals.get(0).getLogIntensity());
                        }
                        if( indexModals.get(1).isSet() ){
                            tvPollutionIndex.setText(indexModals.get(1).getIndexValue());
                            tvPollutionIntensity.setText(indexModals.get(1).getLogIntensity());
                        }

                        _debug.d(LOG_TAG, "AirSensioRestClient.GET_ALLERGY_INDEX.Success");
                    } catch (JSONException e) {
                        e.printStackTrace();
                        _debug.e(LOG_TAG, "Getting Index Exception Error:" + responseString);
                    }
                }
                nCountCallingIndex = 0;
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                //utilityClass.processDialogStop();
                //utilityClass.toast(getResources().getString(R.string.check_internet));
                _debug.e(LOG_TAG, "errorString: " + responseString);
                nCountCallingIndex++;
                if( nCountCallingIndex < 3 ){
                    restCallGetIndexApi();
                } else {
                    nCountCallingIndex = 0;
                }
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried{
                //utilityClass.processDialogStop();
                //utilityClass.toast(getResources().getString(R.string.try_again));
                _debug.d(LOG_TAG, "AirSensioRestClient.GET_ALLERGY_INDEX.onRetry");
                nCountCallingIndex++;
                if( nCountCallingIndex < 3 ){
                    restCallGetIndexApi();
                } else {
                    nCountCallingIndex = 0;
                }
            }

            @Override
            public void onFinish() {
                _debug.d(LOG_TAG, "AirSensioRestClient.GET_ALLERGY_INDEX.onFinish");
            }

        });
    }

    private void loadDataDetails(){
        if( dataModals.size() == 0 ){
            restCallGetDataDetailsApi();
        } else {
            drawDataDetails(dataModals);
        }
    }

    private void restCallGetDataDetailsApi(){

        RequestParams params = new RequestParams();
        UserModal userModal = SaveSharedPreferences.getLoginUserData(getActivity());
        String str_userid = userModal.getId();
        String str_email = userModal.getEmail();
        String str_deviceid = utilityClass.GetDeviceID();
        String str_hash = UtilityClass.MD5(str_deviceid + str_email + Constant.LOGIN_SECTRET);
        String str_cityid = Global.GetInstance().GetCityID();

        params.put(Constant.STR_USERID, str_userid);
        params.put(Constant.STR_EMAIL, str_email);
        params.put(Constant.STR_DEVICEID, str_deviceid);
        params.put(Constant.STR_HASH, str_hash);
        params.put(Constant.STR_CITYID, str_cityid);

        AirSensioRestClient.post(AirSensioRestClient.GET_DATA_DETAILS, params, new TextHttpResponseHandler() {   //new JsonHttpResponseHandler(false) : onSuccess(int statusCode, Header[] headers, String responseString) must be overrided.
            @Override
            public void onStart() {
                // called before request is started
                //utilityClass.processDialogStart(false);
                _debug.d(LOG_TAG, "AirSensioRestClient.GET_DATA_DETAILS.onStart");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                //utilityClass.processDialogStop();
                if (responseString == null) {
                    _debug.e(LOG_TAG, "None response string");
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
                        dataModals = ParsingResponse.parsingDataDetails(new JSONArray(responseString));
                        drawDataDetails(dataModals);

                        _debug.d(LOG_TAG, "AirSensioRestClient.GET_DATA_DETAILS.Success");
                    } catch (JSONException e) {
                        e.printStackTrace();
                        _debug.e(LOG_TAG, "GET_DATA_DETAILS Exception Error:" + responseString);
                    }
                }
                nCallDataDetails = 0;
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                //utilityClass.processDialogStop();
                //utilityClass.toast(getResources().getString(R.string.check_internet));
                _debug.e(LOG_TAG, "errorString: " + responseString);
                nCallDataDetails++;
                if( nCallDataDetails < 3 ){
                    restCallGetDataDetailsApi();
                } else {
                    nCallDataDetails = 0;
                }
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried{
                //utilityClass.processDialogStop();
                //utilityClass.toast(getResources().getString(R.string.try_again));
                _debug.d(LOG_TAG, "AirSensioRestClient.GET_DATA_DETAILS.onRetry");
                nCallDataDetails++;
                if( nCallDataDetails < 3 ){
                    restCallGetDataDetailsApi();
                } else {
                    nCallDataDetails = 0;
                }
            }

            @Override
            public void onFinish() {
                _debug.d(LOG_TAG, "AirSensioRestClient.GET_DATA_DETAILS.onFinish");
            }

        });
    }

    public void drawDataDetails(ArrayList<DataDetailsModal> arr){
        //View item = LayoutInflater.from(getContext()).inflate(R.layout.fragment_statistics_sub, null);
        View itemRow, leftItem, rightItem;
        for(int i=0; i<arr.size(); i+=2 ){
            itemRow = LayoutInflater.from(getContext()).inflate(R.layout.fragment_statistics_sub, null);
            leftItem = itemRow.findViewById(R.id.left_item);
            ((TextView)leftItem.findViewById(R.id.subitem_title)).setText(arr.get(i).getParameter());
            ((TextView)leftItem.findViewById(R.id.subitem_value)).setText(arr.get(i).getLogValue());
            if(arr.get(i).getLogIntensity().equalsIgnoreCase("low"))
                leftItem.findViewById(R.id.low_mark).setVisibility(View.VISIBLE);
            else if (arr.get(i).getLogIntensity().equalsIgnoreCase("moderate"))
                leftItem.findViewById(R.id.moderate_mark).setVisibility(View.VISIBLE);
            else if (arr.get(i).getLogIntensity().equalsIgnoreCase("high"))
                leftItem.findViewById(R.id.high_mark).setVisibility(View.VISIBLE);

            if( i+1 < arr.size() ) {
                rightItem = itemRow.findViewById(R.id.right_item);
                ((TextView) rightItem.findViewById(R.id.subitem_title)).setText(arr.get(i + 1).getParameter());
                ((TextView) rightItem.findViewById(R.id.subitem_value)).setText(arr.get(i + 1).getLogValue());
                if (arr.get(i + 1).getLogIntensity().equalsIgnoreCase("low"))
                    rightItem.findViewById(R.id.low_mark).setVisibility(View.VISIBLE);
                else if (arr.get(i + 1).getLogIntensity().equalsIgnoreCase("moderate"))
                    rightItem.findViewById(R.id.moderate_mark).setVisibility(View.VISIBLE);
                else if (arr.get(i + 1).getLogIntensity().equalsIgnoreCase("high"))
                    rightItem.findViewById(R.id.high_mark).setVisibility(View.VISIBLE);
            }
            vgContainer.addView(itemRow);
        }
    }


    public void drawHomeTutorial(){
        //Show intro

        View viewEnvir = getView().findViewById(R.id.city_environment);
        if( viewEnvir != null ){
            new PreferencesManager(getContext()).reset(Constant.INTRO_ID_1);
            showIntro(viewEnvir, Constant.INTRO_ID_1, getString(R.string.tutorial_home_index), ArrowType.AT_NORMAL);
        }
    }

    private void showIntro(View view, String usageId, String text, ArrowType type){

        new MaterialIntroView.Builder(getActivity())
                .setMaskColor(0x70000000)
                .setTextColor(0xFF3F9CFF)
                .enableDotAnimation(false)
                .setFocusGravity(FocusGravity.CENTER)
                .setFocusType(Focus.MINIMUM)
                .setDelayMillis(200)
                .enableFadeAnimation(true)
                .performClick(true)
                .setListener(this)
                .setArrowType(type)
                .setInfoText(text)
                .enableInfoIDText(true)     //Info ID text appear
                .enableIcon(false)
                .setTarget(view)
                .setUsageId(usageId) //THIS SHOULD BE UNIQUE ID
                .show();
    }
}
