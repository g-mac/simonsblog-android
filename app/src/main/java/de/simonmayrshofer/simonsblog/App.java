package de.simonmayrshofer.simonsblog;

import android.app.Application;

import com.activeandroid.ActiveAndroid;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // Here you start using the ActiveAndroid library.
        ActiveAndroid.initialize(this);
//        ActiveAndroid.dispose();
    }
}
