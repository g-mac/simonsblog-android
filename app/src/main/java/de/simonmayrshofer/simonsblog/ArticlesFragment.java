package de.simonmayrshofer.simonsblog;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ArticlesFragment extends Fragment {

    public ArticlesFragment() {
    }

    @BindView(R.id.articles_results)
    TextView resultsView;

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
                    displayResults("API CALL SUCCESS");
                }, throwable -> {
                    displayResults("API CALL FAILURE");
                    Log.d("ERROR", throwable.toString());
                });

        return view;
    }

    //----------------------------------------------------------------------------------------------

    private void displayResults(String results) {
        resultsView.setText(results);
    }
}
