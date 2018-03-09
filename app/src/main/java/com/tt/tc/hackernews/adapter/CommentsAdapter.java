package com.tt.tc.hackernews.adapter;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tt.tc.hackernews.BR;
import com.tt.tc.hackernews.R;
import com.tt.tc.hackernews.model.Comment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by smu (Chau) on 6/3/18.
 */

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.ItemViewHolder> {
    private boolean isMoreLoading;
    private int visibleThreshold = 1;
    private List<Comment> comments;
    private CommentListener listener;

    public CommentsAdapter(CommentListener commentListener) {
        comments = new ArrayList<>();
        this.listener = commentListener;
    }

    public Comment getItems(int position) {
        return position < getItemCount() ? comments.get(position) : null;
    }

    public void clearData() {
        comments.clear();
        notifyDataSetChanged();
    }

    public void addItemMore(Comment newsItem) {
        comments.add(newsItem);
        notifyItemInserted(comments.size() - 1);
    }

    public void setMoreLoading(boolean isMoreLoading) {
        this.isMoreLoading = isMoreLoading;
    }

    public void enableLoadMore(RecyclerView recyclerView) {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int visibleItemCount = recyclerView.getChildCount();
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItem = layoutManager.findFirstVisibleItemPosition();
                if (!isMoreLoading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
                    isMoreLoading = true;
                    listener.onLoadMore();
                }
            }
        });
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ViewDataBinding binding = DataBindingUtil.inflate(layoutInflater, R.layout.comment, parent, false);
        return new ItemViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        holder.bind(getItems(position));
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ViewDataBinding binding;
        private Comment item;

        public ItemViewHolder(ViewDataBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            View view = binding.getRoot();
            view.setOnClickListener(this);
            view.findViewById(R.id.no_reply_tv).setOnClickListener(this);
        }

        public void bind(Comment comment) {
            binding.setVariable(BR.comment, comment);
            binding.executePendingBindings();
            this.item = comment;
        }

        @Override
        public void onClick(View view) {
            listener.onShowReplyClick(item);
        }
    }

    public interface CommentListener {
        void onLoadMore();
        void onShowReplyClick(Comment item);
    }
}
