package de.simonmayrshofer.simonsblog;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.activeandroid.query.Select;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.simonmayrshofer.simonsblog.pojos.Article;

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


    private int articleId;
    private Article article;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_article, container, false);
        ButterKnife.bind(this, view);

        articleId = getArguments().getInt(ARTICLE_ID);
        article = getArticleWithId(articleId);

        populateView(article);

        return view;
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
    }

}
