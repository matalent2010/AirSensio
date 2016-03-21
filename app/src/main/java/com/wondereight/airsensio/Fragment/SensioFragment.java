package com.wondereight.airsensio.Fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wondereight.airsensio.Activity.SymptomActivity;
import com.wondereight.airsensio.Helper._Debug;
import com.wondereight.airsensio.R;
import com.wondereight.airsensio.UtilClass.Constant;
import com.wondereight.airsensio.UtilClass.UtilityClass;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Miguel on 02/2/2016.
 */
public class SensioFragment extends Fragment {
    private static Context _context;
    private static final String LOG_TAG = "StatisticFragment";
    private static _Debug _debug = new _Debug(true);
    UtilityClass utilityClass;

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
        View view = inflater.inflate(R.layout.fragment_sensio, container, false);
        ButterKnife.bind(this, view);
        utilityClass = new UtilityClass(_context);

        return view;
    }

    @OnClick(R.id.btn_preorder)
    public void onClickPreOrder(){
        goWebsite(Constant.URL_Wlab);
    }

    private void goWebsite(String url){
        Uri uriUrl = Uri.parse(url);
        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
        startActivity(launchBrowser);
    }
}
