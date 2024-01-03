package com.example.assignment.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class RightContentData extends Data implements Serializable {
    private final List<String> p1List;
    private String contentTitle;
    private final List<String> contentDescriptionList;

    public RightContentData() {

        contentDescriptionList = new ArrayList<>();
        p1List = new ArrayList<>();
    }

    public String getContentTitle() {
        return contentTitle;
    }

    public void setContentTitle(String contentTitle) {
        this.contentTitle = contentTitle;
    }

    public List<String> getContentDescriptionList() {
        return contentDescriptionList;
    }

    public void addContentDescription(String contentDescription) {
        contentDescriptionList.add(contentDescription);
    }
    public List<String> getP1() {
        return p1List;
    }
    public void addP1(String p1) {
        p1List.add(p1);
    }



}
