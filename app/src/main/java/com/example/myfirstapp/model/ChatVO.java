package com.example.myfirstapp.model;

import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class ChatVO{
    private String userId;
    private String text;
    private Timestamp timestamp;

    public ChatVO(){};

    public ChatVO(String userId, String text, Timestamp timestamp){
        this.userId = userId;
        this.text = text;
        this.timestamp = timestamp;
    }


    public String getuserId() {
        return userId;
    }

    public String getText() {
        return text;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public String getTimeView(){
        SimpleDateFormat timenow = new SimpleDateFormat("MM/dd a K:mm");
        String today = timenow.format(getTimestamp().toDate());
        return today;
    }

    public HashMap<String, Object> getHashMap(){
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("userId", this.userId);
        hashMap.put("text", this.text);
        hashMap.put("timestamp", this.timestamp);

        return hashMap;
    }
}

