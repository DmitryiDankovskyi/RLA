package com.vedro401.reallifeachievement.adapters.willDie

import android.util.Log
import android.view.View
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.google.firebase.database.*
import com.vedro401.reallifeachievement.R
import com.vedro401.reallifeachievement.adapters.holders.AchievementHolder
import com.vedro401.reallifeachievement.model.Achievement
import com.vedro401.reallifeachievement.utils.AUTHTAG


class AchShortFirebaseAdapter(ref : Query
                              = FirebaseDatabase.getInstance().getReference("achievements/mainData"))
    : FirebaseRecyclerAdapter<Achievement, AchievementHolder>(
        Achievement::class.java,
        R.layout.layout_item_achievement,
        AchievementHolder::class.java,
        ref) {

    init {
        Log.d(AUTHTAG, "AchShortFirebaseAdapter item count: $itemCount")
    }

    override fun populateViewHolder(viewHolder: AchievementHolder,
                                    model: Achievement,
                                    position: Int) {
        viewHolder.bind(model)
    }

    override fun onCancelled(error: DatabaseError?) {
        super.onCancelled(error)
        Log.d(AUTHTAG, "AchShortFirebaseAdapter error: ${error.toString()}")
    }

    override fun onDataChanged() {
        super.onDataChanged()
        Log.d("CakeTag", itemCount.toString() )
        setUpIndicators()
    }
    var spinner: View? = null
        set(value) {
            field = value
//            setUpIndicators()
        }
    var voidContentIndicator: View? = null
        set(value) {
            field = value
            setUpIndicators()
        }

    private fun setUpIndicators(){
        if(spinner != null && spinner!!.visibility != View.GONE)
            spinner!!.visibility = View.GONE
        Log.d("CakeTag", "voidContentIndicator != null ${voidContentIndicator != null}" +
                         "spinner!= null ${spinner!= null}" +
                         "spinner!!.visibility == View.GONE ${spinner!!.visibility == View.GONE}")
        if(voidContentIndicator != null)
            if(itemCount == 0 && (spinner == null
                    || (spinner!= null && spinner!!.visibility == View.GONE))){
                voidContentIndicator!!.visibility = View.VISIBLE
            } else {
                voidContentIndicator!!.visibility = View.GONE
            }
    }
}