package com.example.myfirstapp.story;

public class Stage {
    private String answer;
    private String imageFileName;
    private String hintFileName;

    public String getHintFileName() {
        return hintFileName;
    }

    public void setHintFileName(String hintFileName) {
        this.hintFileName = hintFileName;
    }
//TODO: 힌트 데이터 추가해야함. 이미지?...
    //TODO: 스테이지 사이에 보여줄 이미지 필요

    //문자열

    public Stage() {
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getImageFileName() {
        return imageFileName;
    }

    public void setImageFileName(String imageFileName) {
        this.imageFileName = imageFileName;
    }

    public Stage(String answer, String imageFileName) {
        this.answer = answer;
        this.imageFileName = imageFileName;
    }
    public Stage(String answer, String imageFileName, String hintFileName) {
        this.answer = answer;
        this.imageFileName = imageFileName;
        this.hintFileName = hintFileName;
    }

    @Override
    public String toString() {
        return "Stage{" +
                "answer='" + answer + '\'' +
                ", imageFileName='" + imageFileName + '\'' +
                ", hintFileName='" + hintFileName + '\'' +
                '}';
    }
}