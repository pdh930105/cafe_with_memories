package com.example.myfirstapp;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myfirstapp.fragment.AdminManageChatFragment;
import com.example.myfirstapp.model.AdminChatVO;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;


public class AdminChatActivity extends AppCompatActivity {

    String adminId;
    FirebaseFirestore db;
    CollectionReference chatDB;
    CollectionReference userDB;
    HashMap<String, AdminChatVO> hashMap;

    AdminManageChatFragment adminManageChatFragment;
    private FragmentTransaction transaction;
    private FragmentManager fragmentManager;

    static final String TAG = "adminActivity";


    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "Start AdminChatActivity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_chat);

        Intent intent = getIntent();
        adminId = intent.getStringExtra("id");
        hashMap = new HashMap<>();

        fragmentManager = getSupportFragmentManager();
        transaction = fragmentManager.beginTransaction();


        db = FirebaseFirestore.getInstance();
        final DocumentReference admin = db.collection("adminDB").document("admin");
        Log.d(TAG, "Doc admin check" + admin.get().toString());

        chatDB = db.collection("chatDB");
        userDB = db.collection("userDB");


        final ArrayList<AdminChatVO> adminManageList = new ArrayList<>();
        RecyclerView recyclerView = findViewById(R.id.AdminChatRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        final AdminChatAdapter chatAdapter = new AdminChatAdapter(adminManageList);
        recyclerView.setAdapter(chatAdapter);

        //버튼 클릭했을 때 삭제, 입장을 해야하는 이벤트를 지정하는 코드
        chatAdapter.setOnItemClickListener(new AdminChatAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int pos) {
                if (v.getId() == R.id.insertBtn) {
                    AdminChatVO adminChatVO = adminManageList.get(pos);

                    Bundle bundle = new Bundle();
                    bundle.putString("userName", adminChatVO.getUserId());
                    bundle.putString("roomId", adminChatVO.getChatRoom());
                    bundle.putString("adminId", adminId);
                    adminManageChatFragment = new AdminManageChatFragment();
                    adminManageChatFragment.setArguments(bundle);

                    fragmentManager.beginTransaction().replace(R.id.frame2Layout, adminManageChatFragment).commit();

                }

                //remove 삭제 구현 | Collection이 일괄 삭제가 되지 않기에 이렇게 도큐먼트를 하나하나 접근하면서 삭제하게 함.
                else if (v.getId() == R.id.resetBtn){
                    final AdminChatVO adminChatVO = adminManageList.get(pos);
                    String deleteChatRoom = adminChatVO.getChatRoom();
                    final CollectionReference deleteChatList=chatDB.document(deleteChatRoom).collection("chatList");
                    deleteChatList.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()){
                                for(QueryDocumentSnapshot dc : task.getResult()){
                                    String id = dc.getId();
                                    deleteChatList.document(id).delete();
                                }
                            }
                            userDB.document(adminChatVO.getUserId()).update("playTrigger", false);
                        }
                    });

                    adminManageList.remove(pos);
                    chatAdapter.notifyDataSetChanged();
                }
            }
        });

        ///아래 지울 것

        admin.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                Log.d(TAG, "onComplete: task start");
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    Log.d(TAG, "Document Description \n" + document.getData());

                    userDB.addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException e) {
                            if(e !=null){
                                Log.w(TAG, "Listen Failed", e);
                                return;
                            }
                            for(DocumentChange dc : value.getDocumentChanges()){
                                switch(dc.getType()){
                                    //fireStore에 새로운 메세지가 등장했을 때 불러오는 명령어(내가 보내거나, 상대가 보낼 때 세팅)
                                    case ADDED:
                                        final AdminChatVO add_chatVO = dc.getDocument().toObject(AdminChatVO.class);
                                        Log.d(TAG, "add_chatVO.getChatRoom : " + add_chatVO.getChatRoom());
                                            hashMap.put(add_chatVO.getChatRoom(), add_chatVO);
                                        break;

                                    case MODIFIED:
                                        //수정이나 삭제에 대한 데이터 권한은 웹에서 하던가 하는게 나을듯?
                                        final AdminChatVO modi_chatVO = dc.getDocument().toObject(AdminChatVO.class);
                                        hashMap.put(modi_chatVO.getChatRoom(), modi_chatVO);

                                        Log.d(TAG, "Server Changed Data : " + dc.getDocument().getData());
                                        break;
                                    case REMOVED:
                                        Log.d(TAG, "Server Removed Data" + dc.getDocument().getData());
                                        break;
                                }
                            }
                            adminManageList.clear();
                            adminManageList.addAll(hashMap.values());
                            Collections.sort(adminManageList, new Comparator<AdminChatVO>() {
                                @Override
                                public int compare(AdminChatVO o1, AdminChatVO o2) {
                                    return o1.getUserId().compareTo(o2.getUserId());
                                }
                            });
                            chatAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        });

    }

}
