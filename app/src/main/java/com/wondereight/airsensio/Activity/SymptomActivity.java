package com.wondereight.airsensio.Activity;

import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import butterknife.ButterKnife;
import butterknife.Bind;
import butterknife.OnClick;

import com.wondereight.airsensio.R;
import com.wondereight.airsensio.UtilClass.Global;

import java.util.List;


public class SymptomActivity extends AppCompatActivity {

    int[] symptomList;
    Drawable sDrawable;
    int[][] idDrawable;

    @Bind({R.id.sneezing, R.id.itchyeyes, R.id.runnynose, R.id.nasal, R.id.wateryeyes, R.id.itchynose})
    List<ImageView> ivSymptoms;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.symptom_activity);

        ButterKnife.bind(this);
        declaration();
        initialization();
    }

    private void declaration() {

        symptomList = Global.GetInstance().GetStateSymptomList();
    }

    private void initialization() {
        sDrawable = null;
        idDrawable = new int[][]{
                {R.drawable.symptom_1_0, R.drawable.symptom_1_1, R.drawable.symptom_1_2, R.drawable.symptom_1_3},
                {R.drawable.symptom_2_0, R.drawable.symptom_2_1, R.drawable.symptom_2_2, R.drawable.symptom_2_3},
                {R.drawable.symptom_3_0, R.drawable.symptom_3_1, R.drawable.symptom_3_2, R.drawable.symptom_3_3},
                {R.drawable.symptom_4_0, R.drawable.symptom_4_1, R.drawable.symptom_4_2, R.drawable.symptom_4_3},
                {R.drawable.symptom_5_0, R.drawable.symptom_5_1, R.drawable.symptom_5_2, R.drawable.symptom_5_3},
                {R.drawable.symptom_6_0, R.drawable.symptom_6_1, R.drawable.symptom_6_2, R.drawable.symptom_6_3}
        };

        for (int i=0; i<ivSymptoms.size(); i++)
        {
            switch (symptomList[i]){
                case 0:
                    sDrawable = getResources().getDrawable(idDrawable[i][0]);
                    break;
                case 1:
                    sDrawable = getResources().getDrawable(idDrawable[i][1]);
                    break;
                case 2:
                    sDrawable = getResources().getDrawable(idDrawable[i][2]);
                    break;
                case 3:
                    sDrawable = getResources().getDrawable(idDrawable[i][3]);
                    break;
                default:
                    sDrawable = getResources().getDrawable(idDrawable[i][0]);
                    break;
            }
            ivSymptoms.get(i).setImageDrawable(sDrawable);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sympton, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.btnSubmit)
    void onClickSubmit(){
        Global.GetInstance().SetStateSymptomList(symptomList);
        finish();
    }

    @OnClick(R.id.sneezing)
    void onClickSneezing(){
        symptomList[0]++;
        if(symptomList[0]>3)
            symptomList[0] = 0;

        sDrawable = getResources().getDrawable(idDrawable[0][symptomList[0]]);
        ivSymptoms.get(0).setImageDrawable(sDrawable);

    }
    @OnClick(R.id.itchyeyes)
    void onClickItchyEyes(){
        symptomList[1]++;
        if(symptomList[1]>3)
            symptomList[1] = 0;

        sDrawable = getResources().getDrawable(idDrawable[1][symptomList[1]]);
        ivSymptoms.get(1).setImageDrawable(sDrawable);
    }
    @OnClick(R.id.runnynose)
    void onClickRunnyNose(){
        symptomList[2]++;
        if(symptomList[2]>3)
            symptomList[2] = 0;

        sDrawable = getResources().getDrawable(idDrawable[2][symptomList[2]]);
        ivSymptoms.get(2).setImageDrawable(sDrawable);
    }
    @OnClick(R.id.nasal)
    void onClickNasal(){
        symptomList[3]++;
        if(symptomList[3]>3)
            symptomList[3] = 0;

        sDrawable = getResources().getDrawable(idDrawable[3][symptomList[3]]);
        ivSymptoms.get(3).setImageDrawable(sDrawable);
    }
    @OnClick(R.id.wateryeyes)
    void onClickWateryEyes(){
        symptomList[4]++;
        if(symptomList[4]>3)
            symptomList[4] = 0;

        sDrawable = getResources().getDrawable(idDrawable[4][symptomList[4]]);
        ivSymptoms.get(4).setImageDrawable(sDrawable);
    }
    @OnClick(R.id.itchynose)
    void onClickItchyNose(){
        symptomList[5]++;
        if(symptomList[5]>3)
            symptomList[5] = 0;

        sDrawable = getResources().getDrawable(idDrawable[5][symptomList[5]]);
        ivSymptoms.get(5).setImageDrawable(sDrawable);
    }

}