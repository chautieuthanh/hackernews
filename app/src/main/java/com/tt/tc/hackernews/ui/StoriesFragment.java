package com.tt.tc.hackernews.ui;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tt.tc.hackernews.Flow;
import com.tt.tc.hackernews.R;
import com.tt.tc.hackernews.adapter.StoriesAdapter;
import com.tt.tc.hackernews.model.NewsItem;
import com.tt.tc.hackernews.service.ItemLoaderService;

public class StoriesFragment extends Fragment
        implements SwipeRefreshLayout.OnRefreshListener, StoriesAdapter.TopStoriesListener{
    public static final String TAG = "StoriesFragment";
    private static final int LIMIT = 20;
    private int firstVisibleItem;
    private int lastVisibleItem;
    private ItemLoaderService itemLoaderService;
    private StoriesAdapter storiesAdapter;
    private SwipeRefreshLayout swipeRefresh;
    private LinearLayoutManager mLayoutManager;
    private Flow flow;

    public StoriesFragment() {

    }

    public static StoriesFragment newInstance(){
        StoriesFragment storiesFragment = new StoriesFragment();
        storiesFragment.itemLoaderService = new ItemLoaderService();
        return storiesFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.i("HackerNews", this + "is attached.");
        if (context instanceof Flow){
            flow = (Flow) context;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        Log.i("HackerNews", this + "is created.");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i("HackerNews", this + " is view created.");
        View view = inflater.inflate(R.layout.fragment_stories, container, false);
        swipeRefresh= (SwipeRefreshLayout)view.findViewById(R.id.swipeRefresh);
        RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.rvList);
        mLayoutManager = new LinearLayoutManager(container.getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        storiesAdapter = new StoriesAdapter(this);
        itemLoaderService.setStoriesAdapter(storiesAdapter);
        mRecyclerView.setAdapter(storiesAdapter);
        swipeRefresh.setOnRefreshListener(this);
        storiesAdapter.enableLoadMore(mRecyclerView);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.i("HackerNews", this + " is activity created.");
        if(lastVisibleItem > 0){
            itemLoaderService.loadTopStories(false, lastVisibleItem);
        } else {
            itemLoaderService.loadTopStories(false, LIMIT);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i("HackerNews", this + " is started.");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i("HackerNews", this + " is resumed.");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (firstVisibleItem != -1){
                    mLayoutManager.scrollToPositionWithOffset(firstVisibleItem, 3);
                }
            }
        }, 500);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i("HackerNews", this + " is paused.");
        firstVisibleItem = mLayoutManager.findFirstCompletelyVisibleItemPosition();
        lastVisibleItem = mLayoutManager.findLastCompletelyVisibleItemPosition();
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i("HackerNews", this + " is stopped.");
        itemLoaderService.stop();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.i("HackerNews", this + " is detached.");
    }
    @Override
    public void onRefresh() {
        swipeRefresh.setRefreshing(false);
        itemLoaderService.loadTopStories(true, LIMIT);
    }

    @Override
    public void onLoadMore() {
        Log.i("HackerNews", "offset = " + storiesAdapter.getItemCount());
        if (itemLoaderService == null){
            storiesAdapter.setMoreLoading(false);
        } else {
            itemLoaderService.loadMoreStories(storiesAdapter.getItemCount(), LIMIT);
        }

    }

    @Override
    public void onItemClick(NewsItem item) {
        Log.i("HackerNews", "show item");
        if (item.getKids() != null && item.getKids().size() > 0){
            firstVisibleItem = item.getListIndex();
            flow.goTo(true, CommentsFragment.newInstance(item.getId()), CommentsFragment.TAG);
        }

    }

}
