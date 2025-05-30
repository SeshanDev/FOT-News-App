package com.example.fotnewsapp.model;

public class NewsItem {
    public int imageResId;
    public String title;
    public String date;
    public String body;

    // Track if this item is expanded or collapsed
    public boolean isExpanded = false;

    public NewsItem(int imageResId, String title, String date, String body) {
        this.imageResId = imageResId;
        this.title = title;
        this.date = date;
        this.body = body;
    }
}
