package com.vedro401.reallifeachievement.ui.profile

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.beloo.widget.chipslayoutmanager.ChipsLayoutManager
import com.beloo.widget.chipslayoutmanager.SpacingItemDecoration
import com.bumptech.glide.signature.ObjectKey
import com.google.firebase.storage.StorageReference
import com.vedro401.reallifeachievement.App
import com.vedro401.reallifeachievement.R
import com.vedro401.reallifeachievement.adapters.TagsAdapter
import com.vedro401.reallifeachievement.managers.StorageManager
import com.vedro401.reallifeachievement.managers.interfaces.DatabaseManager
import com.vedro401.reallifeachievement.managers.interfaces.UserManager
import com.vedro401.reallifeachievement.utils.*
import kotlinx.android.synthetic.main.layou_profile_statistic.*
import org.jetbrains.anko.onClick
import rx.subscriptions.CompositeSubscription
import rx.subscriptions.Subscriptions
import javax.inject.Inject


class ProfileStatisticFragment : Fragment(){
    @Inject
    lateinit var um: UserManager

    @Inject
    lateinit var dbm: DatabaseManager

    @Inject
    lateinit var pref: AppPreference

    @Inject
    lateinit var sm: StorageManager

    private val avatarRef : StorageReference by lazy {
        sm.avatarRef(um.uid!!)
    }

    lateinit var adapter: TagsAdapter
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?)
            = inflater.inflate(R.layout.layou_profile_statistic, container, false)!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        App.getComponent().inject(this)
        val fieldsMap = HashMap<String, String>()

        initTags()
        val tagList = ArrayList<Pair<String, Int>>()
        dbm.getUserFavTags().subscribe({
            tag -> tagList.add(tag)
        },{
            e ->
            Log.d(TAGS,e.message )
        },{
            tagList.sortByDescending { it.second }
            tagList.forEach{ adapter.add(it.first)}
        })
        
        if(um.isAuthorised) {
            GlideApp.with(this)
                    .load(avatarRef)
                    .centerCrop()
                    .signature(ObjectKey(pref.avatarLastUpdate))
                    .into(sf_avatar)

            sm.getAvatarLastUpdate(um.uid!!).subscribe{
                lastUpdate ->
                if(lastUpdate != null){
                    GlideApp.with(this)
                            .load(avatarRef)
                            .centerCrop()
                            .signature(ObjectKey(lastUpdate))
                            .into(sf_avatar)
                }
            }
        }
        dbm.getUserStatisticData().subscribe({
            fieldPair ->
            fieldsMap.put(fieldPair.fieldName, fieldPair.value.toString())
            val sb = StringBuilder()
            fieldsMap.entries.forEach {
                sb.append("${it.key} = ${it.value}\n")
            }
            sf_alpha_statistic.text = sb
        }, {
            error ->
            Log.e(FIRETAG,"ProfileStatisticFragment.getUserStatisticData ${error.message}" )
        })

        sf_avatar.onClick {
            this@ProfileStatisticFragment.choseImage()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == PICK_IMAGE_REQUEST){
            if(data != null && data.data != null){
                val filePath = data.data
                sm.saveAvatar(filePath, um.uid!!).subscribe{
                    uri ->

                    sm.getAvatarLastUpdate(um.uid!!).subscribe{
                        lastUpdate ->
                        GlideApp.with(this)
                                .load(uri)
                                .centerCrop()
                                .signature(ObjectKey(lastUpdate))
                                .into(sf_avatar)
                        pref.avatarLastUpdate = lastUpdate!!
                    }
                }
            }
        }
    }

    private fun initTags(){
        adapter = TagsAdapter()
        val chipsLayoutManager = ChipsLayoutManager.newBuilder(activity)
                .build()
        sf_tags.addItemDecoration(SpacingItemDecoration(8,8))
        sf_tags.layoutManager = chipsLayoutManager
        sf_tags.itemAnimator = DefaultItemAnimator()
        sf_tags.adapter = adapter
    }

}