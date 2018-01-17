package com.vedro401.reallifeachievement.ui.AddedAchievements.create

import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.beloo.widget.chipslayoutmanager.ChipsLayoutManager
import com.beloo.widget.chipslayoutmanager.SpacingItemDecoration
import com.vedro401.reallifeachievement.R
import com.vedro401.reallifeachievement.adapters.TagsAdapter
import kotlinx.android.synthetic.main.layout_create_tags.*
import org.jetbrains.anko.onClick


class CreateTagsFragment : CreateFragment() {
    private val adapter = TagsAdapter()
    override fun getData(): Boolean {
        return if (!adapter.isEmpty()) {
            achCreator.setTagList(adapter.tags)
            true
        } else {
            et_tag.error = getString(R.string.create_no_tags_error)
            false
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?)
            = inflater.inflate(R.layout.layout_create_tags, container, false)!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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

    private fun setupRV(){
        val chipsLayoutManager = ChipsLayoutManager.newBuilder(activity)
                .build()
        rv_tags.addItemDecoration(SpacingItemDecoration(16,16))
        rv_tags.layoutManager = chipsLayoutManager
        rv_tags.adapter = adapter
        rv_tags.itemAnimator = DefaultItemAnimator()
    }
}