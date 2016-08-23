package de.simonmayrshofer.simonsblog.pojos;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.SerializedName;

@Table(name = "Article")
public class Article extends Model {

    @Column (name = "backend_id")
    public Integer id;
    @Column
    public String title;
    @Column
    public String text;
    @Column
    @SerializedName("created_at")
    public String createdAt;
    @Column
    @SerializedName("updated_at")
    public String updatedAt;

}
