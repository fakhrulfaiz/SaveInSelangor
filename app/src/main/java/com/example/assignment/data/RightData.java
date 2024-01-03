package com.example.assignment.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class RightData extends Data implements Serializable {
    private String title;
    private List<RightContentData> contentList;

    public RightData() {
        contentList = new ArrayList<>();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<RightContentData> getContentList() {
        return contentList;
    }

    public void addContent(RightContentData contentData) {
        contentList.add(contentData);
    }
}

