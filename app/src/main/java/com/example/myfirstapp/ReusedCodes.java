package com.example.myfirstapp;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;

public class ReusedCodes extends AppCompatActivity{

    public  void goHome(){
        Intent intent = new Intent(this, StartActivity.class);
        //액티비티 스택에 쌓인 중간 액티비티 삭제.
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //스택에 남아있는 액티비티라면, 재사용한다.
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        //현재 액티비티를 종료한다.
        finish();
    }
}

