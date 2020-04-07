package com.example.myfirstapp;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class TestActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    private Button buttonSignOut;
    private Button buttonPage;
    private Button buttonOpenFile;

    private static final int READ_REQUEST_CODE = 42;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        buttonSignOut = findViewById(R.id.button_signout);
        buttonSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(TestActivity.this, LoginActivity.class));
                finish();
            }
        });

        buttonPage = findViewById(R.id.button_pageflip);
        buttonPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TestActivity.this, PageflipActivity.class));
                finish();
            }
        });

        Button buttonImageFlip = findViewById(R.id.button_imageflip);
        buttonImageFlip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TestActivity.this, ImageflipActivity.class));
                finish();
            }
        });

        buttonOpenFile = findViewById(R.id.button_openfile);
        buttonOpenFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(TestActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    // Permission is not granted
                    if(ActivityCompat.shouldShowRequestPermissionRationale(TestActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)){

                        Snackbar.make(v, "권한을 주지않으면 파일을 불러올 수 없습니다.", Snackbar.LENGTH_LONG).setAction("확인", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                finish();
                            }
                        }).show();
                        ActivityCompat.requestPermissions(TestActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                    }else{
                        //권한을 처음 묻는 경우
                        ActivityCompat.requestPermissions(TestActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                    }
                }else{
                    //권한이 있는경우
                    startFilePicker();
                }
            }
        });

        Button buttonReadDir = findViewById(R.id.button_read_directory);
        buttonReadDir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File dir = new File(getFilesDir().toString(), getString(R.string.story_path));

                System.out.println(getFilesDir().toString());
                System.out.println(getFilesDir().getPath());
                System.out.println(dir.toString());
                System.out.println(dir.getPath());

                File lister = dir.getAbsoluteFile();
                for (String list : lister.list()){
                    System.out.println(list);
                }


                AppDataManager.getInstance().storyLoader();

            }
        });

        Button buttonDeleteFile = findViewById(R.id.button_delete_files);
        buttonDeleteFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File dir = new File(getFilesDir().toString(), getString(R.string.story_path));
                System.out.println(dir.getPath());

                File lister = dir.getAbsoluteFile();
                System.out.println(lister.getPath());

                for (File file : lister.listFiles()){
                    System.out.println(file.getName());
                    file.delete();
                }
                AppDataManager.getInstance().storyLoader();
            }
        });

        Button buttonAnyTest = findViewById(R.id.button_any_test);
        buttonAnyTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();

                db.collection("adminDB").document("admin").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        List<String> strings = (List<String>) documentSnapshot.get("adminIDList");

                        for(String a:strings){
                                System.out.println(a);

                        }
                    }
                });




//                // Create a new user with a first and last name
//                Map<String, Object> user = new HashMap<>();
//                user.put("first", "Ada");
//                user.put("last", "Lovelace");
//                user.put("born", 1815);
//
//// Add a new document with a generated ID
//                db.collection("users")
//                        .add(user)
//                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//                            @Override
//                            public void onSuccess(DocumentReference documentReference) {
//                                Log.d("test", "DocumentSnapshot added with ID: " + documentReference.getId());
//                            }
//                        })
//                        .addOnFailureListener(new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull Exception e) {
//                                Log.w("test", "Error adding document", e);
//                            }
//                        });
            }
        });

    }



    public void startFilePicker(){
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        startActivityForResult(intent, READ_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:{
                if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    //권한 획득
                    startFilePicker();
                }
                break;
            }
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;

        }
//
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK){
            if(data != null){
                Uri uri = data.getData();
                TextView textView = findViewById(R.id.textView_out);
                textView.setText(uri.getPath());

                try {
                    unZip(uri, getString(R.string.story_path));
                }catch (IOException e){
                    System.out.println(e.getMessage());
                }
            }
        }
        else{
            super.onActivityResult(requestCode,resultCode,data);
        }
    }

    private void unZip(Uri zipUri, String _targetLocation) throws IOException{
        //만약 폴더가 없다면 만들어준다.
        File folder = new File(getFilesDir().toString(), _targetLocation);
        dirChecker(folder);


        InputStream in = getContentResolver().openInputStream(zipUri);
        BufferedInputStream bufferedInputStream= new BufferedInputStream(in);
        ZipInputStream inputStream = new ZipInputStream(bufferedInputStream);

        try {
            ZipEntry zipEntry;
            while ((zipEntry = inputStream.getNextEntry()) != null){
                System.out.printf("File: %s Size %d Modified on %TD %n", zipEntry.getName(), zipEntry.getSize(), new Date(zipEntry.getTime()));
                extractEntry(zipEntry, inputStream, folder);
            }
        }finally {
            inputStream.close();
        }



    }


    private static void extractEntry(final ZipEntry entry, InputStream is, File dir) throws IOException {
        File fileWithDir = new File(dir, entry.getName());
        FileOutputStream fos = null;

        try {
            fos = new FileOutputStream(fileWithDir);
            final byte[] buf = new byte[8192];
            int read = 0;
            int length;
            while ((length = is.read(buf, 0, buf.length)) >= 0) {
                fos.write(buf, 0, length);
            }
        } catch (IOException ioex) {
            fos.close();
        }
    }


    public void dirChecker(File file){
        if(file.exists()){

        }else{
            file.mkdir();
        }
    }
}
