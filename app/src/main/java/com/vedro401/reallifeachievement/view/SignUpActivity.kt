package com.vedro401.reallifeachievement.view

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.vedro401.reallifeachievement.App
import com.vedro401.reallifeachievement.R
import com.vedro401.reallifeachievement.database.DatabaseManager
import kotlinx.android.synthetic.main.activity_sign_up.*
import org.jetbrains.anko.onClick
import org.jetbrains.anko.toast
import javax.inject.Inject

class SignUpActivity : AppCompatActivity() {

    @Inject
    lateinit var dbm: DatabaseManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        App.getComponent().inject(this)
        sign_in.onClick {
            spinner.visibility = View.VISIBLE
            dbm.signUp(et_name.text.toString(), et_email.text.toString(), et_pass.text.toString())
                    .subscribe{
                s ->
                if (s == "Ok"){
                    finish()
                } else {
                    toast(s)
                }
                spinner.visibility = View.GONE

            }

        }

    }
}
