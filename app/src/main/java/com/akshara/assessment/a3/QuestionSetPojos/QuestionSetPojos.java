package com.akshara.assessment.a3.QuestionSetPojos;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class QuestionSetPojos {

@SerializedName("status")
@Expose
private String status;


@SerializedName("message")
@Expose
private String message;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @SerializedName("description")
@Expose
private String description;


@SerializedName("questionsets")
@Expose
private List<Questionset> questionsets = null;

public String getStatus() {
return status;
}

public void setStatus(String status) {
this.status = status;
}

public String getMessage() {
return message;
}

public void setMessage(String message) {
this.message = message;
}

public List<Questionset> getQuestionsets() {
return questionsets;
}

public void setQuestionsets(List<Questionset> questionsets) {
this.questionsets = questionsets;
}

}