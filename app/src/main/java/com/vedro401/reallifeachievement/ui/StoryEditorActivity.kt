package com.vedro401.reallifeachievement.ui

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
import com.vedro401.reallifeachievement.adapters.RxRvAdapter
import com.vedro401.reallifeachievement.adapters.holders.StoryPostHolder
import com.vedro401.reallifeachievement.model.Story
import com.vedro401.reallifeachievement.model.StoryPost
import com.vedro401.reallifeachievement.ui.interfaces.StoryEditor
import com.vedro401.reallifeachievement.utils.*
import kotlinx.android.synthetic.main.activity_story_editor.*
import kotlinx.android.synthetic.main.layout_rv_container.*
import kotlinx.android.synthetic.main.layout_story_editor_create_panel.*
import kotlinx.android.synthetic.main.layout_story_editor_tool_bar.*
import org.jetbrains.anko.*
import java.text.SimpleDateFormat
import java.util.*

class StoryEditorActivity : AppCompatActivity(), StoryEditor {
    override fun updatePost(post: StoryPost) {
        updating = true
        story_editor_title.setText(post.title)
        story_editor_content.setText(post.content)
        story_editor_time_stamped.text = post.timeStamped
        showCreatePanel()
        this.post = post}

    override fun deletePost(post: StoryPost) {
        story.deletePost(post.id!!)
    }

    override var post: StoryPost = StoryPost()
    lateinit var story: Story
    private var updating = false

    private val dateNow by lazy {
        val sdf = SimpleDateFormat("dd.MM.yyyy")
        sdf.format(Date())
    }

    private var createPanelOpened = false


    val adapter = object : RxRvAdapter<StoryPost, StoryPostHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryPostHolder
                = StoryPostHolder(parent.inflate(R.layout.layout_story_post_item), this@StoryEditorActivity)
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
                clearCreatePanel()
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
        val efc = EmptyFieldsController(getString(R.string.empty_field_warning))
        efc.addField(story_editor_content)
        efc.addField(story_editor_title)
        if(!efc.isOkay()) return

        post.title = story_editor_title.text.toString()
        post.content = story_editor_content.text.toString()
        post.timeStamped = story_editor_time_stamped.text.toString()
        if (updating) {
            story.updatePost(post)
            updating = false
        } else {
            story.addPost(post)
        }
        post = StoryPost()
        clearCreatePanel()
        hideCreatePanel()
        contentView!!.hideKeyboard()
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

    private fun clearCreatePanel(){
        story_editor_content.text.clear()
        story_editor_content.error = null
        story_editor_title.text.clear()
        story_editor_title.error = null
        story_editor_time_stamped.text = dateNow
    }

    override fun onBackPressed() {
        if (createPanelOpened) {
            hideCreatePanel()
            return
        }
        super.onBackPressed()
    }
}
