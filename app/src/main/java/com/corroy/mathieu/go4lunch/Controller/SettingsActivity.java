package com.corroy.mathieu.go4lunch.Controller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Switch;
import com.batch.android.Batch;
import com.corroy.mathieu.go4lunch.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingsActivity extends AppCompatActivity {

    @BindView(R.id.notification_switch)
    Switch mSwitch;
    @BindView(R.id.settings_toolbar)
    Toolbar toolbar;
    private SharedPreferences.Editor sharedPrefEditor;
    private static final String SHARED_PREF_NOTIFICATION = "shared_pref_notification";
    private static final String SHARED_PREF_SWITCH = "shared_pref_switch";
    private String switchStateTrue = "true";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);
        enableSwitchButton();
        restoreSharedPreferences();

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void enableSwitchButton(){
        mSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sharedPrefEditor = getSharedPreferences(SHARED_PREF_NOTIFICATION, MODE_PRIVATE).edit();
            if(isChecked){
                sharedPrefEditor.putString(SHARED_PREF_SWITCH, switchStateTrue);
                sharedPrefEditor.apply();
                mSwitch.setChecked(true);
                Batch.optIn(this);
                Batch.onStart(this);

            } else {
                sharedPrefEditor.clear().apply();
                mSwitch.setChecked(false);
                Batch.optOut(this);
            }
        });
    }

    private void restoreSharedPreferences(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF_NOTIFICATION, Context.MODE_PRIVATE);
        String switchBtn = sharedPreferences.getString(SHARED_PREF_SWITCH, "");

        if(switchBtn.equals(switchStateTrue)){
            this.mSwitch.setChecked(true);
        }
    }
}
