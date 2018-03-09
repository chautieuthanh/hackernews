package com.tt.tc.hackernews.service.api;

import com.tt.tc.hackernews.model.NewsItem;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by smu (Chau) on 3/3/18.
 */

public interface ItemLoader {
    @GET("item/{id}.json")
    Observable<NewsItem> getItem(@Path("id") Long itemId);
}
