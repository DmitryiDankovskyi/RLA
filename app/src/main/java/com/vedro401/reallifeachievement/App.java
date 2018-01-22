package com.vedro401.reallifeachievement;

import android.app.Application;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.vedro401.reallifeachievement.config.AppComponent;
import com.vedro401.reallifeachievement.config.AppModule;
import com.vedro401.reallifeachievement.config.DaggerAppComponent;

/**
 * Created by someone on 16.09.17.
 * hello from new hdd
 */

public class App extends Application {

    private static AppComponent component;
    public static AppComponent getComponent() {
        return component;
    }
        @Override
        public void onCreate() {
            super.onCreate();
            component = DaggerAppComponent.builder().appModule(new AppModule(this)).build();
            FirebaseAuth.getInstance().addAuthStateListener(new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    Log.d("FMan", String.valueOf(firebaseAuth.getCurrentUser() == null));
                }
            });
    }
}
