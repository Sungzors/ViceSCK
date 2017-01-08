package com.example.sungwon.vicenews;

/**
 * Created by SungWon on 9/26/2016.
 */
public class NewsDetail {
    private String title;
    private String body;
    private String url;
    NewsImage[] media = new NewsImage[1];

    public String getUrl() {
        return url;
    }

    public String getBody() {
        return body;
    }

    public String getTitle() {
        return title;
    }


}
