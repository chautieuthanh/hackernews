package com.tt.tc.hackernews.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tt.tc.hackernews.service.api.CommentLoader;
import com.tt.tc.hackernews.service.api.ItemLoader;
import com.tt.tc.hackernews.service.api.TopItemLoader;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by smu (Chau) on 4/3/18.
 */

public class RestClient {
    private static RestClient instance;
    private ItemLoader itemLoader;
    private TopItemLoader topItemLoader;
    private CommentLoader commentLoader;
    private Retrofit retrofit;
    private void init(){
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        retrofit = new Retrofit.Builder()
                .baseUrl("https://hacker-news.firebaseio.com/v0/")
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }

    public RestClient(){
        init();
    }

    public static RestClient getInstance(){
        if(instance == null){
            instance = new RestClient();
        }
        return instance;
    }

    public ItemLoader getItemLoader(){
        if (itemLoader == null){
            itemLoader = retrofit.create(ItemLoader.class);
        }
        return itemLoader;
    }

    public TopItemLoader getTopItemLoader(){
        if (topItemLoader == null){
            topItemLoader = retrofit.create(TopItemLoader.class);
        }
        return topItemLoader;
    }

    public CommentLoader getCommentLoader(){
        if (commentLoader == null){
            commentLoader = retrofit.create(CommentLoader.class);
        }
        return commentLoader;
    }
}
