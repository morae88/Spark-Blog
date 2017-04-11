package com.teamtreehouse.blog.model;

import java.util.*;

/**
 * Created by morgan.welch on 4/5/2017.
 */
public class BlogEntryDAO implements BlogEntriesDAO {

    private List<BlogEntry> entries;


    public BlogEntryDAO() {
        entries = new ArrayList<>();
    }


    @Override
    public boolean add(BlogEntry entry) {return entries.add(entry); }

    @Override
    public boolean remove(BlogEntry entry) {
       return entries.remove(entry);
    }

    @Override
    public List<BlogEntry> findAll() {
        Collections.sort(entries);
        return new ArrayList<>(entries);
    }

    @Override
    public BlogEntry findBySlug(String slug) {
        return entries.stream()
                .filter(entry -> entry.getSlug().equals(slug))
                .findFirst()
                .orElseThrow(NotFoundException::new);
    }


}
