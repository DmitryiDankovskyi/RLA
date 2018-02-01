package com.vedro401.reallifeachievement.ui.AddedAchievements.achievementEditor

import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.beloo.widget.chipslayoutmanager.ChipsLayoutManager
import com.beloo.widget.chipslayoutmanager.SpacingItemDecoration
import com.vedro401.reallifeachievement.R
import com.vedro401.reallifeachievement.adapters.TagsAdapter
import com.vedro401.reallifeachievement.model.Achievement
import com.vedro401.reallifeachievement.utils.TAGS
import kotlinx.android.synthetic.main.layout_ach_editor_tags.*
import org.jetbrains.anko.onClick


class AchievementEditorTagsFragment : AchievementEditorFragment() {
    private lateinit var adapter: TagsAdapter
    private lateinit var achievement: Achievement
    override fun saveData(): Boolean {
        return if (!adapter.isEmpty()) {
            var i = 0
            while (i < achievement.tags.size){
                if(!adapter.tags.contains(achievement.tags[i])){
                    achEditorActivity.oldTags.add(achievement.tags[i])
                    achievement.tags.removeAt(i)
                } else i++
            }
            adapter.tags.filterNot { achievement.tags.contains(it) }
                    .forEach { achievement.tags.add(it)
                    achEditorActivity.newTags.add(it)}

            achievement.tags = adapter.tags
            true
        } else {
            et_tag.error = getString(R.string.create_no_tags_error)
            false
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?)
            = inflater.inflate(R.layout.layout_ach_editor_tags, container, false)!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = TagsAdapter(R.drawable.ic_clear_circle_filled_14dp)
        achievement = achEditorActivity.achievement
        if (achievement.id != null) {
            achievement.initTags().subscribe(
                    {},
                    { e->
                        Log.w(TAGS, e.message)
                    },
                    {
                        achievement.tags.forEach {
                            adapter.add(it)
                        }
                    })
        }

        adapter.buttonOnClick = { position, _ ->
            adapter.tags.removeAt(position)
            adapter.notifyItemRemoved(position)
        }

        setupRV()
        btn_add_tag.onClick {
            if (!et_tag.text.isEmpty()) {
                val tag = et_tag.text.toString()
                adapter.add(tag)
                et_tag.text.clear()
                et_tag.error = null
            } else et_tag.error = getString(R.string.empty_field_warning)
        }
    }

    override fun cleanUp() {
        adapter.tags.clear()
    }

    private fun setupRV() {
        val chipsLayoutManager = ChipsLayoutManager.newBuilder(activity)
                .build()
        ca_tags.addItemDecoration(SpacingItemDecoration(8, 8))
        ca_tags.layoutManager = chipsLayoutManager
        ca_tags.adapter = adapter
        ca_tags.itemAnimator = DefaultItemAnimator()
    }
}