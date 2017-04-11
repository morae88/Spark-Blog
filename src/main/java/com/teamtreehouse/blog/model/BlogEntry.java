package com.teamtreehouse.blog.model;

import com.github.slugify.Slugify;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by morgan.welch on 4/5/2017.
 */
public class BlogEntry implements Comparable{
    private final String slug;
    private String title;
    private String creator;
    private String entry;
    private Date date;
    private List<Tag> tags = new ArrayList<>();

    public void setTitle(String title) {
        this.title = title;
    }

    public void setEntry(String entry) {
        this.entry = entry;
    }

    private List<BlogComment> comments = new ArrayList<>();

    public BlogEntry(String title, String creator, String entry, Date date) {
        this.title = title;
        this.creator = creator;
        this.entry = entry;
        this.date = date;
        Slugify slugify = new Slugify();
        slug = slugify.slugify(title);

    }

    public String getSlug(){
        return slug;
    }

    public String getTitle() {
        return title;
    }

    public String getCreator() {
        return creator;
    }

    public String getEntry() {
        return entry;
    }

    public Date getDate() {
        return date;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void addTag(String tag) {
        Pattern whitespace = Pattern.compile("\\w+");
        List<String> newTags = new ArrayList<>();
        Matcher matcher = whitespace.matcher(tag);

        while (matcher.find()){
            newTags.add(matcher.group());
        }

        for(String t: newTags){
            if (!tags.contains(t)) {
                tags.add(new Tag(t.toLowerCase()));
            }
        }
    }

    public void removeTag(String tag){
        for(Tag t: tags){
            if (t.getTag().equals(tag)){
            tags.remove(t);
            break;
            }
        }
    }

    public boolean addComment(BlogComment comment){
        return comments.add(comment);
    }

    public List<BlogComment> getComments() {
        return comments;
    }

    public String findByTag(String tag){
        if(this.getTags().contains(tag));

        return tag;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BlogEntry blogEntry = (BlogEntry) o;

        return date.equals(blogEntry.date);
    }

    @Override
    public int hashCode() {
        return date.hashCode();
    }

    @Override
    public int compareTo(Object o) {
        BlogEntry blogEntry = (BlogEntry)o;
        return blogEntry.getDate().compareTo(getDate());
    }


}
