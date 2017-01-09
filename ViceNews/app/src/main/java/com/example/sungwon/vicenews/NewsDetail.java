package com.example.sungwon.vicenews;

/**
 * Created by SungWon on 9/26/2016.
 */
public class NewsDetail {
    private String title;
    private String url;
    NewsImage[] multimedia = new NewsImage[2];

    public NewsImage[] getMultimedia() {
        return multimedia;
    }



    public String getUrl() {
        return url;
    }

    public String getTitle() {
        return title;
    }


}
