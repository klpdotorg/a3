package com.akshara.assessment.a3.Pojo;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Result {

@SerializedName("uid")
@Expose
private List<String> uid = null;

public List<String> getUid() {
return uid;
}

public void setUid(List<String> uid) {
this.uid = uid;
}

}
