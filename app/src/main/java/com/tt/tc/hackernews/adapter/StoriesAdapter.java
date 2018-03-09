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
import com.tt.tc.hackernews.model.NewsItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by smu (Chau) on 7/3/18.
 */

public class StoriesAdapter extends RecyclerView.Adapter<StoriesAdapter.ItemViewHolder> {
    private boolean isMoreLoading;
    private int visibleThreshold = 1;
    private List<NewsItem> items;
    private TopStoriesListener listener;

    public StoriesAdapter(TopStoriesListener topStoriesListener) {
        items = new ArrayList<>();
        this.listener = topStoriesListener;
    }

    public NewsItem getItems(int position) {
        return position < getItemCount() ? items.get(position) : null;
    }

    public void clearData() {
        items.clear();
        notifyDataSetChanged();
    }

    public void addItemMore(int index, NewsItem newsItem) {
        newsItem.setListIndex(index + 1);
        if(index >= getItemCount()){
            items.add(newsItem);
            notifyDataSetChanged();
        } else {
            items.add(index, newsItem);
            notifyItemInserted(getItemCount() -1);
        }
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
        ViewDataBinding viewDataBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext())
                , R.layout.story_item, parent, false);
        return new ItemViewHolder(viewDataBinding);
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        holder.bind(getItems(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ViewDataBinding binding;
        private NewsItem item;

        public ItemViewHolder(ViewDataBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            View view = binding.getRoot();
            view.setOnClickListener(this);
            view.findViewById(R.id.comment_tv).setOnClickListener(this);

        }

        public void bind(NewsItem newsItem) {
            binding.setVariable(BR.item,newsItem);
            binding.executePendingBindings();
            this.item = newsItem;
        }

        @Override
        public void onClick(View view) {
            listener.onItemClick(item);
        }
    }

    public interface TopStoriesListener {
        void onLoadMore();
        void onItemClick(NewsItem item);
    }
}
