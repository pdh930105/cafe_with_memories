package com.example.myfirstapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class StartActivity extends AppCompatActivity {

    MyTimer myTimer;
    TextView timeText;
    int clicked;
    private ImageView imageView;
    private static Toast mToast;
    final ArrayList<String> adminIdList = new ArrayList<>();


    public static void showToast(Context context, String s){
        if(mToast == null){
            mToast =Toast.makeText(context, s, Toast.LENGTH_SHORT);
        }
        else
        {
            mToast.setText(s);
        }
        mToast.show();
    }

    int developerModeCount;
    private static final int ACTIVATE_COUNT = 10;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        //타이머용 텍스트
        timeText = findViewById(R.id.timeText);
        //타이머 시간
        myTimer = new MyTimer(90*60*1000, 1000);
        clicked = 0;

        developerModeCount=0;
        imageView = findViewById(R.id.imageView_title);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                developerModeCount++;
                if(developerModeCount>5 &&developerModeCount<ACTIVATE_COUNT){
                    showToast(StartActivity.this.getApplicationContext(), String.format("개발자 모드까지 %d번 남았습니다.", ACTIVATE_COUNT-developerModeCount));
                }else if(developerModeCount>=ACTIVATE_COUNT){
                    developerModeCount = 0;
                    Intent intent = new Intent(StartActivity.this, TestActivity.class);
                    startActivity(intent);
                }
            }
        });
        passPushTokenToServer();

    }
    public void onClick(View view){

        if(view.getId() == R.id.imageButton_story){
            Intent intent = new Intent(this, StoryActivity.class);
            //스택에 남아있는 액티비티라면, 재사용한다.
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        }else if(view.getId() == R.id.imageButton_ing){
            Intent intent = new Intent(this, IngActvity.class);
            //스택에 남아있는 액티비티라면, 재사용한다.
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        }else if(view.getId() == R.id.imageButton_map){
            Intent intent = new Intent(this, MapActivity.class);
            //스택에 남아있는 액티비티라면, 재사용한다.
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        }else if(view.getId() == R.id.imageButton_chat){

            //===========================================================================================

            CollectionReference adminDB = FirebaseFirestore.getInstance().collection("adminDB");
            DocumentReference admin = adminDB.document("admin");
            admin.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {

                    ArrayList<String> adminIDData = (ArrayList<String>) documentSnapshot.get("adminIDList");
                    adminIdList.addAll(adminIDData);
                    String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();//사용자 Uid
                    String userName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
                    Boolean admin = false;
                    Log.d("before", adminIdList.toString() );
                    for (String adminIdData : adminIdList) {
                        Log.d("for", "input User ID : " + adminIdData);
                        if (adminIdData.equals(userName)) {
                            admin= true;
                            break;
                            //finish();
                        }
                    }
                    if (admin==true){
                        Intent intent = new Intent(StartActivity.this, AdminChatActivity.class);
                        intent.putExtra("id", userName);
                        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                    }
                    else
                    {
                        Intent intent = new Intent(StartActivity.this, ClientChatActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                    }
                }
            });



//            FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("level").addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                    int level = dataSnapshot.getValue(int.class);
//                    System.out.println(level+"값이 현재 로그인 계정의 레벨입니다.");
//                    if(level == 1){
//                        FirebaseDatabase.getInstance().getReference().child("admin").addListenerForSingleValueEvent(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                String destinationUid = dataSnapshot.getValue(String.class);
//                                Intent intent = new Intent(StartActivity.this, ClientChatActivity.class);
//                                intent.putExtra("destinationUid", destinationUid);
//                                //스택에 남아있는 액티비티라면, 재사용한다.
//                                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                                startActivity(intent);
//                            }
//
//                            @Override
//                            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                            }
//                        });
//
//                    }else{
//                        Intent intent = new Intent(StartActivity.this, ChatActivity.class);
//                        //스택에 남아있는 액티비티라면, 재사용한다.
//                        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                        startActivity(intent);
//                    }
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                }
//            });
        }
        else{

        }


    }


    public void timerOnClick(View view)
    {
        switch(view.getId())
        {
            case R.id.timeText:
                if(clicked==0){
                    myTimer.start();
                    clicked = 1;
                }else if(clicked == 1){
                    myTimer.cancel();
                    timeText.setText("90:00");
                    clicked=0;
                }

                break;
//            case R.id.btnReset :
//                myTimer.cancel();
//                textView.setText("60 초");
//                break;

        }

    }

    class MyTimer extends CountDownTimer
    {
        public MyTimer(long millisInFuture, long countDownInterval)
        {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            long s = millisUntilFinished/1000;
            timeText.setText(String.format("%02d:%02d",s/60, s%60));
        }

        @Override
        public void onFinish() {
            timeText.setText("00:00");
        }
    }
    void passPushTokenToServer(){
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String token = FirebaseInstanceId.getInstance().getToken();
        Map<String,Object> map = new HashMap<>();
        map.put("pushToken", token);
        FirebaseDatabase.getInstance().getReference().child("users").child(uid).updateChildren(map);
    }
}


