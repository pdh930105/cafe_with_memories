package com.example.myfirstapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;

import java.io.File;

public class HintActivity extends AppCompatActivity {
    private ImageButton imageButton_back;
    private PhotoView photoView;
    private TextView textView_hint;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hint);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        imageButton_back = findViewById(R.id.hintactivity_imageButton_back);
        photoView = findViewById(R.id.hintactivity_photoview);
        textView_hint =findViewById(R.id.hintactivity_textView_hint);

        imageButton_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HintActivity.this, IngActvity.class);
                //액티비티 스택에 쌓인 중간 액티비티 삭제.
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                //스택에 남아있는 액티비티라면, 재사용한다.
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        String[] hintList = getResources().getStringArray(R.array.hint_array);
        int currentStage = AppDataManager.getInstance().getCurrentStage();

        try {
            String filename = AppDataManager.getInstance().getStoryModel().getStages().get(currentStage-1).getHintFileName();
            File file = new File(getFilesDir().getPath()+File.separator+ getResources().getString(R.string.story_path) +File.separator+filename);

            try {
                //비트맵으로 이미지를 바로 만들면 너무 큼
                photoView.setImageURI(Uri.fromFile(file));
            }catch (Exception e){
                Glide.with(this).load(file).fitCenter().into(photoView);
            }
        }catch (Exception e){
            photoView.setVisibility(View.GONE);
            textView_hint.setVisibility(View.VISIBLE);
            String info = String.format("현재 스테이지:%d\n열린 스테이지:%d\n마지막 스테이지%d\n", AppDataManager.getInstance().getCurrentStage(), AppDataManager.getInstance().getOpendStage(), AppDataManager.getInstance().getFinalStage());
            textView_hint.setText(info+"힌트가\n없습니다.\nㅠㅠ");
        }



    }
}
