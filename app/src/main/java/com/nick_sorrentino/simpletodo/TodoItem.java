package com.nick_sorrentino.simpletodo;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by nick_sorrentino on 7/19/16.
 */
public class TodoItem implements Serializable{
    private String title;
    private int id;
    private String priority;
    private Date dueDate;

    public TodoItem(int i, String t, String p, Date d) {
        this.id = i;
        this.title = t;
        this.priority = p;
        this.dueDate = d;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String t){
        title = t;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String p){
        priority = p;
    }

    public Date getDueDate(){
        return dueDate;
    }

    public void setDueDate(Date d){
        dueDate = d;
    }

    @Override
    public String toString() {
        return title;
    }
}
