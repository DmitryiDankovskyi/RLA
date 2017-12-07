package com.vedro401.reallifeachievement.view

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
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
import com.vedro401.reallifeachievement.utils.wordDifficulty
import kotlinx.android.synthetic.main.activity_story_editor.*
import kotlinx.android.synthetic.main.layout_rv_container.*
import kotlinx.android.synthetic.main.layout_story_editor_create_panel.*
import kotlinx.android.synthetic.main.layout_story_editor_tool_bar.*
import org.jetbrains.anko.*
import rx.Subscriber

class StoryEditorActivity : AppCompatActivity() {
    var post: StoryPost = StoryPost()
    lateinit var story: Story
    private var updating = false

    private var createPanelOpened = false

    var editPostListener = object : Subscriber<TransferProtocol<StoryPost>>() {
        override fun onCompleted() {}
        override fun onError(e: Throwable) {
            Log.e(STORY, "StoryEditorActivity: error ${e.message}")
        }
        override fun onNext(pack: TransferProtocol<StoryPost>) {
            when (pack.event) {
                UPDATE -> {
                    updating = true
                    story_editor_title.setText(pack.data.title)
                    story_editor_content.setText(pack.data.content)
                    story_editor_time_stamped.text = pack.data.timeStamped
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
        setContentView(R.layout.activity_story_editor)
        story = intent.getParcelableExtra("story")
        Log.d(STORY, "StoryEditorActivity: story $story")
        Log.d(STORY, "StoryEditorActivity: story.lastPost ${story.lastPost}")
        story_writing_achievement_title.text = story.achievementTitle
        adapter.spinner = container_spinner
        adapter.emptinessIndicator = container_emptiness_indicator_block
        adapter.warningView = container_warning_block
        container_rv.layoutManager = LinearLayoutManager(this)
        container_rv.itemAnimator = DefaultItemAnimator()

        container_rv.adapter = adapter
        story.getPosts().subscribe(adapter)

        story_editor_completed.onClick {
            save()
        }
        story_editor_time_stamped_bar.onClick {
            //TODO call data picker
        }
        if(story.status != Story.FINISHED) {
            story_editor_add_new_post.onClick {
                showCreatePanel()
            }

            story_editor_set_as_finished.onClick {
                alert("Tell us, how hard it was?") {
                    customView {
                        verticalLayout(R.style.AppTheme) {
                            padding = dip(16)
                            val tv = textView("So hard") {
                                textSize = 24f
                                gravity = Gravity.CENTER_HORIZONTAL
                            }
                            val sb = seekBar()
                            sb.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                                    tv.text = wordDifficulty(progress)
                                }

                                override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
                            })
                            yesButton {
                                story.finish(sb.progress)
                            }
                            noButton { }
                        }
                    }
                }.show()
            }
        } else {
            story_editor_down_panel.visibility = View.GONE
        }
    }

    private fun save() {
        if (story_editor_content.text.isEmpty()) {
            story_editor_content.error = getString(R.string.empty_field_warning)
            return
        }
        post.content = story_editor_content.text.toString()
        if (story_editor_title.text.isEmpty()) {
            story_editor_title.error = getString(R.string.empty_field_warning)
            return
        }
        post.title = story_editor_title.text.toString()
        post.timeStamped = story_editor_time_stamped.text.toString()
        if (updating) {
            story.updatePost(post)
            updating = false
        } else {
            story.addPost(post)
        }
        post = StoryPost()
        story_editor_content.text.clear()
        story_editor_title.text.clear()
        hideCreatePanel()
    }


    private fun showCreatePanel() {
        layout_story_editor_create_panel.visibility = View.VISIBLE
        if(story.status != Story.FINISHED)
            story_editor_down_panel.visibility = View.GONE
        createPanelOpened = true
    }

    private fun hideCreatePanel() {
        layout_story_editor_create_panel.visibility = View.GONE
        if(story.status != Story.FINISHED)
            story_editor_down_panel.visibility = View.VISIBLE
        createPanelOpened = false
    }

    override fun onBackPressed() {
        if (createPanelOpened) {
            hideCreatePanel()
            return
        }
        super.onBackPressed()
    }
}