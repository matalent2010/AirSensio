package com.wondereight.airsensio.Activity;

import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.Bind;
import butterknife.OnClick;

import com.wondereight.airsensio.Adapter.ViewPagerAdapter;
import com.wondereight.airsensio.CustomView.CustomViewPager;
import com.wondereight.airsensio.Fragment.HomeFragment;
import com.wondereight.airsensio.Fragment.PreorderFragment;
import com.wondereight.airsensio.Fragment.ProfileFragment;
import com.wondereight.airsensio.Fragment.SettingsFragment;
import com.wondereight.airsensio.Fragment.StatisticsFragment;
import com.wondereight.airsensio.R;

import java.util.ArrayList;

public class HomeActivity extends FragmentActivity {

    private int curTabIndex;
    @Bind(R.id.viewpager) public CustomViewPager mTabPager;

    @Bind(R.id.ivitem_home) public ImageView mBtnHomeImage;
    @Bind(R.id.ivitemtext_home) public TextView mBtnHomeText;
    @Bind(R.id.ivitem_statistics) public ImageView mBtnStatisticsImage;
    @Bind(R.id.ivitemtext_statistics) public TextView mBtnStatisticsText;
    @Bind(R.id.ivitem_preorder) public ImageView mBtnPreorderImage;
    @Bind(R.id.ivitemtext_preorder) public TextView mBtnPreorderText;
    @Bind(R.id.ivitem_profile) public ImageView mBtnProfileImage;
    @Bind(R.id.ivitemtext_profile) public TextView mBtnProfileText;
    @Bind(R.id.ivitem_settings) public ImageView mBtnSettingsImage;
    @Bind(R.id.ivitemtext_settings) public TextView mBtnSettingsText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);
        ButterKnife.bind(HomeActivity.this);

        mTabPager.setPagingEnabled(false);
        try {
            mTabPager.setOnPageChangeListener(new TabPageChangeListener());
        } catch ( Exception e){
            e.printStackTrace();
        }

        ViewPagerAdapter mPagerAdapter = new ViewPagerAdapter(HomeActivity.this, getSupportFragmentManager());
        mPagerAdapter.addFragment(HomeFragment.newInstance(HomeActivity.this));
        mPagerAdapter.addFragment(StatisticsFragment.newInstance(HomeActivity.this));
        mPagerAdapter.addFragment(PreorderFragment.newInstance(HomeActivity.this));
        mPagerAdapter.addFragment(ProfileFragment.newInstance(HomeActivity.this));
        mPagerAdapter.addFragment(SettingsFragment.newInstance(HomeActivity.this));
        mTabPager.setAdapter(mPagerAdapter);
        mTabPager.setCurrentItem(0);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
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

    @OnClick(R.id.btn_home)
    void onClickHome(){
        mTabPager.setCurrentItem(0, false);
    }
    @OnClick(R.id.btn_statistics)
    void onClickStatistics(){
        mTabPager.setCurrentItem(1, false);
    }
    @OnClick(R.id.btn_preorder)
    void onClickPreorder(){
        mTabPager.setCurrentItem(2, false);
    }
    @OnClick(R.id.btn_profile)
    void onClickProfile(){
        mTabPager.setCurrentItem(3, false);
    }
    @OnClick(R.id.btn_settings)
    void onClickSettings(){ mTabPager.setCurrentItem(4, false); }

    public class TabPageChangeListener implements OnPageChangeListener {

        @Override
        public void onPageScrollStateChanged(int arg0) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onPageSelected(int arg0) {
            Drawable sDrawableNormal = null;
            switch (curTabIndex) {
                case 0:
                    sDrawableNormal = getResources().getDrawable(R.drawable.btn_home_n);
                    mBtnHomeImage.setImageDrawable(sDrawableNormal);
                    mBtnHomeText.setTextColor(getResources().getColor(R.color.BottomMenuTextColor_n));
                    break;
                case 1:
                    sDrawableNormal = getResources().getDrawable(R.drawable.btn_statistics_n);
                    mBtnStatisticsImage.setImageDrawable(sDrawableNormal);
                    mBtnStatisticsText.setTextColor(getResources().getColor(R.color.BottomMenuTextColor_n));
                    break;
                case 2:
                    sDrawableNormal = getResources().getDrawable(R.drawable.btn_preorder_n);
                    mBtnPreorderImage.setImageDrawable(sDrawableNormal);
                    mBtnPreorderText.setTextColor(getResources().getColor(R.color.BottomMenuTextColor_n));
                    break;
                case 3:
                    sDrawableNormal = getResources().getDrawable(R.drawable.btn_profile_n);
                    mBtnProfileImage.setImageDrawable(sDrawableNormal);
                    mBtnProfileText.setTextColor(getResources().getColor(R.color.BottomMenuTextColor_n));
                    break;
                case 4:
                    sDrawableNormal = getResources().getDrawable(R.drawable.btn_settings_n);
                    mBtnSettingsImage.setImageDrawable(sDrawableNormal);
                    mBtnSettingsText.setTextColor(getResources().getColor(R.color.BottomMenuTextColor_n));
                    break;
            }

            sDrawableNormal.setCallback(null);

            Drawable sDrawablePressed = null;

            switch (arg0) {
                case 0:
                    sDrawablePressed = getResources().getDrawable(R.drawable.btn_home_s);
                    mBtnHomeImage.setImageDrawable(sDrawablePressed);
                    mBtnHomeText.setTextColor(getResources().getColor(R.color.BottomMenuTextColor_s));
                    break;
                case 1:
                    sDrawablePressed = getResources().getDrawable(R.drawable.btn_statistics_s);
                    mBtnStatisticsImage.setImageDrawable(sDrawablePressed);
                    mBtnStatisticsText.setTextColor(getResources().getColor(R.color.BottomMenuTextColor_s));
                    break;
                case 2:
                    sDrawablePressed = getResources().getDrawable(R.drawable.btn_preorder_s);
                    mBtnPreorderImage.setImageDrawable(sDrawablePressed);
                    mBtnPreorderText.setTextColor(getResources().getColor(R.color.BottomMenuTextColor_s));
                    break;
                case 3:
                    sDrawablePressed = getResources().getDrawable(R.drawable.btn_profile_s);
                    mBtnProfileImage.setImageDrawable(sDrawablePressed);
                    mBtnProfileText.setTextColor(getResources().getColor(R.color.BottomMenuTextColor_s));
                    break;
                case 4:
                    sDrawablePressed = getResources().getDrawable(R.drawable.btn_settings_s);
                    mBtnSettingsImage.setImageDrawable(sDrawablePressed);
                    mBtnSettingsText.setTextColor(getResources().getColor(R.color.BottomMenuTextColor_s));
                    break;
            }

            sDrawablePressed.setCallback(null);
            curTabIndex = arg0;
        }
    }
}
