package de.simonmayrshofer.simonsblog.pojos;

import com.google.gson.annotations.SerializedName;

public class Article {

    public Integer id;
    public String title;
    public String text;
    @SerializedName("created_at")
    public String createdAt;
    @SerializedName("updated_at")
    public String updatedAt;

}
