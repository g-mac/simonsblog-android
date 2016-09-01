package de.simonmayrshofer.simonsblog;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.simonmayrshofer.simonsblog.events.LoginSuccessEvent;
import de.simonmayrshofer.simonsblog.events.LogoutSuccessEvent;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.progressbar_fullscreen)
    View progressBarView;
    @BindView(R.id.progressbar_fullscreen_text)
    TextView progressBarTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        openArticlesFragment();

        String email = PreferenceManager.getString(this, PreferenceManager.PREFS_EMAIL);
        String password = PreferenceManager.getString(this, PreferenceManager.PREFS_PASSWORD);
        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password))
            login(email, password);
    }

    @Override
    protected void onDestroy() {
        logout();
        super.onDestroy();
    }

    //----------------------------------------------------------------------------------------------

    public void openArticlesFragment() {
        replaceFragment(new ArticlesFragment());
    }

    public void openProfileFragment() {
        replaceFragment(new ProfileFragment());

//        Toast.makeText(this, "openProfileFragment()", Toast.LENGTH_SHORT).show();

//        if (PreferenceManager.isLoggedIn(this))
//            logout();
//        else
//            login();
    }

    public void login(String email, String password) {

//        String email = "simon@test.de";
//        String password = "simonsimon";
//        String password = "wrongpw";

        progressBarView.setVisibility(View.VISIBLE);
        progressBarTextView.setText("logging in...");

        APIManager.getInstance().signIn(email, password)
                .subscribeOn(Schedulers.io()) // need to run network call on another bg thread
                .observeOn(AndroidSchedulers.mainThread()) // run onSuccess on UI thread
                .subscribe(user -> {
                    Log.d("SIMON", "SIGN IN SUCCESS, email: " + user.email);
                    Log.d("SIMON", "SIGN IN SUCCESS, authenticationToken: " + user.authenticationToken);
                    PreferenceManager.putString(this, PreferenceManager.PREFS_EMAIL, user.email);
                    PreferenceManager.putString(this, PreferenceManager.PREFS_TOKEN, user.authenticationToken);
                    progressBarView.setVisibility(View.GONE);
                    EventBus.getDefault().post(new LoginSuccessEvent());
                }, throwable -> {
                    Log.d("ERROR", "SIGN IN ERROR: " + throwable.toString());
                    progressBarView.setVisibility(View.GONE);
                });
    }

    public void logout() {

        progressBarView.setVisibility(View.VISIBLE);
        progressBarTextView.setText("logging out...");

        String email = PreferenceManager.getString(this, PreferenceManager.PREFS_EMAIL);
        String token = PreferenceManager.getString(this, PreferenceManager.PREFS_TOKEN);

        APIManager.getInstance().signOut(email, token)
                .subscribeOn(Schedulers.io()) // need to run network call on another bg thread
                .observeOn(AndroidSchedulers.mainThread()) // run onSuccess on UI thread
                .subscribe(result -> {
                    PreferenceManager.deleteString(this, PreferenceManager.PREFS_TOKEN);
                    progressBarView.setVisibility(View.GONE);
                    EventBus.getDefault().post(new LogoutSuccessEvent());
                    Log.d("SIMON", "SIGN OUT SUCCESS");
                }, throwable -> {
                    progressBarView.setVisibility(View.GONE);
                    Log.d("ERROR", "SIGN OUT ERROR: " + throwable.toString());
                });
    }

    public void replaceFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_activity_content_view, fragment, fragment.getClass().getSimpleName())
                .addToBackStack(fragment.getClass().getSimpleName())
                .commit();
    }
}
