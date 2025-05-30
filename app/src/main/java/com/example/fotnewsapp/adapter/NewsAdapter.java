package com.example.fotnewsapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fotnewsapp.R;
import com.example.fotnewsapp.model.NewsItem;

import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {

    private List<NewsItem> newsList;

    public NewsAdapter(List<NewsItem> newsList) {
        this.newsList = newsList;
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_news_card, parent, false);
        return new NewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        NewsItem news = newsList.get(position);
        holder.image.setImageResource(news.imageResId);
        holder.title.setText(news.title);
        holder.date.setText(news.date);
        holder.body.setText(news.body);

        // Use the isExpanded flag from the model to determine current state
        if (news.isExpanded) {
            holder.body.setMaxLines(Integer.MAX_VALUE); // expanded
            holder.readMore.setText("Read less");
        } else {
            holder.body.setMaxLines(3); // collapsed
            holder.readMore.setText("Read more ...");
        }

        holder.readMore.setOnClickListener(v -> {
            // Toggle the state in the model
            news.isExpanded = !news.isExpanded;
            // Notify the adapter to refresh this item
            notifyItemChanged(position);
        });
    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }

    public static class NewsViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView title, date, body, readMore;

        public NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.news_image);
            title = itemView.findViewById(R.id.news_title);
            date = itemView.findViewById(R.id.news_date);
            body = itemView.findViewById(R.id.news_body);
            readMore = itemView.findViewById(R.id.read_more);
        }
    }
}
