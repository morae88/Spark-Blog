package com.teamtreehouse.blog.model;

import java.util.List;
import java.util.Set;

/**
 * Created by morgan.welch on 4/5/2017.
 */
public interface BlogEntriesDAO {

    boolean add(BlogEntry entry);

    boolean remove(BlogEntry entry);

    List<BlogEntry> findAll();

    BlogEntry findBySlug(String slug);
}
