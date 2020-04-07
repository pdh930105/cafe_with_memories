package com.example.myfirstapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.myfirstapp.model.UserModel;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {
    private static final int PICK_FROM_ALBUM = 10;
    private EditText editTextEmail;
    private EditText editTextName;
    private EditText editTextPassword;
    private Button buttonSignup;
    private ConstraintLayout constraintLayout;
    private FirebaseRemoteConfig mFirebaseRemoteConfig;
    private FirebaseAuth mAuth;
    private ImageView imageViewProfile;
    private Uri uriImage;
    private FirebaseStorage firebaseStorage;
    private DocumentReference admin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        constraintLayout = (ConstraintLayout) findViewById(R.id.minigoldactivity_constraintlayout);
        String minigold_background = mFirebaseRemoteConfig.getString("minigold_background");
        constraintLayout = (ConstraintLayout)findViewById(R.id.signupActivity_constraintlayout);
        constraintLayout.setBackgroundColor(Color.parseColor(minigold_background));
        editTextEmail= (EditText)findViewById(R.id.signupActivity_edittext_email);
        editTextName = (EditText)findViewById(R.id.signupActivity_edittext_name);
        editTextPassword = (EditText)findViewById(R.id.signupActivity_edittext_password);
        buttonSignup = (Button)findViewById(R.id.signupActivity_edittext_button_signup);


        buttonSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(editTextEmail.getText().toString() == null || editTextName.getText().toString() == null||editTextPassword.getText().toString() == null){
                    Toast.makeText(SignupActivity.this, "모든 정보를 입력해 주세요.",
                            Toast.LENGTH_SHORT).show();
                }else{
                    FirebaseAuth.getInstance()
                            .createUserWithEmailAndPassword(editTextEmail.getText().toString(), editTextPassword.getText().toString())
                            .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        // Sign in success, update UI with the signed-in user's information
                                        final String uid = task.getResult().getUser().getUid();
                                        UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(editTextName.getText().toString()).build();
                                        task.getResult().getUser().updateProfile(userProfileChangeRequest);
                                        //uid는 고유번호입니다.

                                        UserModel userModel = new UserModel();
                                        userModel.userName = editTextName.getText().toString();
                                        //처음 가입자 레벨은 1
                                        userModel.level = 1;
                                        userModel.uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                                        FirebaseDatabase.getInstance().getReference().child("users").child(uid).setValue(userModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(SignupActivity.this, "회원가입에 성공했습니다.",
                                                        Toast.LENGTH_SHORT).show();


                                                CollectionReference adminDB = FirebaseFirestore.getInstance().collection("adminDB");
                                                admin = adminDB.document("admin");

                                                final String my_uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                                final String my_name = editTextName.getText().toString();
                                                // Create a new user with a first and last name


                                                admin.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                                                        ArrayList<String> adminIDData = (ArrayList<String>) documentSnapshot.get("adminIDList");
                                                        ArrayList<String> activateChatRoomList = (ArrayList<String>)documentSnapshot.get("activateChatRoomList");
                                                        Log.d("adminIDData", "adminIdData :" + adminIDData.toString());
                                                        String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();//사용자 Uid
                                                        String userName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
                                                        Log.d("AuthData", "ID: " + myUid);
                                                        Log.d("AuthData", "userName: " + userName);
                                                        Boolean admin_trigger = false;
                                                        Log.d("before", adminIDData.toString() );
                                                        for (String adminIdData : adminIDData) {
                                                            Log.d("for", "input User ID : " + adminIdData);
                                                            Log.d("for", "userName : " + userName);
                                                            if (adminIdData.equals(userName)) {
                                                                admin_trigger= true;
                                                                Log.d("equalTrigger", "adminTrigger" + admin_trigger.toString());
                                                                break;
                                                            }
                                                        }

                                                        if (admin_trigger==false) {
                                                            Log.d("false_trigger", "adminTrigger" + admin_trigger.toString());
                                                            Map<String, Object> user = new HashMap<>();
                                                            user.put("chatRoom", my_uid);
                                                            user.put("userID", my_name);
                                                            user.put("level", 1);
                                                            admin.update("userIDList", FieldValue.arrayUnion(user));

                                                            //admin 관리하는 document (adminDB/admin)
                                                            DocumentReference docs_admin;
                                                            docs_admin = FirebaseFirestore.getInstance().collection("adminDB").document("admin");//

                                                            //chatRoom# 에 대한 document(chatDB/docs)
                                                            DocumentReference docs_chatRoom;
                                                            //docs_chatRoom에 저장되어있는 채팅 Document에 대한 collection
                                                            CollectionReference coll_chatList;

                                                            docs_admin.update("activateChatRoomList", FieldValue.arrayUnion(my_uid));//
                                                            docs_chatRoom = FirebaseFirestore.getInstance().collection("chatDB").document(my_uid);//

                                                            coll_chatList = docs_chatRoom.collection("chatList");
                                                            Map<String, Object> lastChatTime = new HashMap<>();
                                                            lastChatTime.put("lastChatTimestamp", Timestamp.now());
                                                            lastChatTime.put("lastChat", "방생성");
                                                            lastChatTime.put("chatRoom", my_uid);
                                                            lastChatTime.put("userId", my_name);
                                                            docs_chatRoom.set(lastChatTime, SetOptions.merge());
                                                        }

                                                    }

                                                });
                                                finish();
                                            }
                                        });



                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Toast.makeText(SignupActivity.this, "Authentication failed.",
                                                Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });
                }
            }
        });
    }
}
