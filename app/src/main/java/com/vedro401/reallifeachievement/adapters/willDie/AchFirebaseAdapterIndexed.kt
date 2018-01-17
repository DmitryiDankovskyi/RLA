package com.vedro401.reallifeachievement.adapters.willDie

import android.util.Log
import android.view.View
import com.firebase.ui.database.FirebaseIndexRecyclerAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.vedro401.reallifeachievement.R
import com.vedro401.reallifeachievement.adapters.holders.AchievementHolder
import com.vedro401.reallifeachievement.model.Achievement
import com.vedro401.reallifeachievement.utils.AUTHTAG


class AchFirebaseAdapterIndexed(keys :Query = FirebaseDatabase.getInstance().getReference("users/pinned")
        .child(FirebaseAuth.getInstance().currentUser!!.uid),
                                ref: Query = FirebaseDatabase.getInstance().getReference("achievements/mainData")
) : FirebaseIndexRecyclerAdapter<Achievement, AchievementHolder>(
        Achievement::class.java,
        R.layout.layout_achievement_item,
        AchievementHolder::class.java,
        keys,
        ref
){
    override fun populateViewHolder(viewHolder: AchievementHolder, model: Achievement, position: Int) {
        viewHolder.bind(model)
    }

    override fun onCancelled(error: DatabaseError?) {
        super.onCancelled(error)
        Log.d(AUTHTAG, "AchFirebaseAdapterIndexed error: ${error.toString()}")
    }
    override fun onDataChanged() {
        super.onDataChanged()
        Log.d("CakeTag", itemCount.toString() )
        setUpIndicators()
    }
    var spinner: View? = null
        set(value) {
            field = value
        }
    var voidContentIndicator: View? = null
        set(value) {
            field = value
            setUpIndicators()
        }

    private fun setUpIndicators(){
        if(spinner != null && spinner!!.visibility != View.GONE)
            spinner!!.visibility = View.GONE
        if(voidContentIndicator != null)
            if(itemCount == 0 && (spinner == null
                    || (spinner!= null && spinner!!.visibility.equals(View.GONE)))){
                voidContentIndicator!!.visibility = View.VISIBLE
            } else {
                voidContentIndicator!!.visibility = View.GONE
            }
    }



}