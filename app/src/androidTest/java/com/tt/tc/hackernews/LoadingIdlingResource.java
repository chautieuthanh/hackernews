package com.tt.tc.hackernews;

import android.support.test.espresso.IdlingResource;
import android.support.v7.widget.RecyclerView;

/**
 * Created by smu (Chau) on 8/3/18.
 */

public class LoadingIdlingResource implements IdlingResource {
    private ResourceCallback resourceCallback;
    private boolean isIdle;
    private RecyclerView recyclerView;
    private int expectedCount;

    @Override
    public String getName() {
        return LoadingIdlingResource.class.getName();
    }

    @Override
    public boolean isIdleNow() {
        if (isIdle) return true;
        isIdle = false;
        if (recyclerView == null){
            isIdle = true;
        } else {
            RecyclerView.Adapter adapter = recyclerView.getAdapter();
            isIdle = adapter.getItemCount() > expectedCount
                    && recyclerView.getScrollState() == RecyclerView.SCROLL_STATE_IDLE;
        }
        if (isIdle) {
            resourceCallback.onTransitionToIdle();
        }
        return isIdle;
    }

    public void setRecyclerView(RecyclerView recyclerView, int expectedCount){
        this.recyclerView = recyclerView;
        this.expectedCount = expectedCount;
    }


    @Override
    public void registerIdleTransitionCallback(
            ResourceCallback resourceCallback) {
        this.resourceCallback = resourceCallback;
    }
}
