package com.vedro401.reallifeachievement.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.vedro401.reallifeachievement.App
import com.vedro401.reallifeachievement.R
import com.vedro401.reallifeachievement.managers.interfaces.UserManager
import com.vedro401.reallifeachievement.ui.profile.ProfileActivity
import kotlinx.android.synthetic.main.activity_sign_in.*
import org.jetbrains.anko.*
import javax.inject.Inject

class SignInActivity : AppCompatActivity() {
    @Inject
    lateinit var um: UserManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        App.getComponent().inject(this)

        si_sign_up.onClick {
            spinner_sign_in.visibility = View.VISIBLE
            um.signIn(si_et_email.text.toString(), si_et_pass.text.toString()).subscribe{
                s->
                if(s == "Ok"){
                    startActivity(intentFor<ProfileActivity>().clearTask())
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
