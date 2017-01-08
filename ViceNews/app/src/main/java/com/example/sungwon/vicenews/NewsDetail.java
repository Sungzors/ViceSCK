package com.example.sungwon.vicenews;

/**
 * Created by SungWon on 9/26/2016.
 */
public class NewsDetail {
    private String title;
    private String url;

    public NewsImage[] getMultimedia() {
        return multimedia;
    }

    NewsImage[] multimedia = new NewsImage[2];

    public String getUrl() {
        return url;
    }

    public String getTitle() {
        return title;
    }


}
