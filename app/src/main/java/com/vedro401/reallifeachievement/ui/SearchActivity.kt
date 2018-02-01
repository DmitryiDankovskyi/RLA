package com.vedro401.reallifeachievement.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.TextView
import com.google.firebase.database.FirebaseDatabase
import com.vedro401.reallifeachievement.App
import com.vedro401.reallifeachievement.R
import com.vedro401.reallifeachievement.adapters.willDie.AchFirebaseAdapterIndexed
import com.vedro401.reallifeachievement.adapters.willDie.AchShortFirebaseAdapter
import com.vedro401.reallifeachievement.managers.FirebaseManager
import com.vedro401.reallifeachievement.managers.interfaces.UserManager
import com.vedro401.reallifeachievement.transferProtocols.TransferProtocol
import com.vedro401.reallifeachievement.ui.interfaces.FakeBottomNavigationOwner
import com.vedro401.reallifeachievement.utils.AUTHTAG
import com.vedro401.reallifeachievement.utils.SEARCH
import com.vedro401.reallifeachievement.utils.plusAssign
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.layout_rv_container.*
import kotlinx.android.synthetic.main.layout_search_tool_bar.*
import org.jetbrains.anko.onClick
import javax.inject.Inject


class SearchActivity : BaseActivity(), FakeBottomNavigationOwner {

    override var menuNum = 1
    @Inject
    lateinit var firebaseManager: FirebaseManager

    val requests = HashSet<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        initBottomNavigation(bottom_navigation, this)
        App.getComponent().inject(this)

        container_rv.layoutManager = LinearLayoutManager(this)
        container_rv.itemAnimator = DefaultItemAnimator()
        initRV()
        initSearch()

    }

    private fun search() {
        var request = et_search.text.toString()
//        (container_rv.adapter as AchShortFirebaseAdapter).cleanup()
        if (request.isEmpty()) {
            val adapter = AchShortFirebaseAdapter()
            adapter.spinner = container_spinner
            adapter.voidContentIndicator = container_emptiness_indicator_block
            container_rv.adapter = adapter
            return
        }

        if (request[0] == '#') {
            request = request.substring(1)
            Log.d("searchLog", "<#>" + request)
            val adapter = AchFirebaseAdapterIndexed(FirebaseDatabase.getInstance().
                    getReference("achievements/tags").orderByChild(request).equalTo(true),
                    FirebaseDatabase.getInstance().getReference("achievements/mainData"))
            adapter.spinner = container_spinner
            adapter.voidContentIndicator = container_emptiness_indicator_block
            container_rv.adapter = adapter
        } else {
            Log.d("searchLog", request)
            val adapter = AchShortFirebaseAdapter(firebaseManager.searchAch(request))
            adapter.spinner = container_spinner
            adapter.voidContentIndicator = container_emptiness_indicator_block
            container_rv.adapter = adapter
        }
    }

    private fun initRV() {
        val adapter = AchShortFirebaseAdapter()
        adapter.spinner = container_spinner
        adapter.voidContentIndicator = container_emptiness_indicator_block
        container_rv.adapter = adapter
    }

    private fun initSearch() {
        val adapter = ArrayAdapter<String>(this, android.R.layout.select_dialog_item)
        et_search.setAdapter(adapter)
        et_search.threshold = 3

        et_search.setOnEditorActionListener(
                TextView.OnEditorActionListener { _, actionId, _ ->
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        search()
                        return@OnEditorActionListener true
                    }
                    false
                })

        val fadeInAnim = AnimationUtils.loadAnimation(this, R.anim.clear_btn_bade_in)
        val fadeOutAnim = AnimationUtils.loadAnimation(this, R.anim.clear_btn_bade_out)
        et_search.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                if (s.isEmpty()) {
                    search_tool_bar_clear_button.visibility = View.INVISIBLE
                    search_tool_bar_clear_button.startAnimation(fadeOutAnim)
                }

                if (s.length == et_search.threshold - 1) {
                    Log.d(SEARCH, "got $s")
                    if (requests.contains(s.toString())) {
                        Log.d(SEARCH, "already have $s")
                        return
                    }
                    requests.add(s.toString())
                    subscriptions.clear()
                    subscriptions += dbm.searchTagsTips(s.toString()).subscribe({ tpp ->
                        when (tpp.event) {
                            TransferProtocol.ITEM_ADDED -> {
                                adapter.add(tpp.data)
                                Log.d(SEARCH, "added ${tpp.data}")
                            }
                            TransferProtocol.ITEM_REMOVED -> {
                                adapter.remove(tpp.data)
                                Log.d(SEARCH, "deleted ${tpp.data}")

                            }
                            else -> Log.d(SEARCH, "!?!?!? ${tpp.event} ${tpp.data}")
                        }
                    },
                            { e ->
                                val ee = e
                                Log.d(SEARCH, e.message)
                            })
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                if (s.isEmpty()) {
                    search_tool_bar_clear_button.visibility = View.VISIBLE
                    search_tool_bar_clear_button.startAnimation(fadeInAnim)
                }
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })
        search_tool_bar_clear_button.onClick {
            et_search.text.clear()
        }

        search_go_btn.onClick {
            search()
        }
    }
}
