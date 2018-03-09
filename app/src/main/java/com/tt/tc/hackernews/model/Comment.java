package com.tt.tc.hackernews.model;

import android.text.format.DateUtils;

import java.util.List;

/**
 * Created by smu (Chau) on 6/3/18.
 */

public class Comment {
    public static final Comment NULL_ITEM = new Comment();
    private String by;
    private long id;
    private List<Long> kids;
    private long parent;
    private String text;
    private long time;

    private List<Comment> replies;
    public String replyStr;
    private long loadedTime;

    public Comment() {
        loadedTime = System.currentTimeMillis();
    }

    public void setBy(String by) {
        this.by = by;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setKids(List<Long> kids) {
        this.kids = kids;
    }

    public void setParent(long parent) {
        this.parent = parent;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getId() {
        return id;
    }

    public List<Long> getChilds() {
        return kids;
    }

    public String getBy() {
        return by;
    }

    public String getText() {
        if (text == null){
            text = "";
        }
        return text;
    }

    public CharSequence getTimeStr(){
        return DateUtils.getRelativeTimeSpanString(time * 1000, System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS);
    }

    public String getReplyStr() {
        if (replyStr == null){
            int no = 0;
            if(kids != null){
                no = kids.size();
            }
            replyStr = no > 1 ? no + " replies" : no + " reply";
        }
        return replyStr;
    }

    public boolean isUpdated(long cachedInterval) {
        return System.currentTimeMillis() - loadedTime <= cachedInterval;
    }
}
