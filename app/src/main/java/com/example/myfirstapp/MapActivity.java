package com.example.myfirstapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.github.chrisbanes.photoview.PhotoView;

public class MapActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        PhotoView photoView = (PhotoView) findViewById(R.id.photo_view);
        photoView.setImageResource(R.drawable.map_img);
    }
    public void onClick(View view){
        if(view.getId() == R.id.imageButton_home){
            Intent intent = new Intent(this, StartActivity.class);
            //액티비티 스택에 쌓인 중간 액티비티 삭제.
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            //스택에 남아있는 액티비티라면, 재사용한다.
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        }else{
    }
    }

}