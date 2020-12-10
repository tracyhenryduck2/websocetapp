package com.siterwell.demo;

import java.io.Serializable;

/**
 * Created by gc-0001 on 2017/5/28.
 */

public class QuestionBean implements Serializable {
    private String question;
    private String answer;
    private boolean isopen;

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public boolean isopen() {
        return isopen;
    }

    public void setIsopen(boolean isopen) {
        this.isopen = isopen;
    }

    @Override
    public String toString() {
        return "QuestionBean{" +
                "answer='" + answer + '\'' +
                ", question='" + question + '\'' +
                ", isopen=" + isopen +
                '}';
    }
}
