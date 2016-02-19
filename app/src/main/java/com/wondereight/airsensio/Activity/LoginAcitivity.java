package com.wondereight.airsensio.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import butterknife.ButterKnife;
import butterknife.Bind;
import butterknife.OnClick;

import com.wondereight.airsensio.R;

public class LoginAcitivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        ButterKnife.bind(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login_acitivity, menu);
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

    @OnClick(R.id.btnLogin)
    void onClickBtnLogin() {
        Intent HealthActivity = new Intent(LoginAcitivity.this, HealthActivity.class);
        startActivity(HealthActivity);
    }

    @OnClick(R.id.btnSignup)
    void onClickBtnSignup(){
        Intent SignupActivity = new Intent(LoginAcitivity.this, SignupActivity.class);
        startActivity(SignupActivity);
    }
}
