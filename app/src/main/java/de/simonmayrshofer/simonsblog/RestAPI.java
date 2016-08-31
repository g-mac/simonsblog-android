package de.simonmayrshofer.simonsblog;

import java.util.List;

import de.simonmayrshofer.simonsblog.pojos.Article;
import de.simonmayrshofer.simonsblog.pojos.CommentBody;
import de.simonmayrshofer.simonsblog.pojos.User;
import de.simonmayrshofer.simonsblog.pojos.UserBody;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import rx.Observable;

public interface RestAPI {

    String API_COMMENTS = "/api/v1/articles/" + "{id}" + "/comments";
    String API_ARTICLES = "/api/v1/articles";
    String API_SIGN_IN = "/users/sign_in.json";
    String API_SIGN_OUT = "/users/sign_out.json";

    @POST(API_COMMENTS)
    Observable<Article> createComment(@Path("id") String articleId, @Body CommentBody commentBody);

    @GET(API_ARTICLES)
    Observable<List<Article>> getArticles();
//    Observable<List<Article>> getArticles(@Header("X-User-Email") String email, @Header("X-User-Token") String token);

    @POST(API_SIGN_IN)
    Observable<User> signIn(@Body UserBody userBody);

    @DELETE(API_SIGN_OUT)
    Observable<String> signOut(@Header("X-User-Email") String email, @Header("X-User-Token") String token);

}