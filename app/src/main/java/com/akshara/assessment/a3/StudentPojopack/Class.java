package com.akshara.assessment.a3.StudentPojopack;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Class {

@SerializedName("id")
@Expose
private Integer id;
@SerializedName("institution")
@Expose
private Integer institution;
@SerializedName("name")
@Expose
private String name;
@SerializedName("section")
@Expose
private String section;
@SerializedName("status")
@Expose
private String status;
@SerializedName("group_type")
@Expose
private String groupType;

public Integer getId() {
return id;
}

public void setId(Integer id) {
this.id = id;
}

public Integer getInstitution() {
return institution;
}

public void setInstitution(Integer institution) {
this.institution = institution;
}

public String getName() {
return name;
}

public void setName(String name) {
this.name = name;
}

public String getSection() {
return section;
}

public void setSection(String section) {
this.section = section;
}

public String getStatus() {
return status;
}

public void setStatus(String status) {
this.status = status;
}

public String getGroupType() {
return groupType;
}

public void setGroupType(String groupType) {
this.groupType = groupType;
}

}