package de.simonmayrshofer.simonsblog;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import de.simonmayrshofer.simonsblog.pojos.Article;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;

public class APIManager {

    String endpoint = "https://obscure-oasis-27772.herokuapp.com";

    RestAPI restAPI;

    public APIManager() {

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        Gson gson = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .baseUrl(endpoint)
                .client(client)
                .build();

        restAPI = retrofit.create(RestAPI.class);
    }

    public Observable<List<Article>> getArticles() {
        return restAPI.getArticles();
    }

}
