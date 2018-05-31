package com.akshara.assessment.a3.regstdrespPojo;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RegisterStdPojoResp {

@SerializedName("count")
@Expose
private Integer count;
@SerializedName("results")
@Expose
private List<Result> results = null;

public Integer getCount() {
return count;
}

public void setCount(Integer count) {
this.count = count;
}

public List<Result> getResults() {
return results;
}

public void setResults(List<Result> results) {
this.results = results;
}

}