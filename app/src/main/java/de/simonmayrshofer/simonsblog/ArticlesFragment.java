package de.simonmayrshofer.simonsblog;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.simonmayrshofer.simonsblog.events.LoginSuccessEvent;
import de.simonmayrshofer.simonsblog.events.LogoutSuccessEvent;
import de.simonmayrshofer.simonsblog.pojos.Article;
import de.simonmayrshofer.simonsblog.pojos.Comment;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ArticlesFragment extends Fragment {

    public ArticlesFragment() {
    }

    @BindView(R.id.articles_list)
    ListView resultsListView;

    private List<Article> articles;

    MenuItem profilMenuItem;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_articles, container, false);
        ButterKnife.bind(this, view);

        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Simon's Blog");

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

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu, menu);
        profilMenuItem = menu.getItem(0);
        updateMenu();
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_profile:
                ((MainActivity) getActivity()).openProfileFragment();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void updateMenu() {
        if (PreferenceManager.isLoggedIn(getActivity()))
            profilMenuItem.setIcon(getResources().getDrawable(R.drawable.ic_profile));
        else
            profilMenuItem.setIcon(getResources().getDrawable(R.drawable.ic_login));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLoginSuccessEvent(LoginSuccessEvent loginSuccessEvent) {
        updateMenu();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLogoutSuccessEvent(LogoutSuccessEvent logoutSuccessEvent) {
        updateMenu();
    }

    //----------------------------------------------------------------------------------------------

    @OnClick(R.id.articles_button)
    public void onGetArticlesClick(View v) {
        APIManager.getInstance().getArticles()
                .subscribeOn(Schedulers.io()) // need to run network call on another bg thread
                .doOnNext(articles -> saveData(articles)) //save data on bg thread
                .observeOn(AndroidSchedulers.mainThread()) // run onSuccess on UI thread
                .subscribe(articles -> {
                    this.articles = articles;
                    displayResults(articles);
                }, throwable -> {
                    Log.d("ERROR", throwable.toString());
                });
    }

    //----------------------------------------------------------------------------------------------s

    private void displayResults(List<Article> articles) {
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
