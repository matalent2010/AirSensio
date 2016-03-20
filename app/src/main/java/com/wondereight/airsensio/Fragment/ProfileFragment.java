package com.wondereight.airsensio.Fragment;

import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wondereight.airsensio.Activity.LoginAcitivity;
import com.wondereight.airsensio.Helper._Debug;
import com.wondereight.airsensio.R;
import com.wondereight.airsensio.UtilClass.Global;
import com.wondereight.airsensio.UtilClass.SaveSharedPreferences;
import com.wondereight.airsensio.UtilClass.UtilityClass;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

/**
 * Created by Miguel on 02/2/2016.
 */
public class ProfileFragment extends Fragment {
    private static Context _context;
    private static final String LOG_TAG = "ProfileFragment";
    private static _Debug _debug = new _Debug(true);
    UtilityClass utilityClass;


    private static FeedbackFragment feedbackdlg;

    @Bind(R.id.feedback_container)
    View vContainer;

    public ProfileFragment() {
    }

    public static Fragment newInstance(Context context) {
        _context = context;
        ProfileFragment f = new ProfileFragment();
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, view);

        utilityClass = new UtilityClass(_context);
        feedbackdlg = (FeedbackFragment)FeedbackFragment.newInstance(_context);
        return view;
    }

    @OnClick(R.id.btn_logout)
    public void onClickLogout(){
        SaveSharedPreferences.clearUserdata(getContext());
        Global.GetInstance().init();
        Intent intent = new Intent(getActivity(), LoginAcitivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @OnClick(R.id.btn_feedback)
    public void onClickFeedback(){
        vContainer.setVisibility(View.VISIBLE);
        android.support.v4.app.FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.slide_top_in, R.anim.slide_top_out);
//        ft.add(SensioFragment.newInstance(_context), "feedbackFragment");
        ft.replace(R.id.feedback_container, feedbackdlg, "feedbackFragment");
        ft.commit();

    }
}
