package de.simonmayrshofer.simonsblog;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.simonmayrshofer.simonsblog.pojos.Article;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ArticlesFragment extends Fragment {

    public ArticlesFragment() {
    }

    @BindView(R.id.articles_results)
    TextView resultsView;
    @BindView(R.id.articles_list)
    ListView resultsListView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_articles, container, false);
        ButterKnife.bind(this, view);

        APIManager apiManager = new APIManager();
        apiManager.getArticles()
//                .subscribeOn(Schedulers.newThread())
                .subscribeOn(Schedulers.io()) // need to run network call on another bg thread
//                .doOnNext(hotspots -> saveData(hotspots)) //save data on bg thread
                .observeOn(AndroidSchedulers.mainThread()) // run onSuccess on UI thread
                .subscribe(articles -> {
                    displayResults(articles);
                }, throwable -> {
                    resultsView.setText("API CALL FAILURE: " + throwable.toString());
                    resultsView.setVisibility(View.VISIBLE);
                    Log.d("ERROR", throwable.toString());
                });

        return view;
    }

    //----------------------------------------------------------------------------------------------

    private void displayResults(List<Article> results) {
        resultsListView.setAdapter(new ArticlesListAdapter(getActivity(), results));
    }
}
