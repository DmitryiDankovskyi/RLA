package com.vedro401.reallifeachievement.ui.profile

import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.util.Log
import com.vedro401.reallifeachievement.App
import com.vedro401.reallifeachievement.R
import com.vedro401.reallifeachievement.adapters.SimpleFragmentPagerAdapter
import com.vedro401.reallifeachievement.managers.interfaces.UserManager
import com.vedro401.reallifeachievement.ui.SignInActivity
import com.vedro401.reallifeachievement.ui.interfaces.FakeBottomNavigationOwner
import com.vedro401.reallifeachievement.utils.AUTHTAG
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.layout_profile_tool_bar.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.onClick
import org.jetbrains.anko.startActivity
import javax.inject.Inject

class ProfileActivity : FragmentActivity(), FakeBottomNavigationOwner {
    private lateinit var statisticFragment: ProfileStatisticFragment
    private lateinit var storiesFragment: ProfileStoriesFragment
    private lateinit var unlockedAchFragment: ProfileFinishedStoriesFragment
    override var menuNum: Int = 2

    @Inject
    lateinit var um: UserManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        App.getComponent().inject(this)
        if(!um.isAuthorised) startActivity<SignInActivity>()
        setContentView(R.layout.activity_profile)
        initBottomNavigation(bottom_navigation, this)
        initToolBar()

        um.isAuthorisedObs.subscribe {
            statisticFragment = ProfileStatisticFragment()
            storiesFragment = ProfileStoriesFragment()
            unlockedAchFragment = ProfileFinishedStoriesFragment()

            profile_vp.offscreenPageLimit = 2
            profile_vp.adapter = SimpleFragmentPagerAdapter(arrayListOf(statisticFragment,
                    storiesFragment, unlockedAchFragment),
                    supportFragmentManager,
                    arrayListOf(getString(R.string.profile_statistic),
                            getString(R.string.profile_pinned),
                            getString(R.string.profile_unlocked)))
        }
    }

    private fun initToolBar() {
        //TODO change name immediately when it's changed
//        um.userStatus.subscribe({
//            profile_name.text = um.name()
//        },{ t ->
//            Log.e(AUTHTAG,"ProfileActivity: ${t.message}" )
//        })

        um.isAuthorisedObs.subscribe(
                { _ ->
                   profile_name.text = um.name
                }, { ex ->
                    Log.e(AUTHTAG,"ProfileActivity: ${ex.message}" )
        })
        profile_settings.onClick {
            startActivity<SignInActivity>()
        }
        profile_name.onClick {
            //TODO move it somewhere
            alert {
                title("Are you sure?")
                positiveButton("Sign out") {
                    um.signOut()
                }
                negativeButton("Wait...")
            }.show()
        }
    }
}
