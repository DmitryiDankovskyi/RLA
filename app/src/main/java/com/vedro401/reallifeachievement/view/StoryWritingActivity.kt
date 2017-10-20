package com.vedro401.reallifeachievement.view

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import android.view.ViewGroup
import com.vedro401.reallifeachievement.R
import com.vedro401.reallifeachievement.adapters.RxRvAdapter.RxRvAdapter
import com.vedro401.reallifeachievement.adapters.StoryPostHolder
import com.vedro401.reallifeachievement.model.Story
import com.vedro401.reallifeachievement.model.StoryPost
import com.vedro401.reallifeachievement.utils.STORY
import com.vedro401.reallifeachievement.utils.inflate
import com.vedro401.reallifeachievement.utils.transferProtocols.DELETE
import com.vedro401.reallifeachievement.utils.transferProtocols.TransferProtocol
import com.vedro401.reallifeachievement.utils.transferProtocols.UPDATE
import kotlinx.android.synthetic.main.activity_story_writing.*
import kotlinx.android.synthetic.main.layout_rv_container.*
import kotlinx.android.synthetic.main.layout_story_writing_tool_bar.*
import org.jetbrains.anko.onClick
import rx.Subscriber

class StoryWritingActivity : AppCompatActivity() {

    var updatedPost: StoryPost = StoryPost()
    lateinit var story: Story

    var o2 = object : Subscriber<TransferProtocol<StoryPost>>() {
        override fun onCompleted() {}
        override fun onError(e: Throwable) {
            Log.e(STORY, "StoryWritingActivity: error ${e.message}")
        }
        override fun onNext(pack: TransferProtocol<StoryPost>) {
            when(pack.event) {
                UPDATE -> {
                    Log.d(STORY, "o2")
                    story_writing_text.setText(pack.data.content)
                    story_writing_send.visibility = View.GONE
                    story_writing_save_changes.visibility = View.VISIBLE
                    updatedPost = pack.data
                }
                DELETE -> {
                    story.deletePost(pack.data.id!!)
                }
            }
        }
    }

    val adapter = object : RxRvAdapter<StoryPost, StoryPostHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryPostHolder
                = StoryPostHolder(parent.inflate(R.layout.layout_story_post_item), o2)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_story_writing)
        story = intent.getParcelableExtra("story")
        Log.d(STORY, "StoryWritingActivity: story $story")
        Log.d(STORY, "StoryWritingActivity: story.lastPost ${story.lastPost}")
        story_writing_achievement_title.text = story.achievementTitle
        adapter.spinner = container_spinner
        adapter.emptinessIndicator = container_emptiness_indicator_block
        adapter.warningView = container_warning_block
        container_rv.layoutManager = LinearLayoutManager(this)
        container_rv.itemAnimator = DefaultItemAnimator()

        container_rv.adapter = adapter
        story.getPosts().subscribe(adapter)

        story_writing_send.onClick {
            val newPost = StoryPost()
            newPost.content = story_writing_text.text.toString()
            story.addPost(newPost)
            story_writing_text.text.clear()
        }

        story_writing_save_changes.onClick {
            updatedPost.content = story_writing_text.text.toString()
            story.updatePost(updatedPost)

            story_writing_text.text.clear()
            story_writing_send.visibility = View.VISIBLE
            story_writing_save_changes.visibility = View.GONE
        }


    }
}
