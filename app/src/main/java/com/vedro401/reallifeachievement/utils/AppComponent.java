package com.vedro401.reallifeachievement.utils;

import com.vedro401.reallifeachievement.adapters.AchievementHolder;
import com.vedro401.reallifeachievement.adapters.StoryHolder;
import com.vedro401.reallifeachievement.adapters.StoryPostHolder;
import com.vedro401.reallifeachievement.model.Achievement;
import com.vedro401.reallifeachievement.model.DataModel;
import com.vedro401.reallifeachievement.view.BaseActivity;
import com.vedro401.reallifeachievement.view.FeedActivity;
import com.vedro401.reallifeachievement.view.SignInActivity;
import com.vedro401.reallifeachievement.view.create.CreateActivity;
import com.vedro401.reallifeachievement.view.SearchActivity;
import com.vedro401.reallifeachievement.view.SignUpActivity;
import com.vedro401.reallifeachievement.view.profile.ProfileActivity;
import com.vedro401.reallifeachievement.view.profile.ProfileStoriesFragment;

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

    void inject(@NotNull UserManager userManager);

    void inject(@NotNull ProfileActivity profileActivity);

    void inject(@NotNull ProfileStoriesFragment profilePinnedAchFragment);

    void inject(@NotNull CreateActivity createActivity);

    void inject(@NotNull FeedActivity feedActivity);

    void inject(StoryHolder storyHolder);

    void inject(@NotNull StoryPostHolder storyPostHolder);
}
