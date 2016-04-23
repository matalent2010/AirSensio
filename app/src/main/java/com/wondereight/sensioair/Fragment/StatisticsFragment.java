package com.wondereight.sensioair.Fragment;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
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
import com.wondereight.sensioair.Activity.HomeActivity;
import com.wondereight.sensioair.Helper._Debug;
import com.wondereight.sensioair.Modal.DataDetailsModal;
import com.wondereight.sensioair.Modal.UserModal;
import com.wondereight.sensioair.R;
import com.wondereight.sensioair.UtilClass.AirSensioRestClient;
import com.wondereight.sensioair.UtilClass.ChartItem;
import com.wondereight.sensioair.UtilClass.Constant;
import com.wondereight.sensioair.UtilClass.Global;
import com.wondereight.sensioair.UtilClass.ParsingResponse;
import com.wondereight.sensioair.UtilClass.SaveSharedPreferences;
import com.wondereight.sensioair.UtilClass.UtilityClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EmptyStackException;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;
import co.mobiwise.materialintro.animation.MaterialIntroListener;
import co.mobiwise.materialintro.prefs.PreferencesManager;
import co.mobiwise.materialintro.shape.ArrowType;
import co.mobiwise.materialintro.shape.Focus;
import co.mobiwise.materialintro.shape.FocusGravity;
import co.mobiwise.materialintro.view.MaterialIntroView;
import cz.msebera.android.httpclient.Header;

/**
 * Created by Miguel on 02/2/2016.
 */



public class StatisticsFragment extends Fragment implements MaterialIntroListener {
    public static final int GS_DAY = 0;
    public static final int GS_WEEK = 1;
    public static final int GS_MONTH = 2;
    public static final int GS_YEAR = 3;

    private static Context _context;
    private static View _view;

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

    @Bind({R.id.tab_day, R.id.tab_week, R.id.tab_month, R.id.tab_year})
    List<TextView> tabButton;

    @Bind({R.id.item_particle, R.id.item_allergen, R.id.item_humidity, R.id.item_temperature, R.id.item_uvindex, R.id.item_gas})
    List<LinearLayout> items;

    @Bind(R.id.chartloading)
    View chartloading;

    //    private OnFragmentInteractionListener mListener;
    public StatisticsFragment() {
        for( int i = 0; i<6; i++)
            stateStatistics.add(false);
        mChart = null;
        styleGraph = GS_DAY;
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

        mDayChartItemSet = new ChartItem();
        mWeekChartItemSet = new ChartItem();
        mMonthChartItemSet = new ChartItem();
        mYearChartItemSet = new ChartItem();

        //loadTempChartData();
        displayCurrentState();
        loadCurrentGraphData();

        redrawGraph(_view);

        return _view;
    }

    @OnClick(R.id.tab_day)
    public void onClickTabDay(){
        int oldGraphStyle = styleGraph;

        if ( oldGraphStyle != GS_DAY){
            tabButton.get(oldGraphStyle).setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.itembuttonpannel));
            tabButton.get(oldGraphStyle).setTextColor(ContextCompat.getColor(getContext(), R.color.textColor_Green));
            tabButton.get(GS_DAY).setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.itemgreenpannel));
            tabButton.get(GS_DAY).setTextColor(ContextCompat.getColor(getContext(), R.color.textColor_White));
            styleGraph = GS_DAY;
            loadCurrentGraphData();
            redrawGraph(_view);
        }
    }

    @OnClick(R.id.tab_week)
    public void onClickTabWeek(){
        int oldGraphStyle = styleGraph;

        if ( oldGraphStyle != GS_WEEK){
            tabButton.get(oldGraphStyle).setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.itembuttonpannel));
            tabButton.get(oldGraphStyle).setTextColor(ContextCompat.getColor(getContext(), R.color.textColor_Green));
            tabButton.get(GS_WEEK).setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.itemgreenpannel));
            tabButton.get(GS_WEEK).setTextColor(ContextCompat.getColor(getContext(), R.color.textColor_White));
            styleGraph = GS_WEEK;
            loadCurrentGraphData();
            redrawGraph(_view);
        }
    }

    @OnClick(R.id.tab_month)
    public void onClickTabMonth(){
        int oldGraphStyle = styleGraph;

        if ( oldGraphStyle != GS_MONTH){
            tabButton.get(oldGraphStyle).setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.itembuttonpannel));
            tabButton.get(oldGraphStyle).setTextColor(ContextCompat.getColor(getContext(), R.color.textColor_Green));
            tabButton.get(GS_MONTH).setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.itemgreenpannel));
            tabButton.get(GS_MONTH).setTextColor(ContextCompat.getColor(getContext(), R.color.textColor_White));
            styleGraph = GS_MONTH;
            loadCurrentGraphData();
            redrawGraph(_view);
        }
    }

    @OnClick(R.id.tab_year)
    public void onClickTabYear(){
        int oldGraphStyle = styleGraph;

        if ( oldGraphStyle != GS_YEAR){
            tabButton.get(oldGraphStyle).setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.itembuttonpannel));
            tabButton.get(oldGraphStyle).setTextColor(ContextCompat.getColor(getContext(), R.color.textColor_Green));
            tabButton.get(GS_YEAR).setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.itemgreenpannel));
            tabButton.get(GS_YEAR).setTextColor(ContextCompat.getColor(getContext(), R.color.textColor_White));
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
            items.get(0).setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.itembuttonpannel));
        } else {
            state.set(0, true);
            items.get(0).setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.itemgraypannel));
        }
        stateStatistics = state;
        redrawGraph(_view);
    }

    @OnClick(R.id.item_allergen)
    public void onClickAllergen(){
        ArrayList<Boolean> state = new ArrayList<>(stateStatistics);
        if( state.get(1) == true) {
            state.set(1, false);
            items.get(1).setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.itembuttonpannel));
        } else {
            state.set(1, true);
            items.get(1).setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.itemgraypannel));
        }
        stateStatistics = state;
        redrawGraph(_view);
    }

    @OnClick(R.id.item_humidity)
    public void onClickHumidity(){
        ArrayList<Boolean> state = new ArrayList<>(stateStatistics);
        if( state.get(2) == true) {
            state.set(2, false);
            items.get(2).setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.itembuttonpannel));
        } else {
            state.set(2, true);
            items.get(2).setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.itemgraypannel));
        }
        stateStatistics = state;
        redrawGraph(_view);
    }

    @OnClick(R.id.item_temperature)
    public void onClickTemperature(){
        ArrayList<Boolean> state = new ArrayList<>(stateStatistics);
        if( state.get(3) == true) {
            state.set(3, false);
            items.get(3).setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.itembuttonpannel));
        } else {
            state.set(3, true);
            items.get(3).setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.itemgraypannel));
        }
        stateStatistics = state;
        redrawGraph(_view);
    }

    @OnClick(R.id.item_uvindex)
    public void onClickUvIndex(){
        ArrayList<Boolean> state = new ArrayList<>(stateStatistics);
        if( state.get(4) == true) {
            state.set(4, false);
            items.get(4).setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.itembuttonpannel));
        } else {
            state.set(4, true);
            items.get(4).setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.itemgraypannel));
        }
        stateStatistics = state;
        redrawGraph(_view);
    }

    @OnClick(R.id.item_gas)
    public void onClickGas(){
        ArrayList<Boolean> state = new ArrayList<>(stateStatistics);
        if( state.get(5) == true) {
            state.set(5, false);
            items.get(5).setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.itembuttonpannel));
        } else {
            state.set(5, true);
            items.get(5).setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.itemgraypannel));
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
                .setYLabelsManual(new ArrayList<>(Arrays.asList(new String[]{"Low", "Med", "High"})))
                .setXAxis(false)
                .setYAxis(false);
        if( mChart.getData().isEmpty() )
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
        if(stateStatistics.get(4) == true){   //UV index Graph
            try {
                dataset = new LineSet(itemSet.getLabel(), itemSet.getValueUvIndex());
                dataset.setColor(Color.parseColor("#FFFFFF"))
                        .setDotsColor(Color.parseColor("#f4eb49"))
                        .setThickness(4)
                        .setDotsStrokeColor(Color.parseColor("#FFFFFF"))
                        .setDotsStrokeThickness(2);
                if( styleGraph == GS_DAY) dataset.endAt(8); //don't show 24:00 point, if Graph style is DAY
                mChart.addData(dataset);
            } catch ( IllegalArgumentException e ){ _debug.w(LOG_TAG, "UV index Graph is null.");}
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
                tabButton.get(i).setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.itemgreenpannel));
                tabButton.get(i).setTextColor(ContextCompat.getColor(getContext(), R.color.textColor_White));
            } else{
                tabButton.get(i).setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.itembuttonpannel));
                tabButton.get(i).setTextColor(ContextCompat.getColor(getContext(), R.color.textColor_Green));
            }
        }

        for ( int i=0; i<6; i++){
            if( stateStatistics.get(i) == true ){
                items.get(i).setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.itemgraypannel));
            } else {
                items.get(i).setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.itembuttonpannel));
            }
        }
    }

    private void loadTempChartData(){
        mDayChartItemSet.setLabel(new String[]{"1", "2", "3", "4", "5", "6", "7"});
        mDayChartItemSet.setValueParticles(new float[]{(float) (Math.random() * 10), (float) (Math.random() * 10), (float) (Math.random() * 10), (float) (Math.random() * 10), (float) (Math.random() * 10), (float) (Math.random() * 10), (float) (Math.random() * 10)});
        mDayChartItemSet.setValueAllergen(new float[]{(float) (Math.random() * 10), (float) (Math.random() * 10), (float) (Math.random() * 10), (float) (Math.random() * 10), (float) (Math.random() * 10), (float) (Math.random() * 10), (float) (Math.random() * 10)});
        mDayChartItemSet.setValueHumidity(new float[]{(float) (Math.random() * 10), (float) (Math.random() * 10), (float) (Math.random() * 10), (float) (Math.random() * 10), (float) (Math.random() * 10), (float) (Math.random() * 10), (float) (Math.random() * 10)});
        mDayChartItemSet.setValueTemperature(new float[]{(float) (Math.random() * 10), (float) (Math.random() * 10), (float) (Math.random() * 10), (float) (Math.random() * 10), (float) (Math.random() * 10), (float) (Math.random() * 10), (float) (Math.random() * 10)});
        mDayChartItemSet.setValueUvIndex(new float[]{(float) (Math.random() * 10), (float) (Math.random() * 10), (float) (Math.random() * 10), (float) (Math.random() * 10), (float) (Math.random() * 10), (float) (Math.random() * 10), (float) (Math.random() * 10)});
        mDayChartItemSet.setValueGas(new float[]{(float) (Math.random() * 10), (float) (Math.random() * 10), (float) (Math.random() * 10), (float) (Math.random() * 10), (float) (Math.random() * 10), (float) (Math.random() * 10), (float) (Math.random() * 10)});

        mWeekChartItemSet.setLabel(new String[]{"16", "17", "18", "19"});
        mWeekChartItemSet.setValueParticles(new float[]{(float) (Math.random() * 10), (float) (Math.random() * 10), (float) (Math.random() * 10), (float) (Math.random() * 10)});
        mWeekChartItemSet.setValueAllergen(new float[]{(float) (Math.random() * 10), (float) (Math.random() * 10), (float) (Math.random() * 10), (float) (Math.random() * 10)});
        mWeekChartItemSet.setValueHumidity(new float[]{(float) (Math.random() * 10), (float) (Math.random() * 10), (float) (Math.random() * 10), (float) (Math.random() * 10)});
        mWeekChartItemSet.setValueTemperature(new float[]{(float) (Math.random() * 10), (float) (Math.random() * 10), (float) (Math.random() * 10), (float) (Math.random() * 10)});
        mWeekChartItemSet.setValueUvIndex(new float[]{(float) (Math.random() * 10), (float) (Math.random() * 10), (float) (Math.random() * 10), (float) (Math.random() * 10)});
        mWeekChartItemSet.setValueGas(new float[]{(float) (Math.random() * 10), (float) (Math.random() * 10), (float) (Math.random() * 10), (float) (Math.random() * 10)});

        mMonthChartItemSet.setLabel(new String[]{"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul"});
        mMonthChartItemSet.setValueParticles(new float[]{(float) (Math.random() * 10), (float) (Math.random() * 10), (float) (Math.random() * 10), (float) (Math.random() * 10), (float) (Math.random() * 10), (float) (Math.random() * 10), (float) (Math.random() * 10)});
        mMonthChartItemSet.setValueAllergen(new float[]{(float) (Math.random() * 10), (float) (Math.random() * 10), (float) (Math.random() * 10), (float) (Math.random() * 10), (float) (Math.random() * 10), (float) (Math.random() * 10), (float) (Math.random() * 10)});
        mMonthChartItemSet.setValueHumidity(new float[]{(float) (Math.random() * 10), (float) (Math.random() * 10), (float) (Math.random() * 10), (float) (Math.random() * 10), (float) (Math.random() * 10), (float) (Math.random() * 10), (float) (Math.random() * 10)});
        mMonthChartItemSet.setValueTemperature(new float[]{(float) (Math.random() * 10), (float) (Math.random() * 10), (float) (Math.random() * 10), (float) (Math.random() * 10), (float) (Math.random() * 10), (float) (Math.random() * 10), (float) (Math.random() * 10)});
        mMonthChartItemSet.setValueUvIndex(new float[]{(float) (Math.random() * 10), (float) (Math.random() * 10), (float) (Math.random() * 10), (float) (Math.random() * 10), (float) (Math.random() * 10), (float) (Math.random() * 10), (float) (Math.random() * 10)});
        mMonthChartItemSet.setValueGas(new float[]{(float) (Math.random() * 10), (float) (Math.random() * 10), (float) (Math.random() * 10), (float) (Math.random() * 10), (float) (Math.random() * 10), (float) (Math.random() * 10), (float) (Math.random() * 10)});

        mYearChartItemSet.setLabel(new String[]{"2015", "2016", "2017"});
        mYearChartItemSet.setValueParticles(new float[]{(float) (Math.random() * 10), (float) (Math.random() * 10), (float) (Math.random() * 10)});
        mYearChartItemSet.setValueAllergen(new float[]{(float) (Math.random() * 10), (float) (Math.random() * 10), (float) (Math.random() * 10)});
        mYearChartItemSet.setValueHumidity(new float[]{(float) (Math.random() * 10), (float) (Math.random() * 10), (float) (Math.random() * 10)});
        mYearChartItemSet.setValueTemperature(new float[]{(float) (Math.random() * 10), (float) (Math.random() * 10), (float) (Math.random() * 10)});
        mYearChartItemSet.setValueUvIndex(new float[]{(float) (Math.random() * 10), (float) (Math.random() * 10), (float) (Math.random() * 10)});
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
        String str_userid, str_deviceid, str_email;
        str_userid = Global.GetInstance().GetUserModal().getId();
        str_deviceid = utilityClass.GetDeviceID(); //"DAE24875-8366-4692-8B15-BA8C6634B691";
        str_email = Global.GetInstance().GetUserModal().getEmail();
        String str_hash = UtilityClass.MD5(str_deviceid + str_email + Constant.LOGIN_SECTRET);
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
                try {
                    chartloading.setVisibility(View.VISIBLE);
                } catch ( Exception ignore ){}
                _debug.d(LOG_TAG, "AirSensioRestClient.onStart(GET_GRAPH_DATA)");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // If the response is JSONObject instead of expected JSONArray
                try {
                    chartloading.setVisibility(View.GONE);
                } catch ( Exception ignore ){}
                _debug.d(LOG_TAG, "Recieved JSONObject result");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                // Pull out the first event on the public response
                try {
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
                } catch ( Exception ignore ){}
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                try {
                    chartloading.setVisibility(View.GONE);
                } catch ( Exception ignore ){}
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

                    utilityClass.toast(getString(R.string.not_found));
                    _debug.d(LOG_TAG, "Device not found");
                } else {
                    _debug.e(LOG_TAG, "Get Device Data Error:" + responseString);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                try {
                    chartloading.setVisibility(View.GONE);
                    utilityClass.toast(getString(R.string.check_internet));
                } catch ( Exception ignore ){}
                if (errorResponse == null) _debug.d(LOG_TAG, "errorJSONObject: null");
                else _debug.d(LOG_TAG, "errorJSONObject:" + errorResponse.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                try {
                    chartloading.setVisibility(View.GONE);
                    utilityClass.toast(getString(R.string.check_internet));
                } catch ( Exception ignore ){}
                if (errorResponse == null) _debug.d(LOG_TAG, "errorJSONArray: null");
                else _debug.d(LOG_TAG, "errorJSONArray:" + errorResponse.toString());
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried{
                try {
                    chartloading.setVisibility(View.GONE);
                    utilityClass.toast(getString(R.string.try_again));
                } catch ( Exception ignore ){}
                _debug.d(LOG_TAG, "AirSensioRestClient.GET_GRAPH_DATA.onRetry");
            }

            @Override
            public void onFinish() {
                try {
                    chartloading.setVisibility(View.GONE);
                } catch ( Exception ignore ){}
            }

        });
    }

    @Override
    public void onDestroy() {
        _debug.d(LOG_TAG, "StatisticsFragment destroyed.");
        AirSensioRestClient.cancelRequest(getContext());
        super.onDestroy();
    }

    public void drawStatTutorial(){
        drawStatTutorial(mChart);
    }

    public void drawStatTutorial(View viewEnvir){
        //Show intro
        if( viewEnvir != null ){
            //new PreferencesManager(getContext()).reset(Constant.INTRO_ID_4);
            showIntro(viewEnvir, Constant.INTRO_ID_4, getString(R.string.tutorial_statisticss), ArrowType.AT_NORMAL);
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

    @Override
    public void onUserClicked(String materialIntroViewId) {
        if( materialIntroViewId == Constant.INTRO_ID_4 ){
            ((HomeActivity)getActivity()).mTabPager.setCurrentItem(2);
        }
    }
}
