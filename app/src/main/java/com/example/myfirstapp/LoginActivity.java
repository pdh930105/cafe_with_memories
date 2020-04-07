package com.example.myfirstapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

public class LoginActivity extends AppCompatActivity {

    private Button buttonSignIn;
    private Button buttonSignUp;
    private FirebaseRemoteConfig mFirebaseRemoteConfig;
    private ImageView imageViewLogo;
    private ConstraintLayout constraintLayout;

    private EditText id;
    private EditText password;

    private FirebaseAuth firebaseAuth;
    //로그인 되었는지 확인해주는 리스너
    private FirebaseAuth.AuthStateListener authStateListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();


        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        String minigold_background = mFirebaseRemoteConfig.getString(getString(R.string.rc_bg_color));

        constraintLayout = (ConstraintLayout)findViewById(R.id.loginactivity_constraintlayout);
        constraintLayout.setBackgroundColor(Color.parseColor(minigold_background));
        imageViewLogo = (ImageView)findViewById(R.id.imageView_logo);
        buttonSignIn = (Button)findViewById(R.id.loginActivity_edittext_button_signin);
        buttonSignUp = (Button)findViewById(R.id.loginActivity_edittext_button_signup);

        id = (EditText)findViewById(R.id.loginActivity_edittext_id);
        password = (EditText)findViewById(R.id.loginActivity_edittext_password);
        buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginEvent();
            }
        });
//        imageViewLogo.setBackgroundColor(Color.parseColor(minigold_background));
//        buttonSignIn.setBackgroundColor(Color.parseColor(minigold_background));
//        buttonSignUp.setBackgroundColor(Color.parseColor(minigold_background));
        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, SignupActivity.class));
            }
        });

        //로그인 인터페이스 리스너
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null){//로그인이 된경우
                    Intent intent = new Intent(LoginActivity.this, StartActivity.class);
                    startActivity(intent);
                    finish();

                }else{
                    //로그아웃
                }
            }
        };
    }
    void loginEvent(){
        firebaseAuth.signInWithEmailAndPassword(id.getText().toString(), password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {//로그인 되었는지 받아서 로그인이 되었는지 알려주는 녀석
                if(!task.isSuccessful()){
                    //로그인 실패한 경우
                    Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                }

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        //시작될때 리스너를 붙여줌
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        //멈출때 떼줌
        firebaseAuth.removeAuthStateListener(authStateListener);
    }
}
