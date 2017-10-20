package com.vedro401.reallifeachievement.view.create

import android.os.Bundle
import android.support.v4.app.FragmentActivity
import com.vedro401.reallifeachievement.App
import com.vedro401.reallifeachievement.R
import com.vedro401.reallifeachievement.adapters.SimpleFragmentPagerAdapter
import com.vedro401.reallifeachievement.model.Achievement
import com.vedro401.reallifeachievement.utils.UserManager
import com.vedro401.reallifeachievement.utils.randomTitle
import com.vedro401.reallifeachievement.view.SignInActivity
import kotlinx.android.synthetic.main.activity_create.*
import kotlinx.android.synthetic.main.layout_create_tool_bar.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.onClick
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import javax.inject.Inject
import kotlin.collections.ArrayList

class CreateActivity : FragmentActivity(), AchievementCreator {
    private val achievement = Achievement()
    private val mainFragment = CreateMainFragment()
    private val tagsFragment = CreateTagsFragment()

    @Inject
    lateinit var userManager: UserManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create)
        App.getComponent().inject(this)

        if(!userManager.isAuthorised){
            alert {
                title("You need to log in to create achievements")
                positiveButton("Sign in"){
                    startActivity<SignInActivity>()
                }
                negativeButton("Maybe later"){
                    finish()
                }
            }.show()
        }

        text_btn_add.onClick {
            createAchievement()
        }

        random_ach.onClick {
            var title = randomTitle()

            val description = "Just for test"
            val tags = arrayListOf("random")
            setMainData(title, description, null)
            setTagList(tags)
            achievement.save()
            achievement.id = null
            toast("Created!")

        }

        vp_create.adapter = SimpleFragmentPagerAdapter(arrayListOf(mainFragment,
                tagsFragment), supportFragmentManager)

    }

    override fun setMainData(title: String, shortDescription: String, fullDescription: String?) {
        achievement.title = title
        achievement.shortDescription = shortDescription
        achievement.fullDescription = fullDescription
    }

    override fun createAchievement() {
        if (!mainFragment.getData()) {
            vp_create.currentItem = 0
        } else
            if (!tagsFragment.getData()) {
                vp_create.currentItem = 1
            } else{
                achievement.save()
                mainFragment.cleanUp()
                tagsFragment.cleanUp()
                finish()
            }

    }

    override fun setTagList(tagList: ArrayList<String>) {
        achievement.tags = tagList
    }
}
