package com.vedro401.reallifeachievement.adapters.RxRvAdapter

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.TextView
import com.vedro401.reallifeachievement.adapters.BindableViewHolder
import com.vedro401.reallifeachievement.utils.CAKE_HUNTER
import com.vedro401.reallifeachievement.utils.RXRVTAG
import rx.Observer
import rx.Subscription

abstract class RxRvAdapter<DT, VH : BindableViewHolder<DT>>
    : RecyclerView.Adapter<VH>(), Observer<RxRvTransferProtocol<DT>>{
    private var dataSet = ArrayList<DT>()
    private val indexesMap = HashMap<String,Int>()

    private lateinit var subscription : Subscription

    var warningView : View? = null
    var warningTextView: TextView? = null

    var spinner : View? = null
        set(value) {
            field = value
            if(!dataSet.isEmpty()){
                hideSpinner()
            }
        }

    var emptinessIndicatorTextView: TextView? = null
    var emptinessIndicator : View? = null
        set(value) {
            Log.d(CAKE_HUNTER, "just created")
            field = value
            if(spinner?.visibility == View.VISIBLE){
                field?.visibility = View.GONE
            } else{
                Log.d(CAKE_HUNTER, "just setted")
                setEmptinessIndicator()
            }
                

        }

    override fun onNext(tp: RxRvTransferProtocol<DT>) {
        hideWarning()
        when(tp.event){
            RxRvTransferProtocol.ITEM_ADDED -> {
                hideSpinner()
                dataSet.add(tp.data!!)
                Log.d(RXRVTAG, "RxRvAdapter: item added. ${tp.data.toString()}")
                val dataSetIndex = dataSet.size - 1
                notifyItemChanged(dataSetIndex)
                indexesMap.put(tp.id,dataSetIndex)
                Log.d(CAKE_HUNTER, "add set")
                setEmptinessIndicator()
            }

            RxRvTransferProtocol.ITEM_CHANGED -> {
                val id = indexesMap[tp.id]!!
                dataSet[id] = tp.data!!
                Log.d(RXRVTAG, "RxRvAdapter: item added. ${tp.data.toString()}")
                notifyItemChanged(id)
            }
            RxRvTransferProtocol.ITEM_REMOVED -> {
                val id = indexesMap[tp.id]
                if (id == null){
                    Log.d(RXRVTAG, "RxRvAdapter: Removing item out of set. Key ${tp.id}")
                    return
                }
                dataSet.removeAt(id)
                indexesMap.remove(tp.id)
                indexesMap.entries.filter { e-> e.value > id }
                        .forEach { e-> e.setValue(e.value -1)}
                Log.d(RXRVTAG, "RxRvAdapter: item removed. key \"${tp.id}\" position $id")
                notifyItemRemoved(id)
                notifyItemRangeChanged(id, dataSet.size-1)
//                notifyDataSetChanged()
                Log.d(CAKE_HUNTER, "removed set")
                setEmptinessIndicator()
            }

            RxRvTransferProtocol.FULL_DATA_SET -> {
                dataSet = tp.dataSet
                hideSpinner()
                setEmptinessIndicator()
            }

            RxRvTransferProtocol.EMPTY_DATA -> {
                dataSet.clear()
                indexesMap.clear()
                hideSpinner()
                Log.d(RXRVTAG, "RxRvAdapter: empty data set")
                Log.d(CAKE_HUNTER, "empty data set")
                setEmptinessIndicator()
                notifyDataSetChanged()
            }
            
            RxRvTransferProtocol.RESET -> {
                dataSet.clear()
                indexesMap.clear()
                showSpinner()
                hideEmptinessIndicator()
                notifyDataSetChanged()
                Log.d(RXRVTAG,"RxRvAdapter: Reset.")
            }

            RxRvTransferProtocol.PERMISSION_DENIED -> {
                hideSpinner()
                hideEmptinessIndicator()
                setWarningText("PERMISSION DENIED")
                showWarning()
            }
            else -> {
                Log.w(RXRVTAG,"RxRvAdapter: Unknown event code in protocol: ${tp.event}")
            }
        }
    }

    override fun onBindViewHolder(holder: VH, position: Int)  = holder.bind(dataSet[position])

    override fun getItemCount() = dataSet.size

    private fun hideSpinner(){
        if(spinner?.visibility == View.VISIBLE){
            spinner?.visibility = View.GONE
        }
    }
    private fun showSpinner(){
        spinner?.visibility = View.VISIBLE
    }

    private fun setEmptinessIndicator(){
        if(emptinessIndicator != null){
            if(dataSet.isEmpty()){
                Log.d(CAKE_HUNTER, "Shoved")
                emptinessIndicator?.visibility = View.VISIBLE
                emptinessIndicatorTextView?.visibility = View.VISIBLE
            } else {
                Log.d(CAKE_HUNTER, "Hided")
                hideEmptinessIndicator()
            }
        }
    }

    private fun hideEmptinessIndicator(){
        emptinessIndicator?.visibility = View.GONE
        emptinessIndicatorTextView?.visibility = View.GONE
    }

    fun setEmptinessIndicatorText(warningText: String){
        emptinessIndicatorTextView?.text = warningText
    }


    private fun showWarning(){
        warningView?.visibility = View.VISIBLE
        warningTextView?.visibility = View.VISIBLE
    }

    private fun hideWarning(){
        warningView?.visibility = View.GONE
        warningTextView?.visibility = View.GONE
    }

    fun setWarningText(warningText: String){
        warningTextView?.text = warningText
    }


    override fun onCompleted() {
        Log.e(RXRVTAG,"RxRvAdapter: onCompleted")
    }

    override fun onError(e: Throwable?) {
        Log.e(RXRVTAG,"RxRvAdapter: onError ${e?.message}")
    }
//    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): VH {}

}