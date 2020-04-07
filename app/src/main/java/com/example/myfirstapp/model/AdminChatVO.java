package com.example.myfirstapp.model;

import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class AdminChatVO {
    String userId;
    String lastChat;
    Timestamp lastChatTimestamp;
    String chatRoom;
    int clearedProblem;
    int usedHint;

    public AdminChatVO(){};

    public AdminChatVO(String lastChat, Timestamp lastChatTimestamp, String userId, String chatRoom, int clearedProblem, int usedHint){
        this.userId = userId;
        this.lastChat = lastChat;
        this.lastChatTimestamp = lastChatTimestamp;
        this.chatRoom = chatRoom;
        this.clearedProblem = clearedProblem;
        this.usedHint = usedHint;
    }


    public String toData(){
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("userId", userId);
        hashMap.put("lastChat", lastChat);
        hashMap.put("lastChatTimestamp", lastChatTimestamp);
        hashMap.put("ChatRoom", chatRoom);
        hashMap.put("usedHint", usedHint);
        hashMap.put("clearedProblem", clearedProblem);
        return hashMap.toString();
    }

    public String getTimeView(){
        SimpleDateFormat timeNow = new SimpleDateFormat("MM/dd a K:mm");
        String timeStamp = timeNow.format(this.lastChatTimestamp.toDate());
        return timeStamp;
    }

    public String getChatRoom(){
        return this.chatRoom;
    }

    public String getUserId() {
        return userId;
    }

    public String getLastChat() {
        return lastChat;
    }

    public Timestamp getLastChatTimestamp() {return lastChatTimestamp;}

    public void setAdminChatVO(Map<String, Object> hashmap){
        this.chatRoom = (String)hashmap.get("chatRoom");
        this.userId = (String)hashmap.get("userId");
        this.lastChat = (String)hashmap.get("lastChat");
        Date date = (Date)hashmap.get("lastChatTimestamp");
        this.lastChatTimestamp = new Timestamp(date);
        this.usedHint = (int)hashmap.get("usedHint");
        this.clearedProblem = (int)hashmap.get("clearedProblem");
    }

    @Override
    public int hashCode(){
        return(this.userId.hashCode());
    }

    @Override
    public boolean equals(Object obj){
        if(obj instanceof AdminChatVO){
            AdminChatVO temp = (AdminChatVO) obj;
            if(this.userId.equals(temp.userId)){
                return true;
            }
        }
        return false;
    }

}
