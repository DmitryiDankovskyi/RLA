package com.vedro401.reallifeachievement.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.vedro401.reallifeachievement.App
import com.vedro401.reallifeachievement.R
import com.vedro401.reallifeachievement.managers.interfaces.DatabaseManager
import kotlinx.android.synthetic.main.activity_sign_in.*
import org.jetbrains.anko.onClick
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import javax.inject.Inject

class SignInActivity : AppCompatActivity() {
    @Inject
    lateinit var dbm: DatabaseManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        App.getComponent().inject(this)

        sign_in.onClick {
            spinner_sign_in.visibility = View.VISIBLE
            dbm.signIn(et_email.text.toString(), et_pass.text.toString()).subscribe{
                s->
                if(s == "Ok"){
                    finish()
                } else {
                    toast(s)
                }
                spinner_sign_in.visibility = View.GONE
            }
        }

        text_btn_sign_up.onClick {
            startActivity<SignUpActivity>()
            finish()
        }


    }

}
