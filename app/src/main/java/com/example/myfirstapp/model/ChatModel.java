package com.example.myfirstapp.model;

import java.util.HashMap;
import java.util.Map;

public class ChatModel {

    public Map<String, Boolean> users = new HashMap<>(); //채팅방 유저들
    public  Map<String, Comment> commentList = new HashMap<>();//채팅방의 대화내용

    public static class Comment{
        public String uid;
        public String message;
        public Object timestamp;

        public String getMessage() {
            return message;
        }

        public String getUid() {
            return uid;
        }
    }
}


