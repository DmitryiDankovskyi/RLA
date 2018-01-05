package com.vedro401.reallifeachievement.utils;

import com.vedro401.reallifeachievement.adapters.holders.AchievementHolder;
import com.vedro401.reallifeachievement.managers.FireUserManager;
import com.vedro401.reallifeachievement.model.Achievement;
import com.vedro401.reallifeachievement.model.DataModel;
import com.vedro401.reallifeachievement.ui.BaseActivity;
import com.vedro401.reallifeachievement.ui.FeedActivity;
import com.vedro401.reallifeachievement.ui.SearchActivity;
import com.vedro401.reallifeachievement.ui.SignInActivity;
import com.vedro401.reallifeachievement.ui.SignUpActivity;
import com.vedro401.reallifeachievement.ui.create.CreateActivity;
import com.vedro401.reallifeachievement.ui.profile.ProfileActivity;
import com.vedro401.reallifeachievement.ui.profile.ProfileStatisticFragment;
import com.vedro401.reallifeachievement.ui.profile.ProfileStoriesFragment;
import com.vedro401.reallifeachievement.ui.profile.ProfileFinishedStoriesFragment;

import org.jetbrains.annotations.NotNull;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {
        AppModule.class
})
public interface AppComponent {
    void inject(BaseActivity ba);

    void inject(AchievementHolder achievementHolder);
    void inject(Achievement ach);
    void inject(DataModel dm);
    void inject(SearchActivity searchActivity);
    void inject(SignInActivity logInActivity);

    void inject(@NotNull SignUpActivity signInActivity);

    void inject(@NotNull FireUserManager userManager);

    void inject(@NotNull ProfileActivity profileActivity);

    void inject(@NotNull ProfileStoriesFragment profilePinnedAchFragment);

    void inject(@NotNull CreateActivity createActivity);

    void inject(@NotNull FeedActivity feedActivity);

    void inject(@NotNull ProfileFinishedStoriesFragment profileUnlockedAchFragment);

    void inject(@NotNull ProfileStatisticFragment profileStatisticFragment);
}
