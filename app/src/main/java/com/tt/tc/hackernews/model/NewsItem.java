package com.tt.tc.hackernews.model;

import android.text.format.DateUtils;

import java.util.List;

/**
 * Created by smu (Chau) on 3/3/18.
 */

public class NewsItem {
    public static final NewsItem NULL_ITEM = new NewsItem();
    private int listIndex;
    private String by;
    private String title;
    private int descendants;
    private long id;
    private List<Long> kids;
    private int score;
    private long time;
    private String type;
    private String url;

    private String byStr;
    private String scoreStr;
    private String shortUrl;
    private String commentStr;
    private String listIndexStr;
    private long loadedTime;

    public NewsItem() {
        loadedTime = System.currentTimeMillis();
    }

    public int getListIndex() {
        return listIndex;
    }

    public void setListIndex(int listIndex) {
        this.listIndex = listIndex;
    }

    public void setBy(String by) {
        this.by = by;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescendants(int descendants) {
        this.descendants = descendants;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setKids(List<Long> kids) {
        this.kids = kids;
    }

    public List<Long> getKids() {
        return kids;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setUrl(String url) {
        this.url = url;

    }

    public String getByStr() {
        if (byStr == null && by != null){
            this.byStr = "By " + by + " ";
        }
        return byStr;
    }

    public String getScoreStr() {
        if (scoreStr == null){
            scoreStr = score < 2 ? score + " point " : score + " Points ";
        }
        return scoreStr;
    }

    public String getShortUrl() {
        if (shortUrl == null && url != null){
            int dotIndex = url.indexOf(".");
            int endIndex = url.indexOf("/", dotIndex);
            if (endIndex < 0){
                shortUrl = url;
            } else {
                shortUrl = url.substring(0, endIndex);
            }

        }
        return shortUrl;
    }

    public CharSequence getTimeStr() {
        return DateUtils.getRelativeTimeSpanString(time * 1000, System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS);
    }

    public String getCommentStr() {
        if (commentStr == null){
            int no;
            if (kids != null){
                no = kids.size();
            } else{
                no = 0;
            }
            commentStr = no < 2 ? no + " comment " : no + " comments ";
        }
        return commentStr;
    }

    public String getListIndexStr(){
        if (listIndexStr == null){
            listIndexStr = listIndex + ". ";
        }
        return listIndexStr;
    }

    public String getTitle() {
        return title;
    }

    public boolean isUpdated(long cachedInterval) {
        return System.currentTimeMillis() - loadedTime <= cachedInterval;
    }

    @Override
    public String toString() {
        return String.format("NewsItem{title=%s, kids=%s}", title, kids);
    }
}
