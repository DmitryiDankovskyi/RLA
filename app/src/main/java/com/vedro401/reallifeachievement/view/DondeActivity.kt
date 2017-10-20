package com.vedro401.reallifeachievement.view

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.SeekBar

import com.vedro401.reallifeachievement.R
import com.vedro401.reallifeachievement.utils.wordDifficulty
import kotlinx.android.synthetic.main.activity_donde.*

class DondeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_donde)

        seekBar_difficulty.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                tv_difficulty.text = wordDifficulty(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

        })

    }
}
