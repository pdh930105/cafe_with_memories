package com.example.myfirstapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.github.chrisbanes.photoview.PhotoView;
import com.wajahatkarim3.easyflipview.EasyFlipView;

public class ImageflipActivity extends AppCompatActivity {
    private EasyFlipView view;
    private EasyFlipView view1;
    private EasyFlipView view2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imageflip);
        LinearLayout linearLayout = findViewById(R.id.imageflipActivity_linearlayout);

        LayoutInflater layoutInflater = LayoutInflater.from(this);
        view = (EasyFlipView) layoutInflater.inflate(R.layout.item_imageflip, linearLayout, false);
        view.removeAllViewsInLayout();
        ImageView imageView = new ImageView(this);
        imageView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,FrameLayout.LayoutParams.MATCH_PARENT));
        imageView.setImageResource(R.drawable.ndg1);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        imageView.setAdjustViewBounds(true);
        view.addView(imageView, 0, imageView.getLayoutParams());
        view.addView(layoutInflater.inflate(R.layout.item_card_back, view, false), 1,new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));


        view1 = (EasyFlipView) layoutInflater.inflate(R.layout.item_imageflip, linearLayout, false);
        view1.removeAllViewsInLayout();
        ImageView imageView1 = new ImageView(this);
        imageView1.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,FrameLayout.LayoutParams.MATCH_PARENT));
        imageView1.setImageResource(R.drawable.ndg3);
        imageView1.setScaleType(ImageView.ScaleType.FIT_CENTER);
        imageView1.setAdjustViewBounds(true);
        view1.addView(imageView1, 0, imageView1.getLayoutParams());
        view1.addView(layoutInflater.inflate(R.layout.item_card_back, view, false), 1,new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));



        view2 = (EasyFlipView) layoutInflater.inflate(R.layout.item_imageflip, linearLayout, false);
        view2.removeAllViewsInLayout();
        PhotoView imageView2 = new PhotoView(this);
        imageView2.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,FrameLayout.LayoutParams.MATCH_PARENT));
        imageView2.setImageResource(R.drawable.ndg4);
//        imageView2.setScaleType(ImageView.ScaleType.FIT_CENTER);
//        imageView2.setAdjustViewBounds(true);
        view2.addView(imageView2, 0, imageView2.getLayoutParams());
        view2.addView(layoutInflater.inflate(R.layout.item_card_back, view, false), 1,new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));

        PhotoView photoView = new PhotoView(this);

        View viewTop = findViewById(R.id.imageflipactivity_topview);


        linearLayout.addView(view);
        linearLayout.addView(view1);
        linearLayout.addView(view2);

        viewTop.setOnClickListener(new View.OnClickListener() {
            int counter = 0;
            @Override
            public void onClick(View v) {
                if(counter==0){
                    view.flipTheView();
                    counter++;
                }else if (counter == 1){
                    view1.flipTheView();
                    counter++;
                }else{
                    view2.flipTheView();
                    counter = 0;
                }
            }
        });

//        linearLayout.setOnClickListener(new View.OnClickListener() {
//            int counter = 0;
//            @Override
//            public void onClick(View v) {
//                if(counter==0){
//                    view.flipTheView();
//                    counter++;
//                }else if (counter == 1){
//                    view1.flipTheView();
//                    counter++;
//                }else{
//                    view2.flipTheView();
//                    counter = 0;
//                }
//            }
//        });

//        EasyFlipView easyFlipView = new EasyFlipView(this);
//        ImageView imageView = new ImageView(this);
//        imageView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,FrameLayout.LayoutParams.MATCH_PARENT));
//        imageView.setImageResource(R.drawable.ndg1);
//        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
//        imageView.setAdjustViewBounds(true);
//        easyFlipView.addView(imageView);
//        ImageView imageView2 = new ImageView(this);
//        imageView2.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,FrameLayout.LayoutParams.MATCH_PARENT));
//        imageView2.setImageResource(R.drawable.ndg3);
//        imageView2.setScaleType(ImageView.ScaleType.FIT_CENTER);
//        imageView2.setAdjustViewBounds(true);
//        easyFlipView.addView(imageView);
//        easyFlipView.s
//        linearLayout.addView(easyFlipView);

    }
    private class MyEasyFlipView extends EasyFlipView{

        public MyEasyFlipView(Context context) {
            super(context);
        }

        public MyEasyFlipView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }
    }
}
