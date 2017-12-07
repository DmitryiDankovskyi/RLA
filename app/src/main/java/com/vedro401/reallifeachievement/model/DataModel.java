package com.vedro401.reallifeachievement.model;

import com.google.firebase.database.Exclude;
import com.vedro401.reallifeachievement.App;
import com.vedro401.reallifeachievement.database.DatabaseManager;
import com.vedro401.reallifeachievement.utils.UserManager;

import javax.inject.Inject;


abstract public class DataModel {

    @Exclude
    @Inject
    DatabaseManager databaseManager;

    @Exclude
    @Inject
    UserManager userManager;

    @Exclude
    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    @Exclude
    public UserManager getUserManager() {
        return userManager;
    }

    public DataModel(){
        App.getComponent().inject(this);
    }


//    This method need for cleaning all subscriptions and listeners
    public void clear(){}
}
