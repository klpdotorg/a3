package com.akshara.assessment.a3.regstdrespPojo;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RegisterStdPojoResp {

    @Override
    public String toString() {
        return "RegisterStdPojoResp{" +
                "results=" + results +
                ", count=" + count +
                '}';
    }

    @SerializedName("results")
    @Expose
    private List<Result> results = null;
    @SerializedName("count")
    @Expose
    private Integer count;

    public List<Result> getResults() {
        return results;
    }

    public void setResults(List<Result> results) {
        this.results = results;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

}