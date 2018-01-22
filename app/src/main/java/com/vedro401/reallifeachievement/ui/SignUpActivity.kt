package com.vedro401.reallifeachievement.ui

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.vedro401.reallifeachievement.App
import com.vedro401.reallifeachievement.R
import com.vedro401.reallifeachievement.managers.interfaces.UserManager
import com.vedro401.reallifeachievement.utils.EmptyFieldsController
import kotlinx.android.synthetic.main.activity_sign_up.*
import org.jetbrains.anko.onClick
import org.jetbrains.anko.toast
import javax.inject.Inject

class SignUpActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        App.getComponent().inject(this)

        val efc = EmptyFieldsController(getString(R.string.empty_field_warning))
        efc.addField(su_et_name, "Input your name")
        efc.addField(su_et_name, "Input your email")
        efc.addField(su_et_name, "Input your password")
        su_sign_up.onClick {
            if (efc.isOkay()) {
                su_spinner.visibility = View.VISIBLE
                um.signUp(su_et_name.text.toString(), su_et_email.text.toString(), su_et_pass.text.toString())
                        .subscribe { s ->
                            if (s == "Ok") {
                                finish()
                            } else {
                                toast(s)
                            }
                            su_spinner.visibility = View.GONE

                        }
            }
        }

    }
}
