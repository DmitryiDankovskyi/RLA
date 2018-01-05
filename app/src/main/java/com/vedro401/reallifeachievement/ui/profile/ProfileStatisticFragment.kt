package com.vedro401.reallifeachievement.ui.profile

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.signature.ObjectKey
import com.google.firebase.storage.StorageReference
import com.vedro401.reallifeachievement.App
import com.vedro401.reallifeachievement.R
import com.vedro401.reallifeachievement.managers.StorageManager
import com.vedro401.reallifeachievement.managers.interfaces.DatabaseManager
import com.vedro401.reallifeachievement.managers.interfaces.UserManager
import com.vedro401.reallifeachievement.utils.*
import kotlinx.android.synthetic.main.layou_profile_statistic.*
import org.jetbrains.anko.onClick
import javax.inject.Inject


class ProfileStatisticFragment : Fragment(){
    private val AVATAR_SWITCH = "switch"

    @Inject
    lateinit var um: UserManager

    @Inject
    lateinit var dbm: DatabaseManager

    @Inject
    lateinit var pref: AppPreference

    @Inject
    lateinit var sm: StorageManager

    private val avatarRef : StorageReference by lazy {
        sm.avatarRef(um.uid)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?)
            = inflater.inflate(R.layout.layou_profile_statistic, container, false)!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        App.getComponent().inject(this)

        val fieldsMap = HashMap<String, String>()

        if(um.isAuthorised) {

            GlideApp.with(this)
                    .load(avatarRef)
                    .centerCrop()
                    .signature(ObjectKey(pref.avatarLastUpdate))
                    .into(profile_avatar)

            sm.getAvatarLastUpdate(um.uid).subscribe{
                lastUpdate ->
                if(lastUpdate != null){
                    GlideApp.with(this)
                            .load(avatarRef)
                            .centerCrop()
                            .signature(ObjectKey(lastUpdate))
                            .into(profile_avatar)
                }
            }

//            avatarRef.metadata.addOnSuccessListener {
//                md ->
//                if(md.getCustomMetadata(AVATAR_SWITCH) != null
//                        && pref.avatarLastUpdate != md.getCustomMetadata(AVATAR_SWITCH)) {
//                    GlideApp.with(this)
//                            .load(avatarRef)
//                            .centerCrop()
//                            .signature(ObjectKey(md.getCustomMetadata(AVATAR_SWITCH)))
//                            .into(profile_avatar)
//                }
//            }
        }
        dbm.getUserStatisticData().subscribe({
            fieldPair ->
            fieldsMap.put(fieldPair.fieldName, fieldPair.value.toString())
            val sb = StringBuilder()
            fieldsMap.entries.forEach {
                sb.append("${it.key} = ${it.value}\n")
            }
            alpha_statistic.text = sb
        }, {
            error ->
            Log.e(FIRETAG,"ProfileStatisticFragment.getUserStatisticData ${error.message}" )
        })

        profile_avatar.onClick {
            this@ProfileStatisticFragment.choseImage()
        }

//        val feedBackDialog = Dialog(this, R.style.AppTheme_Dialog_TransparentBg)
//        val layout = inflate(R.layout.layout_rc_feed_back_block, null, false)
//        layout.rc_fb_send.onClick { sendFeedBack(layout)
//            feedBackDialog.dismiss()}
//        feedBackDialog.setContentView(layout)
//        feedBackDialog.show()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == PICK_IMAGE_REQUEST){
            if(data != null && data.data != null){
                val filePath = data.data
                sm.saveAvatar(filePath, um.uid).subscribe{
                    uri ->

                    sm.getAvatarLastUpdate(um.uid).subscribe{
                        lastUpdate ->
                        GlideApp.with(this)
                                .load(uri)
                                .centerCrop()
                                .signature(ObjectKey(lastUpdate))
                                .into(profile_avatar)
                        pref.avatarLastUpdate = lastUpdate!!
                    }
                }

//                avatarRef.putFile(filePath)
//                    .addOnFailureListener({
//                        ex ->
//                    toast("Ops")
//                        Log.d(STORAGETAG, "Saving avatar failed. ${ex.message}")
//                      }).addOnSuccessListener({
//                    val timeCode = System.currentTimeMillis().toString()
//                    pref.avatarLastUpdate = timeCode
//                    avatarRef.updateMetadata(StorageMetadata.Builder().setCustomMetadata(AVATAR_SWITCH,
//                            timeCode).build())
//                    GlideApp.with(this)
//                            .load(avatarRef)
//                            .centerCrop()
//                            .signature(ObjectKey(timeCode))
//                            .into(profile_avatar)
//                    toast("Yos")
//                })
            }
        }
    }
}