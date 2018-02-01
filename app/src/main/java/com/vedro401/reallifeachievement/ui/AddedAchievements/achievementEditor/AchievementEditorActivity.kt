package com.vedro401.reallifeachievement.ui.AddedAchievements.achievementEditor

import android.net.Uri
import android.os.Bundle
import android.view.View
import com.vedro401.reallifeachievement.App
import com.vedro401.reallifeachievement.R
import com.vedro401.reallifeachievement.adapters.SimpleFragmentPagerAdapter
import com.vedro401.reallifeachievement.model.Achievement
import com.vedro401.reallifeachievement.ui.BaseFragmentActivity
import com.vedro401.reallifeachievement.utils.randomTitle
import kotlinx.android.synthetic.main.activity_create.*
import kotlinx.android.synthetic.main.layout_create_tool_bar.*
import org.jetbrains.anko.onClick
import org.jetbrains.anko.toast

class AchievementEditorActivity : BaseFragmentActivity(), AchievementEditor {
    override lateinit var achievement : Achievement
    override var pickPath: Uri? = null
    override var newTags = ArrayList<String>()
    override var oldTags = ArrayList<String>()
    private val mainFragment = AchievementEditorMainFragment()
    private val tagsFragment = AchievementEditorTagsFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create)
        App.getComponent().inject(this)

        achievement = intent.getParcelableExtra("achievement") ?: Achievement()

        text_btn_add.onClick {
            saveAchievement()
        }

        random_ach.onClick {
            achievement.title = randomTitle()
            achievement.description = "Just for test"
            achievement.tags = arrayListOf("random")
            achievement.save()
            achievement.id = null
            toast("Created!")

        }

        vp_create.adapter = SimpleFragmentPagerAdapter(arrayListOf(mainFragment,
                tagsFragment), supportFragmentManager)

    }


    override fun saveAchievement() {
        if (!mainFragment.saveData()) {
            vp_create.currentItem = 0
        } else
            if (!tagsFragment.saveData()) {
                vp_create.currentItem = 1
            } else{
                achievement.setId()
                achievement.authorId = um.uid
                achievement.authorName = um.name
                achievement.authorAvatarUrl = um.avatarUrl.toString()
                if(pickPath != null){
                    ac_spinner.visibility = View.VISIBLE
                    sm.saveAchPick(achievement, pickPath!!).subscribe{
                        url ->
                        if(url != null) {
                            achievement.imageUrl = url.toString()
                            achievement.save()
                            dbm.addNewTags(newTags)
                            dbm.removeTags(oldTags)
                            finish()
                        } else {
                            toast("Saving pick failed")
                        }
                        ac_spinner.visibility = View.GONE
                    }
                } else {
                    achievement.save()
                    dbm.addNewTags(newTags)
                    dbm.removeTags(oldTags)
                    finish()
                }
            }

    }
}
