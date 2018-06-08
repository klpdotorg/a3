package com.akshara.assessment.a3.Pojo;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class StudentExistsPojo {

@SerializedName("count")
@Expose
private Integer count;
@SerializedName("results")
@Expose
private List<String> results = null;

public Integer getCount() {
return count;
}

public void setCount(Integer count) {
this.count = count;
}

public List<String> getResults() {
return results;
}

public void setResults(List<String> results) {
this.results = results;
}

}