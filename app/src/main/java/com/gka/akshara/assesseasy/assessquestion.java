package com.gka.akshara.assesseasy;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class assessquestion {

    private String questionID;
    private String questionText;
    private String questionType;
    private String questionTemplType;
    private String answerCorrect;
    private String answerGiven;
    private String pass; // P - Pass, F - Fail, S - Skipped
    private ArrayList<assessquestiondata> listQuestionData;

    public assessquestion() {

        this.listQuestionData = new ArrayList<assessquestiondata>();
    }

    public void addQuestionData(assessquestiondata aqd) {

        this.listQuestionData.add(aqd);
    }

    public ArrayList getQuestionDataList() {

        return this.listQuestionData;
    }

    public void setQuestionID(String qid) {

        questionID = qid;
    }

    public String getQustionID() {

        return questionID;
    }

    public void setQuestionText(String qtext) {

        this.questionText = qtext;
    }

    public String getQuestionText() {

        return this.questionText;
    }

    public void setQuestionType(String qtype) {

        this.questionType = qtype;
    }

    public String getQuestionType() {

        return this.questionType;
    }

    public void setQuestionTemplType(String qtempltype) {

        this.questionTemplType = qtempltype;
    }

    public String getQuestionTemplType() {

        return this.questionTemplType;
    }

    public void setAnswerCorrect(String correctanswer) {

        this.answerCorrect = correctanswer;
    }

    public String getAnswerCorrect() {

        return this.answerCorrect;
    }

    public void setAnswerGiven(String givenanswer) {

        this.answerGiven = givenanswer;
    }

    public String getAnswerGiven() {

        return this.answerGiven;
    }

    public void setPass(String passstr) {

        this.pass = passstr;
    }

    public String getPass() {

        return this.pass;
    }
}
