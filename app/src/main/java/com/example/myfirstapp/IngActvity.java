package com.example.myfirstapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;

import java.io.File;

public class IngActvity extends AppCompatActivity implements MyAlertDialogFragment.MyDialogListener{
    private ImageButton imageButton_hint;
    private Button button_send;
    private TextView textView_answer;
    private ImageButton imageButton_prev;
    private ImageButton imageButton_next;
    private ScrollView scrollView;
    //이미지 출력용
    private PhotoView photoView;

    private static Toast mToast;
    public static void showToast(Context context, String s){
        if(mToast == null){
            mToast =Toast.makeText(context, s, Toast.LENGTH_LONG);
        }
        else
        {
            mToast.setText(s);
        }
        mToast.show();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View v = getCurrentFocus();

        if (v != null &&
                (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_MOVE) &&
                v instanceof EditText &&
                !v.getClass().getName().startsWith("android.webkit.")) {
            int scrcoords[] = new int[2];
            v.getLocationOnScreen(scrcoords);
            float x = ev.getRawX() + v.getLeft() - scrcoords[0];
            float y = ev.getRawY() + v.getTop() - scrcoords[1];

            if (x < v.getLeft() || x > v.getRight() || y < v.getTop() || y > v.getBottom())
                hideKeyboard(this);
        }
        return super.dispatchTouchEvent(ev);
    }

    public static void hideKeyboard(Activity activity) {
        if (activity != null && activity.getWindow() != null && activity.getWindow().getDecorView() != null) {
            InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
        }
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialogFragment) {
        AppDataManager.getInstance().useHintToIndex(AppDataManager.getInstance().getCurrentStage()-1);
        Intent intent = new Intent(IngActvity.this, HintActivity.class);
        //액티비티 스택에 쌓인 중간 액티비티 삭제.
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //스택에 남아있는 액티비티라면, 재사용한다.
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        imageButton_hint.setEnabled(true);
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialogFragment) {
        imageButton_hint.setEnabled(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ing);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        scrollView = findViewById(R.id.ing_activity_scrollview);

        imageButton_next = findViewById(R.id.ingactivity_imagebutton_next);
        imageButton_prev = findViewById(R.id.ing_activity_previous);

        photoView = findViewById(R.id.ing_activity_photo_view);
        imageButton_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppDataManager.getInstance().setCurrentStage(AppDataManager.getInstance().getCurrentStage()+1);
                onResume();
            }
        });

        imageButton_prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppDataManager.getInstance().setCurrentStage(AppDataManager.getInstance().getCurrentStage()-1);
                onResume();
            }
        });
        button_send = findViewById(R.id.ing_activity_button_send);
        imageButton_hint = findViewById(R.id.imageButton_hint);
        textView_answer = findViewById(R.id.ing_activity_editText_answer);

        imageButton_hint.setOnClickListener(new View.OnClickListener() {
            MyAlertDialogFragment myAlertDialogFragment = new MyAlertDialogFragment();
            @Override
            public void onClick(View v) {
                imageButton_hint.setEnabled(false);
                if(AppDataManager.getInstance().getHintIsUsed(AppDataManager.getInstance().getCurrentStage()-1, Boolean.TRUE))
                {
                    Intent intent = new Intent(IngActvity.this, HintActivity.class);
                    //액티비티 스택에 쌓인 중간 액티비티 삭제.
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    //스택에 남아있는 액티비티라면, 재사용한다.
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    imageButton_hint.setEnabled(true);
                }
                else
                {
                    myAlertDialogFragment.setCancelable(false);
                    myAlertDialogFragment.show(getSupportFragmentManager(), "HintDialog");

                }

            }
        });

        textView_answer.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && keyCode == KeyEvent.KEYCODE_ENTER){
                    checkAnswer();
                    return true;
                }
                return false;
            }
        });

        button_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(textView_answer.getWindowToken(), 0);
                if(textView_answer.getText().length()!=0)
                    checkAnswer();
            }
        });
    }

    private void checkAnswer(){
        String inputAnswer = textView_answer.getText().toString();
        int currentStage = AppDataManager.getInstance().getCurrentStage();
        String answer = AppDataManager.getInstance().getStoryModel().getStages().get(currentStage-1).getAnswer();
        System.out.println(inputAnswer +"  "+ answer);
        inputAnswer = inputAnswer.replaceAll(" ", "");
        inputAnswer = inputAnswer.replaceAll(",", "");
        inputAnswer = inputAnswer.replaceAll("\\.", "");
        if (answer.equalsIgnoreCase(inputAnswer)){
            AppDataManager.getInstance().setOpendStage(currentStage+1);
            AppDataManager.getInstance().incrementCurrentStage();
            showToast(this, "정답입니다.");
            //AppDataManager.getInstance().setCurrentStage(currentStage+1);
            textView_answer.setText("");
            onResume();
        }else{
//                    Toast.makeText(getApplicationContext(), "정답이 아닙니다", Toast.LENGTH_LONG);
            showToast(this, "정답이 아닙니다.");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        int currentStage = AppDataManager.getInstance().getCurrentStage();
        int opendStage = AppDataManager.getInstance().getOpendStage();


        if (currentStage == AppDataManager.START_STAGE) {
            imageButton_prev.setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
        } else {
            imageButton_prev.setColorFilter(null);
        }
        if (currentStage == opendStage) {
            imageButton_next.setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
            scrollView.fullScroll(ScrollView.FOCUS_UP);
        } else {
            imageButton_next.setColorFilter(null);
        }
        try {
            File file = new File(getFilesDir().getPath()+File.separator+ getResources().getString(R.string.story_path) +File.separator+AppDataManager.getInstance().getStoryModel().getStages().get(currentStage-1).getImageFileName());

            try {
                //비트맵으로 이미지를 바로 만들면 너무 큼
                photoView.setImageURI(Uri.fromFile(file));
            }catch (Exception e){
                Glide.with(this).load(file).fitCenter().into(photoView);

            }
        }catch (Exception e){
            photoView.setVisibility(View.GONE);
        }
    }


    public void onClick(View view) {
        if (view.getId() == R.id.imageButton_home) {
            Intent intent = new Intent(this, StartActivity.class);
            //액티비티 스택에 쌓인 중간 액티비티 삭제.
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            //스택에 남아있는 액티비티라면, 재사용한다.
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        } else if (view.getId() == R.id.imageButton_map) {
            Intent intent = new Intent(this, MapActivity.class);
            //액티비티 스택에 쌓인 중간 액티비티 삭제.
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            //스택에 남아있는 액티비티라면, 재사용한다.
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        } else {
        }
    }


}
