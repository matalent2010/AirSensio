package com.wondereight.airsensio.Fragment;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.db.chart.Tools;
import com.db.chart.model.LineSet;
import com.db.chart.view.AxisController;
import com.db.chart.view.ChartView;
import com.db.chart.view.LineChartView;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.loopj.android.http.*;
import com.wondereight.airsensio.Helper._Debug;
import com.wondereight.airsensio.Modal.DeviceDataModal;
import com.wondereight.airsensio.Modal.GraphDataModal;
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
import cz.msebera.android.httpclient.Header;

import static com.wondereight.airsensio.Fragment.StatisticsFragment.GraphStyle.DAY;
import static com.wondereight.airsensio.Fragment.StatisticsFragment.GraphStyle.WEEK;

/**
 * Created by Miguel on 02/2/2016.
 */
public class StatisticsFragment extends Fragment {
    private static Context _context;
    private static View _view;

    private static final String LOG_TAG = "StatisticFragment";
    private static _Debug _debug = new _Debug(true);
    UtilityClass utilityClass;

    private LineChartView mChart;

    private ChartItem mDayChartItemSet;
    private ChartItem mWeekChartItemSet;
    private ChartItem mMonthChartItemSet;
    private ChartItem mYearChartItemSet;

    private Boolean[] loadedState = {false, false, false, false};

    private final String[] mLabels= {"Jan", "Fev", "Mar", "Apr", "Jun", "May", "Jul", "Aug", "Sep"};
    private final float[][] mValues = {{123.5f, 400.7f, 40.3f, 80f, 600.5f, 139.9f, 700f, 800.3f, 7.0f},
            {400.5f, 222.5f, 992.5f, 129f, 40.5f, 9.5f, 50f, 800.3f, 100.8f}};

    @Bind({R.id.tab_day, R.id.tab_week, R.id.tab_month, R.id.tab_year})
    List<TextView> tabButton;

    @Bind({R.id.item_particles, R.id.item_allergen, R.id.item_humidity, R.id.item_temperature, R.id.item_sunlight, R.id.item_gas})
    List<LinearLayout> items;

    @Bind(R.id.chartloading)
    View chartloading;

    public static enum GraphStyle {
        DAY(0),
        WEEK(1),
        MONTH(2),
        YEAR(3);
        private int type;

        private  GraphStyle(int type) {
            this.type = type;
        }
        public int getNumericType() {
            return type;
        }

        public void setNumericType(int type) {
            this.type = type;
        }
    }

    public StatisticsFragment() {
        mChart = null;
    }

    public static Fragment newInstance(Context context) {
        _context = context;
        StatisticsFragment f = new StatisticsFragment();
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        _view = inflater.inflate(R.layout.fragment_statistics, container, false);
        mChart = (LineChartView) _view.findViewById(R.id.chart1);
        ButterKnife.bind(this, _view);
        utilityClass = new UtilityClass(getContext());

        mDayChartItemSet = new ChartItem();
        mWeekChartItemSet = new ChartItem();
        mMonthChartItemSet = new ChartItem();
        mYearChartItemSet = new ChartItem();

        //loadTempChartData();
        displayCurrentState();
        loadCurrentData();

        redrawGraph(_view);

        return _view;
    }

    @OnClick(R.id.tab_day)
     public void onClickTabDay(){
        int oldGraphStyle = Global.GetInstance().GetStyleOfGraph();

        if ( oldGraphStyle != DAY.getNumericType()){
            tabButton.get(oldGraphStyle).setBackgroundDrawable(getResources().getDrawable(R.drawable.itembuttonpannel));
            tabButton.get(oldGraphStyle).setTextColor(getResources().getColor(R.color.textColor_Green));
            tabButton.get(DAY.getNumericType()).setBackgroundDrawable(getResources().getDrawable(R.drawable.itemgreenpannel));
            tabButton.get(DAY.getNumericType()).setTextColor(getResources().getColor(R.color.textColor_White));
            Global.GetInstance().SetStyleOfGraph(DAY.getNumericType());
            loadCurrentData();
            redrawGraph(_view);
        }
    }

    @OnClick(R.id.tab_week)
    public void onClickTabWeek(){
        int oldGraphStyle = Global.GetInstance().GetStyleOfGraph();

        if ( oldGraphStyle != GraphStyle.WEEK.getNumericType()){
            tabButton.get(oldGraphStyle).setBackgroundDrawable(getResources().getDrawable(R.drawable.itembuttonpannel));
            tabButton.get(oldGraphStyle).setTextColor(getResources().getColor(R.color.textColor_Green));
            tabButton.get(GraphStyle.WEEK.getNumericType()).setBackgroundDrawable(getResources().getDrawable(R.drawable.itemgreenpannel));
            tabButton.get(GraphStyle.WEEK.getNumericType()).setTextColor(getResources().getColor(R.color.textColor_White));
            Global.GetInstance().SetStyleOfGraph(GraphStyle.WEEK.getNumericType());
            loadCurrentData();
            redrawGraph(_view);
        }
    }

    @OnClick(R.id.tab_month)
    public void onClickTabMonth(){
        int oldGraphStyle = Global.GetInstance().GetStyleOfGraph();

        if ( oldGraphStyle != GraphStyle.MONTH.getNumericType()){
            tabButton.get(oldGraphStyle).setBackgroundDrawable(getResources().getDrawable(R.drawable.itembuttonpannel));
            tabButton.get(oldGraphStyle).setTextColor(getResources().getColor(R.color.textColor_Green));
            tabButton.get(GraphStyle.MONTH.getNumericType()).setBackgroundDrawable(getResources().getDrawable(R.drawable.itemgreenpannel));
            tabButton.get(GraphStyle.MONTH.getNumericType()).setTextColor(getResources().getColor(R.color.textColor_White));
            Global.GetInstance().SetStyleOfGraph(GraphStyle.MONTH.getNumericType());
            loadCurrentData();
            redrawGraph(_view);
        }
    }

    @OnClick(R.id.tab_year)
    public void onClickTabYear(){
        int oldGraphStyle = Global.GetInstance().GetStyleOfGraph();

        if ( oldGraphStyle != GraphStyle.YEAR.getNumericType()){
            tabButton.get(oldGraphStyle).setBackgroundDrawable(getResources().getDrawable(R.drawable.itembuttonpannel));
            tabButton.get(oldGraphStyle).setTextColor(getResources().getColor(R.color.textColor_Green));
            tabButton.get(GraphStyle.YEAR.getNumericType()).setBackgroundDrawable(getResources().getDrawable(R.drawable.itemgreenpannel));
            tabButton.get(GraphStyle.YEAR.getNumericType()).setTextColor(getResources().getColor(R.color.textColor_White));
            Global.GetInstance().SetStyleOfGraph(GraphStyle.YEAR.getNumericType());
            loadCurrentData();
            redrawGraph(_view);
        }
    }

    @OnClick(R.id.item_particles)
    public void onClickParticles(){
        ArrayList<Boolean> state = Global.GetInstance().GetStateStatistics();
        if( state.get(0) == true) {
            state.set(0, false);
            items.get(0).setBackgroundDrawable(getResources().getDrawable(R.drawable.itembuttonpannel));
        } else {
            state.set(0, true);
            items.get(0).setBackgroundDrawable(getResources().getDrawable(R.drawable.itemgraypannel));
        }
        Global.GetInstance().SetStateStatistics(state);
        redrawGraph(_view);
    }

    @OnClick(R.id.item_allergen)
    public void onClickAllergen(){
        ArrayList<Boolean> state = Global.GetInstance().GetStateStatistics();
        if( state.get(1) == true) {
            state.set(1, false);
            items.get(1).setBackgroundDrawable(getResources().getDrawable(R.drawable.itembuttonpannel));
        } else {
            state.set(1, true);
            items.get(1).setBackgroundDrawable(getResources().getDrawable(R.drawable.itemgraypannel));
        }
        Global.GetInstance().SetStateStatistics(state);
        redrawGraph(_view);
    }

    @OnClick(R.id.item_humidity)
    public void onClickHumidity(){
        ArrayList<Boolean> state = Global.GetInstance().GetStateStatistics();
        if( state.get(2) == true) {
            state.set(2, false);
            items.get(2).setBackgroundDrawable(getResources().getDrawable(R.drawable.itembuttonpannel));
        } else {
            state.set(2, true);
            items.get(2).setBackgroundDrawable(getResources().getDrawable(R.drawable.itemgraypannel));
        }
        Global.GetInstance().SetStateStatistics(state);
        redrawGraph(_view);
    }

    @OnClick(R.id.item_temperature)
    public void onClickTemperature(){
        ArrayList<Boolean> state = Global.GetInstance().GetStateStatistics();
        if( state.get(3) == true) {
            state.set(3, false);
            items.get(3).setBackgroundDrawable(getResources().getDrawable(R.drawable.itembuttonpannel));
        } else {
            state.set(3, true);
            items.get(3).setBackgroundDrawable(getResources().getDrawable(R.drawable.itemgraypannel));
        }
        Global.GetInstance().SetStateStatistics(state);
        redrawGraph(_view);
    }

    @OnClick(R.id.item_sunlight)
    public void onClickSunlight(){
        ArrayList<Boolean> state = Global.GetInstance().GetStateStatistics();
        if( state.get(4) == true) {
            state.set(4, false);
            items.get(4).setBackgroundDrawable(getResources().getDrawable(R.drawable.itembuttonpannel));
        } else {
            state.set(4, true);
            items.get(4).setBackgroundDrawable(getResources().getDrawable(R.drawable.itemgraypannel));
        }
        Global.GetInstance().SetStateStatistics(state);
        redrawGraph(_view);
    }

    @OnClick(R.id.item_gas)
    public void onClickGas(){
        ArrayList<Boolean> state = Global.GetInstance().GetStateStatistics();
        if( state.get(5) == true) {
            state.set(5, false);
            items.get(5).setBackgroundDrawable(getResources().getDrawable(R.drawable.itembuttonpannel));
        } else {
            state.set(5, true);
            items.get(5).setBackgroundDrawable(getResources().getDrawable(R.drawable.itemgraypannel));
        }
        Global.GetInstance().SetStateStatistics(state);
        redrawGraph(_view);
    }

    private void setGraph(View view){

        Paint gridPaint = new Paint();
        gridPaint.setColor(Color.parseColor("#ffffff"));
        gridPaint.setStyle(Paint.Style.STROKE);
        gridPaint.setAntiAlias(true);
        gridPaint.setStrokeWidth(Tools.fromDpToPx(.75f));

        LineSet dataset = new LineSet(new String[]{""}, new float[]{0});
        mChart.addData(dataset);

        // Chart
        mChart.setBorderSpacing(Tools.fromDpToPx(15))
                .setAxisBorderValues(0, 1000)
                .setXLabels(AxisController.LabelPosition.OUTSIDE)
                .setYLabels(AxisController.LabelPosition.OUTSIDE)
                .setLabelsColor(Color.parseColor("#FFFFFF"))
                .setGrid(ChartView.GridType.HORIZONTAL, 10, 5, gridPaint)
                .setStep(100)
                .setXAxis(false)
                .setYAxis(false);

        mChart.setHorizontalScrollBarEnabled(true);
        mChart.show();
    }

    private void redrawGraph(View view){
        mChart.reset();

        switch (Global.GetInstance().GetStyleOfGraph()){
            case 0:   // if GraphStyle.DAY
                drawGraphWithChartItemSet(mDayChartItemSet);
                break;
            case 1:   // if GraphStyle.Week
                drawGraphWithChartItemSet(mWeekChartItemSet);
                break;
            case 2:   // if GraphStyle.Month
                drawGraphWithChartItemSet(mMonthChartItemSet);
                break;
            case 3:   // if GraphStyle.Year
                drawGraphWithChartItemSet(mYearChartItemSet);
                break;
        }

        Paint gridPaint = new Paint();
        gridPaint.setColor(Color.parseColor("#ffffff"));
        gridPaint.setStyle(Paint.Style.STROKE);
        gridPaint.setAntiAlias(true);
        gridPaint.setStrokeWidth(Tools.fromDpToPx(.75f));

        mChart.setBorderSpacing(Tools.fromDpToPx(15))
                .setAxisBorderValues(0, 10)//.setAxisBorderValues(0, 1000)
                .setXLabels(AxisController.LabelPosition.OUTSIDE)
                .setYLabels(AxisController.LabelPosition.OUTSIDE)
                .setLabelsColor(Color.parseColor("#FFFFFF"))
                .setGrid(ChartView.GridType.HORIZONTAL, 10, 5, gridPaint)
                .setStep(1) //.setStep(100)
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
        List<Boolean> state = Global.GetInstance().GetStateStatistics();
        if(state.get(0) == true){   //Particles Graph
            try {
                LineSet dataset = new LineSet(itemSet.getLabel(), itemSet.getValueParticles());
                dataset.setColor(Color.parseColor("#FFFFFF"))
                        .setDotsColor(Color.parseColor("#79c143"))
                        .setThickness(4)
                        .setDotsStrokeColor(Color.parseColor("#FFFFFF"))
                        .setDotsStrokeThickness(2);
                mChart.addData(dataset);

            } catch ( IllegalArgumentException e ){ _debug.w(LOG_TAG, "Particles Graph is null.");}
        }
        if(state.get(1) == true){   //Allergen Graph
            try{
                LineSet dataset = new LineSet(itemSet.getLabel(), itemSet.getValueAllergen());
                dataset.setColor(Color.parseColor("#FFFFFF"))
                        .setDotsColor(Color.parseColor("#77bb9c"))
                        .setThickness(4)
                        .setDotsStrokeColor(Color.parseColor("#FFFFFF"))
                        .setDotsStrokeThickness(2);
                mChart.addData(dataset);
            } catch ( IllegalArgumentException e ){ _debug.w(LOG_TAG, "Allergen Graph is null.");}
        }
        if(state.get(2) == true){   //Humidity Graph
            try {
                LineSet dataset = new LineSet(itemSet.getLabel(), itemSet.getValueHumidity());
                dataset.setColor(Color.parseColor("#FFFFFF"))
                        .setDotsColor(Color.parseColor("#3e9cff"))
                        .setThickness(4)
                        .setDotsStrokeColor(Color.parseColor("#FFFFFF"))
                        .setDotsStrokeThickness(2);
                mChart.addData(dataset);
            } catch ( IllegalArgumentException e ){ _debug.w(LOG_TAG, "Humidity Graph is null.");}
        }
        if(state.get(3) == true){   //Temperature Graph
            try {
                LineSet dataset = new LineSet(itemSet.getLabel(), itemSet.getValueTemperature());
                dataset.setColor(Color.parseColor("#FFFFFF"))
                        .setDotsColor(Color.parseColor("#8dc5f7"))
                        .setThickness(4)
                        .setDotsStrokeColor(Color.parseColor("#FFFFFF"))
                        .setDotsStrokeThickness(2);
                mChart.addData(dataset);
            } catch ( IllegalArgumentException e ){ _debug.w(LOG_TAG, "Temperature Graph is null.");}
        }
        if(state.get(4) == true){   //Sunlight Graph
            try {
                LineSet dataset = new LineSet(itemSet.getLabel(), itemSet.getValueSunlight());
                dataset.setColor(Color.parseColor("#FFFFFF"))
                        .setDotsColor(Color.parseColor("#f4eb49"))
                        .setThickness(4)
                        .setDotsStrokeColor(Color.parseColor("#FFFFFF"))
                        .setDotsStrokeThickness(2);
                mChart.addData(dataset);
            } catch ( IllegalArgumentException e ){ _debug.w(LOG_TAG, "Sunlight Graph is null.");}
        }
        if(state.get(5) == true){   //Gas Graph
            try{
                LineSet dataset = new LineSet(itemSet.getLabel(), itemSet.getValueGas());
                dataset.setColor(Color.parseColor("#FFFFFF"))
                        .setDotsColor(Color.parseColor("#990099"))
                        .setThickness(4)
                        .setDotsStrokeColor(Color.parseColor("#FFFFFF"))
                        .setDotsStrokeThickness(2);
                mChart.addData(dataset);
            } catch ( IllegalArgumentException e ){ _debug.w(LOG_TAG, "Gas Graph is null.");}
        }
    }

    private void displayCurrentState(){

        for ( int i=0; i<4; i++){
            if( i == Global.GetInstance().GetStyleOfGraph() ){
                tabButton.get(i).setBackgroundDrawable(getResources().getDrawable(R.drawable.itemgreenpannel));
                tabButton.get(i).setTextColor(getResources().getColor(R.color.textColor_White));
            } else{
                tabButton.get(i).setBackgroundDrawable(getResources().getDrawable(R.drawable.itembuttonpannel));
                tabButton.get(i).setTextColor(getResources().getColor(R.color.textColor_Green));
            }
        }

        for ( int i=0; i<6; i++){
            if( Global.GetInstance().GetStateStatistics().get(i) == true ){
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

    private void loadCurrentData(){

        switch (Global.GetInstance().GetStyleOfGraph()){
            case 0: //day
                restCallGraphDataApi(GraphStyle.DAY);
                break;
            case 1: //week
                restCallGraphDataApi(GraphStyle.WEEK);
                break;
            case 2: //month
                restCallGraphDataApi(GraphStyle.MONTH);
                break;
            case 3: //year
                restCallGraphDataApi(GraphStyle.YEAR);
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

    private String getDurationName(GraphStyle state){
        String result = "day";
        switch (state){
            case DAY:
                result = "day";
                break;
            case WEEK:
                result = "week";
                break;
            case MONTH:
                result = "month";
                break;
            case YEAR:
                result = "year";
                break;
        }
        return result;
    }
    private void restCallGraphDataApi(final GraphStyle state) {

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
        params.put(Constant.STR_DURATION, getDurationName(state));

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
                switch (state){
                    case DAY:
                        mDayChartItemSet = ParsingResponse.parsingGraphData(response, state.getNumericType());
                        break;
                    case WEEK:
                        mWeekChartItemSet = ParsingResponse.parsingGraphData(response, state.getNumericType());
                        break;
                    case MONTH:
                        mMonthChartItemSet = ParsingResponse.parsingGraphData(response, state.getNumericType());
                        break;
                    case YEAR:
                        mYearChartItemSet = ParsingResponse.parsingGraphData(response, state.getNumericType());
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
                    _debug.e(LOG_TAG, "Get Device Data Error:"+responseString);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                chartloading.setVisibility(View.GONE);
                utilityClass.toast(getResources().getString(R.string.check_internet));
                if (errorResponse==null) _debug.d(LOG_TAG, "errorJSONObject: null"); else _debug.d(LOG_TAG, "errorJSONObject:" + errorResponse.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                chartloading.setVisibility(View.GONE);
                utilityClass.toast(getResources().getString(R.string.check_internet));
                if (errorResponse==null) _debug.d(LOG_TAG, "errorJSONArray: null"); else _debug.d(LOG_TAG, "errorJSONArray:" + errorResponse.toString());
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
}

