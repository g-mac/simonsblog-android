package de.simonmayrshofer.simonsblog;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.activeandroid.query.Select;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.simonmayrshofer.simonsblog.events.GetArticlesCallbackEvent;
import de.simonmayrshofer.simonsblog.events.LoginSuccessEvent;
import de.simonmayrshofer.simonsblog.events.LogoutSuccessEvent;
import de.simonmayrshofer.simonsblog.pojos.Article;

public class ArticlesFragment extends Fragment {

    private static String ACTIONBAR_TITLE_ARTICLES = "Simon's Blog";

    @BindView(R.id.articles_last_updated)
    TextView lastUpdateView;
    @BindView(R.id.articles_list)
    ListView resultsListView;
    @BindView(R.id.articles_swipe_container)
    SwipeRefreshLayout swipeRefreshLayout;

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
        ((MainActivity) getActivity()).getSupportActionBar().setTitle(ACTIONBAR_TITLE_ARTICLES);

        loadArticlesAndDisplay();

        resultsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                int articleId = ((Article) resultsListView.getAdapter().getItem(i)).id;
                openArticleFragment(articleId);
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                ((MainActivity) getActivity()).getArticles();
            }
        });

        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_dark);

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

    //----------------------------------------------------------------------------------------------

    private void loadArticlesAndDisplay() {

        String lastUpdate = PreferenceManager.getString(getActivity(), PreferenceManager.PREFS_LAST_ARTICLES_UPDATE);
        if (!TextUtils.isEmpty(lastUpdate)) {
            lastUpdateView.setText(lastUpdate);
            lastUpdateView.setVisibility(View.VISIBLE);
        }

        articles = loadArticles();
        if (articles.size() > 0)
            displayResults(articles);
    }

    public static List<Article> loadArticles() {
        return new Select()
                .from(Article.class)
                .execute();
    }

    private void displayResults(List<Article> articles) {
        resultsListView.setAdapter(new ArticlesListAdapter(getActivity(), articles));
    }

    //----------------------------------------------------------------------------------------------

    private void openArticleFragment(int articleId) {
        ((MainActivity) getActivity()).replaceFragment(ArticleFragment.newInstance(articleId));
    }

    //----------------------------------------------------------------------------------------------

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onArticlesUpdatedEvent(GetArticlesCallbackEvent getArticlesCallbackEvent) {
        swipeRefreshLayout.setRefreshing(false);
        if (getArticlesCallbackEvent.success)
            loadArticlesAndDisplay();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLoginSuccessEvent(LoginSuccessEvent loginSuccessEvent) {
        updateMenu();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLogoutSuccessEvent(LogoutSuccessEvent logoutSuccessEvent) {
        updateMenu();
    }

}
