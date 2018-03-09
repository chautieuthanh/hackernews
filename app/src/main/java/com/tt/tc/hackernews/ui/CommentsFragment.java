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
import com.tt.tc.hackernews.adapter.CommentsAdapter;
import com.tt.tc.hackernews.model.Comment;
import com.tt.tc.hackernews.service.CommentLoaderService;

public class CommentsFragment extends Fragment implements
        SwipeRefreshLayout.OnRefreshListener, CommentsAdapter.CommentListener {
    public static final String TAG = "CommentsFragment";
    private static final int LIMIT = 10;
    private int firstVisibleItem;
    private int lastVisibleItem;
    private CommentLoaderService commentLoaderService;
    private CommentsAdapter commentsAdapter;
    private SwipeRefreshLayout swipeRefresh;
    private LinearLayoutManager mLayoutManager;
    private Flow flow;

    public CommentsFragment() {

    }

    public static CommentsFragment newInstance(long id){
        CommentsFragment commentsFragment = new CommentsFragment();
        commentsFragment.commentLoaderService = new CommentLoaderService(id);
        return commentsFragment;
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
        View view = inflater.inflate(R.layout.fragment_comments, container, false);
        swipeRefresh= (SwipeRefreshLayout)view.findViewById(R.id.swipeRefresh);
        RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.rvCommentList);
        mLayoutManager = new LinearLayoutManager(container.getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        commentsAdapter = new CommentsAdapter(this);
        commentLoaderService.setCommentsAdapter(commentsAdapter);
        mRecyclerView.setAdapter(commentsAdapter);
        swipeRefresh.setOnRefreshListener(this);
        commentsAdapter.enableLoadMore(mRecyclerView);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.i("HackerNews", this + " is activity created.");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i("HackerNews", this + " is started.");
        if(lastVisibleItem > 0){
            commentLoaderService.loadComments(false, lastVisibleItem);
        } else {
            commentLoaderService.loadComments(false, LIMIT);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i("HackerNews", this + " is resumed.");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (firstVisibleItem != -1){
                    mLayoutManager.scrollToPositionWithOffset(firstVisibleItem, 2);
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
        commentLoaderService.stop();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.i("HackerNews", this + " is detached.");
    }
    @Override
    public void onRefresh() {
        swipeRefresh.setRefreshing(false);
        commentLoaderService.loadComments(true, LIMIT);
    }

    @Override
    public void onLoadMore() {
        Log.i("HackerNews", "offset = " + commentsAdapter.getItemCount());
        if (commentLoaderService == null){
            commentsAdapter.setMoreLoading(false);
        } else {
            commentLoaderService.loadMoreComment(commentsAdapter.getItemCount(), LIMIT);
        }
    }

    @Override
    public void onShowReplyClick(Comment item) {
        if (item.getChilds() != null && item.getChilds().size() > 0){
            flow.goTo(true, CommentsFragment.newInstance(item.getId()), CommentsFragment.TAG + item.getId());
        }
    }

}
