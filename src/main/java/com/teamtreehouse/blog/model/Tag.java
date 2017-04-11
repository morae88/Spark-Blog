package com.teamtreehouse.blog.model;

import com.github.slugify.Slugify;

import java.util.List;

/**
 * Created by morgan.welch on 4/10/2017.
 */
public class Tag {
    private String tag;
    private final String slugTag;

    Tag(String tag){
        this.tag = tag;
        Slugify slugify = new Slugify();
        slugTag = slugify.slugify(tag);
    }

    public String getTag() {return tag.toLowerCase();}

    public String getSlugTag() {
        return slugTag;
    }

    @Override
    public String toString() {
        return "#"+ tag;
    }
}
