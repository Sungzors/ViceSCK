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

    @Override
    public String toString() {
        return "NewsItem{" +
                "results=" + results[0].getTitle() +
                "\n" + results[0].getPreview() +
                "\n" + results[0].getBody() +
                "\n" + results[0].getAuthor() +
                "\n" + results[0].getUrl() +
                "\n" + results[0].getCategory() +
                "\n" + results[0].getThumb();
    }
}
