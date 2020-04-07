package com.example.myfirstapp;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.myfirstapp.model.ChatModel;
import com.example.myfirstapp.model.ChatVO;
import com.example.myfirstapp.model.UserModel;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;



import com.google.firebase.firestore.DocumentChange;

public class ClientChatActivity extends AppCompatActivity {
    private ImageButton imageButton_home;
    private RecyclerView recyclerView;
    private EditText editText_message;
    private Button button_send;
    private String myUid;
    private String userName;
    private UserModel destinationUserModel;
    //private String destinationUid;
    private DatabaseReference databaseReference;
    private ValueEventListener valueEventListener;
    private String chatRoomUid;


    //chatRoom# 에 대한 document(chatDB/docs)
    DocumentReference docs_chatRoom;
    //docs_chatRoom에 저장되어있는 채팅 Document에 대한 collection
    CollectionReference coll_chatList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_chat);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        imageButton_home = findViewById(R.id.client_chatactivity_imageButton_home);
        recyclerView = findViewById(R.id.client_chatactivity_recyclerview);
        editText_message = findViewById(R.id.client_chatactivity_editText);
        button_send = findViewById(R.id.mclient_chatactivity_button);

        imageButton_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ClientChatActivity.this, StartActivity.class);
                //액티비티 스택에 쌓인 중간 액티비티 삭제.
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                //스택에 남아있는 액티비티라면, 재사용한다.
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            }
        });

        myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();//사용자 Uid
        //destinationUid = getIntent().getStringExtra("destinationUid");//채팅의 대상 UID
        userName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        docs_chatRoom = FirebaseFirestore.getInstance().collection("chatDB").document(myUid);
        coll_chatList = docs_chatRoom.collection("chatList");


        final ArrayList<ChatVO> chatList = new ArrayList<>();



        button_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChatModel chatModel = new ChatModel();
                chatModel.users.put(myUid, true);
                //chatModel.users.put(destinationUid, true);

                String text = editText_message.getText().toString();

                if(text.length() != 0 ){
                    //editText_message.setText("");
                    ChatVO chatVO = new ChatVO(userName, text, Timestamp.now());
                    coll_chatList.add(chatVO.getHashMap());
                    editText_message.setText("");
                }

            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(ClientChatActivity.this));

        final RecyclerViewAdapter RVAdapter = new RecyclerViewAdapter(chatList,myUid);
        recyclerView.setAdapter(RVAdapter);

        coll_chatList.addSnapshotListener(new EventListener<QuerySnapshot>() {
            String temp;
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException e) {
                if(e !=null){
                    return;
                }
                for(DocumentChange dc : value.getDocumentChanges()){
                    switch(dc.getType()){
                        //fireStore에 새로운 메세지가 등장했을 때 불러오는 명령어(내가 보내거나, 상대가 보낼 때 세팅)
                        case ADDED:
                            final ChatVO chatVO = dc.getDocument().toObject(ChatVO.class);
                            chatList.add(chatVO);
                            Collections.sort(chatList, new Comparator<ChatVO>() {
                                @Override
                                public int compare(ChatVO chatVO, ChatVO t1) {
                                    return chatVO.getTimestamp().compareTo(t1.getTimestamp());
                                }
                            });

                            Map<String, Object>updateLastChat = new HashMap<>();
                            updateLastChat.put("userId", chatVO.getuserId());
                            updateLastChat.put("lastChatTimestamp", chatVO.getTimestamp());
                            updateLastChat.put("lastChat", chatVO.getText());
                            updateLastChat.put("chatRoom", myUid);
                            docs_chatRoom.set(updateLastChat, SetOptions.merge());

                            break;
                        case MODIFIED:
                            //수정이나 삭제에 대한 데이터
                            break;
                        case REMOVED:
                            break;
                    }

                }
                RVAdapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(chatList.size()-1);
            }
        });

        //checkChatRoom();

    }

    /*
     * 리사이클러뷰 구현
     * */
    class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
        private static final int VIEW_TYPE_MESSAGE_SENT = 1;
        private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;
        //List<ChatModel.Comment> commentList;


        private ArrayList<ChatVO> chatVOArrayList = null;
        private String chatSendId;//이걸로 내가 접속한 채팅방이
        RecyclerViewAdapter(ArrayList<ChatVO> list, String id){//생성자
            chatVOArrayList = list;
            chatSendId = id;

        }

        @Override
        public int getItemViewType(int position) {
            //ChatModel.Comment comment = commentList.get(position);

            ChatVO chatVO = chatVOArrayList.get(position);
            if(chatVO.getuserId().equals(userName)){//내가 쓴 챗
                // If the current user is the sender of the message
                return VIEW_TYPE_MESSAGE_SENT;
            }else {//상대가 쓴 챗
                // If some other user sent the message
                return VIEW_TYPE_MESSAGE_RECEIVED;
            }
            //return super.getItemViewType(position);
        }


        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view;
            if (viewType == VIEW_TYPE_MESSAGE_SENT) {
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_message_sent, parent, false);
                return new ClientChatActivity.RecyclerViewAdapter.SentMessageHolder(view);
            } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_message_received, parent, false);
                return new ClientChatActivity.RecyclerViewAdapter.ReceivedMessageHolder(view);
            }

            return null;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
//            MessageViewHolder messageViewHolder = ((MessageViewHolder)holder);
            //ChatModel.Comment comment = commentList.get(position);
            ChatVO chatVO = chatVOArrayList.get(position);
            //Toast.makeText(ClientChatActivity.this, "onBindViewHolder", Toast.LENGTH_SHORT).show();
            switch (holder.getItemViewType()){
                case VIEW_TYPE_MESSAGE_SENT:
                    ((ClientChatActivity.RecyclerViewAdapter.SentMessageHolder)holder).bind(chatVO);
                    break;
                case VIEW_TYPE_MESSAGE_RECEIVED:
                    ((ClientChatActivity.RecyclerViewAdapter.ReceivedMessageHolder)holder).bind(chatVO);
            }
        }

        @Override
        public int getItemCount() {
            return chatVOArrayList.size();
        }


        private class SentMessageHolder extends RecyclerView.ViewHolder {
            TextView messageText, timeText;

            SentMessageHolder(View itemView) {
                super(itemView);
                messageText = (TextView) itemView.findViewById(R.id.text_message_body);
                timeText = (TextView) itemView.findViewById(R.id.text_message_time);
            }

            void bind(ChatVO chatVO) {
                messageText.setText(chatVO.getText());

                // Format the stored timestamp into a readable String using method.
                timeText.setText(chatVO.getTimeView());
//                timeText.setText(Utils.formatDateTime(message.getCreatedAt()));
            }
        }

        private class ReceivedMessageHolder extends RecyclerView.ViewHolder {
            TextView messageText, timeText, nameText;
            ImageView profileImage;
            View view;

            ReceivedMessageHolder(View itemView) {
                super(itemView);
                view = itemView;
                messageText = (TextView) itemView.findViewById(R.id.text_message_body);
                timeText = (TextView) itemView.findViewById(R.id.text_message_time);
                nameText = (TextView) itemView.findViewById(R.id.text_message_name);
                profileImage = (ImageView) itemView.findViewById(R.id.image_message_profile);
            }

            void bind(ChatVO chatVO) {
                messageText.setText(chatVO.getText());

//                // Format the stored timestamp into a readable String using method.
                timeText.setText(chatVO.getTimeView());

                nameText.setText(chatVO.getuserId());

                Glide.with(view.getContext()).load(R.drawable.baseline_account_circle_black_36).apply(new RequestOptions().circleCrop()).into(profileImage);
            }
        }

    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        if(valueEventListener !=null){
            databaseReference.removeEventListener(valueEventListener);
        }
        finish();

    }
}
