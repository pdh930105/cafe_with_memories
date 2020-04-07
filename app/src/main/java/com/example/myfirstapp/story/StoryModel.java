package com.example.myfirstapp.story;

import java.util.ArrayList;

public class StoryModel {
    private int time;//분으로 계산한다
    private String title;//
    //스테이지 어레이는 0부터 시작 스테이지는 1부터 시작..
    private ArrayList<Stage> stages;
    //Gson
    public StoryModel() {
        stages = new ArrayList<>();
        //TODO:문자열 정리
        //TODO:초기 시작시 보여줄 이미지 필요
        title = "세상예찬";
        time = 60;
    }

    public StoryModel(int time, String title, ArrayList<Stage> stages) {
        this.time = time;
        this.title = title;
        this.stages = stages;
    }

    @Override
    public String toString() {
        return "StoryModel [stages=" + stages + ", time=" + time + ", title=" + title + "]";
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArrayList<Stage> getStages() {
        return stages;
    }

    public void setStages(ArrayList<Stage> stages) {
        this.stages = stages;
    }
}
