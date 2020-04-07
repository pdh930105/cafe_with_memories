package com.example.myfirstapp.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.myfirstapp.R;
import com.example.myfirstapp.model.ChatModel;
import com.example.myfirstapp.model.ChatVO;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
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


public class AdminManageChatFragment extends Fragment {
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.mm.dd hh:mm");

    String userName;
    String roomId;
    String adminId;
    DocumentReference docs_chat;
    CollectionReference coll_chat;
    RecyclerView recyclerView;
    EditText sendEditText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        userName = getArguments().getString("userName");
        roomId = getArguments().getString("roomId");
        adminId = getArguments().getString("adminId");
        final ArrayList<ChatVO> chatVOArrayList = new ArrayList<>();
        String sendId = adminId;

        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        recyclerView = (RecyclerView)view.findViewById(R.id.chatfragment_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(inflater.getContext()));

        final RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(chatVOArrayList, sendId);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(inflater.getContext()));

        docs_chat = FirebaseFirestore.getInstance().collection("chatDB").document(roomId);
        coll_chat = docs_chat.collection("chatList");

        coll_chat.addSnapshotListener(new EventListener<QuerySnapshot>() {
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
                            chatVOArrayList.add(chatVO);
                            Collections.sort(chatVOArrayList, new Comparator<ChatVO>() {
                                @Override
                                public int compare(ChatVO chatVO, ChatVO t1) {
                                    return chatVO.getTimestamp().compareTo(t1.getTimestamp());
                                }
                            });

                            Map<String, Object>updateLastChat = new HashMap<>();
                            updateLastChat.put("userId", chatVO.getuserId());
                            updateLastChat.put("lastChatTimestamp", chatVO.getTimestamp());
                            updateLastChat.put("lastChat", chatVO.getText());
                            updateLastChat.put("chatRoom", roomId);
                            docs_chat.set(updateLastChat, SetOptions.merge());

                            break;
                        case MODIFIED:
                            //수정이나 삭제에 대한 데이터
                            break;
                        case REMOVED:
                            break;
                    }

                }
                recyclerViewAdapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(chatVOArrayList.size()-1);
            }
        });

        Button sendBtn = view.findViewById(R.id.sendBtn);
        sendEditText = view.findViewById(R.id.fragment_sendEditText);

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChatModel chatModel = new ChatModel();
                chatModel.users.put(roomId, true);
                //chatModel.users.put(destinationUid, true);

                String text = sendEditText.getText().toString();

                if(text.length() != 0 ){
                    //editText_message.setText("");
                    ChatVO chatVO = new ChatVO(adminId, text, Timestamp.now());
                    coll_chat.add(chatVO.getHashMap());
                    sendEditText.setText("");
                }
            }
        });

        return view;
    }

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
            if(chatVO.getuserId().equals(chatSendId)){//내가 쓴 챗
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
                return new AdminManageChatFragment.RecyclerViewAdapter.SentMessageHolder(view);
            } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_message_received, parent, false);
                return new AdminManageChatFragment.RecyclerViewAdapter.ReceivedMessageHolder(view);
            }

            return null;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
//            MessageViewHolder messageViewHolder = ((MessageViewHolder)holder);
            //ChatModel.Comment comment = commentList.get(position);
            ChatVO chatVO = chatVOArrayList.get(position);
            //Toast.makeText(AdminManageChatFragment.this, "onBindViewHolder", Toast.LENGTH_SHORT).show();
            switch (holder.getItemViewType()){
                case VIEW_TYPE_MESSAGE_SENT:
                    ((AdminManageChatFragment.RecyclerViewAdapter.SentMessageHolder)holder).bind(chatVO);
                    break;
                case VIEW_TYPE_MESSAGE_RECEIVED:
                    ((AdminManageChatFragment.RecyclerViewAdapter.ReceivedMessageHolder)holder).bind(chatVO);
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
                Log.d("checkTimestamp", "timestamp: " + chatVO.getTimestamp());
                Log.d("checkTimestamp", "date: " + chatVO.getTimestamp().toDate());
                Log.d("checkTimestamp", "getTimeView: " + chatVO.getTimeView());
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
}
