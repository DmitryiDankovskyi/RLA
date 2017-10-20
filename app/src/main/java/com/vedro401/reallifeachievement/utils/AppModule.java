package com.vedro401.reallifeachievement.utils;

import android.content.Context;

import com.vedro401.reallifeachievement.database.DatabaseManager;
import com.vedro401.reallifeachievement.database.FirebaseManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {
    private Context context;
    private FirebaseManager firebaseManager = new FirebaseManager();
    private UserManager userManager = new UserManager(provideDatabase());

    public AppModule(Context context) {
        this.context = context;
    }

    @Provides
    @Singleton
    Context provideContext() {
        return context;
    }

    @Provides
    @Singleton
    DatabaseManager provideDatabase(){
        return firebaseManager;
    }

    @Provides
    @Singleton
    FirebaseManager provideFirebaseDatabase(){
        return firebaseManager;
    }

    @Provides
    @Singleton
    UserManager provideCurrentUser(){ return userManager;}



}
