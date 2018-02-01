package com.vedro401.reallifeachievement.config;

import android.content.Context;

import com.vedro401.reallifeachievement.managers.interfaces.DatabaseManager;
import com.vedro401.reallifeachievement.managers.FirebaseManager;
import com.vedro401.reallifeachievement.managers.StorageManager;
import com.vedro401.reallifeachievement.managers.FireUserManager;
import com.vedro401.reallifeachievement.managers.interfaces.UserManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {
    private Context context;
    private FireUserManager userManager = new FireUserManager();
    private FirebaseManager firebaseManager = new FirebaseManager(userManager);
    private StorageManager storageManager = new StorageManager();

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
    AppPreference provideAppPreference(Context context) {
        return new AppPreference(context);
    }

    @Provides
    @Singleton
    StorageManager provideStorageManager(){
        return storageManager;
    }


    @Provides
    @Singleton
    UserManager provideUserManager(){ return userManager;}


    @Provides
    @Singleton
    DatabaseManager provideDatabase(){
        return firebaseManager;
    }


    //For search
    @Provides
    @Singleton
    FirebaseManager provideFirebaseManager(){
        return firebaseManager;
    }

}
