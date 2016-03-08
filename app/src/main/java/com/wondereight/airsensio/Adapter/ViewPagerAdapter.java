package com.wondereight.airsensio.Adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.wondereight.airsensio.Fragment.HomeFragment;
import com.wondereight.airsensio.Fragment.SensioFragment;
import com.wondereight.airsensio.Fragment.ProfileFragment;
import com.wondereight.airsensio.Fragment.SettingsFragment;
import com.wondereight.airsensio.Fragment.StatisticsFragment;

import java.util.ArrayList;

/**
 * Created by Miguel on 02/2/2016.
 */

public class ViewPagerAdapter extends FragmentPagerAdapter {

	private Context _context;
    private ArrayList<Fragment> pageList = new ArrayList<>();

	public ViewPagerAdapter(Context context, FragmentManager fm) {
		super(fm);
		this._context = context;
	}

	@Override
	public Fragment getItem(int pos) {
        return pageList.get(pos);
	}

	@Override
	public int getCount() {
		return pageList.size();
	}

    public void addFragment(Fragment newFragment){
        pageList.add(newFragment);
    }

    public void removeFragment(Fragment removeFrag){
        pageList.remove(removeFrag);
    }

    public void removeFragment(int index){
        pageList.remove(index);
    }
}
