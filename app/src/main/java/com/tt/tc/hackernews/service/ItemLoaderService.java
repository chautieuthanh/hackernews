package com.tt.tc.hackernews.service;
import android.util.Log;
import com.tt.tc.hackernews.adapter.StoriesAdapter;
import com.tt.tc.hackernews.model.NewsItem;
import com.tt.tc.hackernews.service.api.TopItemLoader;

import java.util.ArrayList;
import java.util.List;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by smu (Chau) on 3/3/18.
 */

public class ItemLoaderService {
    private CachableItemLoaderService itemLoader;
    private TopItemLoader topItemLoader;
    private List<Long> topItems;
    private boolean isLoading;

    private Disposable currentSubscription;

    public ItemLoaderService() {
        itemLoader = new CachableItemLoaderService();
        topItemLoader = RestClient.getInstance().getTopItemLoader();
    }

    public void loadTopStories(StoriesAdapter storiesAdapter, boolean refresh, int limit) {
        if (isLoading) {
            Log.i("HackerNews", "Loading more items, won't run.");
            return;
        }
        if (refresh || topItems == null || topItems.isEmpty()) {
            itemLoader.clear();
            currentSubscription = topItemLoader.loadItems()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .onErrorReturnItem(new ArrayList<>())
                    .subscribe(newItems -> {
                        if (newItems.isEmpty()) {
                            //TODO Show alert error
                            Log.i("HackerNews", "Cannot load top items");
                        } else {
                            Log.i("HackerNews", "Top items: " + newItems);
                            topItems = newItems;
                            storiesAdapter.clearData();
                            loadMoreStories(storiesAdapter, 0, limit);
                        }

                    });
        } else {
            storiesAdapter.clearData();
            loadMoreStories(storiesAdapter, 0, limit);
        }
    }

    public void loadMoreStories(StoriesAdapter storiesAdapter, int offset, int limit) {
        if (isLoading) {
            Log.i("HackerNews", "Loading more items, won't run.");
            return;
        }
        if (topItems == null){
            loadTopStories(storiesAdapter, false, limit);
        } else {
            isLoading = true;
            int endIndex = offset + limit;
            if (endIndex > topItems.size()) {
                endIndex = topItems.size();
            }
            List<Long> loadingItemIds = topItems.subList(offset, endIndex);
            Log.i("HackerNews", "Loading items: " + loadingItemIds);
            currentSubscription = Observable
                    .fromIterable(loadingItemIds)
                    .subscribeOn(Schedulers.io())
                    .flatMap(itemId -> itemLoader.getItem(itemId, false))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            newItem -> {
                                if (newItem != NewsItem.NULL_ITEM) {
                                    Log.i("HackerNews", "Item index: " + topItems.indexOf(newItem.getId()) + ", Id: " + newItem.getId());
                                    storiesAdapter.addItemMore(topItems.indexOf(newItem.getId()), newItem);
                                }
                            },
                            error -> {
                                // We don't expect any network error here, it's handled
                                // by cachable item loader already (return NULL_ITEM).
                            },
                            () -> {
                                Log.i("HackerNews", "All items is loaded");
                                storiesAdapter.setMoreLoading(false);
                                isLoading = false;
                            });
        }
    }

    public void stop() {
        if (currentSubscription != null) {
            currentSubscription.dispose();
        }
        isLoading = false;
    }
}
