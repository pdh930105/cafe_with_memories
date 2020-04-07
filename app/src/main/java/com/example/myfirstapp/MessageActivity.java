package com.example.myfirstapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.myfirstapp.model.ChatModel;
import com.example.myfirstapp.model.NotificationModel;
import com.example.myfirstapp.model.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MessageActivity extends AppCompatActivity {

    private String destinationUid;
    private Button button;
    private EditText editText;

    private String uid;
    private String chatRoomUid;

    private RecyclerView recyclerView;
    private SimpleDateFormat simleDateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm");
    private  UserModel destinationUserModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();//사용자 Uid
        destinationUid = getIntent().getStringExtra("destinationUid");//채팅의 대상 UID
        button = findViewById(R.id.messageactivity_button);
        editText = findViewById(R.id.messageactivity_editText);

        recyclerView = findViewById(R.id.messageactivity_recyclerview);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChatModel chatModel = new ChatModel();
                chatModel.users.put(uid, true);
                chatModel.users.put(destinationUid, true);

                if(chatRoomUid == null){//null인 경우 생성
                    button.setEnabled(false);
                    FirebaseDatabase.getInstance().getReference().child("chatrooms").push().setValue(chatModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            checkChatRoom();
                        }
                    });
                }else{
                    ChatModel.Comment comment = new ChatModel.Comment();
                    comment.uid = uid;
                    comment.message = editText.getText().toString();
                    comment.timestamp = ServerValue.TIMESTAMP;
                    FirebaseDatabase.getInstance().getReference().child("chatrooms").child(chatRoomUid).child("commentList").push().setValue(comment).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            //sendGcm();
                            editText.setText("");
                        }
                    });
                }

            }
        });
        checkChatRoom();
    }
    void sendGcm(){
        Gson gson = new Gson();
        String userName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        NotificationModel notificationModel = new NotificationModel();
        notificationModel.to = destinationUserModel.pushToken;
        notificationModel.notification.title = userName;
        notificationModel.notification.text = editText.getText().toString();
        notificationModel.data.title = userName;
        notificationModel.data.text = editText.getText().toString();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf8"), gson.toJson(notificationModel));
        Request request = new Request.Builder()
                .header("Content-Type", "application/json")
                .addHeader("Authorization", "key=AAAAjy-dQ9Y:APA91bEQVxd4tOQDK6ZXmKg9l861ZhEmncAoU2Nj7k2q7pqo7GIKZLYaI9jUxhg_EaXJiN_BeONUYFHOL7vaA_nhrZo60MzJnRIugJ1OFH-RWfOOWoP6VQIPnmV01SPJGpbFsyvJG1T3")
                .url("https://fcm.googleapis.com/fcm/send")
                .post(requestBody)
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

            }
        });
    }
    //중복을 체크하는 코드
    void checkChatRoom(){
        FirebaseDatabase.getInstance().getReference().child("chatrooms").orderByChild("users/"+uid).equalTo(true).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot item : dataSnapshot.getChildren()){
                    ChatModel chatModel = item.getValue(ChatModel.class);//받아옴
                    if(chatModel.users.containsKey(destinationUid)){
                        chatRoomUid = item.getKey();
                        button.setEnabled(true);
                        recyclerView.setLayoutManager(new LinearLayoutManager(MessageActivity.this));
                        recyclerView.setAdapter(new RecyclerViewAdapter());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////
    class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
        private static final int VIEW_TYPE_MESSAGE_SENT = 1;
        private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;
        List<ChatModel.Comment> commentList;

        public RecyclerViewAdapter() {
            commentList = new ArrayList<>();
            FirebaseDatabase.getInstance().getReference().child("users").child(destinationUid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    //유저 정보를 불러오고
                    destinationUserModel = dataSnapshot.getValue(UserModel.class);
                    //메시지 리스트를 받아옴
                    getMessageList();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }

        @Override
        public int getItemViewType(int position) {
            ChatModel.Comment comment = commentList.get(position);

            if(comment.uid.equals(uid)){//내가 쓴 챗
                // If the current user is the sender of the message
                return VIEW_TYPE_MESSAGE_SENT;
            }else {//상대가 쓴 챗
                // If some other user sent the message
                return VIEW_TYPE_MESSAGE_RECEIVED;
            }
            //return super.getItemViewType(position);
        }

        void getMessageList(){
            FirebaseDatabase.getInstance().getReference().child("chatrooms").child(chatRoomUid).child("commentList").addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    commentList.clear();
                    for (DataSnapshot item: dataSnapshot.getChildren()){
                        commentList.add(item.getValue(ChatModel.Comment.class));
                    }
                         notifyDataSetChanged();
                        recyclerView.scrollToPosition(commentList.size()-1);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);
//            return new MessageViewHolder(view);

            View view;
            if (viewType == VIEW_TYPE_MESSAGE_SENT) {
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_message_sent, parent, false);
                return new SentMessageHolder(view);
            } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_message_received, parent, false);
                return new ReceivedMessageHolder(view);
            }

            return null;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
//            MessageViewHolder messageViewHolder = ((MessageViewHolder)holder);
            ChatModel.Comment comment = commentList.get(position);
            switch (holder.getItemViewType()){
                case VIEW_TYPE_MESSAGE_SENT:
                    ((SentMessageHolder)holder).bind(comment);
                    break;
                case VIEW_TYPE_MESSAGE_RECEIVED:
                    ((ReceivedMessageHolder)holder).bind(comment);
            }
//            destinationUserModel.userName
//            if(commentList.get(position).uid.equals(uid)){//내가 쓴 챗
//                messageViewHolder.textView_message.setText(commentList.get(position).message);
//            }else {//상대가 쓴 챗
//            }
        }

        @Override
        public int getItemCount() {
            return commentList.size();
        }

//        //뷰를 재사용할때 쓰는 이너 클래스
//        private class MessageViewHolder extends RecyclerView.ViewHolder {
//            public TextView textView_message;
//            public TextView textView_name;
//            public ImageView imageView_profile;
//            public ConstraintLayout constraintLayout_destination;
//            public MessageViewHolder(View view) {
//                super(view);
//                textView_message = view.findViewById(R.id.text_message_body);
//
//                textView_name = view.findViewById(R.id.text_message_name);
//                imageView_profile = view.findViewById(R.id.image_message_profile);
//                constraintLayout_destination = view.findViewById(R.id.item_message_constraintlayout);
//
//            }
//        }

        private class SentMessageHolder extends RecyclerView.ViewHolder {
            TextView messageText, timeText;

            SentMessageHolder(View itemView) {
                super(itemView);

                messageText = (TextView) itemView.findViewById(R.id.text_message_body);
                timeText = (TextView) itemView.findViewById(R.id.text_message_time);
            }

            void bind(ChatModel.Comment message) {
                messageText.setText(message.getMessage());

                // Format the stored timestamp into a readable String using method.
                long unixTime = (long)message.timestamp;
                Date date = new Date(unixTime);
                simleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
                timeText.setText(simleDateFormat.format(date));
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

            void bind(ChatModel.Comment message) {
                messageText.setText(message.getMessage());

//                // Format the stored timestamp into a readable String using method.
                long unixTime = (long)message.timestamp;
                Date date = new Date(unixTime);
                simleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
                timeText.setText(simleDateFormat.format(date));

                nameText.setText(destinationUserModel.userName);

//                nameText.setText(message.getUid().getNickname());

                // Insert the profile image from the URL into the ImageView.
                //추후 레벨이나 타입에 따라 이미지 만들어 주기
//                Utils.displayRoundImageFromUrl(mContext, message.getSender().getProfileUrl(), profileImage);
//                Glide.with(itemView.getContext()).load(destinationUserModel.uid).apply(new RequestOptions().circleCrop()).into(profileImage);
                Glide.with(view.getContext()).load(R.drawable.baseline_account_circle_black_36).apply(new RequestOptions().circleCrop()).into(profileImage);
            }
        }
    }

    @Override
    public void onBackPressed() {
        finish();

    }
}
