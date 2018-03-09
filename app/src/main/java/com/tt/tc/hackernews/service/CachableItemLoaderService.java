package com.tt.tc.hackernews.service;

import com.tt.tc.hackernews.model.NewsItem;
import com.tt.tc.hackernews.service.api.ItemLoader;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;

/**
 * Created by smu (Chau) on 5/3/18.
 */

public class CachableItemLoaderService {
    private static final long CACHED_TIME = 60 * 1000; // Cached item for 1 minute
    private Map<Long, NewsItem> cachedItems;
    private ItemLoader itemLoader;

    public CachableItemLoaderService() {
        cachedItems = new HashMap<>();
        itemLoader = RestClient.getInstance().getItemLoader();
    }

    private Observable<NewsItem> getCachedItem(Long itemId) {
        //TODO: We can use sqlite instead of memory to cache.
        NewsItem result = NewsItem.NULL_ITEM;
        if (cachedItems.containsKey(itemId)) {
            result = cachedItems.get(itemId);
        }
        return Observable.just(result);
    }

    public Observable<NewsItem> getItem(Long itemId, boolean refresh) {
        Observable<NewsItem> networkItem = itemLoader
                .getItem(itemId)
                .doOnNext(item -> {
                    cachedItems.put(itemId, item); // Save to cache
                });

        if (refresh) {
            return networkItem;
        }

        Observable<NewsItem> cachedItem = getCachedItem(itemId);
        return Observable
                .concat(cachedItem, networkItem)
                .filter(item -> item != NewsItem.NULL_ITEM && item.isUpdated(CACHED_TIME))
                .onErrorReturnItem(NewsItem.NULL_ITEM)
                .take(1);
    }

    public void clear() {
        cachedItems.clear();
    }
}
