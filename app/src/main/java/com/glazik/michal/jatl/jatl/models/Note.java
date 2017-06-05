package com.glazik.michal.jatl.jatl.models;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;
/**
 * Created by Michał on 2017-05-17.
 */

public class Note implements Serializable
{
    private String id;
    private String title;
    private String body;
    private Date date;
    private String image;
    private String comment;


    public Note() {
        this.id = UUID.randomUUID().toString();
        this.date = new Date();
    }

    public String getId()
    {
        return id;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getBody()
    {
        return body;
    }

    public void setBody(String body)
    {
        this.body = body;
    }

    public Date getDate()
    {
        return date;
    }

    public void setDate(Date date)
    {
        this.date = date;
    }

    public String getImage()
    {
        return image;
    }

    public void setImage(String image)
    {
        this.image = image;
    }

    public String getComment()
    {
        return comment;
    }

    public void setComment(String comment)
    {
        this.comment = comment;
    }


}
