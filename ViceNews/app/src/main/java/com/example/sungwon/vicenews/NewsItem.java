package com.example.sungwon.vicenews;

/**
 * Created by SungWon on 9/26/2016.
 */
public class NewsItem {

    NewsDetail[] results = new NewsDetail[30];

    public NewsDetail[] getResults() {
        return results;
    }

    public void setResults(NewsDetail[] results) {
        this.results = results;
    }


}
