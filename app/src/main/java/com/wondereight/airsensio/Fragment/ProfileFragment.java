package com.wondereight.airsensio.Fragment;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wondereight.airsensio.R;

import butterknife.ButterKnife;

/**
 * Created by Miguel on 02/2/2016.
 */
public class ProfileFragment extends Fragment {
    private static Context _context;

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
        return view;
    }
}
