package com.tt.tc.hackernews.service;

import android.util.Log;

import com.tt.tc.hackernews.model.Comment;
import com.tt.tc.hackernews.model.NewsItem;
import com.tt.tc.hackernews.service.api.ItemLoader;
import com.tt.tc.hackernews.adapter.CommentsAdapter;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
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
    private CommentsAdapter commentsAdapter;

    public CommentLoaderService(Long itemId) {
        cachablecommentsLoader = new CachableCommentLoaderService();
        itemLoader = RestClient.getInstance().getItemLoader();
        this.itemId = itemId;
    }

    public void setCommentsAdapter(CommentsAdapter commentsAdapter) {
        this.commentsAdapter = commentsAdapter;
    }

    public void loadComments(boolean refresh, final int limit) {
        if (refresh || commentIds == null || commentIds.isEmpty()) {
            cachablecommentsLoader.clear();
            currentSubscription = itemLoader.getItem(itemId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<NewsItem>() {
                        @Override
                        public void accept(@NonNull NewsItem newItems) throws Exception {
                            Log.i("HackerNews", "Top items: " + newItems);
                            commentsAdapter.clearData();
                            commentIds = newItems.getKids();
                            loadMoreComment(0, limit);
                        }
                    });
        } else {
            commentsAdapter.clearData();
            loadMoreComment(0, limit);
        }
    }

    public void loadMoreComment(int offset, int limit) {
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
                    .flatMap(new Function<Long, ObservableSource<?>>() {
                        @Override
                        public ObservableSource<?> apply(@NonNull Long itemId) throws Exception {
                            return cachablecommentsLoader.getItem(itemId, false);
                        }
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<Object>() {
                                   @Override
                                   public void accept(@NonNull Object comment) throws Exception {

                                       if (comment != Comment.NULL_ITEM) {
                                           Log.i("HackerNews", "Item index: Id: " + ((Comment) comment).getId());
                                           commentsAdapter.addItemMore((Comment) comment);
                                       }
                                   }
                               },
                            new Consumer<Throwable>() {
                                @Override
                                public void accept(@NonNull Throwable throwable) throws Exception {
                                    Log.e("HackerNews", "Cannot get item", throwable);
                                }
                            },
                            new Action() {
                                @Override
                                public void run() throws Exception {
                                    Log.i("HackerNews", "All items is loaded");
                                    commentsAdapter.setMoreLoading(false);
                                    isLoading = false;
                                }
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
