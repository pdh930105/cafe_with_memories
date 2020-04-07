package com.example.myfirstapp.fragment;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myfirstapp.MessageActivity;
import com.example.myfirstapp.R;
import com.example.myfirstapp.model.ChatModel;
import com.example.myfirstapp.model.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;

public class ChatFragment extends Fragment {
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.mm.dd hh:mm");
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        RecyclerView recyclerView = (RecyclerView)view.findViewById(R.id.chatfragment_recyclerview);
        recyclerView.setAdapter(new ChatRecyclerViewApter());
        recyclerView.setLayoutManager(new LinearLayoutManager(inflater.getContext()));

        return view;

    }

    class ChatRecyclerViewApter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private List<ChatModel> chatModelList = new ArrayList<>();
        private String uid;
        private ArrayList<String> destinationUsers = new ArrayList<>();

        public ChatRecyclerViewApter() {
            uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            //여기서 equalTo(true)가 없다면 내가 속하지 않은 채팅방 정보도 뜨게된다.
            FirebaseDatabase.getInstance().getReference().child("chatrooms").orderByChild("users/" + uid).equalTo(true).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    chatModelList.clear();
                    for (DataSnapshot item : dataSnapshot.getChildren()) {
                        chatModelList.add(item.getValue(ChatModel.class));
                    }
                    notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat, parent, false);

            return new CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
            final CustomViewHolder customViewHolder = (CustomViewHolder)holder;
            String destinationUid = null;
            for(String user:chatModelList.get(position).users.keySet()){
                destinationUid = user;
                destinationUsers.add(destinationUid);
            }
            FirebaseDatabase.getInstance().getReference().child("users").child(destinationUid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    UserModel userModel = dataSnapshot.getValue(UserModel.class);
                    customViewHolder.textView_title.setText(userModel.userName);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            Map<String,ChatModel.Comment> commentMap = new TreeMap<>(Collections.<String>reverseOrder());
            commentMap.putAll(chatModelList.get(position).commentList);
            String lastMessageKey = (String)commentMap.keySet().toArray()[0];
            customViewHolder.textView_last_message.setText(chatModelList.get(position).commentList.get(lastMessageKey).message);

            customViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), MessageActivity.class);
                    intent.putExtra("destinationUid", destinationUsers.get(position));
                    startActivity(intent);
                }
            });

            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
            long unixTime = (long)chatModelList.get(position).commentList.get(lastMessageKey).timestamp;
            Date date = new Date(unixTime);
            customViewHolder.textView_timestamp.setText(simpleDateFormat.format(date));
        }

        @Override
        public int getItemCount() {
            return chatModelList.size();
        }

        private class CustomViewHolder extends RecyclerView.ViewHolder {
            public ImageView imageView;
            public TextView textView_title;
            public TextView textView_last_message;
            public TextView textView_timestamp;
            public CustomViewHolder(View view) {
                super(view);
                imageView = (ImageView)view.findViewById(R.id.chatitem_imageview);
                textView_last_message = (TextView) view.findViewById(R.id.chatitem_textview_lastMessage);
                textView_title = (TextView) view.findViewById(R.id.chatitem_textview_title);
                textView_timestamp = (TextView)view.findViewById(R.id.chatitem_textview_timestamp);
            }
        }
    }

}
