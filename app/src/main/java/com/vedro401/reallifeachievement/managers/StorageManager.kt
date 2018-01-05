package com.vedro401.reallifeachievement.managers

import android.net.Uri
import android.util.Log
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import com.google.firebase.storage.StorageReference
import com.vedro401.reallifeachievement.model.Achievement
import com.vedro401.reallifeachievement.utils.STORAGETAG
import rx.Observable
import rx.subjects.AsyncSubject

class StorageManager{
    val LAST_UPDATE = "lastUpdate"
    private val rootRef = FirebaseStorage.getInstance().reference

    fun avatarRef(uid: String)
        = rootRef.child("usersData/$uid/avatar")

    fun saveAvatar(path: Uri, uid: String): Observable<Uri?> {
        val ref = rootRef.child("usersData/$uid/avatar")
        return savePick(ref, path, "Can't save avatar")
    }

    fun getAvatarLastUpdate(uid: String) : Observable<String?>{
        val ref = rootRef.child("usersData/$uid/avatar")
        return getLastUpdate(ref,"Can't get avatar last update")
    }

    fun saveAchPick(ach: Achievement, path: Uri) : Observable<Uri?> {
        val ref = rootRef.child("achievementsPicks/${ach.id!!}")
        return savePick(ref, path, "Can't save achievement pick")
    }

    fun getLastUpdate(ach: Achievement) : Observable<String?> {
        val ref = rootRef.child("achievementsPicks/${ach.id!!}")
        return getLastUpdate(ref,"Can't get achievement last update")
    }

    private fun getLastUpdate(ref: StorageReference,
                              exceptionMessage: String = "Can't get last update"): Observable<String?> {
        val response = AsyncSubject.create<String?>()
        ref.metadata.addOnSuccessListener {
            md->
            response.onNext(md.getCustomMetadata(LAST_UPDATE))
            response.onCompleted()
        }.addOnFailureListener{
            ex ->
            Log.w(STORAGETAG, "Can't get metadata of ach pick:\n ${ex.message}")
            response.onNext(null)
            response.onCompleted()
        }
        return response
    }

    private fun savePick(ref: StorageReference,
                         path: Uri,
                         exceptionMessage: String = "Can't save pick") : Observable<Uri?> {
        val response = AsyncSubject.create<Uri?>()
        val metadata = StorageMetadata.Builder()
                .setCustomMetadata(LAST_UPDATE,System.currentTimeMillis().toString())
                .build()
        ref.putFile(path, metadata)
                .addOnSuccessListener { md ->
                    response.onNext(md.downloadUrl)
                    response.onCompleted()
                }.addOnFailureListener{
            ex ->
            Log.w(STORAGETAG, "$exceptionMessage:\n ${ex.message}")
            response.onNext(null)
            response.onCompleted()
        }
        return response
    }


}
