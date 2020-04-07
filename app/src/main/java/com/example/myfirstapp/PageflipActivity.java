package com.example.myfirstapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;



import android.app.Activity;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build;
import android.os.Bundle;

import android.preference.PreferenceManager;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.GestureDetector;

import android.view.GestureDetector.OnGestureListener;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.myfirstapp.Pageflip.Constants;
import com.example.myfirstapp.Pageflip.LoadBitmapTask;
import com.example.myfirstapp.Pageflip.PageFlipView;
import com.example.myfirstapp.R;

/**
 * Sample Activity
 *
 * @author eschao
 */
public class PageflipActivity extends Activity implements OnGestureListener {

    PageFlipView mPageFlipView;
    GestureDetector mGestureDetector;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = LayoutInflater.from(this).inflate(R.layout.activity_pageflip, null);
        LinearLayout linearLayout = view.findViewById(R.id.linearLayout_for_test);

        mPageFlipView = new PageFlipView(this);
        linearLayout.addView(mPageFlipView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        setContentView(view);
//        setContentView(R.layout.activity_pageflip);

//        addContentView();
        mGestureDetector = new GestureDetector(this, this);

        if (Build.VERSION.SDK_INT < 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            mPageFlipView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_FULLSCREEN |
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                            View.SYSTEM_UI_FLAG_IMMERSIVE |
                            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }

//        LinearLayout linearLayout = findViewById(R.id.linearLayout_for_test);
//        linearLayout.addView(mPageFlipView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
//        mPageFlipView.setFocusableInTouchMode(true);

    }

    @Override
    protected void onResume() {
        super.onResume();

        LoadBitmapTask.get(this).start();
        mPageFlipView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();

        mPageFlipView.onPause();
        LoadBitmapTask.get(this).stop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.optionmenus, menu);

        int duration = mPageFlipView.getAnimateDuration();
        if (duration == 1000) {
            menu.findItem(R.id.animation_1s).setChecked(true);
        }
        else if (duration == 2000) {
            menu.findItem(R.id.animation_2s).setChecked(true);
        }
        else if (duration == 5000) {
            menu.findItem(R.id.animation_5s).setChecked(true);
        }

        if (mPageFlipView.isAutoPageEnabled()) {
            menu.findItem(R.id.auoto_page).setChecked(true);
        }
        else {
            menu.findItem(R.id.single_page).setChecked(true);
        }

        SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(this);
        int pixels = pref.getInt("MeshPixels", mPageFlipView.getPixelsOfMesh());
        switch (pixels) {
            case 2:
                menu.findItem(R.id.mesh_2p).setChecked(true);
                break;
            case 5:
                menu.findItem(R.id.mesh_5p).setChecked(true);
                break;
            case 10:
                menu.findItem(R.id.mesh_10p).setChecked(true);
                break;
            case 20:
                menu.findItem(R.id.mesh_20p).setChecked(true);
                break;
            default:
                break;
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean isHandled = true;
        SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(this);
        Editor editor = pref.edit();
        switch (item.getItemId()) {
            case R.id.animation_1s:
                mPageFlipView.setAnimateDuration(1000);
                editor.putInt(Constants.PREF_DURATION, 1000);
                break;
            case R.id.animation_2s:
                mPageFlipView.setAnimateDuration(2000);
                editor.putInt(Constants.PREF_DURATION, 2000);
                break;
            case R.id.animation_5s:
                mPageFlipView.setAnimateDuration(5000);
                editor.putInt(Constants.PREF_DURATION, 5000);
                break;
            case R.id.auoto_page:
                mPageFlipView.enableAutoPage(true);
                editor.putBoolean(Constants.PREF_PAGE_MODE, true);
                break;
            case R.id.single_page:
                mPageFlipView.enableAutoPage(false);
                editor.putBoolean(Constants.PREF_PAGE_MODE, false);
                break;
            case R.id.mesh_2p:
                editor.putInt(Constants.PREF_MESH_PIXELS, 2);
                break;
            case R.id.mesh_5p:
                editor.putInt(Constants.PREF_MESH_PIXELS, 5);
                break;
            case R.id.mesh_10p:
                editor.putInt(Constants.PREF_MESH_PIXELS, 10);
                break;
            case R.id.mesh_20p:
                editor.putInt(Constants.PREF_MESH_PIXELS, 20);
                break;
            case R.id.about_menu:
                showAbout();
                return true;
            default:
                isHandled = false;
                break;
        }

        if (isHandled) {
            item.setChecked(true);
            editor.apply();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            mPageFlipView.onFingerUp(event.getX(), event.getY());
            return true;
        }

        return mGestureDetector.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        mPageFlipView.onFingerDown(e.getX(), e.getY());
        return true;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                           float velocityY) {
        return false;
    }


    @Override
    public void onLongPress(MotionEvent e) {
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
                            float distanceY) {
        mPageFlipView.onFingerMove(e2.getX(), e2.getY());
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {
    }

    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    private void showAbout() {
        View aboutView = getLayoutInflater().inflate(R.layout.activity_pageflip, null,
                false);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setTitle(R.string.app_name);
        builder.setView(aboutView);
        builder.create();
        builder.show();
    }
}



//public class PageflipActivity extends AppCompatActivity implements GestureDetector.OnGestureListener {
//
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_pageflip);
//
//
//
//    }
//
//    @Override
//    public boolean onDown(MotionEvent e) {
//        return false;
//    }
//
//    @Override
//    public void onShowPress(MotionEvent e) {
//
//    }
//
//    @Override
//    public boolean onSingleTapUp(MotionEvent e) {
//        return false;
//    }
//
//    @Override
//    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
//        return false;
//    }
//
//    @Override
//    public void onLongPress(MotionEvent e) {
//
//    }
//
//    @Override
//    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
//        return false;
//    }
//
//
//    public class SurfaceView extends GLSurfaceView{
//        public SurfaceView(Context context) {
//            super(context);
//        }
//
//        public SurfaceView(Context context, AttributeSet attrs) {
//            super(context, attrs);
//        }
//    }
//    class MyRenderer implements GLSurfaceView.Renderer{
//
//        @Override
//        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
//
//        }
//
//        @Override
//        public void onSurfaceChanged(GL10 gl, int width, int height) {
//
//        }
//
//        @Override
//        public void onDrawFrame(GL10 gl) {
//
//        }
//    }
//}
