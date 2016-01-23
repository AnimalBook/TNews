package com.example.kokpheng.tnews;

import com.google.gson.annotations.SerializedName;
/**
 * Created by kokpheng on 23-Jan-16.
 */
public class News {
    @SerializedName("uid")
    public int uid;

    @SerializedName("title")
    public String title;

    @SerializedName("publish_date")
    public String publish_date;

    @SerializedName("description")
    public String description;

    @SerializedName("image_url")
    public String image_url;
}
