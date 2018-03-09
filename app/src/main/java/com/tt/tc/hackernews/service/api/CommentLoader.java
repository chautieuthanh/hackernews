package com.tt.tc.hackernews.service.api;

import com.tt.tc.hackernews.model.Comment;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by smu (Chau) on 6/3/18.
 */

public interface CommentLoader {
    @GET("item/{id}.json")
    Observable<Comment> getItem(@Path("id") Long itemId);
}
