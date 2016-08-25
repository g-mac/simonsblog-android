package de.simonmayrshofer.simonsblog;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.simonmayrshofer.simonsblog.pojos.Article;
import de.simonmayrshofer.simonsblog.pojos.Comment;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ArticlesFragment extends Fragment {

    public ArticlesFragment() {
    }

    @BindView(R.id.articles_results)
    TextView resultsView;
    @BindView(R.id.articles_list)
    ListView resultsListView;

    APIManager apiManager;

    private String userEmail;
    private String userToken;

    private List<Article> articles;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_articles, container, false);
        ButterKnife.bind(this, view);

        apiManager = new APIManager();
        resultsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                int articleId = ((Article) resultsListView.getAdapter().getItem(i)).id;
                openArticleFragment(articleId);
            }
        });

        articles = loadArticles();
        if (articles.size() > 0)
            displayResults(articles);

        onGetArticlesClick(null);

        return view;
    }

    //----------------------------------------------------------------------------------------------

    @OnClick(R.id.articles_button)
    public void onGetArticlesClick(View v) {
        resultsView.setVisibility(View.VISIBLE);
        apiManager.getArticles(userEmail, userToken)
                .subscribeOn(Schedulers.io()) // need to run network call on another bg thread
                .doOnNext(articles -> saveData(articles)) //save data on bg thread
                .observeOn(AndroidSchedulers.mainThread()) // run onSuccess on UI thread
                .subscribe(articles -> {
                    this.articles = articles;
                    displayResults(articles);
                }, throwable -> {
                    resultsView.setText("API CALL FAILURE: " + throwable.toString());
                    Log.d("ERROR", throwable.toString());
                });
    }

    @OnClick(R.id.login_button)
    public void onLoginClick(View v) {
        resultsView.setVisibility(View.VISIBLE);
        apiManager.signIn()
                .subscribeOn(Schedulers.io()) // need to run network call on another bg thread
                .observeOn(AndroidSchedulers.mainThread()) // run onSuccess on UI thread
                .subscribe(user -> {
                    resultsView.setText("SIGN IN SUCCESS, email: " + user.email);
                    Log.d("SIMON", "SIGN IN SUCCESS, email: " + user.email);
                    Log.d("SIMON", "SIGN IN SUCCESS, authenticationToken: " + user.authenticationToken);
                    userEmail = user.email;
                    userToken = user.authenticationToken;
                }, throwable -> {
                    resultsView.setText("SIGN IN ERROR: " + throwable.toString());
                    Log.d("ERROR", "SIGN IN ERROR: " + throwable.toString());
                });
    }

    @OnClick(R.id.logout_button)
    public void onLogoutClick(View v) {
        resultsView.setVisibility(View.VISIBLE);
        apiManager.signOut(userEmail, userToken)
                .subscribeOn(Schedulers.io()) // need to run network call on another bg thread
                .observeOn(AndroidSchedulers.mainThread()) // run onSuccess on UI thread
                .subscribe(result -> {
                    userEmail = "";
                    userToken = "";
                    resultsView.setText("SIGN OUT SUCCESS");
                    Log.d("SIMON", "SIGN OUT SUCCESS");
                }, throwable -> {
                    resultsView.setText("SIGN OUT ERROR: " + throwable.toString());
                    Log.d("ERROR", "SIGN OUT ERROR: " + throwable.toString());
                });
    }

    //----------------------------------------------------------------------------------------------s

    private void displayResults(List<Article> articles) {
        resultsView.setVisibility(View.GONE);
        resultsListView.setAdapter(new ArticlesListAdapter(getActivity(), articles));
    }

    //----------------------------------------------------------------------------------------------

    public static List<Article> loadArticles() {
        return new Select()
                .from(Article.class)
                .execute();
    }

    private void saveData(List<Article> articles) {

//        Log.d("SIMON", "articles.size(): " + articles.size());
//        Log.d("SIMON", "articles.get(0).getId: " + articles.get(0).id);

        new Delete().from(Article.class).execute(); // delete all existing records

        for (Article article : articles) {
            article.save();
            for (Comment comment : article.comments) {
                comment.article = article;
                comment.save();
            }
        }

        int savedArticles = (new Select()
                .from(Article.class)
                .execute()).size();

        int savedComments = (new Select()
                .from(Comment.class)
                .execute()).size();

        Log.d("SIMON", "savedArticles: " + savedArticles + ", savedComments: " + savedComments);


//        for (Hotspot hotspot : hotspots) {
//            hotspot.save();
//            Position position = hotspot.getPosition();
//            position.hotspot = hotspot;
//            position.save();
//            for (Translation translation : hotspot.getTranslations()) {
//                translation.hotspot = hotspot;
//                translation.save();
//            }
//        }
    }

    //----------------------------------------------------------------------------------------------

    private void openArticleFragment(int articleId) {
        ((MainActivity) getActivity()).replaceFragment(ArticleFragment.newInstance(articleId));
    }

}
