package com.example.myfirstapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;

import java.util.ArrayList;

import static com.example.myfirstapp.AppDataManager.getInstance;


public class StoryActivity extends AppCompatActivity {
    private static final int MAX_HORIZON = 5;
    private TableLayout tableLayout_bookshelve;
    private int  currentStage;
    private  ArrayList<ImageView> imageViewArrayList;

    @Override
    protected void onResume() {
        super.onResume();
        if(AppDataManager.getInstance().isOK()){
            int currentStage = getInstance().getOpendStage();
            for (int i=0;i<currentStage;i++){
                ImageView imageView = imageViewArrayList.get(i);
                imageView.setImageResource(R.drawable.storybook_img_opend);
                imageView.setEnabled(true);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //마지막 스테이지 수
        int finalStageNumber = getInstance().getFinalStage();
        //뷰 객체 연결
        tableLayout_bookshelve = findViewById(R.id.storyActivity_tableLayout);


        if(AppDataManager.getInstance().isOK())
        {
            View.OnClickListener onClickListener = new View.OnClickListener() {
                private int i=0;
                @Override
                public void onClick(View v) {
                    int tag = (int)v.getTag();
                    //Toast.makeText(getApplicationContext(), String.format("%d번 뷰입니다!", tag), Toast.LENGTH_SHORT).show();
                    //현재 선택한 스테이지 설정
                    getInstance().setCurrentStage(tag+1);

                    Intent intent = new Intent(StoryActivity.this, IngActvity.class);
                    //액티비티 스택에 쌓인 중간 액티비티 삭제.
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    //스택에 남아있는 액티비티라면, 재사용한다.
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    finish();
                }
            };


            imageViewArrayList = new ArrayList<>();
            int viewIndex = 0;
            for(int i=0;i<finalStageNumber/MAX_HORIZON+1;i++){
                TableRow tableRow = new TableRow(this);
                tableRow.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.MATCH_PARENT, 1f));

                if(i<finalStageNumber/MAX_HORIZON){
                    for(int j=0; j<MAX_HORIZON;j++){
                        ImageView imageView = imageViewForTableRow(viewIndex);
                        imageView.setOnClickListener(onClickListener);
                        imageViewArrayList.add(imageView);
                        tableRow.addView(imageView);
                        viewIndex++;
                    }
                }else{
                    for(int j=0; j<finalStageNumber%MAX_HORIZON;j++){
                        ImageView imageView = imageViewForTableRow(viewIndex);
                        imageView.setOnClickListener(onClickListener);
                        imageViewArrayList.add(imageView);
                        tableRow.addView(imageView);
                        viewIndex++;
                    }
                }
                tableLayout_bookshelve.addView(tableRow);
            }


        }


//        TableRow tableRow = new TableRow(this);
//        tableRow.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.MATCH_PARENT));
//
//        ImageView imageView = new ImageView(this);
//        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams();
//        layoutParams.width = dpToPx(90, this);
//        layoutParams.height = dpToPx(120, this);
//        imageView.setLayoutParams(layoutParams);
//        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
//        imageView.setImageResource(R.drawable.storybook_img);
//        tableRow.addView(imageView);
//
//        tableLayout_bookshelve.addView(tableRow);



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
    public static int dpToPx(int dp, Context context) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }
    public ImageView imageViewForTableRow(int tag){
        ImageView imageView = new ImageView(this);
        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams();
        layoutParams.width = TableRow.LayoutParams.MATCH_PARENT;//dpToPx(90, this);
        layoutParams.height = TableRow.LayoutParams.MATCH_PARENT;//dpToPx(120, this);
        imageView.setLayoutParams(layoutParams);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        imageView.setImageResource(R.drawable.storybook_img);
//        imageView.setClickable(false);
        imageView.setAdjustViewBounds(true);
        imageView.setEnabled(false);
        imageView.setTag(tag);
        return imageView;
    }
}
