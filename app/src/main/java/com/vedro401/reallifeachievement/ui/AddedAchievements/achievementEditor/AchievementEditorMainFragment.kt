package com.vedro401.reallifeachievement.ui.AddedAchievements.achievementEditor

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.vedro401.reallifeachievement.R
import com.vedro401.reallifeachievement.config.GlideApp
import com.vedro401.reallifeachievement.utils.PICK_IMAGE_REQUEST
import com.vedro401.reallifeachievement.utils.choseImage
import kotlinx.android.synthetic.main.layout_ach_editor_main.*
import org.jetbrains.anko.onClick

class AchievementEditorMainFragment : AchievementEditorFragment() {
    private var pickPath : Uri? = null

    override fun saveData(): Boolean {
        var flag = true
        if (aem_title.text.isEmpty()) {
            aem_title.error = getString(R.string.empty_field_warning)
            flag = false
        } else aem_title.error = null
        if (aem_description.text.isEmpty()) {
            aem_description.error = getString(R.string.empty_field_warning)
            flag = false
        } else aem_description.error = null
        if (flag) {
            achEditorActivity.achievement.title = aem_title.text.toString()
            achEditorActivity.achievement.description = aem_description.text.toString()
        }
        return flag
    }

    override fun cleanUp() {
        aem_title.setText("")
        aem_description.setText("")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?)
            = inflater.inflate(R.layout.layout_ach_editor_main, container, false)!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(achEditorActivity.achievement.id != null){
            aem_title.setText(achEditorActivity.achievement.title)
            aem_description.setText(achEditorActivity.achievement.description)
            if(achEditorActivity.achievement.imageUrl != null) {
                GlideApp.with(this)
                        .load(achEditorActivity.achievement.imageUrl)
                        .centerCrop()
                        .into(aem_ach_pick)
            }
        }

        aem_ach_pick.onClick {
            this.choseImage()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == PICK_IMAGE_REQUEST){
            if(data != null){
                pickPath = data.data
                achEditorActivity.pickPath = pickPath
                if(pickPath != null){
                    GlideApp.with(this)
                            .load(pickPath)
                            .centerCrop()
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .into(aem_ach_pick)
                }
            }
        }
    }
}