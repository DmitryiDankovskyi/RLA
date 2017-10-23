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
import kotlinx.android.synthetic.main.layout_story_writing_create_panel.*
import kotlinx.android.synthetic.main.layout_story_writing_tool_bar.*
import org.jetbrains.anko.onClick
import rx.Subscriber

class StoryWritingActivity : AppCompatActivity() {

    var post: StoryPost = StoryPost()
    lateinit var story: Story
    private var updating = false

    private var createPanelOpened = false

    var editPostListener = object : Subscriber<TransferProtocol<StoryPost>>() {
        override fun onCompleted() {}
        override fun onError(e: Throwable) {
            Log.e(STORY, "StoryWritingActivity: error ${e.message}")
        }
        override fun onNext(pack: TransferProtocol<StoryPost>) {
            when(pack.event) {
                UPDATE -> {
                    updating = true
                    story_writing_title.setText(pack.data.title)
                    story_writing_content.setText(pack.data.content)
                    story_writing_time_stamped.text = pack.data.timeStamped
                    showCreatePanel()
                    post = pack.data
                }
                DELETE -> {
                    story.deletePost(pack.data.id!!)
                }
            }
        }
    }

    val adapter = object : RxRvAdapter<StoryPost, StoryPostHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryPostHolder
                = StoryPostHolder(parent.inflate(R.layout.layout_story_post_item), editPostListener)
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

        story_writing_add_new_post.onClick {
            showCreatePanel()
        }
        story_writing_completed.onClick {
            save()
        }

        story_writing_time_stamped_bar.onClick {
            //TODO call data picker
        }
    }

    private fun save(){
        if(story_writing_content.text.isEmpty()){
            story_writing_content.error = getString(R.string.empty_field_warning)
            return
        }
        post.content = story_writing_content.text.toString()
        if(story_writing_title.text.isEmpty()){
            story_writing_title.error = getString(R.string.empty_field_warning)
            return
        }
        post.title = story_writing_title.text.toString()
        post.timeStamped = story_writing_time_stamped.text.toString()
        if(updating){
            story.updatePost(post)
            updating = false
        } else {
            story.addPost(post)
        }
        post = StoryPost()
        story_writing_content.text.clear()
        hideCreatePanel()
    }

    private fun showCreatePanel(){
        layout_story_writing_create_panel.visibility = View.VISIBLE
        story_writing_down_panel.visibility = View.GONE
        createPanelOpened = true
    }

    private fun hideCreatePanel(){
        layout_story_writing_create_panel.visibility = View.GONE
        story_writing_down_panel.visibility = View.VISIBLE
        createPanelOpened = true
    }

    override fun onBackPressed() {
        if(createPanelOpened){
            hideCreatePanel()
            return
        }
        super.onBackPressed()
    }
}
