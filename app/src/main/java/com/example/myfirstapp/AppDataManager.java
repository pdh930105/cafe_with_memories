package com.example.myfirstapp;

import com.example.myfirstapp.story.StoryModel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class AppDataManager {
    private static final AppDataManager ourInstance = new AppDataManager();
    private static final String STORY_PATH = "/data/user/0/com.example.myfirstapp/files/Stories";
    private static final String JSON_PATH = "story.json";
    public static final int START_STAGE = 1;

    private ArrayList<Boolean> hint_array = new ArrayList<>();
    private int currentStage;
    private int opendStage;
    private int hintCount;
    private long startTime;
    private long finishTime;
    private int finalStage;


    private StoryModel storyModel;

    public static AppDataManager getInstance() {
        return ourInstance;
    }

    public void storyLoader(){
        try {
        File jsonFile = new File(STORY_PATH, JSON_PATH);
        System.out.println(jsonFile.getPath());
        System.out.println(jsonFile.length());

        String readData = "";
        StringBuilder stringBuilder = new StringBuilder("");
        BufferedReader bufferedReader = new BufferedReader(new FileReader(jsonFile));
        while ((readData = bufferedReader.readLine()) != null) {
            stringBuilder.append(readData);
            System.out.println(readData);
        }
            bufferedReader.close();

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            storyModel = gson.fromJson(stringBuilder.toString(), StoryModel.class);
        } catch (IOException e) {
            e.printStackTrace();
            storyModel = new StoryModel();
        }

        //초기화
        finalStage = storyModel.getStages().size();
        setCurrentStage(getCurrentStage());
        setOpendStage(getOpendStage());

        for (int i=0; i<finalStage;i++){
            hint_array.add(Boolean.FALSE);
        }
    }

    /**     getter    **/

    public StoryModel getStoryModel() {
        return storyModel;
    }
    public static int getStartStage() {
        return START_STAGE;
    }

    public static String getStoryPath() {
        return STORY_PATH;
    }

    public int getOpendStage() {
        return opendStage;
    }

    public int getHintCount() {
        return hintCount;
    }

    public int getCurrentStage() {
        return currentStage;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getFinishTime() {
        return finishTime;
    }

    public int getFinalStage() {
        return finalStage;
    }

    public void useHintToIndex(int idx)
    {
        hint_array.set(idx,Boolean.TRUE);
        return ;
    }

    public boolean getHintIsUsed(int idx, Boolean target)
    {
        return hint_array.get(idx).equals(target);
    }



    /**     Setter      */

    public void setCurrentStage(int currentStage) {
        if (currentStage <= 1) {
            this.currentStage = 1;
        } else if (currentStage >= this.opendStage) {
            this.currentStage = this.opendStage;
        } else {
            this.currentStage = currentStage;
        }
    }

    public void incrementCurrentStage(){
        setCurrentStage(getCurrentStage()+1);
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public void setFinishTime(long finishTime) {
        this.finishTime = finishTime;
    }

    public void setOpendStage(int opendStage) {
        if (opendStage <= 1) {
            this.opendStage = 1;
        } else if (opendStage >= finalStage) {
            this.opendStage = finalStage;
        } else {
            this.opendStage = opendStage;
        }
    }

    /** 힌트 카운터*/
    public void setHintCount(int hintCount) {
        this.hintCount = hintCount;
    }

    public int useHint(){
        return  ++hintCount;
    }

    /**     스테이지가 사용가능한 상태인지 확인    */
    public boolean isOK(){
        return (finalStage>0) ? true : false;
    }

    /**
     * 생성자
     * */
    private AppDataManager() {
//        현재 진행중인 스테이지
        System.out.println("AppDataManager가 생성되었습니다.");
        currentStage = 1;
        opendStage = 1;
        hintCount = 0;
        startTime = 0;
        finishTime = 0;

        finalStage = 0;
        storyLoader();
   }
}
