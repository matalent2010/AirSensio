package com.wondereight.sensioair.Fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.wondereight.sensioair.Activity.HomeActivity;
import com.wondereight.sensioair.Activity.SymptomActivity;
import com.wondereight.sensioair.Helper._Debug;
import com.wondereight.sensioair.R;
import com.wondereight.sensioair.UtilClass.Constant;
import com.wondereight.sensioair.UtilClass.UtilityClass;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import co.mobiwise.materialintro.animation.MaterialIntroListener;
import co.mobiwise.materialintro.prefs.PreferencesManager;
import co.mobiwise.materialintro.shape.ArrowType;
import co.mobiwise.materialintro.shape.Focus;
import co.mobiwise.materialintro.shape.FocusGravity;
import co.mobiwise.materialintro.view.MaterialIntroView;

/**
 * Created by Miguel on 02/2/2016.
 */
public class SensioFragment extends Fragment implements MaterialIntroListener {
    private static Context _context;
    private static final String LOG_TAG = "SensioFragment";
    private static _Debug _debug = new _Debug(true);
    UtilityClass utilityClass;

    @Bind(R.id.scan_view)
    ImageView ivScanView;
    @Bind(R.id.preorder_description)
    View vPreorderDesc;

    public SensioFragment() {
    }

    public static Fragment newInstance(Context context) {
        _context = context;
        SensioFragment f = new SensioFragment();
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        _debug.d(LOG_TAG, "onCreateView");
        View view = inflater.inflate(R.layout.fragment_sensio, container, false);
        ButterKnife.bind(this, view);
        utilityClass = new UtilityClass(_context);

        return view;
    }

    @OnClick(R.id.btn_preorder)
    public void onClickPreOrder(){
        ((HomeActivity)getActivity()).setPreorderNow(true);
        goWebsite(Constant.URL_Wlab);
    }

    private void goWebsite(String url){
        Uri uriUrl = Uri.parse(url);
        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
        startActivity(launchBrowser);
    }

    public void drawSensioTutorial(){
        drawSensioTutorial(ivScanView);
    }

    public void drawSensioTutorial(View view){
        //Show intro
        PreferencesManager pm = new PreferencesManager(getContext());

        if( view != null && !pm.isDisplayed(Constant.INTRO_ID_5)){
            //new PreferencesManager(getContext()).reset(Constant.INTRO_ID_5);
            showIntro(view, Constant.INTRO_ID_5, getString(R.string.tutorial_sensio_scanning), ArrowType.AT_RED);
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
        _debug.d(LOG_TAG, "Tutor pressed: " + materialIntroViewId);

        if( materialIntroViewId == Constant.INTRO_ID_5 ){
            //new PreferencesManager(getContext()).reset(Constant.INTRO_ID_6);
            PreferencesManager pm = new PreferencesManager(getContext());

            if( vPreorderDesc != null && !pm.isDisplayed(Constant.INTRO_ID_6)) {
                //showIntro(vPreorderDsc, Constant.INTRO_ID_6, getString(R.string.tutorial_sensio), ArrowType.AT_NORMAL);
                new MaterialIntroView.Builder(getActivity())
                        .setMaskColor(0x70000000)
                        .setTextColor(0xFF3F9CFF)
                        .enableDotAnimation(false)
                        .setFocusGravity(FocusGravity.CENTER)
                        .setFocusType(Focus.NORMAL)     //changed
                        .setDelayMillis(200)
                        .enableFadeAnimation(true)
                        .performClick(true)
                        .setListener(this)
                        .setArrowType(ArrowType.AT_NORMAL)
                        .setInfoText(getString(R.string.tutorial_sensio))
                        .enableInfoIDText(true)     //Info ID text appear
                        .enableIcon(false)
                        .setTarget(vPreorderDesc)
                        .setUsageId(Constant.INTRO_ID_6) //THIS SHOULD BE UNIQUE ID
                        .show();
            }
        } else if( materialIntroViewId == Constant.INTRO_ID_6 ){
            ((HomeActivity)getActivity()).mTabPager.setCurrentItem(0);
        }

    }
}
