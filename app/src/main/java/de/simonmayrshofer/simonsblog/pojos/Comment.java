package de.simonmayrshofer.simonsblog.pojos;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.SerializedName;

@Table(name = "Comments")
public class Comment extends Model {

    @Column
    public String commenter;
    @Column
    public String body;
    @Column
    @SerializedName("created_at")
    public String createdAt;
    @Column
    @SerializedName("updated_at")
    public String updatedAt;

    @Column(name = "Article", onDelete = Column.ForeignKeyAction.CASCADE)
    public Article article;
}
