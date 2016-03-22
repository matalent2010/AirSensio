package com.wondereight.airsensio.Fragment;

import android.content.Context;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.db.chart.Tools;
import com.db.chart.model.LineSet;
import com.db.chart.view.AxisController;
import com.db.chart.view.ChartView;
import com.db.chart.view.LineChartView;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.wondereight.airsensio.Helper._Debug;
import com.wondereight.airsensio.Modal.DataDetailsModal;
import com.wondereight.airsensio.Modal.UserModal;
import com.wondereight.airsensio.R;
import com.wondereight.airsensio.UtilClass.AirSensioRestClient;
import com.wondereight.airsensio.UtilClass.ChartItem;
import com.wondereight.airsensio.UtilClass.Constant;
import com.wondereight.airsensio.UtilClass.Global;
import com.wondereight.airsensio.UtilClass.ParsingResponse;
import com.wondereight.airsensio.UtilClass.SaveSharedPreferences;
import com.wondereight.airsensio.UtilClass.UtilityClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;
import cz.msebera.android.httpclient.Header;

/**
 * Created by Miguel on 02/2/2016.
 */



public class StatisticsFragment extends Fragment {
    public static final int GS_DAY = 0;
    public static final int GS_WEEK = 1;
    public static final int GS_MONTH = 2;
    public static final int GS_YEAR = 3;

    private static Context _context;
    private static View _view;
    private static View _mainContainer;

    private static final String LOG_TAG = "StatisticFragment";
    private static _Debug _debug = new _Debug(true);
    UtilityClass utilityClass;

//    private android.support.v4.app.Fragment mStasticsMain;
    private ArrayList<Boolean> stateStatistics = new ArrayList<Boolean>();
    private int styleGraph = GS_DAY; //0:day, 1:week, 2:month, 3: year

    private LineChartView mChart;

    private ChartItem mDayChartItemSet;
    private ChartItem mWeekChartItemSet;
    private ChartItem mMonthChartItemSet;
    private ChartItem mYearChartItemSet;

    private Boolean[] loadedState = {false, false, false, false};
    private ArrayList<DataDetailsModal> dataModals = new ArrayList<>();
    private int nCallDataDetails;
    private ViewGroup vgContainer;

    @Bind({R.id.tab_day, R.id.tab_week, R.id.tab_month, R.id.tab_year})
    List<TextView> tabButton;

    @Bind({R.id.item_particle, R.id.item_allergen, R.id.item_humidity, R.id.item_temperature, R.id.item_uvindex, R.id.item_gas})
    List<LinearLayout> items;

    @Bind(R.id.chartloading)
    View chartloading;

    @Bind(R.id.main_page)
    View mMainpage;

    @Bind(R.id.sub_page)
    View mSubpage;



    // TODO: Rename and change types of parameters
//    private String mParam1;
//    private String mParam2;

    //    private OnFragmentInteractionListener mListener;
    public StatisticsFragment() {
        for( int i = 0; i<6; i++)
            stateStatistics.add(false);
        mChart = null;
        styleGraph = GS_DAY;
        nCallDataDetails = 0;
    }

    public static Fragment newInstance(Context context) {
        _context = context;
        StatisticsFragment f = new StatisticsFragment();
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        utilityClass = new UtilityClass(_context);
        _view = inflater.inflate(R.layout.fragment_statistics, container, false);

        mChart = (LineChartView) _view.findViewById(R.id.chart1);
        ButterKnife.bind(this, _view);
        _mainContainer = _view.findViewById(R.id.stasticsCanvas);
        vgContainer = (ViewGroup)_view.findViewById(R.id.data_details_container);

        _mainContainer.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                _mainContainer.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                int width = _mainContainer.getMeasuredWidth();
                int height = _mainContainer.getMeasuredHeight();
                mMainpage.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height));
                //mSubpage.setLayoutParams( new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height));
                mSubpage.setMinimumHeight(height);
            }
        });

        mDayChartItemSet = new ChartItem();
        mWeekChartItemSet = new ChartItem();
        mMonthChartItemSet = new ChartItem();
        mYearChartItemSet = new ChartItem();

        //loadTempChartData();
        displayCurrentState();
        loadCurrentGraphData();
        loadDataDetails();

        redrawGraph(_view);

        return _view;
    }

    @OnClick(R.id.tab_day)
    public void onClickTabDay(){
        int oldGraphStyle = styleGraph;

        if ( oldGraphStyle != GS_DAY){
            tabButton.get(oldGraphStyle).setBackgroundDrawable(getResources().getDrawable(R.drawable.itembuttonpannel));
            tabButton.get(oldGraphStyle).setTextColor(getResources().getColor(R.color.textColor_Green));
            tabButton.get(GS_DAY).setBackgroundDrawable(getResources().getDrawable(R.drawable.itemgreenpannel));
            tabButton.get(GS_DAY).setTextColor(getResources().getColor(R.color.textColor_White));
            styleGraph = GS_DAY;
            loadCurrentGraphData();
            redrawGraph(_view);
        }
    }

    @OnClick(R.id.tab_week)
    public void onClickTabWeek(){
        int oldGraphStyle = styleGraph;

        if ( oldGraphStyle != GS_WEEK){
            tabButton.get(oldGraphStyle).setBackgroundDrawable(getResources().getDrawable(R.drawable.itembuttonpannel));
            tabButton.get(oldGraphStyle).setTextColor(getResources().getColor(R.color.textColor_Green));
            tabButton.get(GS_WEEK).setBackgroundDrawable(getResources().getDrawable(R.drawable.itemgreenpannel));
            tabButton.get(GS_WEEK).setTextColor(getResources().getColor(R.color.textColor_White));
            styleGraph = GS_WEEK;
            loadCurrentGraphData();
            redrawGraph(_view);
        }
    }

    @OnClick(R.id.tab_month)
    public void onClickTabMonth(){
        int oldGraphStyle = styleGraph;

        if ( oldGraphStyle != GS_MONTH){
            tabButton.get(oldGraphStyle).setBackgroundDrawable(getResources().getDrawable(R.drawable.itembuttonpannel));
            tabButton.get(oldGraphStyle).setTextColor(getResources().getColor(R.color.textColor_Green));
            tabButton.get(GS_MONTH).setBackgroundDrawable(getResources().getDrawable(R.drawable.itemgreenpannel));
            tabButton.get(GS_MONTH).setTextColor(getResources().getColor(R.color.textColor_White));
            styleGraph = GS_MONTH;
            loadCurrentGraphData();
            redrawGraph(_view);
        }
    }

    @OnClick(R.id.tab_year)
    public void onClickTabYear(){
        int oldGraphStyle = styleGraph;

        if ( oldGraphStyle != GS_YEAR){
            tabButton.get(oldGraphStyle).setBackgroundDrawable(getResources().getDrawable(R.drawable.itembuttonpannel));
            tabButton.get(oldGraphStyle).setTextColor(getResources().getColor(R.color.textColor_Green));
            tabButton.get(GS_YEAR).setBackgroundDrawable(getResources().getDrawable(R.drawable.itemgreenpannel));
            tabButton.get(GS_YEAR).setTextColor(getResources().getColor(R.color.textColor_White));
            styleGraph = GS_YEAR;
            loadCurrentGraphData();
            redrawGraph(_view);
        }
    }

    @OnClick(R.id.item_particle)
    public void onClickParticles(){
        ArrayList<Boolean> state = new ArrayList<>(stateStatistics);
        if( state.get(0) == true) {
            state.set(0, false);
            items.get(0).setBackgroundDrawable(getResources().getDrawable(R.drawable.itembuttonpannel));
        } else {
            state.set(0, true);
            items.get(0).setBackgroundDrawable(getResources().getDrawable(R.drawable.itemgraypannel));
        }
        stateStatistics = state;
        redrawGraph(_view);
    }

    @OnClick(R.id.item_allergen)
    public void onClickAllergen(){
        ArrayList<Boolean> state = new ArrayList<>(stateStatistics);
        if( state.get(1) == true) {
            state.set(1, false);
            items.get(1).setBackgroundDrawable(getResources().getDrawable(R.drawable.itembuttonpannel));
        } else {
            state.set(1, true);
            items.get(1).setBackgroundDrawable(getResources().getDrawable(R.drawable.itemgraypannel));
        }
        stateStatistics = state;
        redrawGraph(_view);
    }

    @OnClick(R.id.item_humidity)
    public void onClickHumidity(){
        ArrayList<Boolean> state = new ArrayList<>(stateStatistics);
        if( state.get(2) == true) {
            state.set(2, false);
            items.get(2).setBackgroundDrawable(getResources().getDrawable(R.drawable.itembuttonpannel));
        } else {
            state.set(2, true);
            items.get(2).setBackgroundDrawable(getResources().getDrawable(R.drawable.itemgraypannel));
        }
        stateStatistics = state;
        redrawGraph(_view);
    }

    @OnClick(R.id.item_temperature)
    public void onClickTemperature(){
        ArrayList<Boolean> state = new ArrayList<>(stateStatistics);
        if( state.get(3) == true) {
            state.set(3, false);
            items.get(3).setBackgroundDrawable(getResources().getDrawable(R.drawable.itembuttonpannel));
        } else {
            state.set(3, true);
            items.get(3).setBackgroundDrawable(getResources().getDrawable(R.drawable.itemgraypannel));
        }
        stateStatistics = state;
        redrawGraph(_view);
    }

    @OnClick(R.id.item_uvindex)
    public void onClickUvIndex(){
        ArrayList<Boolean> state = new ArrayList<>(stateStatistics);
        if( state.get(4) == true) {
            state.set(4, false);
            items.get(4).setBackgroundDrawable(getResources().getDrawable(R.drawable.itembuttonpannel));
        } else {
            state.set(4, true);
            items.get(4).setBackgroundDrawable(getResources().getDrawable(R.drawable.itemgraypannel));
        }
        stateStatistics = state;
        redrawGraph(_view);
    }

    @OnClick(R.id.item_gas)
    public void onClickGas(){
        ArrayList<Boolean> state = new ArrayList<>(stateStatistics);
        if( state.get(5) == true) {
            state.set(5, false);
            items.get(5).setBackgroundDrawable(getResources().getDrawable(R.drawable.itembuttonpannel));
        } else {
            state.set(5, true);
            items.get(5).setBackgroundDrawable(getResources().getDrawable(R.drawable.itemgraypannel));
        }
        stateStatistics = state;
        redrawGraph(_view);
    }

    private void redrawGraph(View view){
        mChart.reset();

        switch (styleGraph){
            case GS_DAY:   // if GraphStyle.DAY
                drawGraphWithChartItemSet(mDayChartItemSet);
                break;
            case GS_WEEK:   // if GraphStyle.Week
                drawGraphWithChartItemSet(mWeekChartItemSet);
                break;
            case GS_MONTH:   // if GraphStyle.Month
                drawGraphWithChartItemSet(mMonthChartItemSet);
                break;
            case GS_YEAR:   // if GraphStyle.Year
                drawGraphWithChartItemSet(mYearChartItemSet);
                break;
        }

        Paint gridPaint = new Paint();
        gridPaint.setColor(Color.parseColor("#DDDDDD"));
        gridPaint.setStyle(Paint.Style.STROKE);
        gridPaint.setAntiAlias(true);
        gridPaint.setStrokeWidth(Tools.fromDpToPx(.75f));
        gridPaint.setPathEffect(new DashPathEffect(new float[] {10, 10}, 0));

        mChart.setBorderSpacing(Tools.fromDpToPx(15))
                .setAxisBorderValues(0, 10)//.setAxisBorderValues(0, 1000)
                .setXLabels(AxisController.LabelPosition.OUTSIDE)
                .setYLabels(AxisController.LabelPosition.OUTSIDE)
                .setLabelsColor(Color.parseColor("#FFFFFF"))
                .setGrid(ChartView.GridType.HORIZONTAL, 2, 5, gridPaint)
                .setStep(5) //.setStep(100)
                .setXAxis(false)
                .setYAxis(false);
        if( mChart.getData().isEmpty() == true)
        {
            LineSet dataset = new LineSet(new String[]{""}, new float[]{0});
            mChart.addData(dataset);
        }
        mChart.setHorizontalScrollBarEnabled(true);
        mChart.show();

        mChart.invalidate();
    }

    public void drawGraphWithChartItemSet(ChartItem itemSet){
        LineSet dataset;

        if( styleGraph == GS_DAY) {  //Day Graph
            try {
                dataset = new LineSet(itemSet.getLabel(), itemSet.getValueOutbreak());
                dataset.setColor(Color.parseColor("#FFFFFF"))
                        .setDotsColor(Color.parseColor("#FF0000"))
                        .setThickness(4)
                        .setDotsStrokeColor(Color.parseColor("#FFFFFF"))
                        .setDotsStrokeThickness(2)
                        .setHidden(true);
                mChart.addData(dataset);
                if( itemSet.getValueDayOutbreak().getPercentValue().length != 0) {
                    dataset = new LineSet(new String[0], new float[0], itemSet.getValueDayOutbreak().getPercentValue(), itemSet.getValueDayOutbreak().getfValue());
                    dataset.setColor(Color.parseColor("#FFFFFF"))
                            .setDotsColor(Color.parseColor("#FF0000"))
                            .setThickness(4)
                            .setDotsStrokeColor(Color.parseColor("#FFFFFF"))
                            .setDotsStrokeThickness(2);
                    mChart.addData(dataset);
                }
            } catch (IllegalArgumentException e) {
                _debug.w(LOG_TAG, "Outbreak Graph is null.");
            }
        } else {
            try {
                dataset = new LineSet(itemSet.getLabel(), itemSet.getValueOutbreak());
                dataset.setColor(Color.parseColor("#FFFFFF"))
                        .setDotsColor(Color.parseColor("#FF0000"))
                        .setThickness(4)
                        .setDotsStrokeColor(Color.parseColor("#FFFFFF"))
                        .setDotsStrokeThickness(2);
                if( styleGraph == GS_DAY) dataset.endAt(8); //don't show 24:00 point, if Graph style is DAY
                mChart.addData(dataset);

            } catch (IllegalArgumentException e) {
                _debug.w(LOG_TAG, "Outbreak Graph is null.");
            }
        }
        if(stateStatistics.get(0) == true){   //Particles Graph
            try {
                dataset = new LineSet(itemSet.getLabel(), itemSet.getValueParticles());
                dataset.setColor(Color.parseColor("#FFFFFF"))
                        .setDotsColor(Color.parseColor("#79c143"))
                        .setThickness(4)
                        .setDotsStrokeColor(Color.parseColor("#FFFFFF"))
                        .setDotsStrokeThickness(2);
                if( styleGraph == GS_DAY) dataset.endAt(8); //don't show 24:00 point, if Graph style is DAY
                mChart.addData(dataset);

            } catch ( IllegalArgumentException e ){ _debug.w(LOG_TAG, "Particles Graph is null.");}
        }
        if(stateStatistics.get(1) == true){   //Allergen Graph
            try{
                dataset = new LineSet(itemSet.getLabel(), itemSet.getValueAllergen());
                dataset.setColor(Color.parseColor("#FFFFFF"))
                        .setDotsColor(Color.parseColor("#77bb9c"))
                        .setThickness(4)
                        .setDotsStrokeColor(Color.parseColor("#FFFFFF"))
                        .setDotsStrokeThickness(2);
                if( styleGraph == GS_DAY) dataset.endAt(8); //don't show 24:00 point, if Graph style is DAY
                mChart.addData(dataset);
            } catch ( IllegalArgumentException e ){ _debug.w(LOG_TAG, "Allergen Graph is null.");}
        }
        if(stateStatistics.get(2) == true){   //Humidity Graph
            try {
                dataset = new LineSet(itemSet.getLabel(), itemSet.getValueHumidity());
                dataset.setColor(Color.parseColor("#FFFFFF"))
                        .setDotsColor(Color.parseColor("#3e9cff"))
                        .setThickness(4)
                        .setDotsStrokeColor(Color.parseColor("#FFFFFF"))
                        .setDotsStrokeThickness(2);
                if( styleGraph == GS_DAY) dataset.endAt(8); //don't show 24:00 point, if Graph style is DAY
                mChart.addData(dataset);
            } catch ( IllegalArgumentException e ){ _debug.w(LOG_TAG, "Humidity Graph is null.");}
        }
        if(stateStatistics.get(3) == true){   //Temperature Graph
            try {
                dataset = new LineSet(itemSet.getLabel(), itemSet.getValueTemperature());
                dataset.setColor(Color.parseColor("#FFFFFF"))
                        .setDotsColor(Color.parseColor("#8dc5f7"))
                        .setThickness(4)
                        .setDotsStrokeColor(Color.parseColor("#FFFFFF"))
                        .setDotsStrokeThickness(2);
                if( styleGraph == GS_DAY) dataset.endAt(8); //don't show 24:00 point, if Graph style is DAY
                mChart.addData(dataset);
            } catch ( IllegalArgumentException e ){ _debug.w(LOG_TAG, "Temperature Graph is null.");}
        }
        if(stateStatistics.get(4) == true){   //Sunlight Graph
            try {
                dataset = new LineSet(itemSet.getLabel(), itemSet.getValueSunlight());
                dataset.setColor(Color.parseColor("#FFFFFF"))
                        .setDotsColor(Color.parseColor("#f4eb49"))
                        .setThickness(4)
                        .setDotsStrokeColor(Color.parseColor("#FFFFFF"))
                        .setDotsStrokeThickness(2);
                if( styleGraph == GS_DAY) dataset.endAt(8); //don't show 24:00 point, if Graph style is DAY
                mChart.addData(dataset);
            } catch ( IllegalArgumentException e ){ _debug.w(LOG_TAG, "Sunlight Graph is null.");}
        }
        if(stateStatistics.get(5) == true){   //Gas Graph
            try{
                dataset = new LineSet(itemSet.getLabel(), itemSet.getValueGas());
                dataset.setColor(Color.parseColor("#FFFFFF"))
                        .setDotsColor(Color.parseColor("#990099"))
                        .setThickness(4)
                        .setDotsStrokeColor(Color.parseColor("#FFFFFF"))
                        .setDotsStrokeThickness(2);
                if( styleGraph == GS_DAY) dataset.endAt(8); //don't show 24:00 point, if Graph style is DAY
                mChart.addData(dataset);
            } catch ( IllegalArgumentException e ){ _debug.w(LOG_TAG, "Gas Graph is null.");}
        }
    }

    private void displayCurrentState(){

        for ( int i=0; i<4; i++){
            if( i == styleGraph ){
                tabButton.get(i).setBackgroundDrawable(getResources().getDrawable(R.drawable.itemgreenpannel));
                tabButton.get(i).setTextColor(getResources().getColor(R.color.textColor_White));
            } else{
                tabButton.get(i).setBackgroundDrawable(getResources().getDrawable(R.drawable.itembuttonpannel));
                tabButton.get(i).setTextColor(getResources().getColor(R.color.textColor_Green));
            }
        }

        for ( int i=0; i<6; i++){
            if( stateStatistics.get(i) == true ){
                items.get(i).setBackgroundDrawable(getResources().getDrawable(R.drawable.itemgraypannel));
            } else {
                items.get(i).setBackgroundDrawable(getResources().getDrawable(R.drawable.itembuttonpannel));
            }
        }
    }

    private void loadTempChartData(){
        mDayChartItemSet.setLabel(new String[]{"1", "2", "3", "4", "5", "6", "7"});
        mDayChartItemSet.setValueParticles(new float[]{(float) (Math.random() * 10), (float) (Math.random() * 10), (float) (Math.random() * 10), (float) (Math.random() * 10), (float) (Math.random() * 10), (float) (Math.random() * 10), (float) (Math.random() * 10)});
        mDayChartItemSet.setValueAllergen(new float[]{(float) (Math.random() * 10), (float) (Math.random() * 10), (float) (Math.random() * 10), (float) (Math.random() * 10), (float) (Math.random() * 10), (float) (Math.random() * 10), (float) (Math.random() * 10)});
        mDayChartItemSet.setValueHumidity(new float[]{(float) (Math.random() * 10), (float) (Math.random() * 10), (float) (Math.random() * 10), (float) (Math.random() * 10), (float) (Math.random() * 10), (float) (Math.random() * 10), (float) (Math.random() * 10)});
        mDayChartItemSet.setValueTemperature(new float[]{(float) (Math.random() * 10), (float) (Math.random() * 10), (float) (Math.random() * 10), (float) (Math.random() * 10), (float) (Math.random() * 10), (float) (Math.random() * 10), (float) (Math.random() * 10)});
        mDayChartItemSet.setValueSunlight(new float[]{(float) (Math.random() * 10), (float) (Math.random() * 10), (float) (Math.random() * 10), (float) (Math.random() * 10), (float) (Math.random() * 10), (float) (Math.random() * 10), (float) (Math.random() * 10)});
        mDayChartItemSet.setValueGas(new float[]{(float) (Math.random() * 10), (float) (Math.random() * 10), (float) (Math.random() * 10), (float) (Math.random() * 10), (float) (Math.random() * 10), (float) (Math.random() * 10), (float) (Math.random() * 10)});

        mWeekChartItemSet.setLabel(new String[]{"16", "17", "18", "19"});
        mWeekChartItemSet.setValueParticles(new float[]{(float) (Math.random() * 10), (float) (Math.random() * 10), (float) (Math.random() * 10), (float) (Math.random() * 10)});
        mWeekChartItemSet.setValueAllergen(new float[]{(float) (Math.random() * 10), (float) (Math.random() * 10), (float) (Math.random() * 10), (float) (Math.random() * 10)});
        mWeekChartItemSet.setValueHumidity(new float[]{(float) (Math.random() * 10), (float) (Math.random() * 10), (float) (Math.random() * 10), (float) (Math.random() * 10)});
        mWeekChartItemSet.setValueTemperature(new float[]{(float) (Math.random() * 10), (float) (Math.random() * 10), (float) (Math.random() * 10), (float) (Math.random() * 10)});
        mWeekChartItemSet.setValueSunlight(new float[]{(float) (Math.random() * 10), (float) (Math.random() * 10), (float) (Math.random() * 10), (float) (Math.random() * 10)});
        mWeekChartItemSet.setValueGas(new float[]{(float) (Math.random() * 10), (float) (Math.random() * 10), (float) (Math.random() * 10), (float) (Math.random() * 10)});

        mMonthChartItemSet.setLabel(new String[]{"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul"});
        mMonthChartItemSet.setValueParticles(new float[]{(float) (Math.random() * 10), (float) (Math.random() * 10), (float) (Math.random() * 10), (float) (Math.random() * 10), (float) (Math.random() * 10), (float) (Math.random() * 10), (float) (Math.random() * 10)});
        mMonthChartItemSet.setValueAllergen(new float[]{(float) (Math.random() * 10), (float) (Math.random() * 10), (float) (Math.random() * 10), (float) (Math.random() * 10), (float) (Math.random() * 10), (float) (Math.random() * 10), (float) (Math.random() * 10)});
        mMonthChartItemSet.setValueHumidity(new float[]{(float) (Math.random() * 10), (float) (Math.random() * 10), (float) (Math.random() * 10), (float) (Math.random() * 10), (float) (Math.random() * 10), (float) (Math.random() * 10), (float) (Math.random() * 10)});
        mMonthChartItemSet.setValueTemperature(new float[]{(float) (Math.random() * 10), (float) (Math.random() * 10), (float) (Math.random() * 10), (float) (Math.random() * 10), (float) (Math.random() * 10), (float) (Math.random() * 10), (float) (Math.random() * 10)});
        mMonthChartItemSet.setValueSunlight(new float[]{(float) (Math.random() * 10), (float) (Math.random() * 10), (float) (Math.random() * 10), (float) (Math.random() * 10), (float) (Math.random() * 10), (float) (Math.random() * 10), (float) (Math.random() * 10)});
        mMonthChartItemSet.setValueGas(new float[]{(float) (Math.random() * 10), (float) (Math.random() * 10), (float) (Math.random() * 10), (float) (Math.random() * 10), (float) (Math.random() * 10), (float) (Math.random() * 10), (float) (Math.random() * 10)});

        mYearChartItemSet.setLabel(new String[]{"2015", "2016", "2017"});
        mYearChartItemSet.setValueParticles(new float[]{(float) (Math.random() * 10), (float) (Math.random() * 10), (float) (Math.random() * 10)});
        mYearChartItemSet.setValueAllergen(new float[]{(float) (Math.random() * 10), (float) (Math.random() * 10), (float) (Math.random() * 10)});
        mYearChartItemSet.setValueHumidity(new float[]{(float) (Math.random() * 10), (float) (Math.random() * 10), (float) (Math.random() * 10)});
        mYearChartItemSet.setValueTemperature(new float[]{(float) (Math.random() * 10), (float) (Math.random() * 10), (float) (Math.random() * 10)});
        mYearChartItemSet.setValueSunlight(new float[]{(float) (Math.random() * 10), (float) (Math.random() * 10), (float) (Math.random() * 10)});
        mYearChartItemSet.setValueGas(new float[]{(float) (Math.random() * 10), (float) (Math.random() * 10), (float) (Math.random() * 10)});

        return ;
    }

    private void loadCurrentGraphData(){

        switch (styleGraph){
            case GS_DAY: //day
                restCallGraphDataApi(GS_DAY);
                break;
            case GS_WEEK: //week
                restCallGraphDataApi(GS_WEEK);
                break;
            case GS_MONTH: //month
                restCallGraphDataApi(GS_MONTH);
                break;
            case GS_YEAR: //year
                restCallGraphDataApi(GS_YEAR);
                break;
        }
    }

    private void loadDataDetails(){
        if( dataModals.size() == 0 ){
            restCallGetDataDetailsApi();
        } else {
            drawDataDetails(dataModals);
        }
    }

    private Boolean isloaded(int StateStatiscs){
        return loadedState[StateStatiscs];
    }

    private void setDayloaded(Boolean state){
        loadedState[0] = state;
    }
    private void setWeekloaded(Boolean state){
        loadedState[1] = state;
    }
    private void setMonthloaded(Boolean state){
        loadedState[2] = state;
    }
    private void setYearloaded(Boolean state){
        loadedState[3] = state;
    }

    private String getDurationName(int style){
        String result = "day";
        switch (style){
            case GS_DAY:
                result = "day";
                break;
            case GS_WEEK:
                result = "week";
                break;
            case GS_MONTH:
                result = "month";
                break;
            case GS_YEAR:
                result = "year";
                break;
        }
        return result;
    }
    private void restCallGraphDataApi(final int style) {

        RequestParams params = new RequestParams();
        String str_userid = SaveSharedPreferences.getLoginUserData(getContext()).getId();
        String str_deviceid = utilityClass.GetDeviceID(); //"DAE24875-8366-4692-8B15-BA8C6634B691";
        String str_email = SaveSharedPreferences.getLoginUserData(getContext()).getEmail();
        String str_hash = utilityClass.MD5(str_deviceid + str_email + Constant.LOGIN_SECTRET);
        String str_cityid =  "1"; //Global.GetInstance().GetCityName().isEmpty() ? Constant.DEFAULT_CITYNAME : Global.GetInstance().GetCityName();

        params.put(Constant.STR_USERID, str_userid);
        params.put(Constant.STR_DEVICEID, str_deviceid);
        params.put(Constant.STR_EMAIL, str_email);
        params.put(Constant.STR_CITYID, str_cityid);
        params.put(Constant.STR_HASH, str_hash);
        params.put(Constant.STR_DURATION, getDurationName(style));

        _debug.d(LOG_TAG, params.toString());
        // AirSensioRestClient.post(AirSensioRestClient.LOGIN, params, new AsyncHttpResponseHandler() {
        AirSensioRestClient.post(AirSensioRestClient.GET_GRAPH_DATA, params, new JsonHttpResponseHandler() {   //new JsonHttpResponseHandler(false) : onSuccess(int statusCode, Header[] headers, String responseString) must be overrided.
            @Override
            public void onStart() {
                // called before request is started
                chartloading.setVisibility(View.VISIBLE);
                _debug.d(LOG_TAG, "AirSensioRestClient.onStart(GET_GRAPH_DATA)");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // If the response is JSONObject instead of expected JSONArray
                chartloading.setVisibility(View.GONE);
                _debug.d(LOG_TAG, "Recieved JSONObject result");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                // Pull out the first event on the public response
                chartloading.setVisibility(View.GONE);
                switch (style) {
                    case GS_DAY:
                        mDayChartItemSet = ParsingResponse.parsingGraphData(response, style);
                        break;
                    case GS_WEEK:
                        mWeekChartItemSet = ParsingResponse.parsingGraphData(response, style);
                        break;
                    case GS_MONTH:
                        mMonthChartItemSet = ParsingResponse.parsingGraphData(response, style);
                        break;
                    case GS_YEAR:
                        mYearChartItemSet = ParsingResponse.parsingGraphData(response, style);
                        break;
                }
                redrawGraph(_view);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

                chartloading.setVisibility(View.GONE);
                if (responseString == null) {

                    _debug.e(LOG_TAG, "None response string");
                } else if (responseString.equals("1")) {

                    utilityClass.showAlertMessage("Alert", "Incorrect Hash");
                    _debug.e(LOG_TAG, "Incorrect Hash");
                } else if (responseString.equals("2")) {

                    _debug.d(LOG_TAG, "Device ID not provided");
                } else if (responseString.equals("3")) {

                    _debug.d(LOG_TAG, "User ID not provided");
                } else if (responseString.equals("9")) {

                    utilityClass.toast(getResources().getString(R.string.not_found));
                    _debug.d(LOG_TAG, "Device not found");
                } else {
                    _debug.e(LOG_TAG, "Get Device Data Error:" + responseString);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                chartloading.setVisibility(View.GONE);
                utilityClass.toast(getResources().getString(R.string.check_internet));
                if (errorResponse == null) _debug.d(LOG_TAG, "errorJSONObject: null");
                else _debug.d(LOG_TAG, "errorJSONObject:" + errorResponse.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                chartloading.setVisibility(View.GONE);
                utilityClass.toast(getResources().getString(R.string.check_internet));
                if (errorResponse == null) _debug.d(LOG_TAG, "errorJSONArray: null");
                else _debug.d(LOG_TAG, "errorJSONArray:" + errorResponse.toString());
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried{
                chartloading.setVisibility(View.GONE);
                utilityClass.toast(getResources().getString(R.string.try_again));
                _debug.d(LOG_TAG, "AirSensioRestClient.GET_GRAPH_DATA.onRetry");
            }

            @Override
            public void onFinish() {
                chartloading.setVisibility(View.GONE);
            }

        });
    }

    private void restCallGetDataDetailsApi(){

        RequestParams params = new RequestParams();
        UserModal userModal = SaveSharedPreferences.getLoginUserData(getActivity());
        String str_userid = userModal.getId();
        String str_email = userModal.getEmail();
        String str_deviceid = utilityClass.GetDeviceID();
        String str_hash = utilityClass.MD5(str_deviceid + str_email + Constant.LOGIN_SECTRET);
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
                _debug.d(LOG_TAG, "AirSensioRestClient.GET_ALLERGY_INDEX.onFinish");
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
        //vgContainer.layout
    }
}
