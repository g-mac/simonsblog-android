package de.simonmayrshofer.simonsblog.pojos;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

@Table(name = "Articles")
public class Article extends Model {

    @Column(name = "backend_id", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
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

    public List<Comment> comments = new ArrayList<Comment>();

    public List<Comment> comments() {
        return getMany(Comment.class, "Article");
    }

}
