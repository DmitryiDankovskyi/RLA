package com.vedro401.reallifeachievement.ui.AddedAchievements.create

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.vedro401.reallifeachievement.R
import com.vedro401.reallifeachievement.utils.GlideApp
import com.vedro401.reallifeachievement.utils.PICK_IMAGE_REQUEST
import com.vedro401.reallifeachievement.utils.choseImage
import kotlinx.android.synthetic.main.layout_create1.*
import org.jetbrains.anko.onClick

/**
 * Created by someone on 02.10.17.
 */
class CreateMainFragment : CreateFragment() {
    private var pickPath : Uri? = null

    override fun getData(): Boolean {
        var flag = true
        if (et_title.text.isEmpty()) {
            et_title.error = getString(R.string.empty_field_warning)
            flag = false
        } else et_title.error = null
        if (et_short_description.text.isEmpty()) {
            et_short_description.error = getString(R.string.empty_field_warning)
            flag = false
        } else et_short_description.error = null
        if (flag) {
            achCreator.setMainData(et_title.text.toString(),
                    et_short_description.text.toString(),
                    et_full_description.text.toString(),
                    pickPath)
        }
        return flag
    }

    override fun cleanUp() {
        et_title.setText("")
        et_short_description.setText("")
        et_full_description.setText("")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?)
            = inflater.inflate(R.layout.layout_create1, container, false)!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        img_ach_icon.onClick {
            this.choseImage()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == PICK_IMAGE_REQUEST){
            if(data != null){
                pickPath = data.data
                if(pickPath != null){
                    GlideApp.with(this)
                            .load(pickPath)
                            .centerCrop()
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .into(img_ach_icon)
                }

            }
        }
    }
}