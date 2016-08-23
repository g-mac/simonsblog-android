package de.simonmayrshofer.simonsblog;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.List;

import de.simonmayrshofer.simonsblog.pojos.Article;
import de.simonmayrshofer.simonsblog.pojos.User;
import de.simonmayrshofer.simonsblog.pojos.login.LoginBody;
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

//        need to add a logging interceptor to see the okhttp request logs
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

//        need to add an interceptor for adding headers to every call that is made
//        Interceptor interceptor = new Interceptor() {
//            @Override
//            public Response intercept(Chain chain) throws IOException {
//                Request original = chain.request();
//                Request request = original.newBuilder()
//                        .header("XXX", "XXX")
//                        .method(original.method(), original.body())
//                        .build();
//                return chain.proceed(request);
//            }
//        };

        OkHttpClient client = new OkHttpClient.Builder()
                .addNetworkInterceptor(loggingInterceptor)
//                .addInterceptor(interceptor)
                .build();

        // needed in order to be able to use pojos for both Active Android and Retrofit/GSON
        Gson gson = new GsonBuilder()
                .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
//                .excludeFieldsWithoutExposeAnnotation()
//                .serializeNulls()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
//                .addConverterFactory(GsonConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create()) //need to add for gson functionality
                .baseUrl(endpoint)
                .client(client)
                .build();

        restAPI = retrofit.create(RestAPI.class);
    }

    public Observable<List<Article>> getArticles(String email, String token) {
        return restAPI.getArticles(email, token);
    }

    public Observable<User> signIn() {
        return restAPI.signIn(new LoginBody("simon@test.de", "simonsimon"));
    }

    public Observable<String> signOut(String email, String token) {
        return restAPI.signOut(email, token);
    }

}