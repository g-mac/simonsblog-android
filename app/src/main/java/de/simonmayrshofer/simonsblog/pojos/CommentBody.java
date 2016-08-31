package de.simonmayrshofer.simonsblog.pojos;

public class CommentBody {

    public CommentBody(String commenter, String body) {
        comment = new Comment();
        comment.commenter = commenter;
        comment.body = body;
    }

    public Comment comment;

}