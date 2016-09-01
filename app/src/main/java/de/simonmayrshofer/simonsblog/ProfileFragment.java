package de.simonmayrshofer.simonsblog;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.simonmayrshofer.simonsblog.events.LoginSuccessEvent;
import de.simonmayrshofer.simonsblog.events.LogoutSuccessEvent;

public class ProfileFragment extends Fragment {

    private static String ARTICLE_ID = "ARTICLE_ID";

    private static String ACTIONBAR_TITLE_LOGIN = "Login";
    private static String ACTIONBAR_TITLE_PROFILE = "Profile";

    public static ProfileFragment newInstance(int articleId) {
        Bundle bundle = new Bundle();
        bundle.putInt(ARTICLE_ID, articleId);
        ProfileFragment articleFragment = new ProfileFragment();
        articleFragment.setArguments(bundle);
        return articleFragment;
    }

//------------------------------------------------------------------------------------------

    @BindView(R.id.profile_login_email)
    EditText loginEmailView;
    @BindView(R.id.profile_login_password)
    EditText loginPasswordView;
    @BindView(R.id.profile_login_checkbox)
    CheckBox loginCheckBox;
    @BindView(R.id.profile_login_button)
    Button loginButton;
    @BindView(R.id.profile_logout_text)
    TextView logoutTextView;
    @BindView(R.id.profile_logout_email)
    TextView logoutEmailView;
    @BindView(R.id.profile_logout_button)
    Button logoutButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, view);

//        ((MainActivity) getActivity()).getSupportActionBar().hide();

        updateViews();

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

    private void updateViews() {
        if (PreferenceManager.isLoggedIn(getActivity())) {
            ((MainActivity) getActivity()).getSupportActionBar().setTitle(ACTIONBAR_TITLE_PROFILE);

            //hide login views
            loginEmailView.setVisibility(View.GONE);
            loginPasswordView.setVisibility(View.GONE);
            loginCheckBox.setVisibility(View.GONE);
            loginButton.setVisibility(View.GONE);

            //set logout views
            logoutTextView.setVisibility(View.VISIBLE);
            logoutEmailView.setVisibility(View.VISIBLE);
            logoutButton.setVisibility(View.VISIBLE);
            String email = PreferenceManager.getString(getActivity(), PreferenceManager.PREFS_EMAIL);
            logoutEmailView.setText(email);
        } else {
            ((MainActivity) getActivity()).getSupportActionBar().setTitle(ACTIONBAR_TITLE_LOGIN);

            //hide logout views
            logoutTextView.setVisibility(View.GONE);
            logoutButton.setVisibility(View.GONE);
            logoutEmailView.setVisibility(View.GONE);
            logoutEmailView.setText("");

            //set login views
            loginCheckBox.setVisibility(View.VISIBLE);
            loginEmailView.setVisibility(View.VISIBLE);
            loginPasswordView.setVisibility(View.VISIBLE);
            loginButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        super.onCreateOptionsMenu(menu, inflater);
    }

    @OnClick(R.id.profile_login_button)
    public void onLoginClick(View v) {
        String email = loginEmailView.getText().toString();
        String password = loginPasswordView.getText().toString();
        if (!(TextUtils.isEmpty(email) || TextUtils.isEmpty(password)))
            ((MainActivity) getActivity()).login(email, password);
    }

    @OnClick(R.id.profile_logout_button)
    public void onLogoutClick(View v) {
        ((MainActivity) getActivity()).logout();
    }

    //    @Subscribe
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLoginSuccessEvent(LoginSuccessEvent loginSuccessEvent) {
        if (loginCheckBox.isChecked()) {
            String email = loginEmailView.getText().toString();
            String password = loginPasswordView.getText().toString();
            PreferenceManager.putString(getActivity(), PreferenceManager.PREFS_EMAIL, email);
            PreferenceManager.putString(getActivity(), PreferenceManager.PREFS_PASSWORD, password);
        }
        updateViews();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLogoutSuccessEvent(LogoutSuccessEvent logoutSuccessEvent) {
        PreferenceManager.deleteString(getActivity(), PreferenceManager.PREFS_EMAIL);
        PreferenceManager.deleteString(getActivity(), PreferenceManager.PREFS_PASSWORD);
        updateViews();
    }

}
