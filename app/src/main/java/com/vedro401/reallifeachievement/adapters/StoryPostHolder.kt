package com.vedro401.reallifeachievement.adapters

import android.util.Log
import android.view.View
import com.vedro401.reallifeachievement.model.StoryPost
import com.vedro401.reallifeachievement.utils.STORY
import com.vedro401.reallifeachievement.utils.transferProtocols.DELETE
import com.vedro401.reallifeachievement.utils.transferProtocols.TransferProtocol
import com.vedro401.reallifeachievement.utils.transferProtocols.UPDATE
import kotlinx.android.synthetic.main.layout_story_post.view.*
import kotlinx.android.synthetic.main.layout_story_post_item.view.*
import org.jetbrains.anko.onClick
import rx.Observable
import rx.Subscriber


class StoryPostHolder(itemView: View,
                      private var editPostListener: Subscriber<TransferProtocol<StoryPost>>)
    : BindableViewHolder<StoryPost>(itemView) {


    override fun bind(data: StoryPost) {
        itemView.story_post_content.text = data.content
        itemView.story_post_title.text = data.title
        itemView.story_post_time_stamped.text = data.timeStamped
        itemView.story_post_item_change.onClick {
            Log.i(STORY,"StoryPostHolder.story_post_item_change: Clicked")
            Observable.create<TransferProtocol<StoryPost>> { subscriber ->
                subscriber.onNext(TransferProtocol(UPDATE,data))
                subscriber.unsubscribe()}
                    .subscribe(editPostListener)
        }
        itemView.story_post_item_delete.onClick {
            Log.i(STORY,"StoryPostHolder.story_post_item_delete: Clicked")
            Observable.create<TransferProtocol<StoryPost>> { subscriber ->
                subscriber.onNext(TransferProtocol(DELETE,data))
                subscriber.unsubscribe()}
                    .subscribe(editPostListener)
        }
    }

}