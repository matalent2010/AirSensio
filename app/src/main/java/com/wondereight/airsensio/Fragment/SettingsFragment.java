package com.wondereight.airsensio.Fragment;

import android.animation.ValueAnimator;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.wondereight.airsensio.R;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A placeholder fragment containing a simple view.
 */
public class SettingsFragment extends Fragment {
    private static Context _context;

    public SettingsFragment() {
    }

    public static Fragment newInstance(Context context) {
        _context = context;
        SettingsFragment f = new SettingsFragment();
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @OnClick(R.id.btnSendEmail)
    public void onclickSendEmail(){
        Toast.makeText(_context, "SendEmail", Toast.LENGTH_SHORT).show(); ;
    }

}
