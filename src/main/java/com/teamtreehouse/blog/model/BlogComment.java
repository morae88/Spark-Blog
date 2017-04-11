package com.teamtreehouse.blog.model;

import java.util.Date;

/**
 * Created by morgan.welch on 4/5/2017.
 */
public class BlogComment {
    private String comment;
    private Date date;
    private String creator;

    public BlogComment(String comment, String creator){
        this.comment = comment;
        this.creator = creator;
        this.date = new Date();
    }

    public String getComment() {
        return comment;
    }

    public Date getDate() {
        return date;
    }

    public String getCreator() {
        return creator;
    }
}
