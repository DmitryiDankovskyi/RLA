package com.vedro401.reallifeachievement.view.profile

import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.util.Log
import com.vedro401.reallifeachievement.App
import com.vedro401.reallifeachievement.R
import com.vedro401.reallifeachievement.adapters.SimpleFragmentPagerAdapter
import com.vedro401.reallifeachievement.database.DatabaseManager
import com.vedro401.reallifeachievement.utils.LOGTAG
import com.vedro401.reallifeachievement.utils.UserManager
import com.vedro401.reallifeachievement.view.SignInActivity
import com.vedro401.reallifeachievement.view.interfaces.FakeBottomNavigationOwner
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.layout_profile_tool_bar.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.onClick
import org.jetbrains.anko.startActivity
import javax.inject.Inject

class ProfileActivity : FragmentActivity(), FakeBottomNavigationOwner {
    private lateinit var statisticFragment: ProfileStatisticFragment
    private lateinit var storiesFragment: ProfileStoriesFragment
    private lateinit var unlockedAchFragment: ProfileUnlockedAchFragment
    override var menuNum: Int = 2

    @Inject
    lateinit var dbm: DatabaseManager
    @Inject
    lateinit var userManager: UserManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        initBottomNavigation(bottom_navigation, this)
        App.getComponent().inject(this)

        initToolBar()

        statisticFragment = ProfileStatisticFragment()
        storiesFragment = ProfileStoriesFragment()
        unlockedAchFragment = ProfileUnlockedAchFragment()



        profile_vp.adapter = SimpleFragmentPagerAdapter(arrayListOf(statisticFragment,
                storiesFragment, unlockedAchFragment),
                supportFragmentManager,
                arrayListOf(getString(R.string.profile_statistic),
                            getString(R.string.profile_pinned),
                            getString(R.string.profile_unlocked)))
    }

    private fun initToolBar(){
        userManager.userStatus.subscribe({
            profile_name.text = userManager.name()
        },{ t ->
            Log.e(LOGTAG,"ProfileActivity: ${t.message}" )
        })
        profile_icon.onClick {
            startActivity<SignInActivity>()
        }
        profile_name.onClick {
            //TODO move it somewhere
            alert {
                title("Are you sure?")
                positiveButton("Sign out"){
                    dbm.signOut()
                }
                negativeButton("Wait...")
            }.show()
        }
    }
}
