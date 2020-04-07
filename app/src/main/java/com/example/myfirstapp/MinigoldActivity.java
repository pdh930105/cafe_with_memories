package com.example.myfirstapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

public class MinigoldActivity extends AppCompatActivity {

    private ConstraintLayout constraintLayout;
    private FirebaseRemoteConfig mFirebaseRemoteConfig;

    @Override
    protected void onResume() {
        super.onResume();
        getConfig();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_minigold);
//
//        constraintLayout = (ConstraintLayout) findViewById(R.id.minigoldactivity_constraintlayout);
//        constraintLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                getConfig();
//            }
//        });
    }
    void getConfig(){
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(0)
                .build();
        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings);
        mFirebaseRemoteConfig.setDefaultsAsync(R.xml.default_config);

        mFirebaseRemoteConfig.fetchAndActivate()
                .addOnCompleteListener(this, new OnCompleteListener<Boolean>() {
                    @Override
                    public void onComplete(@NonNull Task<Boolean> task) {
                        if (task.isSuccessful()) {
                            boolean updated = task.getResult();


                        } else {

                        }
                        displayMessage();
                    }
                });
    }
    void displayMessage(){
        String minigold_background = mFirebaseRemoteConfig.getString("minigold_background");
        boolean caps = mFirebaseRemoteConfig.getBoolean("minigold_message_caps");
        String minigold_message = mFirebaseRemoteConfig.getString("minigold_message");

        //constraintLayout.setBackgroundColor(Color.parseColor(minigold_background));
        if(caps){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(minigold_message).setPositiveButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finish();
                }
            });
            builder.create().show();
        }else{
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }

    }
}
