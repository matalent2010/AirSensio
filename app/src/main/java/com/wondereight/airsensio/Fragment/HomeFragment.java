package com.wondereight.airsensio.Fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.Bind;
import butterknife.OnClick;

import com.wondereight.airsensio.Activity.SymptomActivity;
import com.wondereight.airsensio.R;


/**
 * Created by Miguel on 02/2/2016.
 */
public class HomeFragment extends Fragment {

    private static Context _context;
    public HomeFragment() {
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
        return view;
    }

    @OnClick(R.id.btnPress)
    public void onClickPress(){
        Intent SymptomActivity = new Intent(_context, SymptomActivity.class);
        _context.startActivity(SymptomActivity);
    }

}
