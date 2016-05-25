package com.wondereight.sensioair.Fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
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
import com.wondereight.sensioair.Activity.HomeActivity;
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
import com.wondereight.sensioair.UtilClass.UtilityClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;


/**
 * Created by Miguel on 02/2/2016.
 */

public class HomeFragment extends Fragment implements MaterialIntroListener {
    private static Context _context;
    private static final String LOG_TAG = "HomeFragment";
    private static _Debug _debug = new _Debug(true);

    UtilityClass utilityClass;

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
    @Bind(R.id.city_environment)
        View vEnviron;
    @Bind(R.id.spinnerimage)
        ImageView ivSpinner;
    @Bind(R.id.city_list_container)
        View svCityListContainer;
    @Bind(R.id.city_list)
        ListView lvCityList;

    @Bind(R.id.svContainer)
        ScrollView svContainer;

    private ArrayList<DataDetailsModal> dataModals = new ArrayList<>();
    private int nCallDataDetails;

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
//        initCityMenu();

        resetTouchListener();
        initCityList();

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
        startActivityForResult(SymptomActivity, Constant.RESULT_SYMPTOM);
    }


    @OnClick(R.id.field_cityname)
    public void onClickCityname(){
        getCityList();
        isShownCityMenu = true;

        if( svCityListContainer.getVisibility() == View.VISIBLE ) {
            svCityListContainer.setVisibility(View.GONE);
            ivSpinner.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.dropdown_icon));
        }
        else {
            svCityListContainer.setVisibility(View.VISIBLE);
            ivSpinner.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.dropup_icon));

            int height = getItemHeightofListView(lvCityList);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)lvCityList.getLayoutParams();
            if(params.height != height) {
                params.height = height;
                lvCityList.setLayoutParams(params);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case (Constant.RESULT_SYMPTOM) : {
                if (resultCode == Activity.RESULT_OK) {
                    ((HomeActivity)getActivity()).mTabPager.setCurrentItem(1);
                    new android.os.Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            StatisticsFragment statPage = (StatisticsFragment)((ViewPagerAdapter)((HomeActivity)getActivity()).mTabPager.getAdapter()).getItem(1);
                            statPage.tabButton.get(0).performClick();
                        }
                    }, 100);
                } else if (resultCode == Activity.RESULT_CANCELED ) {
                    Bundle bundle = data.getExtras();
                    if( bundle != null && bundle.getBoolean(Constant.TUTORIAL, false) ) {
                        new android.os.Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                ((HomeActivity) getActivity()).mTabPager.setCurrentItem(1);
                            }
                        }, 100);
                    }
                }
                break;
            }
        }
    }

    @Override
    public void onDestroy() {
        _debug.d(LOG_TAG, "HomeFragment destroyed.");
        AirSensioRestClient.cancelRequest(getContext());
        super.onDestroy();
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

    private void restCallGetCitiesApi(){
        if( !Global.GetInstance().isLogedinUser() ) return;

        AirSensioRestClient.post(AirSensioRestClient.GET_CITYLIST, new RequestParams(), new JsonHttpResponseHandler() {

            public void onStart() {
                _debug.d(LOG_TAG, "AirSensioRestClient.GET_CITIES_LIST.onStart");
            }

            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                ArrayList<CityModal> arraylist = ParsingResponse.parsingCitiesList(response);
                if (!arraylist.isEmpty()) {
                    Global.GetInstance().SetCityList(arraylist);

                    initCityList();
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

    public static int getItemHeightofListView(ListView listView) {
        ListAdapter adapter = listView.getAdapter();

        int grossElementHeight = 0;
        for (int i = 0; i < adapter.getCount(); i++) {
            View childView = adapter.getView(i, null, listView);
            childView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            grossElementHeight += childView.getMeasuredHeight();
        }
        return grossElementHeight;
    }

    private void initCityList( ){

        if( Global.GetInstance().IsSetCitiesList() ) {

            // Define a new Adapter
            // First parameter - Context
            // Second parameter - Layout for the row
            // Third parameter - ID of the TextView to which the data is written
            // Forth - the Array of data
            ArrayList<CityModal> cityModals = Global.GetInstance().GetCityList();
            ArrayList<String> cityList = new ArrayList<>(cityModals.size());
            for( CityModal cityModal : cityModals){
                cityList.add(cityModal.getCityName());
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                    R.layout.city_listview_item, android.R.id.text1, cityList);


            // Assign adapter to ListView
            lvCityList.setAdapter(adapter);

            // ListView Item Click Listener
            lvCityList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {

                    ArrayList<CityModal> cityList = Global.GetInstance().GetCityList();
                    Global.GetInstance().SetCityName(cityList.get(position).getCityName());
                    Global.GetInstance().SetCityID(cityList.get(position).getCityId());
                    initIndex();
                    initAdvice();
                    restCallGetAdviceApi();
                    restCallGetIndexApi();
                    tvCityname.setText(Global.GetInstance().GetCityName());
                    svCityListContainer.setVisibility(View.GONE);
                    ivSpinner.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.dropdown_icon));
                }
            });
        } else {

            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.city_listview_item,
                    android.R.id.text1, new ArrayList<>(Arrays.asList(new String[]{"Connecting..."})));

            // Assign adapter to ListView
            lvCityList.setAdapter(adapter);

            // ListView Item Click Listener
            lvCityList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    svCityListContainer.setVisibility(View.GONE);
                    ivSpinner.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.dropdown_icon));
                }
            });
        }
    }

    @Override
    public void onUserClicked(String materialIntroViewId) {
        _debug.d(LOG_TAG, "Tutor pressed: " + materialIntroViewId);
        if(materialIntroViewId.equalsIgnoreCase(Constant.INTRO_ID_1)){
            View btnPress = getView().findViewById(R.id.btnPress);
            PreferencesManager pm = new PreferencesManager(getContext());

            if( btnPress != null && !pm.isDisplayed(Constant.INTRO_ID_2)){
                //new PreferencesManager(getContext()).reset(Constant.INTRO_ID_2);
                showIntro(btnPress, Constant.INTRO_ID_2, getString(R.string.tutorial_home_press), ArrowType.AT_BLUE);
            }
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
        if( !Global.GetInstance().isLogedinUser() ) return;

        RequestParams params = new RequestParams();
        UserModal userModal = Global.GetInstance().GetUserModal();
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
        if( !Global.GetInstance().isLogedinUser() ) return;

        RequestParams params = new RequestParams();
        UserModal userModal = Global.GetInstance().GetUserModal();
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
        if( !Global.GetInstance().isLogedinUser() ) return;

        RequestParams params = new RequestParams();
        UserModal userModal = Global.GetInstance().GetUserModal();
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

        AirSensioRestClient.post(AirSensioRestClient.GET_DATA_DETAILS2, params, new TextHttpResponseHandler() {   //new JsonHttpResponseHandler(false) : onSuccess(int statusCode, Header[] headers, String responseString) must be overrided.
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
        String format_html = "%s: %s %s <strong>%s</strong>";
        ((LinearLayout)(vgContainer.findViewById(R.id.allergen_detail)).findViewById(R.id.data_detail_content)).removeAllViews();
        ((LinearLayout)(vgContainer.findViewById(R.id.harmful_detail)).findViewById(R.id.data_detail_content)).removeAllViews();
        ((LinearLayout)(vgContainer.findViewById(R.id.particle_detail)).findViewById(R.id.data_detail_content)).removeAllViews();
        ((LinearLayout)(vgContainer.findViewById(R.id.temperature_detail)).findViewById(R.id.data_detail_content)).removeAllViews();
        ((LinearLayout)(vgContainer.findViewById(R.id.uxindex_detail)).findViewById(R.id.data_detail_content)).removeAllViews();
        for( DataDetailsModal modal : arr ){
            LinearLayout text_container;
            try {
                if (modal.getCategory().equalsIgnoreCase(Constant.CATEGORY_ALLERGEN)) {
                    text_container = (LinearLayout) vgContainer.findViewById(R.id.allergen_detail).findViewById(R.id.data_detail_content);
                } else if (modal.getCategory().equalsIgnoreCase(Constant.CATEGORY_HARMFUL)) {
                    text_container = (LinearLayout) vgContainer.findViewById(R.id.harmful_detail).findViewById(R.id.data_detail_content);
                } else if (modal.getCategory().equalsIgnoreCase(Constant.CATEGORY_PARTICLE)) {
                    text_container = (LinearLayout) vgContainer.findViewById(R.id.particle_detail).findViewById(R.id.data_detail_content);
                } else if (modal.getCategory().equalsIgnoreCase(Constant.CATEGORY_TEMPERATUR)) {
                    text_container = (LinearLayout) vgContainer.findViewById(R.id.temperature_detail).findViewById(R.id.data_detail_content);
                } else if (modal.getCategory().equalsIgnoreCase(Constant.CATEGORY_UXINDEX)) {
                    text_container = (LinearLayout) vgContainer.findViewById(R.id.uxindex_detail).findViewById(R.id.data_detail_content);
                } else {
                    text_container = new LinearLayout(getContext());
                }
                TextView valueTV = new TextView(getContext());
                String html = String.format(format_html, modal.getParameter(), modal.getLogValue(), modal.getLogUnit(), modal.getLogIntensity());
                valueTV.setText(Html.fromHtml(html));
                //valueTV.setId(5);
                valueTV.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                text_container.addView(valueTV);
            } catch (Exception e) {}
        }
    }

    public void drawHomeTutorial(){
        drawHomeTutorial(vEnviron);
    }

    public void drawHomeTutorial(View viewEnvir){
        //Show intro
        PreferencesManager pm = new PreferencesManager(getContext());

        if( viewEnvir != null && !pm.isDisplayed(Constant.INTRO_ID_1)){
            //new PreferencesManager(getContext()).reset(Constant.INTRO_ID_1);
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

    private void resetTouchListener(){
        if( Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            svContainer.setOnTouchListener(new View.OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    _debug.v(LOG_TAG, "PARENT TOUCH");

                    svContainer.requestDisallowInterceptTouchEvent(false);
                    return false;
                }
            });

            lvCityList.setOnTouchListener(new View.OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    _debug.v(LOG_TAG, "CHILD TOUCH");

                    // Disallow the touch request for parent scroll on touch of  child view
                    svContainer.requestDisallowInterceptTouchEvent(true);
                    return false;
                }
            });
        }
    }
}

