package com.teamtreehouse.blog;

import com.teamtreehouse.blog.model.*;
import spark.ModelAndView;
import spark.Request;
import spark.template.handlebars.HandlebarsTemplateEngine;

import java.util.*;

import static java.util.Collections.*;
import static spark.Spark.*;

/**
 * Created by morgan.welch on 4/5/2017.
 */
public class Main {
    private static final String FLASH_MESSAGE_KEY = "flash_message";

    public static void main(String[] args) {
        staticFileLocation("/public");
        BlogEntriesDAO dao = new BlogEntryDAO();

//        **BEFORE**

        before(((req,res)->{
            if(req.cookie("password") != null){
                req.attribute("password", req.cookie("password"));
            }
        }));

        before("/new-entry", (req,res)->{
            if(req.attribute("password") == null){
                setFlashMessage(req, "Please sign in first!");
                res.redirect("/password");
                halt();
            }
        });

        before("/:slug/edit", (req,res)->{
            if(req.attribute("password") == null){
                setFlashMessage(req, "Please sign in first!");
                res.redirect("/password");
                halt();
            }
        });

//        **INDEX**

        get("/", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            model.put("entries", dao.findAll());
            model.put("flashMessage", captureFlashMessage(req));
            return new ModelAndView(model, "index.hbs");
        }, new HandlebarsTemplateEngine());


//        **NEW PAGE**

        get("/new-entry", (req, res) -> new ModelAndView(null, "new.hbs") , new HandlebarsTemplateEngine());

        post("/new-entry", (req, res) -> {
            BlogEntry blogEntry = new BlogEntry(req.queryParams("title"),req.attribute("password"),req.queryParams("entry"), new Date());
            blogEntry.addTag(req.queryParams("tags"));
            dao.add(blogEntry);
            sort(dao.findAll());
            res.redirect("/");
            return null;
        });


//        **PASSWORD**

        get("/password", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            model.put("flashMessage", captureFlashMessage(req));
            return new ModelAndView(model, "password.hbs");
        }, new HandlebarsTemplateEngine());


        post("/password", (req, res)->{
            String password = req.queryParams("password");
            if(password.toLowerCase().equals("admin")){
                res.cookie("password", password);
                res.redirect("/");
            }else {
               setFlashMessage(req, "Invalid user. Please try again.");
               res.redirect("/password");
            }
            return null;
        });

//        **SLUG**

        get("/:slug", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            model.put("entry", dao.findBySlug(req.params("slug")));
            return new ModelAndView(model, "detail.hbs");
        }, new HandlebarsTemplateEngine());

        post("/:slug", (req, res) ->{
            String name = req.queryParams("name");
            String text = req.queryParams("comment");
            BlogEntry blogEntry = dao.findBySlug(req.params("slug"));
            BlogComment comment = new BlogComment(text, name);
            blogEntry.addComment(comment);
            res.redirect("/" + blogEntry.getSlug());
            return null;
        });


//        **TAGS**

        get("/tags/:slugTag", (req,res) ->{
            List<BlogEntry> entriesWithTag = new ArrayList<>();
            for(BlogEntry b: dao.findAll()){
                for (Tag t: b.getTags()){
                    if(t.getTag().equals(req.params("slugTag"))){
                        entriesWithTag.add(b);}
                }
            }
            Map<String,Object> model = new HashMap<>();
            model.put("tagEntry", entriesWithTag);
            return new ModelAndView(model, "by-tag.hbs");
        }, new HandlebarsTemplateEngine());

        get("/edit//:slug/:slugTag/delete", (req,res) -> {
            BlogEntry blogEntry = dao.findBySlug(req.params("slug"));
            blogEntry.removeTag(req.params("slugTag"));
            Map<String, Object> model = new HashMap<>();
            model.put("entry", dao.findBySlug(req.params("slug")));
            return new ModelAndView(model, "edit.hbs");
        }, new HandlebarsTemplateEngine());


//        **DELETE**

        get("/:slug/delete", (req, res) ->
        {
            BlogEntry blogEntry = dao.findBySlug(req.params("slug"));
            dao.remove(blogEntry);
            res.redirect("/");
            return null;
        });

//        **EDIT**

        get("/:slug/edit", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            model.put("entry", dao.findBySlug(req.params("slug")));
            return new ModelAndView(model, "edit.hbs");
        }, new HandlebarsTemplateEngine());

        post("/:slug/edit", (req, res) ->{
            BlogEntry blogEntry = dao.findBySlug(req.params("slug"));
            blogEntry.setTitle(req.queryParams("title"));
            blogEntry.setEntry(req.queryParams("entry"));
            blogEntry.addTag(req.queryParams("tags"));
            res.redirect("/" + blogEntry.getSlug());
            return null;
        });




//        **404**
        exception(NotFoundException.class,(exc, req, res) -> {
            res.status(404);

            HandlebarsTemplateEngine engine = new HandlebarsTemplateEngine();

            String html = engine.render(new ModelAndView(null , "not-found.hbs"));
            res.body(html);

        });

//        **INITIALIZE PAGE WITH BLOG ENTRIES**

        BlogEntry blogEntry1, blogEntry2, blogEntry3, blogEntry4;


        dao.add(blogEntry1 = new BlogEntry("The best day I’ve ever had",
                "Morgan"
                ,"<p>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nunc ut rhoncus felis, vel tincidunt neque." +
                "Vestibulum ut metus eleifend, malesuada nisl at, scelerisque sapien. " +
                "Vivamus pharetra massa libero, sed feugiat turpis efficitur at.<p>" +
                "<p>Cras egestas ac ipsum in posuere. Fusce suscipit, libero id malesuada placerat, " +
                "orci velit semper metus, quis pulvinar sem nunc vel augue. In ornare tempor metus, sit amet congue justo porta et. " +
                "Etiam pretium, sapien non fermentum consequat, <a href=\"\">dolor augue</a> gravida lacus, non accumsan lorem odio id risus. " +
                "Vestibulum pharetra tempor molestie. Integer sollicitudin ante ipsum, a luctus nisi egestas eu. Cras accumsan cursus ante, non dapibus tempor.<p>"
                ,new Date()));
        blogEntry1.addTag("happy");
        blogEntry1.addTag("diary entry");
        blogEntry1.addComment(new BlogComment("<p>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nunc ut rhoncus felis, vel tincidunt neque. Vestibulum ut metus eleifend, malesuada nisl at, scelerisque sapien. Vivamus pharetra massa libero, sed feugiat turpis efficitur at.</p>",
                "Carling Kirk"));


        dao.add(blogEntry2 = new BlogEntry("The absolute worst day I’ve ever had",
                "Morgan"
                ,"<p>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nunc ut rhoncus felis, vel tincidunt neque." +
                "Vestibulum ut metus eleifend, malesuada nisl at, scelerisque sapien. " +
                "Vivamus pharetra massa libero, sed feugiat turpis efficitur at.<p>" +
                "<p>Cras egestas ac ipsum in posuere. Fusce suscipit, libero id malesuada placerat, " +
                "orci velit semper metus, quis pulvinar sem nunc vel augue. In ornare tempor metus, sit amet congue justo porta et. " +
                "Etiam pretium, sapien non fermentum consequat, <a href=\"\">dolor augue</a> gravida lacus, non accumsan lorem odio id risus. " +
                "Vestibulum pharetra tempor molestie. Integer sollicitudin ante ipsum, a luctus nisi egestas eu. Cras accumsan cursus ante, non dapibus tempor.<p>"
                ,new Date()));

        dao.add(blogEntry3 = new BlogEntry("That time at the mall",
                "Morgan"
                ,"<p>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nunc ut rhoncus felis, vel tincidunt neque." +
                "Vestibulum ut metus eleifend, malesuada nisl at, scelerisque sapien. " +
                "Vivamus pharetra massa libero, sed feugiat turpis efficitur at.<p>" +
                "<p>Cras egestas ac ipsum in posuere. Fusce suscipit, libero id malesuada placerat, " +
                "orci velit semper metus, quis pulvinar sem nunc vel augue. In ornare tempor metus, sit amet congue justo porta et. " +
                "Etiam pretium, sapien non fermentum consequat, <a href=\"\">dolor augue</a> gravida lacus, non accumsan lorem odio id risus. " +
                "Vestibulum pharetra tempor molestie. Integer sollicitudin ante ipsum, a luctus nisi egestas eu. Cras accumsan cursus ante, non dapibus tempor.<p>"
                ,new Date()));

        dao.add(blogEntry4 = new BlogEntry("Dude, where’s my car?",
                "Morgan"
                ,"<p>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nunc ut rhoncus felis, vel tincidunt neque." +
                "Vestibulum ut metus eleifend, malesuada nisl at, scelerisque sapien. " +
                "Vivamus pharetra massa libero, sed feugiat turpis efficitur at.<p>" +
                "<p>Cras egestas ac ipsum in posuere. Fusce suscipit, libero id malesuada placerat, " +
                "orci velit semper metus, quis pulvinar sem nunc vel augue. In ornare tempor metus, sit amet congue justo porta et. " +
                "Etiam pretium, sapien non fermentum consequat, <a href=\"\">dolor augue</a> gravida lacus, non accumsan lorem odio id risus. " +
                "Vestibulum pharetra tempor molestie. Integer sollicitudin ante ipsum, a luctus nisi egestas eu. Cras accumsan cursus ante, non dapibus tempor.<p>"
                ,new Date()));
        blogEntry4.addTag("movie");
        blogEntry4.addTag("reference");




}


//    **FLASH MESSAGE METHODS**

    private static String captureFlashMessage(Request req) {
        String message = getFlashMessage(req);
        if(message != null) {
            req.session().removeAttribute(FLASH_MESSAGE_KEY);
        }
        return message;
    }

    private static String getFlashMessage(Request req) {
        if(req.session(false) == null){
            return null;
        }
        if(!req.session().attributes().contains(FLASH_MESSAGE_KEY)){
            return null;
        }
        return (String)req.session().attribute(FLASH_MESSAGE_KEY);
    }

    private static void setFlashMessage(Request req, String message) {
        req.session().attribute(FLASH_MESSAGE_KEY,message);
    }


}

