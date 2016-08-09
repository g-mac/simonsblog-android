package de.simonmayrshofer.simonsblog;

import java.util.List;

import de.simonmayrshofer.simonsblog.pojos.Article;
import retrofit2.http.GET;
import rx.Observable;

public interface RestAPI {

    String API_ARTICLES = "/api/v1/articles";

    @GET(API_ARTICLES)
    Observable<List<Article>> getArticles();

}