package de.simonmayrshofer.simonsblog;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.progressbar_fullscreen)
    View progressBarView;
    @BindView(R.id.progressbar_fullscreen_text)
    TextView progressBarTextView;


    ArticlesFragment articlesFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        login();
    }

    @Override
    protected void onDestroy() {
        logout();
        super.onDestroy();
    }

    private void openArticlesFragment() {
        articlesFragment = new ArticlesFragment();
        replaceFragment(articlesFragment);
    }

    public void login() {

        //todo
        String email = "simon@test.de";
        String password = "wrongpw";
//        String password = "simonsimon";

        progressBarView.setVisibility(View.VISIBLE);
        progressBarTextView.setText("Trying to log in...");

        APIManager.getInstance().signIn(email, password)
                .subscribeOn(Schedulers.io()) // need to run network call on another bg thread
                .observeOn(AndroidSchedulers.mainThread()) // run onSuccess on UI thread
                .subscribe(user -> {
                    Log.d("SIMON", "SIGN IN SUCCESS, email: " + user.email);
                    Log.d("SIMON", "SIGN IN SUCCESS, authenticationToken: " + user.authenticationToken);
                    PreferenceManager.putString(this, PreferenceManager.PREFS_EMAIL, user.email);
                    PreferenceManager.putString(this, PreferenceManager.PREFS_TOKEN, user.authenticationToken);
                    progressBarView.setVisibility(View.GONE);
                    openArticlesFragment();
                }, throwable -> {
                    Log.d("ERROR", "SIGN IN ERROR: " + throwable.toString());
                    progressBarView.setVisibility(View.GONE);
                    openArticlesFragment();
                });
    }

    public void logout() {

        String email = PreferenceManager.getString(this, PreferenceManager.PREFS_EMAIL);
        String token = PreferenceManager.getString(this, PreferenceManager.PREFS_TOKEN);

        APIManager.getInstance().signOut(email, token)
                .subscribeOn(Schedulers.io()) // need to run network call on another bg thread
                .observeOn(AndroidSchedulers.mainThread()) // run onSuccess on UI thread
                .subscribe(result -> {
                    PreferenceManager.deleteString(this, PreferenceManager.PREFS_EMAIL);
                    PreferenceManager.deleteString(this, PreferenceManager.PREFS_TOKEN);
                    Log.d("SIMON", "SIGN OUT SUCCESS");
                }, throwable -> {
                    Log.d("ERROR", "SIGN OUT ERROR: " + throwable.toString());
                });
    }

    public void replaceFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_activity_content_view, fragment)
                .addToBackStack(fragment.getClass().getSimpleName())
                .commit();
    }
}
