package com.vedro401.reallifeachievement.config;

import com.vedro401.reallifeachievement.adapters.holders.AchievementHolder;
import com.vedro401.reallifeachievement.adapters.holders.MyAchievementHolder;
import com.vedro401.reallifeachievement.model.DataModel;
import com.vedro401.reallifeachievement.ui.BaseActivity;
import com.vedro401.reallifeachievement.ui.BaseFragment;
import com.vedro401.reallifeachievement.ui.BaseFragmentActivity;
import com.vedro401.reallifeachievement.ui.SearchActivity;

import org.jetbrains.annotations.NotNull;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {
        AppModule.class
})
public interface AppComponent {
    void inject(@NotNull BaseActivity baseActivity);
    void inject(@NotNull BaseFragmentActivity baseFragmentActivity);
    void inject(@NotNull BaseFragment baseFragment);
    void inject(@NotNull DataModel dm);

    void inject(@NotNull AchievementHolder achievementHolder);
    void inject(@NotNull MyAchievementHolder myAchievementHolder);
    void inject(@NotNull SearchActivity searchActivity);
}
