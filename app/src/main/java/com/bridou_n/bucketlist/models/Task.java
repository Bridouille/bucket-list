package com.bridou_n.bucketlist.models;

import java.util.Date;
import java.util.UUID;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

/**
 * Created by bridou_n on 21/12/2016.
 */

public class Task extends RealmObject {
    /**
     * Thoses indexes should be the indexes of the "priorities" string array
     */
    @Ignore public static final int PRIORITY_LOW = 0;
    @Ignore public static final int PRIORITY_MEDIUM = 1;
    @Ignore public static final int PRIORITY_HIGH = 3;

    @PrimaryKey
    private String id;
    private String title;
    private String content;
    private long lastEdit;
    private boolean done;
    private int priority;

    public Task() {

    }

    public Task(String title, String content, boolean done, int priority) {
        this.id = UUID.randomUUID().toString();
        this.title = title;
        this.content = content;
        this.lastEdit = new Date().getTime();
        this.done = done;
        this.priority = priority;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getLastEdit() {
        return lastEdit;
    }

    public void setLastEdit(long lastEdit) {
        this.lastEdit = lastEdit;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
}
