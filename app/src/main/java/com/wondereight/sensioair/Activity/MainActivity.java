package com.wondereight.sensioair.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.BinderThread;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.Bind;
import butterknife.OnClick;

import com.wondereight.sensioair.R;
import com.wondereight.sensioair.UtilClass.SaveSharedPreferences;

import org.w3c.dom.Text;

/**
 * Created by Miguel on 02/2/2016.
 */

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.btnAcceptTerms)
    TextView btnAcceptTerms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        if(getIntent().getExtras()==null) {
            if (SaveSharedPreferences.getInstallInfo(MainActivity.this) == false) {
                SaveSharedPreferences.setInstallInfo(MainActivity.this, true);
            } else {
                Intent LogninIntent = new Intent(MainActivity.this, LoginAcitivity.class);
                startActivity(LogninIntent);
                finish();
            }
        } else {
            btnAcceptTerms.setText(getString(R.string.button_continue));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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


    @OnClick(R.id.btnAcceptTerms)
    void onClickBtnAcceptTerms() {
        if( getIntent().getExtras()==null ) {
            Intent LogninIntent = new Intent(MainActivity.this, LoginAcitivity.class);
            startActivity(LogninIntent);
        }
        finish();
    }
}
