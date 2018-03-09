package com.tt.tc.hackernews.service;

import android.util.Log;

import com.tt.tc.hackernews.model.Comment;
import com.tt.tc.hackernews.service.api.ItemLoader;
import com.tt.tc.hackernews.adapter.CommentsAdapter;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by smu (Chau) on 6/3/18.
 */

public class CommentLoaderService {
    private CachableCommentLoaderService cachablecommentsLoader;
    private ItemLoader itemLoader;
    private List<Long> commentIds;
    private Long itemId;
    private boolean isLoading;
    private Disposable currentSubscription;

    public CommentLoaderService(Long itemId) {
        cachablecommentsLoader = new CachableCommentLoaderService();
        itemLoader = RestClient.getInstance().getItemLoader();
        this.itemId = itemId;
    }
    public void loadComments(CommentsAdapter commentsAdapter, boolean refresh, int limit) {

        if (refresh || commentIds == null || commentIds.isEmpty()) {
            cachablecommentsLoader.clear();
            currentSubscription = itemLoader.getItem(itemId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(newItems -> {
                        Log.i("HackerNews", "Top items: " + newItems);
                        commentsAdapter.clearData();
                        commentIds = newItems.getKids();
                        loadMoreComment(commentsAdapter, 0, limit);
                    });
        } else {
            commentsAdapter.clearData();
            loadMoreComment(commentsAdapter, 0, limit);
        }
    }

    public void loadMoreComment(CommentsAdapter commentsAdapter, int offset, int limit) {
        if (isLoading) {
            Log.i("HackerNews", "Loading more items, won't run.");
            return;
        }
        if (commentIds != null){
            isLoading = true;
            int endIndex = offset + limit;
            if (endIndex > commentIds.size()) {
                endIndex = commentIds.size();
            }
            List<Long> loadingItemIds = commentIds.subList(offset, endIndex);
            Log.i("HackerNews", "Loading items: " + loadingItemIds);
            currentSubscription = Observable
                    .fromIterable(loadingItemIds)
                    .subscribeOn(Schedulers.io())
                    .flatMap(itemId -> cachablecommentsLoader.getItem(itemId, false))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            comment -> {
                                Log.i("HackerNews", "Item index: " + commentIds.indexOf(comment.getId()) + ", Id: " + comment.getId());
                                if ( comment != Comment.NULL_ITEM) {
                                    commentsAdapter.addItemMore(comment);
                                }
                            },
                            error -> {
                                Log.e("HackerNews", "Cannot get item", error);
                                // We don't expect any network error here, it's handled
                                // by cachable item loader already (return NULL_ITEM).
                            },
                            () -> {
                                Log.i("HackerNews", "All items is loaded");
                                commentsAdapter.setMoreLoading(false);
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
