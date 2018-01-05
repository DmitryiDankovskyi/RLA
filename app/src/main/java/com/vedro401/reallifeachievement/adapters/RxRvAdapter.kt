package com.vedro401.reallifeachievement.adapters


import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.TextView
import com.vedro401.reallifeachievement.adapters.holders.BindableViewHolder
import com.vedro401.reallifeachievement.transferProtocols.RxRvTransferProtocol
import com.vedro401.reallifeachievement.utils.RXRVTAG
import rx.Observer
import rx.Subscription

abstract class RxRvAdapter<DT, VH : BindableViewHolder<DT>>
    : RecyclerView.Adapter<VH>(), Observer<RxRvTransferProtocol<DT>>{
    private var dataSet = ArrayList<DT>()
    private val indexesMap = HashMap<String,Int>()
    var comparator : Comparator<DT>? = null
        set(c){
            field = c
            onComparatorChanged()
        }

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
            field = value
            if(spinner?.visibility == View.VISIBLE){
                field?.visibility = View.GONE
            } else{
                setEmptinessIndicator()
            }
        }

    override fun onNext(tp: RxRvTransferProtocol<DT>) {
        hideWarning()
        when(tp.event){
            RxRvTransferProtocol.ITEM_ADDED -> {
                hideSpinner()
                Log.d(RXRVTAG, "RxRvAdapter: item added. ${tp.data.toString()}")
                if(comparator == null || dataSet.isEmpty()) {
                    dataSet.add(tp.data!!)
                    val dataSetIndex = dataSet.size - 1
                    indexesMap.put(tp.id, dataSetIndex)
                    notifyItemChanged(dataSetIndex)
                } else {
                    var dataSetIndex = -1
                    dataSet.takeWhile { comparator!!.compare(it, tp.data) > -1 }
                            .forEachIndexed { index, _ ->
                                dataSetIndex = index
                            }
                    dataSetIndex++
                    dataSet.add(dataSetIndex,tp.data!!)
                    indexesMap.entries.filter { it.value >= dataSetIndex }
                            .forEach { it.setValue(it.value + 1) }
                    indexesMap.put(tp.id, dataSetIndex)
                    notifyItemRangeChanged(dataSetIndex,dataSet.size-1)
                }
                setEmptinessIndicator()
            }

            RxRvTransferProtocol.ITEM_CHANGED -> {
                Log.d(RXRVTAG, "RxRvAdapter: item changed. ${tp.data.toString()}")
                val id = indexesMap[tp.id]!!
                if(comparator == null || comparator!!.compare(dataSet[id], tp.data!!) == 0){
                    dataSet[id] = tp.data!!
                    notifyItemChanged(id)
                } else {
                    tp.event = RxRvTransferProtocol.ITEM_REMOVED
                    this.onNext(tp)
                    tp.event = RxRvTransferProtocol.ITEM_ADDED
                    this.onNext(tp)
                }

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
                emptinessIndicator?.visibility = View.VISIBLE
                emptinessIndicatorTextView?.visibility = View.VISIBLE
            } else {
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

    fun onComparatorChanged(){
        Log.d(RXRVTAG,"RxRvAdapter: onComparatorChanged")
        val idMap = HashMap<DT, String>()
        indexesMap.entries.forEach { idMap.put(dataSet[it.value], it.key) }
        indexesMap.clear()
        dataSet.sortWith(comparator!!)
        dataSet.reverse()
        dataSet.forEachIndexed { index, dt -> indexesMap.put(idMap[dt]!!,index) }
        notifyDataSetChanged()
    }

    fun forEach(action: (DT) -> Unit) {
        dataSet.forEach(action)
    }
}