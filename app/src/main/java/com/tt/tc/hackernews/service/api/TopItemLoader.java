package com.tt.tc.hackernews.service.api;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;

/**
 * Created by smu (Chau) on 4/3/18.
 */

public interface TopItemLoader {
    @GET("topstories.json")
    Observable<List<Long>> loadItems();
}
