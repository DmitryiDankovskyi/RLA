package com.vedro401.reallifeachievement.view.create

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import com.vedro401.reallifeachievement.R
import kotlinx.android.synthetic.main.layout_create1.*

/**
 * Created by someone on 02.10.17.
 */
class CreateMainFragment : CreateFragment() {
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
                    et_full_description.text.toString())

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

//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//    }
}