package de.simonmayrshofer.simonsblog;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.activeandroid.query.Select;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.simonmayrshofer.simonsblog.pojos.Article;
import de.simonmayrshofer.simonsblog.pojos.Comment;
import de.simonmayrshofer.simonsblog.pojos.CommentBody;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ArticleFragment extends Fragment {

    private static String ARTICLE_ID = "ARTICLE_ID";

    public static ArticleFragment newInstance(int articleId) {
        Bundle bundle = new Bundle();
        bundle.putInt(ARTICLE_ID, articleId);
        ArticleFragment articleFragment = new ArticleFragment();
        articleFragment.setArguments(bundle);
        return articleFragment;
    }

//------------------------------------------------------------------------------------------

    @BindView(R.id.fragment_article_date)
    TextView dateView;
    @BindView(R.id.fragment_article_body)
    TextView bodyView;
    @BindView(R.id.fragment_article_title)
    TextView titleView;
    @BindView(R.id.fragment_article_comment_count)
    TextView commentCountView;
    @BindView(R.id.fragment_article_comment_body)
    EditText commentBodyView;

    @BindView(R.id.fragment_article_comments)
    LinearLayout commentsLayout;
    @BindView(R.id.fragment_article_comment)
    LinearLayout commentLayout;


    private int articleId;
    private Article article;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_article, container, false);
        ButterKnife.bind(this, view);

        articleId = getArguments().getInt(ARTICLE_ID);
        article = getArticleWithId(articleId);

        populateView(article);

        boolean loggedIn = PreferenceManager.isLoggedIn(this.getActivity());
        if (!loggedIn)
            commentLayout.setVisibility(View.GONE);


        return view;
    }


    @OnClick(R.id.fragment_article_comment_send)
    public void onSendCommentClick(View v) {

        String commenter = "Anonymous";
        String body = commentBodyView.getText().toString();

        if (body.isEmpty()) {
            Toast.makeText(getActivity(), "Comment cannot be empty.", Toast.LENGTH_SHORT).show();
            return;
        }

        CommentBody commentBody = new CommentBody(commenter, body);

        APIManager.getInstance().createComment(articleId + "", commentBody)
                .subscribeOn(Schedulers.io()) // need to run network call on another bg thread
                .doOnNext(article -> saveData(article)) //save data on bg thread
                .observeOn(AndroidSchedulers.mainThread()) // run onSuccess on UI thread
                .subscribe(article -> {
                    Log.d("SIMON", "onSendCommentClick() SUCCESS");
                    commentBodyView.setText("");
                    populateView(getArticleWithId(article.id));
                }, throwable -> {
                    Log.d("ERROR", throwable.toString());
                });
    }


    private void saveData(Article article) {
        article.save();
        for (Comment comment : article.comments) {
            Log.d("SIMON", "saved a comment for article: " + article.id);
            comment.article = article;
            comment.save();
        }
    }

//------------------------------------------------------------------------------------------

//    public static List<Article> getAll() {
//        return new Select()
//                .from(Article.class)
//                .execute();
//    }

//    private List<Article> getArticlesWithId(int articleId) {
//        return new Select()
//                .from(Article.class)
//                .where("backend_id = ?", articleId)
//                .execute();
//    }

    private Article getArticleWithId(int articleId) {
        return new Select()
                .from(Article.class)
                .where("backend_id = ?", articleId)
                .executeSingle();
    }

    private void populateView(Article article) {
        dateView.setText(Helpers.convertDate(article.createdAt));
        titleView.setText(article.title);
        bodyView.setText(article.text);

//        if (article.comments().size() > 0) {
//            Comment comment = article.comments().get(0);
//            commenterView.setText(comment.commenter);
//            commentBodyView.setText(comment.body);
//            commentDateView.setText(Helpers.convertDate(comment.createdAt));
//        } else {
//            commenterView.setText("No Comments found.");
//        }

        if (article.comments() != null)
            commentCountView.setText(Helpers.getCommentCountString(article.comments().size()));

        populateCommentViews(article);
    }

    private void populateCommentViews(Article article) {

        if (article.comments().size() < 1)
            return;

        commentsLayout.removeAllViews();

        for (Comment comment : article.comments()) {
            LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View newCommentView = layoutInflater.inflate(R.layout.list_item_comment, null);

            TextView dateView = (TextView) newCommentView.findViewById(R.id.list_item_comment_date);
            TextView commenterView = (TextView) newCommentView.findViewById(R.id.list_item_comment_commenter);
            TextView bodyView = (TextView) newCommentView.findViewById(R.id.list_item_comment_body);

            dateView.setText(Helpers.convertDate(comment.createdAt));
            commenterView.setText(comment.commenter);
            bodyView.setText(comment.body);

            commentsLayout.addView(newCommentView);
        }
    }

}
