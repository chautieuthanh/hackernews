package com.tt.tc.hackernews.service;

import android.util.Log;

import com.tt.tc.hackernews.model.Comment;
import com.tt.tc.hackernews.service.api.CommentLoader;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;

/**
 * Created by smu (Chau) on 6/3/18.
 */

public class CachableCommentLoaderService {
    private static final long CACHED_TIME = 60 * 1000; // Cached item for 1 minute
    private Map<Long, Comment> cachedItems;
    private CommentLoader commentLoader;

    public CachableCommentLoaderService() {
        cachedItems = new HashMap<>();
        commentLoader = RestClient.getInstance().getCommentLoader();
    }

    private Observable<Comment> getCachedItem(Long itemId) {
        //TODO: We can use sqlite instead of memory to cache.
        if (cachedItems.containsKey(itemId)) {
            return Observable.just(cachedItems.get(itemId));
        } else {
            return Observable.empty();
        }
    }

    public Observable<Comment> getItem(Long itemId, boolean refresh) {
        Observable<Comment> networkItem = commentLoader
                .getItem(itemId)
                .doOnNext(new Consumer<Comment>() {
                    @Override
                    public void accept(@NonNull Comment item) throws Exception {
                        cachedItems.put(item.getId(), item); // Save to cache
                    }
                });

        if (refresh) {
            return networkItem;
        }

        Observable<Comment> cachedItem = getCachedItem(itemId);
        return Observable
                .concat(cachedItem, networkItem)
                .filter(new Predicate<Comment>() {
                    @Override
                    public boolean test(@NonNull Comment item) throws Exception {
                        return item.isUpdated(CACHED_TIME);
                    }
                })
                .onErrorReturnItem(Comment.NULL_ITEM)
                .take(1);
    }

    public void clear() {
        cachedItems.clear();
    }
}
